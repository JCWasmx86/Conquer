using Gee;

namespace Launcher {
	class ClasspathCollector {
		private Gee.List<string> userDefined;
		public ClasspathCollector(Gee.List<string> classpaths) {
			this.userDefined = classpaths;
		}

		public string collectClasspath() {
			string ret = "-Djava.class.path=";
			string programFiles = getProgramFiles();
			ret += (programFiles + "\\Conquer\\Conquer_resources.jar;");
			ret += (programFiles + "\\Conquer\\Conquer_frontend_resources.jar;");
			ret += (programFiles + "\\Conquer\\jlayer.jar;");
			ret += (programFiles + "\\Conquer\\jorbis.jar;");
			ret += (programFiles + "\\Conquer\\mp3spi.jar;");
			ret += (programFiles + "\\Conquer\\tritonus.jar;");
			ret += (programFiles + "\\Conquer\\music;");
			ret += (programFiles + "\\Conquer\\sounds;");
			ret += (programFiles + "\\Conquer\\images;");
			ret += (appendAllJarsFromDir + "\\plugins");
			ret += (appendAllJarsFromDir + "\\strategies");
			foreach(var i in this.userDefined) {
				ret += i +"/;";
			}
			string baseDir = getBaseDirectory();
			ret += appendAllJarsFromDir(baseDir,";");
			ret += (baseDir +"/music/;");
			ret += (baseDir +"/sounds/;");
			ret += (baseDir +"/images/;");
			ret += ".";
			free(programFiles);
			return ret;
		}
	}
	extern string getProgramFiles();
	class ModulePathCreator {
		public string create() {
			string pf = getProgramFiles();
			string ret = "--module-path="+pf+"\\Conquer\\Conquer.jar;";
			ret += pf+"\\Conquer\\Conquer_frontend.jar;";
			ret += pf+"\\Conquer\\ConquerFrontendSPI.jar;";
			free(pf);
			return ret;
		}
	}
}
