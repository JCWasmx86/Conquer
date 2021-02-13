using Posix;

namespace Launcher {
	string? hasToDownloadJava() {
		char* alreadyInstalledJava = findExistingJavaInstallWithMatchingVersion();
		if(alreadyInstalledJava != null) {
			return null;
		}
		string baseDir = geteuid() == 0?"/opt":getBaseDirectory();
		string java15Dir = baseDir + "/java-15";
		string outputFile = baseDir + "/java-15.tar.gz";
		return (opendir(java15Dir) != null || opendir("/opt/java-15") != null) ? null : outputFile;
	}

	string getOutputDirectory() {
		if(geteuid() != 0) {
			return getBaseDirectory() + "/java-15/";
		} else {
			return "/opt/java-15";
		}
	}
	string? findExistingJavaInstallWithMatchingVersion() {
		string jvmDirectory = getLinuxJavaDirectory();
		if(jvmDirectory != null && opendir(jvmDirectory) != null) {
			int version = readReleaseFile(jvmDirectory);
			if(version == 15) {
				return jvmDirectory;
			}
		}
		string javaHomeDirectory = Environment.get_variable("JAVA_HOME");
		if (javaHomeDirectory != null && opendir(javaHomeDirectory) != null) {
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
}
