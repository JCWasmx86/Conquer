package conquer.gui;

import java.awt.Component;
import java.io.Serial;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import conquer.data.ConquerInfo;
import conquer.data.ICity;
import conquer.data.Resource;
import conquer.gui.utils.ImageResource;

/**
 * Allows the player to see some information about a resource and upgrade it.
 */
final class ResourceButton extends JPanel {
	@Serial
	private static final long serialVersionUID = 8574350366288971896L;
	private final JButton upgradeThisResource;
	private final JButton maximumUpgrade;
	private final JLabel infoLabel;
	private final transient ICity city;
	private final Resource resource;
	private final ConquerInfo info;

	/**
	 * Create a new button for a specified city
	 *
	 * @param resource The resource to show information for. If {@code null}, the
	 *                 defense may be upgraded
	 * @param city     A reference to the city.
	 * @param cip      A reference to the parent CityInfoPanel.
	 */
	ResourceButton(final Resource resource, final ICity city, final CityInfoPanel cip) {
		this.city = city;
		this.resource = resource;
		this.info = city.getInfo();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.infoLabel = new JLabel(this.getInfoLabelText(),
			new ImageResource(resource == null ? "defenseUpgrade.png" : resource.getImage()),
			SwingConstants.LEFT);
		this.add(this.infoLabel);
		this.upgradeThisResource = new JButton();
		this.upgradeThisResource.setText(this.getUpgradeThisResourceText());
		this.upgradeThisResource.addActionListener(e -> {
			if (resource == null) {
				city.getInfo().upgradeDefense(city);
			} else {
				city.getInfo().upgradeResource(resource, city);
			}
			ResourceButton.this.upgradeThisResource.setText(this.getUpgradeThisResourceText());
			cip.doUpdate();
		});
		this.add(this.upgradeThisResource);
		this.maximumUpgrade = new JButton();
		this.maximumUpgrade.setIcon(new ImageResource("max.png"));
		this.maximumUpgrade.setText(this.getMaxUpgradeText());
		this.add(this.maximumUpgrade);
		this.maximumUpgrade.addActionListener(e -> {
			if (resource == null) {
				city.getInfo().upgradeDefenseFully(city);
			} else {
				city.getInfo().upgradeResourceFully(resource, city);
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
		if (this.city.getInfo().isDead(this.city.getInfo().getPlayerClan())) {
			this.upgradeThisResource.setEnabled(false);
			this.maximumUpgrade.setEnabled(false);
		}
	}

	private int getIndex() {
		return this.resource == null ? Resource.values().length : this.resource.getIndex();
	}

	private String getInfoLabelText() {
		final var level = this.city.getLevels().get(this.getIndex());
		return (this.resource == null ? Messages.getString("Shared.defense") : this.resource.getName()) + " "
			+ Messages.getString("ResourceButton.level") + " " + level;
	}

	private String getMaxUpgradeText() {
		final var level = this.city.getLevels().get(this.getIndex());
		var currentCoins = this.city.getInfo().getPlayerClan().getCoins();
		int currentLevel = level;
		while (true) {
			final var costs = this.city.getClan().costs(currentLevel + 1);
			currentCoins -= costs;
			if (currentCoins <= 0) {
				currentCoins += costs;
				break;
			}
			currentLevel++;
		}
		return Messages.getMessage("ResourceButton.maxUpgrade", currentLevel,
			Utils.format(this.city.getInfo().getPlayerClan().getCoins() - currentCoins));
	}

	private String getUpgradeThisResourceText() {
		final var level = this.city.getLevels().get(this.getIndex());
		final var costsForUpgrade = Utils
			.format(this.city.getClan().costs(this.city.getLevels().get(this.getIndex()) + 1));
		return Messages.getMessage("Shared.upgradeToLevel", level + 1, costsForUpgrade);
	}

	private void updateLabel(final int level) {
		if (this.city.isPlayerCity()) {
			if (level < this.info.getMaximumLevel()) {
				this.infoLabel.setText(this.getInfoLabelText());
			} else {
				this.infoLabel.setText(Messages.getString("Shared.maxValueReached"));
			}
		} else {
			this.infoLabel
				.setText((this.resource == null ? Messages.getString("Shared.defense") : this.resource.getName())

					+ " " + Messages.getString("ResourceButton.level") + " ???");
		}
	}

	private void updateMaximumUpgrade(final int level) {
		var currentCoins = this.city.getInfo().getPlayerClan().getCoins();
		var currentLevel = level;
		while (true) {
			final var costs = this.city.getClan().costs(currentLevel + 1);
			currentCoins -= costs;
			if (currentCoins <= 0) {
				break;
			}
			currentLevel++;
		}
		if ((currentLevel != level) && this.city.isPlayerCity()) {
			this.maximumUpgrade.setEnabled(true);
			this.maximumUpgrade.setText(this.getMaxUpgradeText());
		} else if ((this.city.isPlayerCity()) && (level == this.info.getMaximumLevel())) {
			this.maximumUpgrade.setEnabled(false);
			this.maximumUpgrade.setText(Messages.getString("Shared.maxValueReached"));
		} else {
			this.maximumUpgrade.setEnabled(false);
			this.maximumUpgrade.setText(Messages.getString("ResourceButton.noUpgrade"));
		}
	}

	private void updateUpgradeThisResource(final int level) {
		final var costsForUpgrade = this.city.getClan().costs(this.city.getLevels().get(this.getIndex()) + 1);
		final var currentCoins = this.city.getInfo().getPlayerClan().getCoins();
		if ((costsForUpgrade < currentCoins) && (this.city.isPlayerCity())) {
			this.upgradeThisResource.setEnabled(true);
			this.upgradeThisResource.setText(this.getUpgradeThisResourceText());
		} else if ((this.city.isPlayerCity()) && (level == this.info.getMaximumLevel())) {
			this.upgradeThisResource.setEnabled(false);
			this.upgradeThisResource.setText(Messages.getString("Shared.maxValueReached"));
		} else {
			this.upgradeThisResource.setEnabled(false);
			this.upgradeThisResource.setText(Messages.getString("ResourceButton.noUpgrade"));
		}
	}
}
