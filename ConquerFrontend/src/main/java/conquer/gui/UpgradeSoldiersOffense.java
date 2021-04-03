package conquer.gui;

import conquer.data.IClan;
import conquer.data.SoldierUpgrade;
import conquer.gui.utils.ImageResource;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Allows the player to upgrade the offense strength of the soldiers
 */
final class UpgradeSoldiersOffense extends JPanel implements ActionListener {
	private static final long serialVersionUID = -681006129127926269L;
	private final transient IClan clan;
	private final JLabel infoLabel;
	private final JButton upgradeOnce;
	private final JButton upgradeMax;

	/**
	 * Create a new UpgradeSoldiersOffense
	 *
	 * @param clan For which clan to upgrade the soldiers
	 */
	UpgradeSoldiersOffense(final IClan clan) {
		this.clan = clan;
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
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
		final Timer timer = new ExtendedTimer(Utils.getRefreshRate(), this);
		timer.start();
	}

	/**
	 * Update labels/buttons/...
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		this.infoLabel.setText(this.getInfoText());
		this.initUpgradeOnce();
		this.initUpgradeMax();
	}

	private String getInfoText() {
		return Messages.getMessage("UpgradeSoldiersOffense.soldiersPower",
				Utils.format(this.clan.getSoldiersOffenseStrength()), this.clan.getSoldiersOffenseLevel());
	}

	private String getOneLevelString() {
		return Messages.getMessage("Shared.upgradeToLevel", (this.clan.getSoldiersOffenseLevel() + 1),
				Utils.format(this.clan.upgradeCosts(SoldierUpgrade.OFFENSE, this.clan.getSoldiersOffenseLevel() + 1)));
	}

	private void initUpgradeMax() {
		if (this.clan.getSoldiersOffenseLevel() == this.clan.getInfo().getMaximumLevel()) {
			this.upgradeMax.setEnabled(false);
			this.upgradeMax.setText(Messages.getString("Shared.maxValueReached"));
			return;
		}
		final var count = this.clan.maxLevels(SoldierUpgrade.OFFENSE, this.clan.getSoldiersOffenseLevel() + 1,
				this.clan.getCoins());
		this.upgradeMax.setAction(new AbstractAction() {
			private static final long serialVersionUID = -8569686717119904143L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				UpgradeSoldiersOffense.this.clan.upgradeSoldiersOffenseFully();
			}
		});
		this.upgradeMax.setEnabled(count > 0);
		if (count > 0) {
			this.upgradeMax.setText(Messages.getMessage("Shared.upgradeToSpecifiedLevel",
					this.clan.getSoldiersOffenseLevel() + count));
		} else {
			this.upgradeMax.setText(Messages.getString("Shared.notEnoughCoins"));
		}
	}

	private void initUpgradeOnce() {
		if (this.clan.getSoldiersOffenseLevel() == this.clan.getInfo().getMaximumLevel()) {
			this.upgradeOnce.setEnabled(false);
			this.upgradeOnce.setText(Messages.getString("Shared.maxValueReached"));
			return;
		}
		final var coins = this.clan.getCoins();
		final var costs = this.clan.upgradeCosts(SoldierUpgrade.OFFENSE, this.clan.getSoldiersOffenseLevel() + 1);
		this.upgradeOnce.setAction(new AbstractAction() {
			private static final long serialVersionUID = 4992960498730337186L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				UpgradeSoldiersOffense.this.clan.upgradeSoldiersOffense();
			}
		});
		this.upgradeOnce.setEnabled(costs < coins);
		this.upgradeOnce.setText(this.getOneLevelString());
	}
}
