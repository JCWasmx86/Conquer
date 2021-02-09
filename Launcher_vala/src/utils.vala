using Posix;

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
}
