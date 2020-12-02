package org.jel.gui;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;

import org.jel.game.data.Game;
import org.jel.game.data.Shared;

final class MoneySlider extends JPanel {
	private static final long serialVersionUID = 794966448509855336L;
	private final Game game;
	private JSlider slider;
	private JTextField textfield;

	MoneySlider(Game game) {
		this.game = game;
	}

	double getMoney() {
		return (0.01 * this.slider.getValue()) * this.game.getClan(Shared.PLAYER_CLAN).getCoins();
	}

	private String getText() {
		return "Coins: " + String.format("%.2f", (0.01 * this.slider.getValue()) * this.game.getClan(0).getCoins());
	}

	void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.slider = new JSlider(0, 100);
		this.textfield = new JTextField(this.getText());
		this.textfield.setEditable(false);
		this.slider.addChangeListener(a -> this.textfield.setText(this.getText()));
		this.add(this.slider);
		this.add(this.textfield);
		new Timer(17, a -> this.textfield.setText(this.getText())).start();
	}
}
