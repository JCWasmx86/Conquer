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
	ResourceButton(Resource resource, City city, CityInfoPanel cip) {
		this.city = city;
		this.resource = resource;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.infoLabel = new JLabel(getInfoLabelText(),
				new ImageResource(resource == null ? "defenseUpgrade.png" : resource.getImage()), SwingConstants.LEFT);
		this.add(this.infoLabel);
		this.upgradeThisResource = new JButton();
		this.upgradeThisResource.setText(getUpgradeThisResourceText());
		this.upgradeThisResource.addActionListener(e -> {
			if (resource != null) {
				city.getGame().upgradeResource((byte) Shared.PLAYER_CLAN, resource, city);
			} else {
				city.getGame().upgradeDefense((byte) Shared.PLAYER_CLAN, city);
			}
			final var costsForUpgrade1 = Shared.costs(city.getLevels().get(getIndex()) + 1);
			final var level1 = city.getLevels().get(getIndex());
			ResourceButton.this.upgradeThisResource.setText(
					"Upgrade to level " + (level1 + 1) + ": " + String.format("%.2f", costsForUpgrade1) + "coins");
			cip.doUpdate();
		});
		this.add(this.upgradeThisResource);

		this.maximumUpgrade = new JButton();
		this.maximumUpgrade.setIcon(new ImageResource("max.png"));
		this.maximumUpgrade.setText(getMaxUpgradeText());
		this.add(this.maximumUpgrade);
		this.maximumUpgrade.addActionListener(e -> {
			if (resource != null) {
				city.getGame().upgradeResourceFully((byte) Shared.PLAYER_CLAN, resource, city);
			} else {
				city.getGame().upgradeDefenseFully((byte) Shared.PLAYER_CLAN, city);
			}
			final var level1 = city.getLevels().get(getIndex());
			double currentCoins1 = city.getGame().getCoins().get(Shared.PLAYER_CLAN);
			int currentLevel1 = level1;
			while (true) {
				var costs = Shared.costs(currentLevel1 + 1);
				currentCoins1 -= costs;
				if (currentCoins1 <= 0) {
					currentCoins1 += costs;
					break;
				}
				currentLevel1++;
			}
			ResourceButton.this.maximumUpgrade.setText("Maximum upgrade to level " + currentLevel1 + " for "
					+ String.format("%.2f", city.getGame().getCoins().get(Shared.PLAYER_CLAN) - currentCoins1)
					+ "coins");
			cip.doUpdate();
		});
		this.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.upgradeThisResource.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.maximumUpgrade.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.setVisible(true);
	}

	private int getIndex() {
		return this.resource == null ? Resource.values().length : this.resource.getIndex();
	}

	private String getInfoLabelText() {
		final var level = this.city.getLevels().get(getIndex());
		return (this.resource == null ? "Defense" : this.resource.getName()) + " Level " + level;
	}

	private String getUpgradeThisResourceText() {
		final var level = this.city.getLevels().get(getIndex());
		final var costsForUpgrade = Shared.costs(
				this.city.getLevels().get(this.resource == null ? Resource.values().length : this.resource.getIndex())
						+ 1);
		return "Upgrade to level " + (level + 1) + ": " + String.format("%.2f", costsForUpgrade) + "coins";
	}

	private String getMaxUpgradeText() {
		final var level = this.city.getLevels().get(getIndex());
		double currentCoins = city.getGame().getCoins().get(Shared.PLAYER_CLAN);
		int currentLevel = level;
		while (true) {
			var costs = Shared.costs(currentLevel + 1);
			currentCoins -= costs;
			if (currentCoins <= 0) {
				currentCoins += costs;
				break;
			}
			currentLevel++;
		}
		return "Maximum upgrade to level " + currentLevel + " for "
				+ String.format("%.2f", city.getGame().getCoins().get(Shared.PLAYER_CLAN) - currentCoins) + "coins";
	}

	/**
	 * Updates all components of this component.
	 */
	void doUpdate() {
		final var level = this.city.getLevels().get(getIndex());
		if (this.city.getClan() == Shared.PLAYER_CLAN) {
			if (level < 1000) {
				this.infoLabel.setText(getInfoLabelText());
			} else {
				this.infoLabel.setText("Maximum value reached!");
			}
		} else {
			this.infoLabel.setText((this.resource == null ? "Defense" : this.resource.getName()) + " Level ???");
		}
		final var costsForUpgrade = Shared.costs(this.city.getLevels().get(getIndex()) + 1);
		double currentCoins = this.city.getGame().getCoins().get(0);
		if ((costsForUpgrade < currentCoins) && (this.city.getClan() == Shared.PLAYER_CLAN)) {
			this.upgradeThisResource.setEnabled(true);
			this.upgradeThisResource.setText(getUpgradeThisResourceText());
		} else if ((this.city.getClan() == 0) && (level == 1000)) {
			this.upgradeThisResource.setEnabled(false);
			this.upgradeThisResource.setText("Maximum value reached!");
		} else {
			this.upgradeThisResource.setEnabled(false);
			this.upgradeThisResource.setText("No upgrade available!");
		}
		int currentLevel = level;
		while (true) {
			var costs = Shared.costs(currentLevel + 1);
			currentCoins -= costs;
			if (currentCoins <= 0) {
				break;
			}
			currentLevel++;
		}
		if ((currentLevel != level) && (this.city.getClan() == Shared.PLAYER_CLAN)) {
			this.maximumUpgrade.setEnabled(true);
			this.maximumUpgrade.setText(getMaxUpgradeText());
		} else if ((this.city.getClan() == 0) && (level == 1000)) {
			this.maximumUpgrade.setEnabled(false);
			this.maximumUpgrade.setText("Maximum value reached!");
		} else {
			this.maximumUpgrade.setEnabled(false);
			this.maximumUpgrade.setText("No upgrade available!");
		}
	}
}
