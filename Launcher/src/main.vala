using Gee;
using Gtk;
using Posix;

namespace Launcher {
	class ConquerLauncher : Gtk.Application {
		private InputList jvmOptions;
		private InputList classpaths;
		private SelectJavaBox selectJava;
		private MemorySettings memorySettings;
		protected override void activate() {
			var window = new ApplicationWindow(this);
			var box = new Box(Orientation.VERTICAL, 0);
			this.jvmOptions = new InputList("JVM Arguments", "Add JVM argument",
			  new JVMOptionsCompletion());
			box.pack_start(this.jvmOptions, false, false);
			this.classpaths = new InputList("Classpaths", "Add classpath", new ClasspathCompletion());
			box.pack_start(this.classpaths, false, false);
			this.selectJava = new SelectJavaBox();
			box.pack_start(this.selectJava, false, false);
			this.memorySettings = new MemorySettings();
			box.pack_start(this.memorySettings, false, false);
			var startButtonPanel = new StartButton(this.classpaths, this.jvmOptions, this.selectJava,
			  this.memorySettings, window);
			box.pack_start(startButtonPanel, false, false);
			window.add(box);
			window.set_title("Conquer launcher 2.0.0");
			window.show_all();
			selectJava.change();
			this.initConfig();
		}
		void initConfig() {
			Configuration config = Configuration.readConfig();
			if(config == null) {
				return;
			}
			foreach(var i in config.classpaths) {
				this.classpaths.addElement(i);
			}
			foreach(var i in config.arguments) {
				this.jvmOptions.addElement(i);
			}
			if(config.javaFolder != null) {
				this.selectJava.configure(config.javaFolder);
			}
			this.memorySettings.configure(config);
		}
	}

	public int main(string[] args) {
		return new ConquerLauncher().run(args);
	}

	class StartButton : Box, IDownloadProgress {
		private ProgressBar progressBar;
		private ExtractProgress extractProgress;
		private AsyncQueue<DownloadProgress> asyncQueue;

		public StartButton(InputList classpaths, InputList jvmOptions, SelectJavaBox selectJava, MemorySettings
		 memorySettings, ApplicationWindow window) {
			this.asyncQueue = new AsyncQueue<DownloadProgress>();
			this.set_orientation(Orientation.VERTICAL);
			var button = new Button.with_label("Start");
			this.pack_start(button, false, false);
			button.clicked.connect(() => {
				var javaFolder = selectJava.getFolder();
				var givenVersion = javaFolder == null ? -1 : readReleaseFile(javaFolder);
				var isMatching = javaFolder == null ? false : givenVersion == 16;
				this.remove(button);
				button.destroy();
				this.progressBar = new ProgressBar();
				this.progressBar.set_show_text(true);
				this.pack_start(this.progressBar, false);
				window.show_all();
				if((!isMatching) && hasToDownloadJava() != null) {
					new Thread<void>("downloadAndExtractThread", () => {
						if(hasInternetConnection()) {
							downloadJDK(this);
							extractJDK(extractReceiver);
							tryUpdating();
							window.hide();
							new Thread<void>("runJVM", () => {
								JVM jvm = new JVM(null);
								jvm.addJVMArguments(jvmOptions.toList());
								jvm.addClasspaths(classpaths.toList());
								Configuration.dump(jvmOptions.toList(),
								classpaths.toList(), javaFolder,
								memorySettings.toMap());
								jvm.run(memorySettings.getOptions(), null);
								Process.exit(0);
							});
						} else {
							var dialog = new MessageDialog(null, DESTROY_WITH_PARENT |
							MODAL, ERROR, OK, "No internet connection!");
							dialog.run();
							Process.exit(-1);
						}
					});
				} else {
					tryUpdating();
					window.hide();
					new Thread<void>("runJVM", () => {
						JVM jvm = new JVM(null);
						jvm.addJVMArguments(jvmOptions.toList());
						jvm.addClasspaths(classpaths.toList());
						Configuration.dump(jvmOptions.toList(), classpaths.toList(), javaFolder,
						memorySettings.toMap());
						jvm.run(memorySettings.getOptions(), isMatching ? javaFolder : null);
						Process.exit(0);
					});
				}
			});
		}
		bool updateDownloadProgressBar() {
			var data = asyncQueue.pop();
			if(data.getPercentage() != data.getPercentage()) {
				this.progressBar.set_text("Starting download...");
				return false;
			}
			this.progressBar.set_text("Downloaded %.0lf of %.0lf bytes (%.2lf %%)".printf(data.dlnow,
			 data.dltotal, data.getPercentage()));
			this.progressBar.set_fraction(data.getPercentage() / 100);
			return false;
		}
		public void onProgress(double dltotal, double dlnow, double ultotal, double ulnow) {
			var progress = new DownloadProgress(dltotal, dlnow);
			asyncQueue.push(progress);
			Gdk.threads_add_idle(updateDownloadProgressBar);
		}
		bool updateExtractProgressbar() {
			this.progressBar.set_text("Extracting %s (%d/%d)".printf(this.extractProgress.filename,
			 this.extractProgress.current, this.extractProgress.numberOfFiles));
			return false;
		}
		void extractReceiver(string name, int current, int max) {
			this.extractProgress = new ExtractProgress(name, current, max);
			Gdk.threads_add_idle(updateExtractProgressbar);
		}
	}
	class InputList : Box {
		private Gtk.ListStore listStore;
		private TreeViewWithPopup treeView;

