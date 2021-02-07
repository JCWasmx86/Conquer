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
	private TreeView treeView;

	public InputList(string name,string label) {
		this.set_orientation(Orientation.VERTICAL);
		this.pack_start(new Label(name));
		this.listStore = new Gtk.ListStore(1,typeof(ListElement));
		this.treeView = new TreeView.with_model(this.listStore);
		this.pack_start(this.treeView);
		this.pack_start(new InputBox(this,label,this.listStore));
		var column = new TreeViewColumn();
		column.set_title(label);
		this.treeView.append_column(column);
		this.treeView.set_model(this.listStore);
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
			entry.text = "";
			TreeIter tp;
			store.append(out tp);
			store.set(tp, 0, new ListElement(text), -1);
			this.show_all();
			list.show_all();
		});
	}
}
class ListElement : Label{
	private Gtk.Menu menu;
	private string value;

	public ListElement(string value) {
		this.value = value;
		stdout.printf("%s\n",value);
		this.set_label(value);
		this.menu = new Gtk.Menu();
		var menuItem = new Gtk.MenuItem.with_label("Remove");
		this.menu.append(menuItem);
		this.menu.show();
		this.button_press_event.connect(() => {
			return true;
		});
		stdout.printf("F:%s\n",this.get_text());
		this.set_label(value);
		this.set_text(value);
		this.show_all();
	}
}
