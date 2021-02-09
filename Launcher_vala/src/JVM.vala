using Gee;

namespace Launcher {
	class JVM {
		onErrorFunc onErrorFunc;
		List<string> arguments;
		List<string> classpaths;

		public JVM(onErrorFunc? func) {
			this.onErrorFunc = func;
		}
		public void addJVMArguments(List<string> arguments) {
			
		}
		public void addClasspaths(List<string> classpaths) {
			
		}
		public void run() {
			string[] options = new string[arguments.size + 6];
			options[1] = new ClasspathCollector(classpaths).collectClasspath();
			options[2] = "--enable-preview";
			options[3] = "-Xms1G";
			options[4] = new ModulePathCreator().create();
			options[5] = "--add-modules=org.jel.game,org.jel.frontend";
			for(int i = 0; i < arguments.size; i++) {
				options[6 + i] = arguments.get(i);
			}
		}
	}
}
