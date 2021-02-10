namespace Launcher {
	string? hasToDownloadJava() {
		string s = getProgramFiles();
		if(opendir(s+"\\Conquer\\java-15") == null) {
			string ret = s+"\\Conquer\\java-15.zip";
			free(s);
			return ret;
		}
		return null;
	}
	string getBaseDirectory() {
		return Environment.get_variable("APPDATA")+"\\.conquer";
	}
}
