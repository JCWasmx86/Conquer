package org.jel.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jel.game.data.Game;
import org.jel.game.data.Gift;
import org.jel.game.data.Resource;

final class GiftPanel extends JPanel {

	private static final long serialVersionUID = -2927785362578307419L;
	private final Game game;
	private final List<ResourceSlider> sliders;
	private JButton button;
	private JComboBox<String> box;

	GiftPanel(Game game) {
		this.game = game;
		this.sliders = new ArrayList<>();
	}

	void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		for (final var r : Resource.values()) {
			final var rs = new ResourceSlider(r, this.game);
			rs.init();
			this.add(rs);
			this.sliders.add(rs);
		}
		final var ms = new MoneySlider(this.game);
		ms.init();
		this.add(ms);
		final var strings = new String[this.game.getClans().size() - 1];
		for (var i = 1; i < this.game.getClans().size(); i++) {
			strings[i - 1] = this.game.getClanNames().get(i);
		}
		this.box = new JComboBox<>(strings);
		this.button = new JButton("Give gift");
		this.button.addActionListener(a -> {
			final var gift = new Gift(this.sliders.stream().map(ResourceSlider::getValue).collect(Collectors.toList()),
					ms.getMoney());
			final var clan = this.game.getClans().stream().filter(b -> b.getName().equals(this.box.getSelectedItem()))
					.findFirst().orElseThrow();
			if (!this.game.sendGift(this.game.getClan(0), clan, gift)) {
				JOptionPane.showMessageDialog(null, this.box.getSelectedItem() + " rejected your gift", "Gift",
						JOptionPane.PLAIN_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, this.box.getSelectedItem() + " accepted your gift", "Gift",
						JOptionPane.PLAIN_MESSAGE);
			}
		});
		final var p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(this.box);
		p.add(this.button);
		this.add(p);
	}

}