		public InputList(string name, string label, EntryCompletion? completion) {
			this.set_orientation(Orientation.VERTICAL);
			this.listStore = new Gtk.ListStore(1, GLib.Type.STRING);
			this.treeView = new TreeViewWithPopup();
			this.treeView.init(this.listStore);
			this.treeView.set_model(this.listStore);
			this.pack_start(this.treeView);
			this.pack_start(new InputBox(this, label, this.listStore, completion));
			var column = new TreeViewColumn();
			column.set_title(name);
			var renderer = new CellRendererText();
			column.pack_start(renderer, true);
			column.add_attribute(renderer, "text", 0);
			this.treeView.append_column(column);
			this.treeView.set_model(this.listStore);
		}

		public Gee.List<string> toList() {
			var ret = new ArrayList<string>();
			TreeIter iter;
			if(this.listStore.get_iter_first(out iter)) {
				do {
					Value s;
					this.listStore.get_value(iter, 0, out s);
					ret.add(s.get_string());
				}while(this.listStore.iter_next(ref iter));
			}
			return ret;
		}
		public void addElement(string s) {
			TreeIter tp;
			this.listStore.insert_with_values(out tp, -1, 0, s.strip(), -1);
		}
	}

	class InputBox : Box {
		public InputBox(InputList list, string buttonLabel, Gtk.ListStore store, EntryCompletion? completion) {
			this.set_orientation(Orientation.HORIZONTAL);
			var entry = new Entry();
			if(completion != null) {
				entry.set_completion(completion);
			}
			this.pack_start(entry);
			var button = new Button.with_label(buttonLabel);
			this.pack_start(button, false, false);
			button.clicked.connect(() => {
				var text = entry.text;
				if(text.strip().length == 0) {
					return;
				}
				list.addElement(text.strip());
				entry.text = "";
			});
		}
	}

	class SelectJavaBox : Box {
		private FileChooserButton fileChooserButton;
		private CheckButton check;
		public SelectJavaBox() {
			this.set_orientation(Orientation.HORIZONTAL);
			this.check = new CheckButton.with_label("Find Java 16 automatically");
			this.check.set_active(true);
			this.pack_start(this.check);
			this.fileChooserButton = new FileChooserButton("Select Java 16 installation",
			  FileChooserAction.SELECT_FOLDER);
			this.check.toggled.connect(() => {
				if(this.check.get_active()) {
					this.check.set_label("Find Java 16 automatically");
					this.fileChooserButton.hide();
				} else {
					this.check.set_label("Use local Java 16 installation: ");
					this.show_all();
				}
			});
			this.pack_start(this.fileChooserButton);
		}
		public void change() {
			this.fileChooserButton.hide();
		}
		public string? getFolder() {
			if(check.get_active()) {
				return null;
			}
			var ret = this.fileChooserButton.get_filename();
			if(ret != null) {
				if(ret.has_suffix("bin") || ret.has_suffix("bin\\") || ret.has_suffix("bin/")) {
					ret += "/../";
				}
			}
			return ret;
		}
		public void configure(string s) {
			check.set_active(false);
			this.check.set_label("Use local Java 16 installation: ");
			this.fileChooserButton.set_filename(s);
			this.show_all();
		}
	}
	class TreeViewWithPopup : TreeView {
		public void init(Gtk.ListStore store) {
			this.get_selection().set_mode(SelectionMode.BROWSE);
			this.set_model(store);
			var menu = new Gtk.Menu();
			var item = new Gtk.MenuItem.with_label("Remove");
			this.hover_selection = true;
			item.activate.connect(() => {
				var selected = get_selection();
				TreeModel model;
				TreeIter iter;
				selected.get_selected(out model, out iter);
				((Gtk.ListStore)model).remove(ref iter);
			});
			menu.append(item);
			menu.show_all();
			button_press_event.connect(event => {
				if(event.type == Gdk.EventType.BUTTON_PRESS && event.button == 3 &&
				store.iter_n_children(null) > 0) {
					menu.popup_at_pointer(event);
				}
				return true;
			});
		}
	}
	class DownloadProgress {
		public double dltotal {get; set;}
		public double dlnow {get; set;}

