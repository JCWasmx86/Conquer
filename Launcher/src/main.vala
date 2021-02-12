using Gtk;
using Gee;
namespace Launcher {
	class ConquerLauncher : Gtk.Application {
		private InputList jvmOptions;
		private InputList classpaths;

		protected override void activate() {
			var window = new ApplicationWindow(this);
			var box = new Box(Orientation.VERTICAL, 2);
			this.jvmOptions=new InputList("JVM Arguments","Add JVM argument");
			box.pack_start(this.jvmOptions);
			this.classpaths = new InputList("Classpaths", "Add classpath");
			initConfig();
			box.pack_start(this.classpaths);
			var startButtonPanel = new StartButton(this.classpaths, this.jvmOptions,window);
			box.pack_start(startButtonPanel);
			window.add(box);
			window.set_title("Conquer launcher 2.0.0");
			window.show_all();
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
		}
	}

	public int main(string[] args) {
		return new ConquerLauncher().run(args);
	}

	class StartButton : Box,IDownloadProgress {
		private ProgressBar progressBar;
		private ExtractProgress extractProgress;
		private AsyncQueue<DownloadProgress> asyncQueue;
		public StartButton(InputList classpaths, InputList jvmOptions, ApplicationWindow window) {
			this.asyncQueue = new AsyncQueue<DownloadProgress>();
			this.set_orientation(Orientation.VERTICAL);
			var button = new Button.with_label("Start");
			this.pack_start(button, false, false);
			button.clicked.connect(() => {
				this.remove(button);
				button.destroy();
				this.progressBar = new ProgressBar();
				this.progressBar.set_show_text(true);
				this.pack_start(this.progressBar,false);
				window.show_all();
				if(hasToDownloadJava() != null) {
					new Thread<void> ("thread_a", ()=>{
						downloadJDK(this);
						extractJDK(extractReceiver);
						tryUpdating();
						window.hide();
						new Thread<void>("thread_b", () => {
							JVM jvm = new JVM(showErrorScreen);
							jvm.addJVMArguments(jvmOptions.toList());
							jvm.addClasspaths(classpaths.toList());
							Configuration.dump(jvmOptions.toList(),classpaths.toList());
							jvm.run();
							Process.exit(0);
						});
					});
				}else {
					window.hide();
					new Thread<void>("jvm", () => {
						tryUpdating();
						JVM jvm = new JVM(showErrorScreen);
						jvm.addJVMArguments(jvmOptions.toList());
						jvm.addClasspaths(classpaths.toList());
						Configuration.dump(jvmOptions.toList(),classpaths.toList());
						jvm.run();
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
			this.progressBar.set_text("Downloaded %.0lf of %.0lf bytes (%.2lf %%)".printf(data.dlnow,data.dltotal,data.getPercentage()));
			this.progressBar.set_fraction(data.getPercentage()/100);
			return false;
		}
		public void onProgress(double dltotal,double dlnow,double ultotal,double ulnow) {
			var progress = new DownloadProgress(dltotal,dlnow);
			asyncQueue.push(progress);
			Gdk.threads_add_idle(updateDownloadProgressBar);
		}
		bool updateExtractProgressbar() {
			this.progressBar.set_text("Extracting %s (%d/%d)".printf(this.extractProgress.filename,this.extractProgress.current,this.extractProgress.numberOfFiles));
			return false;
		}
		void extractReceiver(string name, int current, int max) {
			this.extractProgress = new ExtractProgress(name,current,max);
			Gdk.threads_add_idle(updateExtractProgressbar);
		}
	}
	class InputList : Box {
		private Gtk.ListStore listStore;
		private TreeViewWithPopup treeView;

		public InputList(string name,string label) {
			this.set_orientation(Orientation.VERTICAL);
			this.listStore = new Gtk.ListStore(1,GLib.Type.STRING);
			this.treeView = new TreeViewWithPopup();
			this.treeView.init(this.listStore);
			this.treeView.set_model(this.listStore);
			this.pack_start(this.treeView);
			this.pack_start(new InputBox(this,label, this.listStore),false,false);
			var column = new TreeViewColumn();
			column.set_title(name);
			var renderer = new CellRendererText();
			column.pack_start(renderer,true);
			column.add_attribute(renderer,"text", 0);
			this.treeView.append_column(column);
			this.treeView.set_model(this.listStore);
		}

		public Gee.List<string> toList() {
			var ret = new ArrayList<string>();
			TreeIter iter;
			if(this.listStore.get_iter_first(out iter)) {
				do {
					Value s;
					this.listStore.get_value(iter,0, out s);
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
		public InputBox(InputList list, string buttonLabel, Gtk.ListStore store) {
			this.set_orientation(Orientation.HORIZONTAL);
			var entry = new Entry();
			this.pack_start(entry);
			var button = new Button.with_label(buttonLabel);
			this.pack_start(button,false,false);
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
				selected.get_selected(out model,out iter);
				((Gtk.ListStore)model).remove(ref iter);
			});
			menu.append(item);
			menu.show_all();
			button_press_event.connect((event) => {
				if(event.type == Gdk.EventType.BUTTON_PRESS
						&& event.button == 3
						&& store.iter_n_children(null) > 0) {
					menu.popup_at_pointer(event);
				}
				return true;
			});
		}
	}
	class DownloadProgress {
		public double dltotal {get; set;}
		public double dlnow {get; set;}
		public DownloadProgress(double total,double now) {
			this.dltotal = total;
			this.dlnow = now;
		}
		public double getPercentage() {
			return (dlnow/dltotal)*100;
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
}
