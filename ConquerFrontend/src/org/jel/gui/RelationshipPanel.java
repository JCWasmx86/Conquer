package org.jel.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jel.game.data.Game;
import org.jel.game.data.Shared;

/**
 * One of the panels in the JTabbedPane of {@link GameFrame}. It allows to see
 * the relationship to other clans and to improve it by sending gifts.
 */
final class RelationshipPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -7720369284705527955L;
	private final transient Game game;
	private final List<JLabel> labels;

	/**
	 * Construct a new RelationshipPanel
	 *
	 * @param game The source of the data.
	 */
	RelationshipPanel(final Game game) {
		this.game = game;
		this.labels = new ArrayList<>();
	}

	/**
	 * Updates the information about the relationship to other clans.
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		final var clans = this.game.getClans();
		for (var i = 1; i < clans.size(); i++) {
			final var clan = clans.get(i);
			final var label = this.labels.get(i - 1);
			label.setText(clan.getName() + ": " + String.format("%.2f",
					this.game.getRelations().getWeight((int) Shared.PLAYER_CLAN, clan.getId())));
			this.repaint();
		}
	}

	/**
	 * Initialises this component.
	 */
	void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		final var clans = this.game.getClans();
		for (var i = Shared.PLAYER_CLAN + 1; i < clans.size(); i++) {
			final var clan = clans.get(i);
			final var text = clan.getName();
			final var clanLabel = new JLabel(text);
			clanLabel.setOpaque(true);
			clanLabel.setForeground(clan.getColor());
			clanLabel.setFont(clanLabel.getFont().deriveFont(22.0f));
			this.add(clanLabel);
			this.labels.add(clanLabel);
		}
		final var giftPanel = new GiftPanel(this.game);
		giftPanel.init();
		this.add(giftPanel);
		final var timer = new ExtendedTimer(17, this);
		timer.start();
	}
}
