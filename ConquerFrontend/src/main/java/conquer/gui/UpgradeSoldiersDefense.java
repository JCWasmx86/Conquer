package conquer.gui;

import conquer.data.IClan;
import conquer.data.SoldierUpgrade;

import java.awt.event.ActionEvent;
import java.io.Serial;
import javax.swing.AbstractAction;

/**
 * Allows the player to upgrade the defense strength of the soldiers
 */
final class UpgradeSoldiersDefense extends UpgradePanel {
	@Serial
	private static final long serialVersionUID = -681006129127926269L;

	public UpgradeSoldiersDefense(final IClan clan) {
		super(clan);
	}

	@Override
	String getInfoText() {
		return Messages.getMessage("UpgradeSoldiersDefense.soldiersPower",
				Utils.format(this.clan.getSoldiersDefenseStrength()), this.clan.getSoldiersDefenseLevel());
	}

	@Override
	String getOneLevelString() {
		return Messages.getMessage("Shared.upgradeToLevel", (this.clan.getSoldiersDefenseLevel() + 1),
				Utils.format(this.clan.upgradeCosts(SoldierUpgrade.DEFENSE, this.clan.getSoldiersDefenseLevel() + 1)));
	}

	@Override
	void initUpgradeMax() {
		if (this.clan.getSoldiersDefenseLevel() == this.clan.getInfo().getMaximumLevel()) {
			this.upgradeMax.setEnabled(false);
			this.upgradeMax.setText(Messages.getString("Shared.maxValueReached"));
			return;
		}
		final var count = this.clan.maxLevels(SoldierUpgrade.DEFENSE, this.clan.getSoldiersDefenseLevel() + 1,
				this.clan.getCoins());
		this.upgradeMax.setAction(new AbstractAction() {
			@Serial
			private static final long serialVersionUID = -8569686717119904143L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				UpgradeSoldiersDefense.this.clan.upgradeSoldiersDefenseFully();
			}
		});
		this.upgradeMax.setEnabled(count > 0);
		if (count > 0) {
			this.upgradeMax.setText(Messages.getMessage("Shared.upgradeToSpecifiedLevel",
					this.clan.getSoldiersDefenseLevel() + count));
		} else {
			this.upgradeMax.setText(Messages.getString("Shared.notEnoughCoins"));
		}
	}

	@Override
	void initUpgradeOnce() {
		if (this.clan.getSoldiersDefenseLevel() == this.clan.getInfo().getMaximumLevel()) {
			this.upgradeOnce.setEnabled(false);
			this.upgradeOnce.setText(Messages.getString("Shared.maxValueReached"));
			return;
		}
		final var coins = this.clan.getCoins();
		final var costs = this.clan.upgradeCosts(SoldierUpgrade.DEFENSE, this.clan.getSoldiersDefenseLevel() + 1);
		this.upgradeOnce.setAction(new AbstractAction() {
			@Serial
			private static final long serialVersionUID = 4992960498730337186L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				UpgradeSoldiersDefense.this.clan.upgradeSoldiersDefense();
			}
		});
		this.upgradeOnce.setEnabled(costs < coins);
		this.upgradeOnce.setText(this.getOneLevelString());
	}
}
