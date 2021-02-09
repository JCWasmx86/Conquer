namespace Launcher {
	string hasToDownloadJava() {
		//Done on installation
		return (string)null;
	}
	string getBaseDirectory() {
		return Environment.get_variable("APPDATA"))+"\\.conquer";
	}
}
