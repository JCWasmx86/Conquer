using Posix;

namespace Launcher {
	string? hasToDownloadJava() {
		char* alreadyInstalledJava = findExistingJavaInstallWithMatchingVersion();
		if(alreadyInstalledJava != null) {
			//return null;
		}
		string baseDir = geteuid() == 0?"/opt":getBaseDirectory();
		string java15Dir = baseDir + "/java-15";
		string outputFile = baseDir +"/java-15.tar.gz";
		return (opendir(java15Dir)!=null||opendir("/opt/java-15")!=null) ? null : outputFile;
	}
	delegate void onErrorFunc(string stacktrace,string systemProperties, string environmentVariables);
	
	string getOutputDirectory() {
		if(geteuid() != 0) {
			return getBaseDirectory()+"/java-15/";
		}else {
			return "/opt/java-15";
		}
	}
	private string? findExistingJavaInstallWithMatchingVersion() {
		string jvmDirectory = getLinuxJavaDirectory();
		if(jvmDirectory != null&&opendir(jvmDirectory) != null) {
			int version = readReleaseFile(jvmDirectory);
			if(version == 15){
				return jvmDirectory;
			}
		}
		string javaHomeDirectory = Environment.get_variable("JAVA_HOME");
		if (javaHomeDirectory!= null && opendir(javaHomeDirectory) != null) {
			int version = readReleaseFile(javaHomeDirectory);
			if (version == 15) {
				return javaHomeDirectory;
			}
		}
		return null;
	}
	private string getBaseDirectory() {
		string homedir;
		if ((homedir = Environment.get_variable("HOME")) == null) {
			homedir = getpwuid(getuid()).pw_dir;
		}
		return homedir + "/.config/.conquer";
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
