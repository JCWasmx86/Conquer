using Gtk;
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
			box.pack_start(this.classpaths);
			var startButtonPanel = new StartButton(this.classpaths, this.jvmOptions);
			box.pack_start(startButtonPanel);
			window.add(box);
			window.set_title("Conquer launcher 2.0.0");
			window.show_all();
		}
	}

	public int main(string[] args) {
		return new ConquerLauncher().run(args);
	}

	class StartButton : Box {
		private ProgressBar progressBar;
		private DownloadProgress progress;
		public StartButton(InputList classpaths, InputList jvmOptions) {
			this.set_orientation(Orientation.VERTICAL);
			var button = new Button.with_label("Start");
			this.pack_start(button, false, false);
			button.clicked.connect(() => {
				this.remove(button);
				button.destroy();
				if(hasToDownloadJava() != null) {
					this.progressBar = new ProgressBar();
					this.progressBar.set_show_text(true);
					this.pack_start(this.progressBar,false);
					downloadJDK(downloadStatsReceiver);
				}
				this.progress = null;
			});
		}
		bool updateProgressBar() {
			return false;
		}
		void downloadStatsReceiver(void* ptr,uint64 dltotal,uint64 dlnow,uint64 ultotal,uint64 ulnow) {
			this.progress = new DownloadProgress(dlnow,dltotal);
			Gdk.threads_add_idle(updateProgressBar);
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
			column.set_title(label);
			var renderer = new CellRendererText();
			column.pack_start(renderer,true);
			column.add_attribute(renderer,"text", 0);
			this.treeView.append_column(column);
			this.treeView.set_model(this.listStore);
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
				TreeIter tp;
				store.insert_with_values(out tp, -1, 0, text.strip(), -1);
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
		uint64 dltotal {get; set;}
		uint64 dlnow {get; set;}
		public DownloadProgress(uint64 total,uint64 now) {
			this.dltotal = total;
			this.dlnow = now;
		}
	}
}
