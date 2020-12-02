package org.jel.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jel.game.data.Clan;
import org.jel.game.data.Game;
import org.jel.game.data.Shared;
import org.jel.gui.utils.ImageResource;

final class UpgradeSoldiersPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -7456324799677381608L;
	private final Clan clan;
	private final Game game;
	private final Timer timer;
	private final JLabel infoLabel;
	private final JButton upgradeOnce;
	private final JButton upgradeMax;

	UpgradeSoldiersPanel(Clan clan, Game game) {
		this.clan = clan;
		this.game = game;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.infoLabel = new JLabel();
		this.infoLabel.setText(this.getInfoText());
		this.add(this.infoLabel);
		this.upgradeOnce = new JButton("");
		this.initUpgradeOnce();
		this.add(this.upgradeOnce);
		this.upgradeMax = new JButton(new ImageResource("max.png"));
		this.initUpgradeMax();
		this.add(this.upgradeMax);
		this.timer = new ExtendedTimer(17, this);
		this.timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.infoLabel.setText(this.getInfoText());
		this.initUpgradeOnce();
		this.initUpgradeMax();
	}

	private String getInfoText() {
		return "Soldiers power: " + String.format("%.2f", this.clan.getSoldiersStrength()) + " Level "
				+ this.clan.getSoldiersLevel();
	}

	private String getOneLevelString() {
		return "Upgrade to level " + (this.clan.getSoldiersLevel() + 1) + ": "
				+ String.format("%.2f", Shared.upgradeCostsForSoldiers(this.clan.getSoldiersLevel() + 1)) + "coins";
	}

	private void initUpgradeMax() {
		if (this.clan.getSoldiersLevel() == 1000) {
			this.upgradeMax.setEnabled(false);
			this.upgradeMax.setText("Maximum value reached!");
			return;
		}
		final var count = Shared.maxLevelsAddSoldiersUpgrade(this.clan.getSoldiersLevel(), this.clan.getCoins());
		this.upgradeMax.setAction(new AbstractAction() {
			private static final long serialVersionUID = 8078867867027862129L;

			@Override
			public void actionPerformed(ActionEvent e) {
				UpgradeSoldiersPanel.this.game.upgradeSoldiersFully((byte) UpgradeSoldiersPanel.this.clan.getId());
			}
		});
		this.upgradeMax.setEnabled(count > 1);
		if (count > 1) {
			this.upgradeMax.setText("Upgrade to level " + ((count + this.clan.getSoldiersLevel()) - 1));
		} else {
			this.upgradeMax.setText("Not enough coins!");
		}
	}

	private void initUpgradeOnce() {
		if (this.clan.getSoldiersLevel() == 1000) {
			this.upgradeOnce.setEnabled(false);
			this.upgradeOnce.setText("Maximum value reached!");
			return;
		}
		final var coins = this.clan.getCoins();
		final var costs = Shared.upgradeCostsForSoldiers(this.clan.getSoldiersLevel() + 1);
		this.upgradeOnce.setAction(new AbstractAction() {
			private static final long serialVersionUID = 8078867867027862129L;

			@Override
			public void actionPerformed(ActionEvent e) {
				UpgradeSoldiersPanel.this.game.upgradeSoldiers((byte) UpgradeSoldiersPanel.this.clan.getId());
			}
		});
		this.upgradeOnce.setEnabled(costs < coins);
		this.upgradeOnce.setText(this.getOneLevelString());
	}
}
