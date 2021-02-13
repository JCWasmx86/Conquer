using Gee;

namespace Launcher {
	class ClasspathCollector {
		private Gee.List<string> userDefined;
		public ClasspathCollector(Gee.List<string> classpaths) {
			this.userDefined = classpaths;
		}
		public string collectClasspath() {
			string ret = "-Djava.class.path=/usr/share/java/Conquer_resources.jar:/usr/share/java/Conquer_frontend_resources.jar:";
			foreach(var i in this.userDefined) {
				ret += i + ":";
			}
			string baseDir = getBaseDirectory();
			ret += appendAllJarsFromDir(baseDir + "/libs/", ":");
			ret += appendAllJarsFromDir("/usr/share/java/conquer/plugins/", ":");
			ret += appendAllJarsFromDir("/usr/share/java/conquer/strategies/", ":");
			ret += "/usr/share/conquer/music:";
			ret += "/usr/share/conquer/sounds:";
			ret += "/usr/share/conquer/images:";
			ret += "/usr/share/java/conquer/jlayer.jar:/usr/share/java/conquer/jorbis.jar:/usr/share/java/conquer/mp3spi.jar:";
			ret += "/usr/share/java/conquer/tritonus.jar:/usr/share/java/conquer/vorbisspi.jar:";
			ret += (baseDir + "/libs/music/:");
			ret += (baseDir + "/libs/sounds/:");
			ret += (baseDir + "/libs/images/:");
			ret += ".";
			return ret;
		}
	}
	class ModulePathCreator {
		public string create() {
			return "--module-path=/usr/share/java/Conquer.jar:/usr/share/java/Conquer_frontend.jar:/usr/share/java/ConquerFrontendSPI.jar";
		}
	}
}
