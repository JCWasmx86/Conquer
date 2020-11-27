package org.jel.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jel.game.data.Game;

final class RelationshipPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -7720369284705527955L;
	private final Game game;
	private final List<JLabel> labels;
	private Timer timer;

	RelationshipPanel(Game game) {
		this.game = game;
		this.labels = new ArrayList<>();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final var clans = this.game.getClans();
		for (var i = 1; i < clans.size(); i++) {
			final var clan = clans.get(i);
			final var label = this.labels.get(i - 1);
			label.setText(
					clan.getName() + ": " + String.format("%.2f", this.game.getRelations().getWeight(0, clan.getId())));
			this.repaint();
		}
	}

	void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		final var clans = this.game.getClans();
		// i=1 because i=0 is the clan of the player
		for (var i = 1; i < clans.size(); i++) {
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
		this.timer = new Timer(17, this);
		this.timer.start();
	}
}
