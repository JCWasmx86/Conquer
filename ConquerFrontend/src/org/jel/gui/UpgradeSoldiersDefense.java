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
import org.jel.game.data.ConquerInfo;
import org.jel.game.data.Shared;
import org.jel.gui.utils.ImageResource;

/**
 * Allows the player to upgrade the defense strength of the soldiers
 */
final class UpgradeSoldiersDefense extends JPanel implements ActionListener {
	private static final long serialVersionUID = -681006129127926269L;
	private final transient Clan clan;
	private final transient ConquerInfo game;
	private final JLabel infoLabel;
	private final JButton upgradeOnce;
	private final JButton upgradeMax;
	private final Timer timer;

	/**
	 * Create a new UpgradeSoldiersDefense
	 *
	 * @param clan For which clan to upgrade the soldiers
	 * @param game A reference to the game.
	 */
	UpgradeSoldiersDefense(final Clan clan, final ConquerInfo game) {
		this.clan = clan;
		this.game = game;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.infoLabel = new JLabel();
		this.infoLabel.setText(this.getInfoText());
		this.add(this.infoLabel);
		this.upgradeOnce = new JButton(""); //$NON-NLS-1$
		this.initUpgradeOnce();
		this.add(this.upgradeOnce);
		this.upgradeMax = new JButton(new ImageResource("max.png")); //$NON-NLS-1$
		this.initUpgradeMax();
		this.add(this.upgradeMax);
		this.repaint();
		this.timer = new ExtendedTimer(17, this);
		this.timer.start();
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
		return Messages.getMessage("UpgradeSoldiersDefense.soldiersPower", //$NON-NLS-1$
				Utils.format(this.clan.getSoldiersDefenseStrength()), this.clan.getSoldiersDefenseLevel());
	}

	private String getOneLevelString() {
		return Messages.getMessage("Shared.upgradeToLevel", (this.clan.getSoldiersDefenseLevel() + 1),
				Utils.format(Shared.upgradeCostsForOffenseAndDefense(this.clan.getSoldiersDefenseLevel() + 1)));
	}

	private void initUpgradeMax() {
		if (this.clan.getSoldiersDefenseLevel() == Shared.MAX_LEVEL) {
			this.upgradeMax.setEnabled(false);
			this.upgradeMax.setText(Messages.getString("Shared.maxValueReached")); //$NON-NLS-1$
			return;
		}
		final var count = Shared.maxLevelsAddOffenseDefenseUpgrade(this.clan.getSoldiersDefenseLevel() + 1,
				this.clan.getCoins());
		this.upgradeMax.setAction(new AbstractAction() {
			private static final long serialVersionUID = -8569686717119904143L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				UpgradeSoldiersDefense.this.game
						.upgradeSoldiersDefenseFully((byte) UpgradeSoldiersDefense.this.clan.getId());
			}
		});
		this.upgradeMax.setEnabled(count > 0);
		if (count > 0) {
			this.upgradeMax.setText(Messages.getMessage("Shared.upgradeToSpecifiedLevel", //$NON-NLS-1$
					this.clan.getSoldiersDefenseLevel() + count));
		} else {
			this.upgradeMax.setText(Messages.getString("Shared.notEnoughCoins")); //$NON-NLS-1$
		}
	}

	private void initUpgradeOnce() {
		if (this.clan.getSoldiersDefenseLevel() == Shared.MAX_LEVEL) {
			this.upgradeOnce.setEnabled(false);
			this.upgradeOnce.setText(Messages.getString("Shared.maxValueReached")); //$NON-NLS-1$
			return;
		}
		final var coins = this.clan.getCoins();
		final var costs = Shared.upgradeCostsForOffenseAndDefense(this.clan.getSoldiersDefenseLevel() + 1);
		this.upgradeOnce.setAction(new AbstractAction() {
			private static final long serialVersionUID = 4992960498730337186L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				UpgradeSoldiersDefense.this.game.upgradeDefense((byte) UpgradeSoldiersDefense.this.clan.getId());
			}
		});
		this.upgradeOnce.setEnabled(costs < coins);
		this.upgradeOnce.setText(this.getOneLevelString());
	}
}
