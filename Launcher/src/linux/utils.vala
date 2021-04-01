using Posix;

namespace Launcher {
	string? hasToDownloadJava() {
		var alreadyInstalledJava = findExistingJavaInstallWithMatchingVersion();
		if(alreadyInstalledJava != null) {
			return null;
		}
		var baseDir = geteuid() == 0? "/opt" : getBaseDirectory();
		var java16Dir = baseDir + "/java-16";
		var outputFile = baseDir + "/java-16.tar.gz";
		return (opendir(java16Dir) != null || opendir("/opt/java-16") != null) ? null : outputFile;
	}

	string getOutputDirectory() {
		if(geteuid() != 0) {
			return getBaseDirectory() + "/java-16/";
		} else {
			return "/opt/java-16";
		}
	}
	string? findExistingJavaInstallWithMatchingVersion() {
		var jvmDirectory = getLinuxJavaDirectory();
		if(jvmDirectory != null && opendir(jvmDirectory) != null) {
			int version = readReleaseFile(jvmDirectory);
			if(version == 16) {
				return jvmDirectory;
			}
		}
		var javaHomeDirectory = Environment.get_variable("JAVA_HOME");
		if (javaHomeDirectory != null && opendir(javaHomeDirectory) != null) {
			int version = readReleaseFile(javaHomeDirectory);
			if (version == 16) {
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
