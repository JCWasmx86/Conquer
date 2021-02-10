using Gee;

namespace Launcher {
	delegate void onErrorFunc(string stacktrace,string systemProperties, string environmentVariables);
	class JVM {
		onErrorFunc onErrorFunc;
		Gee.List<string> arguments;
		Gee.List<string> classpaths;

		public JVM(onErrorFunc? func) {
			this.onErrorFunc = func;
		}
		public void addJVMArguments(Gee.List<string> arguments) {
			this.arguments = arguments;
		}
		public void addClasspaths(Gee.List<string> classpaths) {
			this.classpaths = classpaths;
		}
		public void run() {
			string[] options = new string[arguments.size + 6];
			options[0] = "-XX:+ShowCodeDetailsInExceptionMessages";
			options[1] = new ClasspathCollector(classpaths).collectClasspath();
			options[2] = "--enable-preview";
			options[3] = "-Xms1G";
			options[4] = new ModulePathCreator().create();
			options[5] = "--add-modules=org.jel.game,org.jel.frontend";
			for(int i = 0; i < arguments.size; i++) {
				options[6 + i] = arguments.get(i);
			}
			for(int i = 0; i<arguments.size + 6; i++) {
				stdout.printf("%s\n",options[i]);
			}
		}
	}
}
