package conquer.gui;

import conquer.data.ConquerInfo;
import conquer.data.Resource;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

/**
 * Shows a slider that determines the amount of a resource to be send as a gift
 */
final class ResourceSlider extends JPanel {
	private static final long serialVersionUID = 595825972773535456L;
	private final Resource resource;
	private final transient ConquerInfo game;
	private JSlider slider;

	/**
	 * Creates a new slider for a specified resource.
	 *
	 * @param r    The resource
	 * @param game A reference to a game.
	 */
	ResourceSlider(final Resource r, final ConquerInfo game) {
		this.resource = r;
		this.game = game;
	}

	private String getText() {
		return this.resource.getName() + ": " + String.format("%.2f", (0.01 * this.slider.getValue())
				* this.game.getPlayerClan().getResources().get(this.resource.getIndex()));
	}

	/**
	 * Returns the amount of this resource to be gifted.
	 *
	 * @return Amount of the specified resource.
	 */
	double getValue() {
		return (0.01 * this.slider.getValue()) * this.game.getPlayerClan().getResources().get(this.resource.getIndex());
	}

	/**
	 * Initialises this component.
	 */
	void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.slider = new JSlider(0, 100);
		final var textfield = new JTextField(this.getText());
		textfield.setEditable(false);
		this.slider.addChangeListener(a -> textfield.setText(this.getText()));
		this.add(this.slider);
		this.add(textfield);
		new ExtendedTimer(Utils.getRefreshRate(), a -> textfield.setText(this.getText())).start();
	}
}
