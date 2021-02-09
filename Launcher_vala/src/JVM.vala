namespace Launcher {
	class JVM {
		onErrorFunc onErrorFunc;
		public JVM(onErrorFunc? func) {
			this.onErrorFunc = func;
		}
		public void addJVMArguments(Gee.List<string> arguments) {
			
		}
		public void addClasspaths(Gee.List<string> classpaths) {
			
		}
	}
}