		public DownloadProgress(double total, double now) {
			this.dltotal = total;
			this.dlnow = now;
		}
		public double getPercentage() {
			return (dlnow / dltotal) * 100;
		}
	}
	class ExtractProgress {
		public int current {get; set;}
		public int numberOfFiles {get; set;}
		public string filename {get; set;}

		public ExtractProgress(string name, int current, int max) {
			this.filename = name;
			this.current = current;
			this.numberOfFiles = max;
		}
	}
	class JVMOptionsCompletion : EntryCompletion {
		public JVMOptionsCompletion() {
			this.set_text_column(0);
			var store = new Gtk.ListStore(1, GLib.Type.STRING);
			TreeIter tp;
			store.insert_with_values(out tp, -1, 0, "-verbose:class", -1);
			store.insert_with_values(out tp, -1, 0, "-verbose:module", -1);
			store.insert_with_values(out tp, -1, 0, "-verbose:gc", -1);
			store.insert_with_values(out tp, -1, 0, "-verbose:jni", -1);
			store.insert_with_values(out tp, -1, 0, "-showversion", -1);
			store.insert_with_values(out tp, -1, 0, "--show-version", -1);
			store.insert_with_values(out tp, -1, 0, "-ea", -1);
			store.insert_with_values(out tp, -1, 0, "-esa", -1);
			store.insert_with_values(out tp, -1, 0, "-enablesystemassertions", -1);
			store.insert_with_values(out tp, -1, 0, "-da", -1);
			store.insert_with_values(out tp, -1, 0, "-dsa", -1);
			store.insert_with_values(out tp, -1, 0, "-disablesystemassertions", -1);
			store.insert_with_values(out tp, -1, 0, "-Xbatch", -1);
			store.insert_with_values(out tp, -1, 0, "-Xcheck:jni", -1);
			store.insert_with_values(out tp, -1, 0, "-Xcomp", -1);
			store.insert_with_values(out tp, -1, 0, "-Xdiag", -1);
			store.insert_with_values(out tp, -1, 0, "-Xint", -1);
			store.insert_with_values(out tp, -1, 0, "-Xrs", -1);
			store.insert_with_values(out tp, -1, 0, "-Xshare:auto", -1);
			store.insert_with_values(out tp, -1, 0, "-Xshare:off", -1);
			store.insert_with_values(out tp, -1, 0, "-XshowSettings", -1);
			store.insert_with_values(out tp, -1, 0, "-XshowSettings:all", -1);
			store.insert_with_values(out tp, -1, 0, "-XshowSettings:locale", -1);
			store.insert_with_values(out tp, -1, 0, "-XshowSettings:properties", -1);
			store.insert_with_values(out tp, -1, 0, "-XshowSettings:vm", -1);
			store.insert_with_values(out tp, -1, 0, "--illegal-access=deny", -1);
			store.insert_with_values(out tp, -1, 0, "--illegal-access=warn", -1);
			store.insert_with_values(out tp, -1, 0, "--illegal-access=permit", -1);
			store.insert_with_values(out tp, -1, 0, "--illegal-access=debug", -1);
			this.set_model(store);
		}
	}
	class ClasspathCompletion : EntryCompletion {
		public ClasspathCompletion() {
			this.set_text_column(0);
			var store = new Gtk.ListStore(1, GLib.Type.STRING);
			TreeIter tp;
			var classpath = Environment.get_variable("CLASSPATH");
			if(classpath != null) {
				string[] paths = classpath.split(getSeparator());
				var pathSet = new Gee.HashSet<string>();
				foreach (var str in paths) {
					pathSet.add(str);
				}
				foreach(var path in pathSet) {
					store.insert_with_values(out tp, -1, 0, path, -1);
					if(!path.has_suffix(".jar")) {
						Posix.Dir dir = opendir(path);
						if(dir != null) {
							unowned DirEnt entry;
							while((entry = readdir(dir)) != null) {
								var name = (string) entry.d_name;
								if(name != "." && name != ".." && name.has_suffix(
									 ".jar")) {
									//Avoid paths like /foo/bar//foo.jar
									var sep = path.has_suffix(getPathSeparator())?
									 "" : getPathSeparator();
									store.insert_with_values(out tp, -1, 0, path +
									 sep + name, -1);
								}
							}
						}
					}
				}
			}
			this.set_model(store);
		}
	}

