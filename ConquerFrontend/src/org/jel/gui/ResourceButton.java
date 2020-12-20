package org.jel.gui;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jel.game.data.City;
import org.jel.game.data.Resource;
import org.jel.game.data.Shared;
import org.jel.gui.utils.ImageResource;

/**
 * Allows the player to see some information about a resource and upgrade it.
 */
final class ResourceButton extends JPanel {
	private static final long serialVersionUID = 8574350366288971896L;
	private final JButton upgradeThisResource;
	private final JButton maximumUpgrade;
	private final JLabel infoLabel;
	private final transient City city;
	private final Resource resource;

	/**
	 * Create a new button for a specified city
	 *
	 * @param resource The resource to show information for. If {@code null}, the
	 *                 defense may be upgraded
	 * @param city     A reference to the city.
	 * @param cip      A reference to the parent CityInfoPanel.
	 */
	ResourceButton(final Resource resource, final City city, final CityInfoPanel cip) {
		this.city = city;
		this.resource = resource;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.infoLabel = new JLabel(this.getInfoLabelText(),
				new ImageResource(resource == null ? "defenseUpgrade.png" : resource.getImage()), SwingConstants.LEFT); //$NON-NLS-1$
		this.add(this.infoLabel);
		this.upgradeThisResource = new JButton();
		this.upgradeThisResource.setText(this.getUpgradeThisResourceText());
		this.upgradeThisResource.addActionListener(e -> {
			if (resource != null) {
				city.getInfo().upgradeResource(Shared.PLAYER_CLAN, resource, city);
			} else {
				city.getInfo().upgradeDefense(Shared.PLAYER_CLAN, city);
			}
			ResourceButton.this.upgradeThisResource.setText(this.getUpgradeThisResourceText());
			cip.doUpdate();
		});
		this.add(this.upgradeThisResource);
		this.maximumUpgrade = new JButton();
		this.maximumUpgrade.setIcon(new ImageResource("max.png")); //$NON-NLS-1$
		this.maximumUpgrade.setText(this.getMaxUpgradeText());
		this.add(this.maximumUpgrade);
		this.maximumUpgrade.addActionListener(e -> {
			if (resource != null) {
				city.getInfo().upgradeResourceFully(Shared.PLAYER_CLAN, resource, city);
			} else {
				city.getInfo().upgradeDefenseFully(Shared.PLAYER_CLAN, city);
			}
			ResourceButton.this.maximumUpgrade.setText(this.getMaxUpgradeText());
			cip.doUpdate();
		});
		this.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.upgradeThisResource.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.maximumUpgrade.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.setVisible(true);
	}

	/**
	 * Updates all components of this component.
	 */
	void doUpdate() {
		final var level = this.city.getLevels().get(this.getIndex());
		this.updateLabel(level);
		this.updateUpgradeThisResource(level);
		this.updateMaximumUpgrade(level);
		if (this.city.getInfo().isDead(Shared.PLAYER_CLAN)) {
			this.upgradeThisResource.setEnabled(false);
			this.maximumUpgrade.setEnabled(false);
		}
	}

	private int getIndex() {
		return this.resource == null ? Resource.values().length : this.resource.getIndex();
	}

	private String getInfoLabelText() {
		final var level = this.city.getLevels().get(this.getIndex());
		return (this.resource == null ? Messages.getString("Shared.defense") : this.resource.getName()) + " " //$NON-NLS-1$
				+ Messages.getString("ResourceButton.level") + " " + level; //$NON-NLS-1$
	}

	private String getMaxUpgradeText() {
		final var level = this.city.getLevels().get(this.getIndex());
		double currentCoins = this.city.getInfo().getCoins().get(Shared.PLAYER_CLAN);
		int currentLevel = level;
		while (true) {
			final var costs = Shared.costs(currentLevel + 1);
			currentCoins -= costs;
			if (currentCoins <= 0) {
				currentCoins += costs;
				break;
			}
			currentLevel++;
		}
		return Messages.getMessage("ResourceButton.maxUpgrade", currentLevel, //$NON-NLS-1$
				Utils.format(this.city.getInfo().getCoins().get(Shared.PLAYER_CLAN) - currentCoins));
	}

	private String getUpgradeThisResourceText() {
		final var level = this.city.getLevels().get(this.getIndex());
		final var costsForUpgrade = Utils.format(Shared.costs(this.city.getLevels().get(this.getIndex()) + 1));
		return Messages.getMessage("Shared.upgradeToLevel", level + 1, costsForUpgrade);//$NON-NLS-1$
	}

	private void updateLabel(final int level) {
		if (this.city.isPlayerCity()) {
			if (level < Shared.MAX_LEVEL) {
				this.infoLabel.setText(this.getInfoLabelText());
			} else {
				this.infoLabel.setText(Messages.getString("Shared.maxValueReached")); //$NON-NLS-1$
			}
		} else {
			this.infoLabel
					.setText((this.resource == null ? Messages.getString("Shared.defense") : this.resource.getName()) //$NON-NLS-1$
							+ " " + Messages.getString("ResourceButton.level") + " ???"); //$NON-NLS-1$
		}
	}

	private void updateMaximumUpgrade(final int level) {
		double currentCoins = this.city.getInfo().getCoins().get(Shared.PLAYER_CLAN);
		var currentLevel = level;
		while (true) {
			final var costs = Shared.costs(currentLevel + 1);
			currentCoins -= costs;
			if (currentCoins <= 0) {
				break;
			}
			currentLevel++;
		}
		if ((currentLevel != level) && this.city.isPlayerCity()) {
			this.maximumUpgrade.setEnabled(true);
			this.maximumUpgrade.setText(this.getMaxUpgradeText());
		} else if ((this.city.isPlayerCity()) && (level == Shared.MAX_LEVEL)) {
			this.maximumUpgrade.setEnabled(false);
			this.maximumUpgrade.setText(Messages.getString("Shared.maxValueReached")); //$NON-NLS-1$
		} else {
			this.maximumUpgrade.setEnabled(false);
			this.maximumUpgrade.setText(Messages.getString("ResourceButton.noUpgrade")); //$NON-NLS-1$
		}
	}

	private void updateUpgradeThisResource(final int level) {
		final var costsForUpgrade = Shared.costs(this.city.getLevels().get(this.getIndex()) + 1);
		final double currentCoins = this.city.getInfo().getCoins().get(Shared.PLAYER_CLAN);
		if ((costsForUpgrade < currentCoins) && (this.city.isPlayerCity())) {
			this.upgradeThisResource.setEnabled(true);
			this.upgradeThisResource.setText(this.getUpgradeThisResourceText());
		} else if ((this.city.isPlayerCity()) && (level == Shared.MAX_LEVEL)) {
			this.upgradeThisResource.setEnabled(false);
			this.upgradeThisResource.setText(Messages.getString("Shared.maxValueReached")); //$NON-NLS-1$
		} else {
			this.upgradeThisResource.setEnabled(false);
			this.upgradeThisResource.setText(Messages.getString("ResourceButton.noUpgrade")); //$NON-NLS-1$
		}
	}
}
