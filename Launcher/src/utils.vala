using Posix;
using Gee;
using Json;
using Curl;
using Gtk;

namespace Launcher {
	string appendAllJarsFromDir(string directory, string separator) {
		Posix.Dir dir = opendir(directory);
		if(dir == null){
			return "";
		}
		unowned DirEnt entry;
		string ret="";
		while((entry = readdir(dir)) != null) {
			var name = (string)entry.d_name;
			if(name != "." && name != ".." && name.has_suffix(".jar")) {
				ret += (directory+"/"+name +separator);
			}
		}
		return ret;
	}
	delegate void extractFunc(string name, int current, int max);
	void extractJDK(extractFunc func) {
		Archive.ExtractFlags flags;
		flags = Archive.ExtractFlags.TIME;
		flags |= Archive.ExtractFlags.PERM;
		flags |= Archive.ExtractFlags.ACL;
		flags |= Archive.ExtractFlags.FFLAGS;
		Archive.Read archive = new Archive.Read ();
		archive.support_filter_all();
		archive.support_format_all();
		Archive.WriteDisk extractor = new Archive.WriteDisk();
		extractor.set_options(flags);
		extractor.set_standard_lookup();
		string outputFile = hasToDownloadJava();
		if(outputFile == null) {
			return;
		}
		if (archive.open_filename(outputFile, 10240) != Archive.Result.OK) {
			critical("Error opening %s: %s (%d)", outputFile, archive.error_string (), archive.errno ());
			return;
		}
		unowned Archive.Entry entry;
		Archive.Result last_result;
		string outputDirectory = getOutputDirectory();
		var cnt = 0;
		while ((last_result = archive.next_header (out entry)) == Archive.Result.OK) {
			cnt++;
			string entryName = entry.pathname();
			int firstSlash = entry.pathname().index_of("/");
			string toReplace = entry.pathname().slice(0,firstSlash+1);
			entry.set_pathname(entry.pathname().replace(toReplace,outputDirectory));
			if (extractor.write_header (entry) != Archive.Result.OK) {
				continue;
			}
			unowned uint8[] buffer = null;
			off_t offset;
			func(entryName,cnt, archive.file_count());
			while (archive.read_data_block(out buffer, out offset) == Archive.Result.OK) {
				if (extractor.write_data_block(buffer, offset) != Archive.Result.OK) {
					break;
				}
			}
		}
		try{
			GLib.File.new_for_path(outputFile).@delete();
		}catch(Error e) {
			GLib.stderr.printf("%s\n",e.message);
		}
		if (last_result != Archive.Result.EOF) {
			critical ("Error: %s (%d)", archive.error_string (), archive.errno ());
		}
	}

	void tryUpdating() {
		//TODO
	}
	delegate void progressFunc(void* data,double dltotal,double dlnow,double ultotal,double ulnow);
	
	interface IDownloadProgress : GLib.Object{
		public abstract void onProgress(double dltotal,double dlnow,double ultotal,double ulnow);
	}
	