	class MemorySettings : Expander {
		InputSpinner xss;
		InputSpinner xmn;
		InputSpinner xms;
		InputSpinner xmx;
		public MemorySettings() {
			this.set_label("Advanced");
			var box = new Gtk.Box(Gtk.Orientation.VERTICAL, 0);
			var label = new Gtk.Label("");
			label.set_markup(
				"<b><i><span background=\'#ff0000\' foreground=\'#ffffff\'>Only change anything here, if you know what you do!\nA wrong setting may crash Conquer!</span></i></b>");
			this.xss = new InputSpinner("-Xss", "Thread stack size (-Xss): ");
			this.xmn = new InputSpinner("-Xmn", "Initial and maximum size of the heap (-Xmn): ");
			this.xms = new InputSpinner("-Xms", "Initial and minimum size of the heap (-Xms): ");
			this.xmx = new InputSpinner("-Xmx", "Maximum size of the heap (-Xmx): ");
			box.pack_start(label);
			box.pack_start(this.xss);
			box.pack_start(this.xmn);
			box.pack_start(this.xms);
			box.pack_start(this.xmx);
			this.add(box);
		}

		public void configure(Configuration config) {
			if(config.xss != null) {
				this.xss.init(config.xss);
			}
			if(config.xmn != null) {
				this.xmn.init(config.xmn);
			}
			if(config.xms != null) {
				this.xms.init(config.xms);
			}
			if(config.xmx != null) {
				this.xmx.init(config.xmx);
			}
		}
		public Gee.List<string> getOptions() {
			var ret = new Gee.ArrayList<string>();
			if(xss.getOptionText() != null) {
				ret.add(xss.getOptionText());
			}
			if(xmn.getOptionText() != null) {
				ret.add(xmn.getOptionText());
			}
			if(xms.getOptionText() != null) {
				ret.add(xms.getOptionText());
			}
			if(xmx.getOptionText() != null) {
				ret.add(xmx.getOptionText());
			}
			return ret;
		}

		public Gee.Map<string, string> toMap() {
			var ret = new Gee.HashMap<string, string>();
			if(xss.getOptionText() != null) {
				ret.@set("xss", xss.getOptionText());
			}
			if(xmn.getOptionText() != null) {
				ret.@set("xmn", xmn.getOptionText());
			}
			if(xms.getOptionText() != null) {
				ret.@set("xms", xms.getOptionText());
			}
			if(xmx.getOptionText() != null) {
				ret.@set("xmx", xmx.getOptionText());
			}
			return ret;
		}
	}
	class InputSpinner : Box {
		ComboBox box;
		string internalOption;
		string displayOption;
		Entry text;
		public InputSpinner(string internalOption, string displayOption) {
			this.set_orientation(Orientation.HORIZONTAL);
			this.text = new Entry();
			this.internalOption = internalOption;
			this.displayOption = displayOption;
			var store = new Gtk.ListStore(1, GLib.Type.STRING);
			TreeIter tp;
			store.insert_with_values(out tp, -1, 0, "KB", -1);
			store.insert_with_values(out tp, -1, 0, "MB", -1);
			store.insert_with_values(out tp, -1, 0, "GB", -1);
			this.box = new ComboBox.with_model(store);
			this.box.id_column = 0;
			var renderer = new CellRendererText();
			this.box.pack_start(renderer, true);
			this.box.add_attribute(renderer, "text", 0);
			this.pack_start(new Label(displayOption));
			this.pack_start(this.text);
			this.pack_start(this.box);
		}

		public void init(string config) {
			string str = config.replace(this.internalOption, "");
			char unit = str.@get(str.length - 1);
			switch(unit) {
			case 'K':
				this.box.active = 0;
				this.box.active_id = "KB";
				break;
			case 'M':
				this.box.active = 0;
				this.box.active_id = "MB";
				break;
			case 'G':
				this.box.active = 0;
				this.box.active_id = "GB";
				break;
			}
			this.text.text = str.substring(0, str.length - 1);
		}
		public string? getOptionText() {
			if(this.box.active_id == null) {
				return null;
			}
			return this.text.text.strip() == "" ? null : this.internalOption + this.text.text.strip() +
			       this.box.active_id.substring(0, 1);
		}
	}
}
