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

final class UpgradeSoldiersDefense extends JPanel implements ActionListener {
	private static final long serialVersionUID = -681006129127926269L;
	private final Clan clan;
	private final Game game;
	private final JLabel infoLabel;
	private final JButton upgradeOnce;
	private final JButton upgradeMax;
	private final Timer timer;

	UpgradeSoldiersDefense(Clan clan, Game game) {
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
		this.repaint();
		this.timer = new Timer(17, this);
		this.timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.infoLabel.setText(this.getInfoText());
		this.initUpgradeOnce();
		this.initUpgradeMax();
	}

	private String getInfoText() {
		return "Soldiers defense power: " + String.format("%.2f", this.clan.getSoldiersDefenseStrength()) + " Level "
				+ this.clan.getSoldiersDefenseLevel();
	}

	private String getOneLevelString() {
		return "Upgrade to level " + (this.clan.getSoldiersDefenseLevel() + 1) + ": " + String.format("%.2f",
				Shared.upgradeCostsForOffenseAndDefense(this.clan.getSoldiersDefenseLevel() + 1)) + "coins";
	}

	private void initUpgradeMax() {
		if (this.clan.getSoldiersDefenseLevel() == 1000) {
			this.upgradeMax.setEnabled(false);
			this.upgradeMax.setText("Maximum value reached!");
			return;
		}
		final var count = Shared.maxLevelsAddOffenseDefenseUpgrade(this.clan.getSoldiersDefenseLevel(),
				this.clan.getCoins());
		this.upgradeMax.setAction(new AbstractAction() {
			private static final long serialVersionUID = -8569686717119904143L;

			@Override
			public void actionPerformed(ActionEvent e) {
				UpgradeSoldiersDefense.this.game
						.upgradeSoldiersDefenseFully((byte) UpgradeSoldiersDefense.this.clan.getId());
			}
		});
		this.upgradeMax.setEnabled(count > 1);
		if (count > 1) {
			this.upgradeMax.setText("Upgrade to level " + ((this.clan.getSoldiersDefenseLevel() + count) - 1));
		} else {
			this.upgradeMax.setText("Not enough coins!");
		}
	}

	private void initUpgradeOnce() {
		if (this.clan.getSoldiersDefenseLevel() == 1000) {
			this.upgradeOnce.setEnabled(false);
			this.upgradeOnce.setText("Maximum value reached!");
			return;
		}
		final var coins = this.clan.getCoins();
		final var costs = Shared.upgradeCostsForOffenseAndDefense(this.clan.getSoldiersDefenseLevel() + 1);
		this.upgradeOnce.setAction(new AbstractAction() {
			private static final long serialVersionUID = 4992960498730337186L;

			@Override
			public void actionPerformed(ActionEvent e) {
				UpgradeSoldiersDefense.this.game.upgradeDefense((byte) UpgradeSoldiersDefense.this.clan.getId());
			}
		});
		this.upgradeOnce.setEnabled(costs < coins);
		this.upgradeOnce.setText(this.getOneLevelString());
	}
}
