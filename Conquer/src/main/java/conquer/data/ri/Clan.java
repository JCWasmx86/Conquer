package conquer.data.ri;

import java.awt.*;
import java.util.List;

import conquer.data.ConquerInfo;
import conquer.data.IClan;
import conquer.data.Resource;
import conquer.data.Shared;
import conquer.data.SoldierUpgrade;
import conquer.data.Version;
import conquer.data.strategy.Strategy;
import conquer.data.strategy.StrategyData;
import conquer.data.strategy.StrategyProvider;

/**
 * A clan represents a group of cities.
 */
final class Clan implements IClan {
	private int id = -1;
	private double coins;
	private String name;
	private Color color;
	private List<Double> resources;
	private List<Double> resourceStats;
	private Strategy strategy;
	private StrategyData strategyData;
	private double soldiersStrength = 1;
	private int soldiersLevel = 0;
	private double soldiersDefenseStrength = 1;
	private int soldiersDefenseLevel = 0;
	private double soldiersOffenseStrength = 1;
	private int soldiersOffenseLevel = 0;
	private int flags;
	private ConquerInfo info;

	Clan() {
		// Constructor for incrementally building a clan.
	}

	void setInfo(final ConquerInfo info) {
		this.info = info;
	}

	/**
	 * Get the number of coins this clan has
	 *
	 * @return Number of coins.
	 */
	@Override
	public double getCoins() {
		return this.coins;
	}

	/**
	 * Returns the color associated with this clan
	 *
	 * @return Clan color
	 */
	@Override
	public Color getColor() {
		return this.color;
	}

	/**
	 * Returns optional data for the strategy.
	 *
	 * @return Optional Data. May be null
	 */
	@Override
	public StrategyData getData() {
		return this.strategyData;
	}

	/**
	 * Returns some unspecified, implementation defined integer
	 *
	 * @return Some integer
	 */
	@Override
	public int getFlags() {
		return this.flags;
	}

	/**
	 * Return the id of the clan.
	 *
	 * @return Clan id.
	 */
	@Override
	public int getId() {
		return this.id;
	}

	/**
	 * Returns the name of the clan
	 *
	 * @return Name of the clan
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Returns a mutable list of the amount of all resources. The index for a
	 * resource is obtained by {@link Resource#getIndex()}
	 *
	 * @return List of resources.
	 */
	@Override
	public List<Double> getResources() {
		return this.resources;
	}

	/**
	 * Returns a mutable list of the production of all resources. The index for a
	 * resource is obtained by {@link Resource#getIndex()}
	 *
	 * @return Production of resources.
	 */
	@Override
	public List<Double> getResourceStats() {
		return this.resourceStats;
	}

	/**
	 * Returns the defenselevel of the soldiers of the clan
	 *
	 * @return Defenselevel
	 */
	@Override
	public int getSoldiersDefenseLevel() {
		return this.soldiersDefenseLevel;
	}

	/**
	 * Returns the defense strength of the soldiers of the clan
	 *
	 * @return Defensestrength
	 */
	@Override
	public double getSoldiersDefenseStrength() {
		return this.soldiersDefenseStrength;
	}

	/**
	 * Returns the level of the soldiers of the clan
	 *
	 * @return Level
	 */
	@Override
	public int getSoldiersLevel() {
		return this.soldiersLevel;
	}

	/**
	 * Returns the offensivelevel of the soldiers of the clan
	 *
	 * @return Offensivelevel
	 */
	@Override
	public int getSoldiersOffenseLevel() {
		return this.soldiersOffenseLevel;
	}

	/**
	 * Returns the offense strength of the soldiers of the clan
	 *
	 * @return Offensestrength
	 */
	@Override
	public double getSoldiersOffenseStrength() {
		return this.soldiersOffenseStrength;
	}

	/**
	 * Returns the strength of the soldiers of the clan.
	 *
	 * @return Strength
	 */
	@Override
	public double getSoldiersStrength() {
		return this.soldiersStrength;
	}