	string obtainURL() {
		var liberica = new LibericaJDK();
		var url = liberica.obtain();
		if(url == null) {
			var adopt = new AdoptOpenJDK();
			url = adopt.obtain();
			if(url == null) {
				critical("No JDK binary for your system found!");
			}
		}
		GLib.stdout.printf("Using URL: %s\n",url);
		return url;
	}
	void downloadJDK(IDownloadProgress dp) {
		var handle = new EasyHandle();
		if(handle != null) {
			FILE fp = Posix.FILE.open(hasToDownloadJava(), "wb");
			if(fp == null) {
				perror("fopen");
				return;
			}
			handle.setopt(URL, obtainURL());
			handle.setopt(WRITEFUNCTION, writeData);
			handle.setopt(WRITEDATA, fp);
			handle.setopt(FOLLOWLOCATION, true);
			handle.setopt(PROGRESSDATA, dp);
			handle.setopt(VERBOSE,true);
			//TODO: This is not a good solution
			handle.setopt(SSL_VERIFYPEER,false);
			handle.setopt(NOPROGRESS, 0);
			handle.setopt(PROGRESSFUNCTION, handleProgressCurl);
			Code c = handle.perform();
			if(c != OK){
				GLib.stderr.printf("%s\n",Global.strerror(c));
			}
		}
	}
	size_t writeData(void* ptr, size_t size, size_t nmemb, FILE stream) {
		return stream.write(ptr,size,nmemb);
	}
	int handleProgressCurl(void *clientp, double dltotal, double dlnow, double ultotal, double ulnow) {
		IDownloadProgress dp = (IDownloadProgress) clientp;
		dp.onProgress(dltotal,dlnow,ultotal,ulnow);
		return 0;
	}
	extern void makeDirectory(string s);
	class Configuration {
		public Gee.List<string> classpaths;
		public Gee.List<string> arguments;
		
		public static Configuration? readConfig() {
			makeDirectory(getBaseDirectory());
			string file = getBaseDirectory()+"/config.json";
			Parser parser = new Parser();
			try {
				parser.load_from_file(file);
			}catch(Error e) {
				print("Error while loading config.json: %s\n",e.message);
				return null;
			}
			var ret = new Configuration();
			ret.classpaths = new ArrayList<string>();
			ret.arguments = new ArrayList<string>();
			var parent = parser.get_root();
			if(parent == null) {
				return null;
			}
			var object = parent.get_object();
			if(object != null) {
				if(object.has_member("classpaths")) {
					object.get_array_member("classpaths").foreach_element((array, index, node) =>{
						ret.classpaths.add(node.get_string());
					});
				}
				if(object.has_member("options")) {
					object.get_array_member("options").foreach_element((array, index, node) =>{
						ret.arguments.add(node.get_string());
					});
				}
			}
			return ret;
		}
		public static void dump(Gee.List<string> arguments, Gee.List<string> classpaths) {
			try{
				GLib.File.new_for_path(getBaseDirectory()+"/config.json").@delete();
			}catch(Error e) {
				GLib.stderr.printf("%s\n",e.message);
			}
			var jsonClasspaths = new Json.Array();
			foreach(var cp in classpaths) {
				jsonClasspaths.add_string_element(cp);
			}
			var jsonArguments = new Json.Array();
			foreach(var arg in arguments) {
				jsonArguments.add_string_element(arg);
			}
			var object = new Json.Object();
			object.set_array_member("classpaths",jsonClasspaths);
			object.set_array_member("options",jsonArguments);
			var generator = new Json.Generator();
			var node = new Json.Node(NodeType.OBJECT);
			node.init_object(object);
			generator.set_root(node);
			generator.set_pretty(true);
			generator.set_indent_char('\t');
			try{
				generator.to_file(getBaseDirectory()+"/config.json");
			}catch(Error e) {
				GLib.stderr.printf("%s\n",e.message);
			}
		}
	}
	void showErrorScreen(owned char* stacktrace, owned char* reportLocation) {
		new CrashReporter((string)stacktrace,(string)reportLocation).doIt();
	}
	class CrashReporter : GLib.Object {
		string stacktrace;
		string reportLocation;
		public CrashReporter(string st,string rl) {
			this.stacktrace=st;
			this.reportLocation=rl;
		}
		bool showWindow() {
			var window = new Window();
			window.title = "Conquer crashed";
			window.window_position = WindowPosition.CENTER;
			var buffer = new TextBuffer(null);
			buffer.text= "Full report at: %s\n\n%s\n\n".printf(this.reportLocation, this.stacktrace);
			var textView = new TextView.with_buffer(buffer);
			window.add(textView);
			window.show_all();
			Gtk.main();
			return true;
		}
		public void doIt() {
			Gdk.threads_add_idle(showWindow);
		}
	}
}
