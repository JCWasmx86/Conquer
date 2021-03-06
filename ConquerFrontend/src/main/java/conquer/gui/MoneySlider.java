package conquer.gui;

import java.io.Serial;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import conquer.data.ConquerInfo;

/**
 * Shows a slider that determines the amount of coins to be send as a gift
 */
final class MoneySlider extends JPanel {
	@Serial
	private static final long serialVersionUID = 794966448509855336L;
	private final transient ConquerInfo game;
	private JSlider slider;

	/**
	 * Construct a new slider
	 *
	 * @param game The source of the data.
	 */
	MoneySlider(final ConquerInfo game) {
		this.game = game;
	}

	/**
	 * Returns the amount of coins to be gifted.
	 *
	 * @return Amount of coins
	 */
	double getMoney() {
		return (0.01 * this.slider.getValue()) * this.game.getPlayerClan().getCoins();
	}

	private String getText() {
		return Messages.getString("Shared.coins") + ": "
			+ String.format("%.2f", (0.01 * this.slider.getValue()) * this.game.getPlayerClan().getCoins());

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
