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
 * Allows the player to upgrade the strength of the soldiers
 */
final class UpgradeSoldiersPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -7456324799677381608L;
	private final transient Clan clan;
	private final transient ConquerInfo game;
	private final Timer timer;
	private final JLabel infoLabel;
	private final JButton upgradeOnce;
	private final JButton upgradeMax;

	/**
	 * Create a new UpgradeSoldiersPanel
	 *
	 * @param clan For which clan to upgrade the soldiers
	 * @param game A reference to the game.
	 */
	UpgradeSoldiersPanel(final Clan clan, final ConquerInfo game) {
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
		return Messages.getMessage("UpgradeSoldiersPanel.soldiersPower", Utils.format(this.clan.getSoldiersStrength()), //$NON-NLS-1$
				this.clan.getSoldiersLevel());
	}

	private String getOneLevelString() {
		return Messages.getMessage("Shared.upgradeToLevel", (this.clan.getSoldiersLevel() + 1), //$NON-NLS-1$
				Utils.format(Shared.upgradeCostsForSoldiers(this.clan.getSoldiersLevel() + 1)));
	}

	private void initUpgradeMax() {
		if (this.clan.getSoldiersLevel() == Shared.MAX_LEVEL) {
			this.upgradeMax.setEnabled(false);
			this.upgradeMax.setText(Messages.getString("Shared.maxValueReached")); //$NON-NLS-1$
			return;
		}
		final var count = Shared.maxLevelsAddSoldiersUpgrade(this.clan.getSoldiersLevel() + 1, this.clan.getCoins());
		this.upgradeMax.setAction(new AbstractAction() {
			private static final long serialVersionUID = 8078867867027862129L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				UpgradeSoldiersPanel.this.game.upgradeSoldiersFully(UpgradeSoldiersPanel.this.clan.getId());
			}
		});
		this.upgradeMax.setEnabled(count > 0);
		if (count > 0) {
			this.upgradeMax.setText(
					Messages.getMessage("Shared.upgradeToSpecifiedLevel", (this.clan.getSoldiersLevel() + count))); //$NON-NLS-1$
		} else {
			this.upgradeMax.setText(Messages.getString("Shared.notEnoughCoins")); //$NON-NLS-1$
		}
	}

	private void initUpgradeOnce() {
		if (this.clan.getSoldiersLevel() == Shared.MAX_LEVEL) {
			this.upgradeOnce.setEnabled(false);
			this.upgradeOnce.setText(Messages.getString("Shared.maxValueReached")); //$NON-NLS-1$
			return;
		}
		final var coins = this.clan.getCoins();
		final var costs = Shared.upgradeCostsForSoldiers(this.clan.getSoldiersLevel() + 1);
		this.upgradeOnce.setAction(new AbstractAction() {
			private static final long serialVersionUID = 8078867867027862129L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				UpgradeSoldiersPanel.this.game.upgradeSoldiers((byte) UpgradeSoldiersPanel.this.clan.getId());
			}
		});
		this.upgradeOnce.setEnabled(costs < coins);
		this.upgradeOnce.setText(this.getOneLevelString());
	}
}
