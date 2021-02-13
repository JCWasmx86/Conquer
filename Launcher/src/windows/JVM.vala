using Gee;

namespace Launcher {
	class ClasspathCollector {
		private Gee.List<string> userDefined;
		public ClasspathCollector(Gee.List<string> classpaths) {
			this.userDefined = classpaths;
		}

		public string collectClasspath() {
			var ret = "-Djava.class.path=";
			var programFiles1 = getProgramFiles();
			var programFiles = (string) programFiles1;
			ret += (programFiles + "\\Conquer\\Conquer_resources.jar;");
			ret += (programFiles + "\\Conquer\\Conquer_frontend_resources.jar;");
			ret += (programFiles + "\\Conquer\\jlayer.jar;");
			ret += (programFiles + "\\Conquer\\jorbis.jar;");
			ret += (programFiles + "\\Conquer\\mp3spi.jar;");
			ret += (programFiles + "\\Conquer\\tritonus.jar;");
			ret += (programFiles + "\\Conquer\\vorbisspi.jar;");
			ret += (programFiles + "\\Conquer\\music;");
			ret += (programFiles + "\\Conquer\\sounds;");
			ret += (programFiles + "\\Conquer\\images;");
			ret += appendAllJarsFromDir(programFiles + "\\plugins", ";");
			ret += appendAllJarsFromDir(programFiles + "\\strategies", ";");
			foreach(var i in this.userDefined) {
				ret += i + ";";
			}
			string baseDir = getBaseDirectory() + "/libs/";
			ret += appendAllJarsFromDir(baseDir, ";");
			ret += (baseDir + "/music/;");
			ret += (baseDir + "/sounds/;");
			ret += (baseDir + "/images/;");
			ret += ".";
			free(programFiles1);
			return ret;
		}
	}

	class ModulePathCreator {
		public string create() {
			var pf = getProgramFiles();
			string ret = "--module-path=" + ((string) pf) + "\\Conquer\\Conquer.jar;";
			ret += ((string) pf) + "\\Conquer\\Conquer_frontend.jar;";
			ret += ((string) pf) + "\\Conquer\\ConquerFrontendSPI.jar;";
			free(pf);
			return ret;
		}
	}
	extern char* getProgramFiles();

	string getOutputDirectory() {
		var s = getProgramFiles();
		var ret = ((string) s) + "\\Conquer\\java-15\\";
		free(s);
		return ret;
	}
}
