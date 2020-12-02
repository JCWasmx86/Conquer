package org.jel.gui;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import org.jel.game.data.Game;
import org.jel.game.data.Resource;
import org.jel.game.data.Shared;

final class ResourceSlider extends JPanel {
	private static final long serialVersionUID = 595825972773535456L;
	private final Resource resource;
	private final Game game;
	private JSlider slider;
	private JTextField textfield;

	ResourceSlider(Resource r, Game game) {
		this.resource = r;
		this.game = game;
	}

	private String getText() {
		return this.resource.getName() + ": " + String.format("%.2f", (0.01 * this.slider.getValue())
				* this.game.getClan(Shared.PLAYER_CLAN).getResources().get(this.resource.getIndex()));
	}

	double getValue() {
		return (0.01 * this.slider.getValue())
				* this.game.getClan(Shared.PLAYER_CLAN).getResources().get(this.resource.getIndex());
	}

	void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.slider = new JSlider(0, 100);
		this.textfield = new JTextField(this.getText());
		this.textfield.setEditable(false);
		this.slider.addChangeListener(a -> this.textfield.setText(this.getText()));
		this.add(this.slider);
		this.add(this.textfield);
		new ExtendedTimer(17, a -> this.textfield.setText(this.getText())).start();
	}
}
