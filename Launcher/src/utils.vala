using Curl;
using Gee;
using Gtk;
using Json;
using Posix;

namespace Launcher {
	string appendAllJarsFromDir(string directory, string separator) {
		Posix.Dir dir = opendir(directory);
		if(dir == null) {
			return "";
		}
		unowned DirEnt entry;
		string ret = "";
		while((entry = readdir(dir)) != null) {
			var name = (string) entry.d_name;
			if(name != "." && name != ".." && name.has_suffix(".jar")) {
				ret += (directory + "/" + name + separator);
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
			string toReplace = entry.pathname().slice(0, firstSlash + 1);
			entry.set_pathname(entry.pathname().replace(toReplace, outputDirectory));
			if (extractor.write_header (entry) != Archive.Result.OK) {
				continue;
			}
			unowned uint8[] buffer = null;
			off_t offset;
			func(entryName, cnt, archive.file_count());
			while (archive.read_data_block(out buffer, out offset) == Archive.Result.OK) {
				if (extractor.write_data_block(buffer, offset) != Archive.Result.OK) {
					break;
				}
			}
		}
		if (last_result != Archive.Result.EOF) {
			critical ("Error: %s (%d)", archive.error_string (), archive.errno ());
		}
		try{
			//Fails with "Invalid argument" on windows. TODO
			GLib.File.new_for_path(outputFile).@delete();
		}catch(Error e) {
			GLib.stderr.printf("%s\n", e.message);
		}
	}

	void tryUpdating() {
		//TODO
	}
	delegate void progressFunc(void* data, double dltotal, double dlnow, double ultotal, double ulnow);

	interface IDownloadProgress : GLib.Object {
		public abstract void onProgress(double dltotal, double dlnow, double ultotal, double ulnow);
	}

	string? cachedURL = null;

	string obtainURL() {
		if(cachedURL != null) {
			return cachedURL;
		}
		var liberica = new LibericaJDK();
		var url = liberica.obtain();
		if(url == null) {
			var adopt = new AdoptOpenJDK();
			url = adopt.obtain();
			if(url == null) {
				critical("No JDK binary for your system found!");
			}
		}
		cachedURL = url;
		GLib.stdout.printf("Using URL: %s\n", url);
		return url;
	}

	bool hasInternetConnection() {
		var handle = new EasyHandle();
		if(handle != null) {
			handle.setopt(URL, "http://example.com/");
			handle.setopt(VERBOSE, true);
			//TODO: This is not a good solution
			handle.setopt(SSL_VERIFYPEER, false);
			var c = handle.perform();
			return c == OK;
		}
		return false;
	}
	void downloadJDK(IDownloadProgress dp) {
		var handle = new EasyHandle();
		if(handle != null) {
			var fp = Posix.FILE.open(hasToDownloadJava(), "wb");
			if(fp == null) {
				perror("fopen");
				return;
			}
			handle.setopt(URL, obtainURL());
			handle.setopt(WRITEFUNCTION, writeData);
			handle.setopt(WRITEDATA, fp);
			handle.setopt(FOLLOWLOCATION, true);
			handle.setopt(PROGRESSDATA, dp);
			handle.setopt(VERBOSE, true);
			//TODO: This is not a good solution
			handle.setopt(SSL_VERIFYPEER, false);
			handle.setopt(NOPROGRESS, 0);
			handle.setopt(PROGRESSFUNCTION, handleProgressCurl);
			Code c = handle.perform();
			if(c != OK) {
				GLib.stderr.printf("%s\n", Global.strerror(c));
			}
		}
	}
	size_t writeData(void* ptr, size_t size, size_t nmemb, FILE stream) {
		return stream.write(ptr, size, nmemb);
	}
	int handleProgressCurl(void *clientp, double dltotal, double dlnow, double ultotal, double ulnow) {
		IDownloadProgress dp = (IDownloadProgress) clientp;
		dp.onProgress(dltotal, dlnow, ultotal, ulnow);
		return 0;
	}
	extern void makeDirectory(string s);
	class Configuration {
		public Gee.List<string> classpaths;
		public Gee.List<string> arguments;
		public string? javaFolder;
		public string? xss;
		public string? xmn;
		public string? xms;
		public string? xmx;
		public bool useNativeLAF;
		public static Configuration? readConfig() {
			makeDirectory(getBaseDirectory());
			var file = getBaseDirectory() + "/config.json";
			Parser parser = new Parser();
			try {
				parser.load_from_file(file);
			}catch(Error e) {
				print("Error while loading config.json: %s\n", e.message);
				return null;
			}
			var ret = new Configuration();
			ret.classpaths = new ArrayList<string>();
			ret.arguments = new ArrayList<string>();
			ret.useNativeLAF = true;
			var parent = parser.get_root();
			if(parent == null) {
				return null;
			}
			var object = parent.get_object();
			if(object != null) {
				if(object.has_member("classpaths")) {
					object.get_array_member("classpaths").foreach_element((array, index, node) => {
						ret.classpaths.add(node.get_string());
					});
				}
				if(object.has_member("options")) {
					object.get_array_member("options").foreach_element((array, index, node) => {
						ret.arguments.add(node.get_string());
					});
				}
				if(object.has_member("useNativeLAF")) {
					ret.useNativeLAF = object.get_string_member("useNativeLAF") == "true"?true:false;
				}
				if(object.has_member("java")) {
					ret.javaFolder = object.get_string_member("java");
				}
				if(object.has_member("xmn")) {
					ret.xmn = object.get_string_member("xmn");
				}
				if(object.has_member("xms")) {
					ret.xms = object.get_string_member("xms");
				}
				if(object.has_member("xmx")) {
					ret.xmx = object.get_string_member("xmx");
				}
			}
			return ret;
		}
		public static void dump(Gee.List<string> arguments, Gee.List<string> classpaths, string? javaFolder,
		 Gee.Map<string, string> memorySettings, bool useNativeLAF) {
			try{
				GLib.File.new_for_path(getBaseDirectory() + "/config.json").@delete();
			}catch(Error e) {
				GLib.stderr.printf("%s\n", e.message);
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
			object.set_array_member("classpaths", jsonClasspaths);
			object.set_array_member("options", jsonArguments);
			object.set_string_member("useNativeLAF", useNativeLAF?"true":"false");
			if(javaFolder == null) {
				object.set_null_member("java");
			} else {
				object.set_string_member("java", javaFolder);
			}
			memorySettings.map_iterator().@foreach((k, v) => {
				object.set_string_member(k, v);
				return true;
			});
			var generator = new Json.Generator();
			var node = new Json.Node(NodeType.OBJECT);
			node.init_object(object);
			generator.set_root(node);
			generator.set_pretty(true);
			generator.set_indent_char('\t');
			try{
				generator.to_file(getBaseDirectory() + "/config.json");
			}catch(Error e) {
				GLib.stderr.printf("%s\n", e.message);
			}
		}
	}

	private int readReleaseFile(string directory) {
		var filePath = string.join("/", directory, "release");
		var file = File.new_for_path(filePath);
		if (!file.query_exists()) {
			return -1;
		}
		try {
			var dis = new DataInputStream(file.read());
			string line;
			while((line = dis.read_line(null)) != null) {
				line = line.strip();
				if(line.contains("JAVA_VERSION=")) {
					line = line.replace("JAVA_VERSION=", "");
					line = line.replace("\"", "");
					if(line[0] == '1' && line[1] == '6') {
						return 16;
					}
				}
			}
		}catch(Error e) {
			error("%s", e.message);
		}
		return 0;
	}

	extern string getSeparator();
	extern string getPathSeparator();
}
