using Gtk;


class ConquerLauncher : Gtk.Application {
	protected override void activate() {
		var window = new ApplicationWindow(this);
		var box = new Box(Orientation.VERTICAL, 10);
		box.pack_start(new InputList("JVM Arguments","Add JVM argument"));
		box.pack_start(new InputList("Classpaths","Add classpath"));
		window.add(box);
		window.set_title("Conquer launcher 2.0.0");
		window.show_all();
	}
}

public int main(string[] args) {
	return new ConquerLauncher().run(args);
}

class InputList : Box {
	private Gtk.ListStore listStore;
	private TreeViewWithPopup treeView;

	public InputList(string name,string label) {
		this.set_orientation(Orientation.VERTICAL);
		this.pack_start(new Label(name));
		this.listStore = new Gtk.ListStore(1,GLib.Type.STRING);
		this.treeView = new TreeViewWithPopup();
		this.treeView.init();
		this.treeView.set_model(this.listStore);
		this.pack_start(this.treeView);
		this.pack_start(new InputBox(this,label, this.listStore));
		var column = new TreeViewColumn();
		column.set_title(label);
		var renderer = new CellRendererText();
		column.pack_start(renderer,true);
		column.add_attribute(renderer,"text", 0);
		this.treeView.append_column(column);
		this.treeView.set_model(this.listStore);
		this.treeView.button_press_event.connect ((event) => {	
			return true;
		});
	}
}
class InputBox : Box {
	private Entry entry;
	private Button button;
	public InputBox(InputList list, string buttonLabel, Gtk.ListStore store) {
		this.set_orientation(Orientation.HORIZONTAL);
		this.entry = new Entry();
		this.pack_start(this.entry);
		this.button = new Button.with_label(buttonLabel);
		this.pack_start(this.button);
		button.clicked.connect(() => {
			var text = this.entry.text;
			if(text.length == 0) {
				return;
			}
			TreeIter tp;
			store.insert_with_values(out tp, -1, 0, text, -1);
			entry.text = "";
		});
	}
}

class TreeViewWithPopup : TreeView {
	private Gtk.Menu menu;
	public void init() {
		this.menu = new Gtk.Menu();
		var item = new Gtk.MenuItem.with_label("Remove");
		item.activate.connect(()=>{
			var selected = get_selection();
			if(selected != null) {
				TreeModel model;
				TreeIter iter;
				selected.get_selected(out model,out iter);
				Gtk.ListStore listStore = (Gtk.ListStore)model;
				listStore.remove(ref iter);
			}
		});
		this.menu.append(item);
		this.menu.show_all();
		this.button_press_event.connect((event)=>{
			if(event.type == Gdk.EventType.BUTTON_PRESS && event.button == 3) {
				this.menu.popup_at_pointer(event);
			}
			return true;
		});
	}
}