	/**
	 * Returns the Strategy this clan uses
	 *
	 * @return The strategy of the clan.
	 */
	@Override
	public Strategy getStrategy() {
		return this.strategy;
	}

	/**
	 * Initialises the clan.
	 *
	 * @param strategies An array of all strategies available.
	 */
	@Override
	public void init(final StrategyProvider[] strategies, final Version version) {
		if (strategies == null) {
			throw new IllegalArgumentException("strategies==null");
		} else if (version == null) {
			throw new IllegalArgumentException("version==null");
		}
		final var list = new GoodDoubleList();
		for (var j = 0; j < Resource.values().length; j++) {
			list.add(0.0);
		}
		this.setResources(list);
		final var resourceStatsList = new GoodDoubleList();
		for (var i = 0; i < Resource.values().length; i++) {
			resourceStatsList.add(0.0);
		}
		this.setResourceStats(resourceStatsList);
		final var givenPlayType = this.flags;
		if ((strategies[givenPlayType] == null) || !strategies[givenPlayType].compatibleTo(version)) {
			this.strategy = strategies[1].buildStrategy();
			Shared.LOGGER.error("Found invalid strategy id: " + this.flags);
			Shared.LOGGER.message("Clan: " + this.name + " " + strategies[1].getName());
		} else {
			final var s = strategies[givenPlayType].buildStrategy();
			this.strategy = s;
			this.strategyData = s.getData();
			Shared.LOGGER.message("Clan: " + this.name + " " + strategies[givenPlayType].getName());
		}
	}

	/**
	 * Returns whether the clan is played by the human player or not
	 *
	 * @return {@code true} if the clan is played by the human.
	 */
	@Override
	public boolean isPlayerClan() {
		return this.id == 0;
	}

	/**
	 * Set the coins.
	 *
	 * @param coins If {@code coins} is smaller than 0, the new amount of coins is
	 *              0.
	 */
	@Override
	public void setCoins(final double coins) {
		this.coins = coins < 0 ? 0 : coins;
	}

	/**
	 * Set the color of the clan. This method may only be called once.
	 *
	 * @param color May not be null
	 */
	@Override
	public void setColor(final Color color) {
		if (color == null) {
			throw new IllegalArgumentException("color == null");
		} else if (this.color != null) {
			throw new UnsupportedOperationException("Can't change color of clan!");
		}
		this.color = color;
	}

	/**
	 * Set the flags of a clan.
	 *
	 * @param flags Some unspecified value.
	 */
	@Override
	public void setFlags(final int flags) {
		this.flags = flags;
	}

	/**
	 * Change the id of the clan. May only be called once.
	 *
	 * @param id Has to be between zero and {@code Byte#MAX_VALUE}.
	 */
	@Override
	public void setId(final int id) {
		if (this.id != -1) {
			throw new UnsupportedOperationException("Can't change id of clan!");
		} else if (id < 0) {
			throw new IllegalArgumentException("Out of bounds!");
		}
		this.id = id;
	}

	/**
	 * Set the name. May only be called once.
	 *
	 * @param name May not be null
	 */
	@Override
	public void setName(final String name) {
		if (this.name != null) {
			throw new UnsupportedOperationException("Can't change name of clan!");
		} else if (name == null) {
			throw new IllegalArgumentException("name == null");
		}
		this.name = name;
	}

	@Override
	public void setResources(final List<Double> resources) {
		if (this.resources != null) {
			throw new UnsupportedOperationException("resources can't be changed!");
		} else if (resources == null) {
			throw new IllegalArgumentException("resources==null");
		} else if (resources.size() != Resource.values().length) {
			throw new IllegalArgumentException("resources.size() != Resource.values.length: " + resources.size());
		}
		this.resources = new GoodDoubleList(resources);
	}

