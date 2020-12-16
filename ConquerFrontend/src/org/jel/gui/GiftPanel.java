package org.jel.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jel.game.data.Clan;
import org.jel.game.data.Game;
import org.jel.game.data.Gift;
import org.jel.game.data.Resource;
import org.jel.game.data.Shared;

/**
 * Enables the player to give gifts to other clans. It consists of several
 * components: <br>
 * For each resource available there is an {@code ResourceSlider}. Furthermore
 * there is one {@code MoneySlider}.<br>
 * The last two components are a {@code JComboBox} with all clans available and
 * a button to send the gift.
 */
final class GiftPanel extends JPanel {

	private static final long serialVersionUID = -2927785362578307419L;
	private final transient Game game;
	private final List<ResourceSlider> sliders;
	private JComboBox<String> box;

	/**
	 * Creates a new GiftPanel with a specified game as data source
	 *
	 * @param game Data source
	 */
	GiftPanel(final Game game) {
		this.game = game;
		this.sliders = new ArrayList<>();
	}

	/**
	 * Initialises the panel.
	 */
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
		this.box = new JComboBox<>(
				this.game.getClans().stream().filter(a -> !a.isPlayerClan() && !this.game.isDead(a))
						.map(Clan::getName).collect(Collectors.toList()).toArray(new String[0]));
		final var button = new JButton(Messages.getString("GiftPanel.giveGift")); //$NON-NLS-1$
		button.addActionListener(a -> {
			final var gift = new Gift(this.sliders.stream().map(ResourceSlider::getValue).collect(Collectors.toList()),
					ms.getMoney());
			final var clan = this.game.getClans().stream().filter(b -> b.getName().equals(this.box.getSelectedItem()))
					.findFirst().orElseThrow();
			if (!this.game.sendGift(this.game.getClan(0), clan, gift)) {
				final var string = Messages.getMessage("GiftPanel.rejected", this.box.getSelectedItem()); //$NON-NLS-1$
				JOptionPane.showMessageDialog(null, string, Messages.getString("GiftPanel.gift"), //$NON-NLS-1$
						JOptionPane.PLAIN_MESSAGE);
			} else {
				final var string = Messages.getMessage("GiftPanel.accepted", this.box.getSelectedItem()); //$NON-NLS-1$
				JOptionPane.showMessageDialog(null, string, Messages.getString("GiftPanel.gift"), //$NON-NLS-1$
						JOptionPane.PLAIN_MESSAGE);
			}
		});
		final var p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(this.box);
		p.add(button);
		this.box.setIgnoreRepaint(true);
		new ExtendedTimer(17, e -> {
			if (!this.box.isPopupVisible()) {// Else the popup will close all the time
				final var selectedIndex = this.box.getSelectedIndex();
				final var selectedObject = this.box.getSelectedItem();
				final var list = GiftPanel.this.game.getClans().stream()
						.filter(a -> (a.getId() != 0) && !GiftPanel.this.game.isDead(a)).map(Clan::getName)
						.collect(Collectors.toList());
				final var model = new DefaultComboBoxModel<>(list.toArray(new String[0]));
				GiftPanel.this.box.setModel(model);
				try {
					if (list.get(selectedIndex).equals(selectedObject)) {
						this.box.setSelectedIndex(selectedIndex);
					} else {
						final var index = list.indexOf(selectedObject);
						this.box.setSelectedIndex(index == -1 ? 0 : index);
					}
				} catch (final IndexOutOfBoundsException ioobe) {
					Shared.LOGGER.exception(ioobe);
				}
			}

		}).start();
		this.add(p);
	}

}
