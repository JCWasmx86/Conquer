using Posix;

namespace Launcher {
	string? hasToDownloadJava() {
		var s1 = getProgramFiles();
		string s = (string) s1;
		GLib.stdout.printf("%s\n", s + "\\Conquer\\java-16");
		if(opendir(s + "\\Conquer\\java-16") == null) {
			string ret = s + "\\Conquer\\java-16.zip";
			GLib.free(s1);
			return ret;
		}
		return null;
	}
	string getBaseDirectory() {
		return Environment.get_variable("APPDATA") + "\\.conquer";
	}
}
