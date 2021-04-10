package conquer.gui;

import conquer.data.IClan;
import conquer.data.SoldierUpgrade;

import java.awt.event.ActionEvent;
import java.io.Serial;
import javax.swing.AbstractAction;

/**
 * Allows the player to upgrade the strength of the soldiers
 */
final class UpgradeSoldiersPanel extends UpgradePanel {
	@Serial
	private static final long serialVersionUID = -7456324799677381608L;

	UpgradeSoldiersPanel(IClan clan) {
		super(clan);
	}

	@Override
	String getInfoText() {
		return Messages.getMessage("UpgradeSoldiersPanel.soldiersPower", Utils.format(this.clan.getSoldiersStrength())
				,
				this.clan.getSoldiersLevel());
	}

	@Override
	String getOneLevelString() {
		return Messages.getMessage("Shared.upgradeToLevel", (this.clan.getSoldiersLevel() + 1),
				Utils.format(this.clan.upgradeCosts(SoldierUpgrade.BOTH, this.clan.getSoldiersLevel() + 1)));
	}

	@Override
	void initUpgradeMax() {
		if (this.clan.getSoldiersLevel() == this.clan.getInfo().getMaximumLevel()) {
			this.upgradeMax.setEnabled(false);
			this.upgradeMax.setText(Messages.getString("Shared.maxValueReached"));
			return;
		}
		final var count = this.clan.maxLevels(SoldierUpgrade.BOTH, this.clan.getSoldiersLevel() + 1,
				this.clan.getCoins());
		this.upgradeMax.setAction(new AbstractAction() {
			@Serial
			private static final long serialVersionUID = 8078867867027862129L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				UpgradeSoldiersPanel.this.clan.upgradeSoldiersFully();
			}
		});
		this.upgradeMax.setEnabled(count > 0);
		if (count > 0) {
			this.upgradeMax.setText(
					Messages.getMessage("Shared.upgradeToSpecifiedLevel", (this.clan.getSoldiersLevel() + count)));

		} else {
			this.upgradeMax.setText(Messages.getString("Shared.notEnoughCoins"));
		}
	}

	@Override
	void initUpgradeOnce() {
		if (this.clan.getSoldiersLevel() == this.clan.getInfo().getMaximumLevel()) {
			this.upgradeOnce.setEnabled(false);
			this.upgradeOnce.setText(Messages.getString("Shared.maxValueReached"));
			return;
		}
		final var coins = this.clan.getCoins();
		final var costs = this.clan.upgradeCosts(SoldierUpgrade.BOTH, this.clan.getSoldiersLevel() + 1);
		this.upgradeOnce.setAction(new AbstractAction() {
			@Serial
			private static final long serialVersionUID = 8078867867027862129L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				UpgradeSoldiersPanel.this.clan.upgradeSoldiers();
			}
		});
		this.upgradeOnce.setEnabled(costs < coins);
		this.upgradeOnce.setText(this.getOneLevelString());
	}
}
