using Posix;

namespace Launcher {
	string? hasToDownloadJava() {
		var s1 = getProgramFiles();
		string s = (string)s1;
		if(opendir(s+"\\Conquer\\java-15") == null) {
			string ret = s+"\\Conquer\\java-15.zip";
			GLib.free(s1);
			return ret;
		}
		return null;
	}
	string getBaseDirectory() {
		return Environment.get_variable("APPDATA")+"\\.conquer";
	}
}
