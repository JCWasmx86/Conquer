using Posix;
using Gee;
using Json;

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
			GLib.stdout.printf("Extracting %s to %s\n",entryName, entry.pathname());
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

		if (last_result != Archive.Result.EOF) {
			critical ("Error: %s (%d)", archive.error_string (), archive.errno ());
		}
	}
	class Configuration {
		Gee.List<string> classpaths;
		Gee.List<string> arguments;
		
		public static Configuration? readConfig() {
			string file = getBaseDirectory()+"/config.json";
			Parser parser = new Parser();
			try {
				parser.load_from_file(file);
			}catch(Error e) {
				print("Error while loading config.json: %s\n",e.message);
				return null;
			}
			Configuration ret = new Configuration();
			ret.classpaths = new ArrayList<string>();
			ret.arguments = new ArrayList<string>();
			Json.Node parent = parser.get_root();
			if(parent == null) {
				return null;
			}
			GLib.stdout.printf("%s\n",parent.type_name());
			return ret;
		}
	}
}