	@Override
	public void setResourceStats(final List<Double> resourceStats) {
		if (this.resourceStats != null) {
			throw new UnsupportedOperationException("Resource stats can't be changed!");
		} else if (resourceStats == null) {
			throw new IllegalArgumentException("resourceStats==null");
		} else if (resourceStats.size() != Resource.values().length) {
			throw new IllegalArgumentException(
				"resourceStats.size() != Resource.values.length: " + resourceStats.size());
		}
		this.resourceStats = new GoodDoubleList(resourceStats, true);
	}

	void setSoldiersDefenseLevel(final int soldiersDefenseLevel) {
		this.soldiersDefenseLevel = soldiersDefenseLevel;
	}

	@Override
	public boolean upgradeSoldiersDefense() {
		final var currLevel = this.getSoldiersDefenseLevel();
		if (currLevel == this.info.getMaximumLevel()) {
			return false;
		}
		final var costs = this.upgradeCosts(SoldierUpgrade.DEFENSE, currLevel + 1);
		if (costs > this.getCoins()) {
			return false;
		}
		this.setCoins(this.getCoins() - costs);
		this.setSoldiersDefenseLevel(currLevel + 1);
		this.setSoldiersDefenseStrength(1 + this.newPower(SoldierUpgrade.DEFENSE, currLevel + 1));
		return true;
	}

	void setSoldiersDefenseStrength(final double soldiersDefenseStrength) {
		this.soldiersDefenseStrength = soldiersDefenseStrength;
	}

	void setSoldiersLevel(final int soldiersLevel) {
		this.soldiersLevel = soldiersLevel;
	}

	@Override
	public boolean upgradeSoldiers() {
		final var currLevel = this.getSoldiersLevel();
		if (currLevel == this.info.getMaximumLevel()) {
			return false;
		}
		final var costs = this.upgradeCosts(SoldierUpgrade.BOTH, currLevel + 1);
		if (costs > this.getCoins()) {
			return false;
		}
		this.setCoins(this.getCoins() - costs);
		this.setSoldiersLevel(currLevel + 1);
		this.setSoldiersStrength(1 + this.newPower(SoldierUpgrade.BOTH, currLevel + 1));
		return true;
	}

	void setSoldiersOffenseLevel(final int soldiersOffenseLevel) {
		this.soldiersOffenseLevel = soldiersOffenseLevel;
	}

	@Override
	public boolean upgradeSoldiersOffense() {
		final var currLevel = this.getSoldiersOffenseLevel();
		if (currLevel == this.info.getMaximumLevel()) {
			return false;
		}
		final var costs = this.upgradeCosts(SoldierUpgrade.OFFENSE, currLevel + 1);
		if (costs > this.getCoins()) {
			return false;
		}
		this.setCoins(this.getCoins() - costs);
		this.setSoldiersOffenseLevel(currLevel + 1);
		this.setSoldiersOffenseStrength(1 + this.newPower(SoldierUpgrade.DEFENSE, currLevel + 1));
		return true;
	}

	void setSoldiersOffenseStrength(final double soldiersOffenseStrength) {
		this.soldiersOffenseStrength = soldiersOffenseStrength;
	}

	void setSoldiersStrength(final double soldiersStrength) {
		this.soldiersStrength = soldiersStrength;
	}

	@Override
	public void setStrategy(final Strategy strategy) {
		if (this.strategy != null) {
			throw new IllegalArgumentException("strategy can't be changed!");
		} else if (strategy == null) {
			throw new IllegalArgumentException("strategy==null");
		}
		this.strategy = strategy;
	}

	@Override
	public void setStrategyData(final StrategyData strategyData) {
		this.strategyData = strategyData;
	}

	@Override
	public void update(final int currentRound) {
		if (currentRound < 0) {
			throw new IllegalArgumentException("currentRound < 0");
		}
		if (this.strategyData != null) {
			this.strategyData.update(currentRound);
		}
	}

	@Override
	public ConquerInfo getInfo() {
		return this.info;
	}
}
