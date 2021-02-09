using Posix;

namespace Launcher {
	string hasToDownloadJava() {
		char* alreadyInstalledJava = findExistingJavaInstallWithMatchingVersion();
		if(alreadyInstalledJava != null) {
			return (string)null;
		}
		string baseDir = geteuid() == 0?"/opt":getBaseDirectory();
		string java15Dir = baseDir + "/java-15";
		string outputFile = baseDir +"/java-15.tar.gz";
		return opendir(java15Dir)!=null||opendir("/opt/java-15")!=null? (string)null : outputFile;
	}
	delegate void progressFunc(void* unused,uint64 dltotal,uint64 dlnow,uint64 ultotal,uint64 ulnow);
	delegate void extractFunc(string name, int current, int max);
	delegate void onErrorFunc(string stacktrace,string systemProperties, string environmentVariables);
	
	void downloadJDK(progressFunc func) {
		
	}
	void extractJDK(extractFunc func) {
		
	}
	private string findExistingJavaInstallWithMatchingVersion() {
		string jvmDirectory = getLinuxJavaDirectory();
		if(jvmDirectory != null) {
			Posix.Dir dir = opendir(jvmDirectory);
			if(dir != null) {
				int version = readReleaseFile(jvmDirectory);
				if(version == 15){
					return jvmDirectory;
				}
			}
		}
		string javaHomeDirectory = Environment.get_variable("JAVA_HOME");
		if (javaHomeDirectory!= null && opendir(javaHomeDirectory) != null) {
			int version = readReleaseFile(javaHomeDirectory);
			if (version == 15) {
				return javaHomeDirectory;
			}
		}
		return (string)null;
	}
	private string getLinuxJavaDirectory() {
		return "";
	}
	private string getBaseDirectory() {
		return "";
	}
	private int readReleaseFile(string directory) {
		var filePath = string.join("/",directory,"release");
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
					line = line.replace("JAVA_VERSION=","");
					line = line.replace("\"","");
					if(line[0]=='1'&&line[1]=='5') {
						return 15;
					}
				}
			}
		}catch(Error e) {
			error("%s",e.message);
		}
		return 0;
	}
}
