using Gee;

namespace Launcher {
	delegate void onErrorFunc(string stacktrace, string reportLocation);
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
		public void run(string? directory) {
			var size = arguments.size;
			string[] options = new string[size + 8];
			options[0] = "-XX:+ShowCodeDetailsInExceptionMessages";
			options[1] = new ClasspathCollector(classpaths).collectClasspath();
			options[2] = "--enable-preview";
			options[3] = new ModulePathCreator().create();
			options[4] = "--add-modules=conquer,conquer.frontend";
			options[5] = "-Dsun.java2d.opengl=true";
			options[6 + size] = "-m";
			options[7 + size] = "conquer.frontend/conquer.gui.Intro";
			for(int i = 0; i < size; i++) {
				options[6 + i] = arguments.get(i);
			}
			invokeJVM(options, size + 8, (char*) directory, this.onErrorFunc);
		}
	}
	extern void invokeJVM(char** options, int numOptions, char* directory, onErrorFunc onErrorFunc);
}
