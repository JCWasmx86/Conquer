using Gee;

namespace Launcher {
	delegate void onErrorFunc(string stacktrace,string reportLocation);
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
			string[] options = new string[arguments.size + 9];
			options[0] = "-XX:+ShowCodeDetailsInExceptionMessages";
			options[1] = new ClasspathCollector(classpaths).collectClasspath();
			options[2] = "--enable-preview";
			options[3] = "-Xms1G";
			options[4] = new ModulePathCreator().create();
			options[5] = "--add-modules=org.jel.game,org.jel.frontend";
			options[6] = "-Dsun.java2d.opengl=True";
			options[7 + arguments.size] = "-m";
			options[8 + arguments.size] = "org.jel.frontend/org.jel.gui.Intro";
			for(int i = 0; i < arguments.size; i++) {
				options[7 + i] = arguments.get(i);
			}
			invokeJVM(options,arguments.size + 9, this.onErrorFunc);
		}
	}
	extern void invokeJVM(char** options, int numOptions, onErrorFunc onErrorFunc);
}
