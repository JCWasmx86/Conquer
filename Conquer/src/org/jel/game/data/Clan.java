package org.jel.game.data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.jel.game.data.strategy.Strategy;
import org.jel.game.data.strategy.StrategyData;
import org.jel.game.data.strategy.StrategyProvider;

/**
 * A clan represents a group of cities.
 */
public final class Clan {
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

	Clan() {
	}

	/**
	 * Get the number of coins this clan has
	 *
	 * @return Number of coins.
	 */
	public double getCoins() {
		return this.coins;
	}

	/**
	 * Returns the color associated with this clan
	 *
	 * @return Clan color
	 */
	public Color getColor() {
		return this.color;
	}

	/**
	 * Returns optional data for the strategy.
	 *
	 * @return Optional Data. May be null
	 */
	public StrategyData getData() {
		return this.strategyData;
	}

	/**
	 * Returns some unspecified, implementation defined integer
	 *
	 * @return Some integer
	 */
	public int getFlags() {
		return this.flags;
	}

	/**
	 * Return the id of the clan.
	 *
	 * @return Clan id.
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Returns the name of the clan
	 *
	 * @return Name of the clan
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns a mutable list of the amount of all resources. The index for a
	 * resource is obtained by {@link Resource#getIndex()}
	 *
	 * @return List of resources.
	 */
	public List<Double> getResources() {
		return this.resources;
	}

	/**
	 * Returns a mutable list of the production of all resources. The index for a
	 * resource is obtained by {@link Resource#getIndex()}
	 *
	 * @return Production of resources.
	 */
	public List<Double> getResourceStats() {
		return this.resourceStats;
	}

	/**
	 * Returns the defenselevel of the soldiers of the clan
	 *
	 * @return Defenselevel
	 */
	public int getSoldiersDefenseLevel() {
		return this.soldiersDefenseLevel;
	}

	/**
	 * Returns the defense strength of the soldiers of the clan
	 *
	 * @return Defensestrength
	 */
	public double getSoldiersDefenseStrength() {
		return this.soldiersDefenseStrength;
	}

	/**
	 * Returns the level of the soldiers of the clan
	 *
	 * @return Level
	 */
	public int getSoldiersLevel() {
		return this.soldiersLevel;
	}

	/**
	 * Returns the offensivelevel of the soldiers of the clan
	 *
	 * @return Offensivelevel
	 */
	public int getSoldiersOffenseLevel() {
		return this.soldiersOffenseLevel;
	}

	/**
	 * Returns the offense strength of the soldiers of the clan
	 *
	 * @return Offensestrength
	 */
	public double getSoldiersOffenseStrength() {
		return this.soldiersOffenseStrength;
	}

	/**
	 * Returns the strength of the soldiers of the clan.
	 *
	 * @return Strength
	 */
	public double getSoldiersStrength() {
		return this.soldiersStrength;
	}

	/**
	 * Returns the Strategy this clan uses
	 *
	 * @return The strategy of the clan.
	 */
	public Strategy getStrategy() {
		return this.strategy;
	}

	/**
	 * Initialises the clan.
	 *
	 * @param strategies An array of all strategies available.
	 */
	void init(final StrategyProvider[] strategies) {
		if (strategies == null) {
			throw new IllegalArgumentException("strategies==null");
		}
		final List<Double> list = new ArrayList<>();
		for (var j = 0; j < Resource.values().length; j++) {
			list.add(0.0);
		}
		this.setResources(new ArrayList<>(list));
		this.setResourceStats(new ArrayList<>());
		for (@SuppressWarnings("unused")
		final Resource unusedVariable : Resource.values()) {
			this.getResourceStats().add(0.0);
		}
		final var givenPlayType = this.flags;
		if (strategies[givenPlayType] == null) {
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
	 * Set the coins.
	 *
	 * @param coins If {@code coins} is smaller than 0, the new amount of coins is
	 *              0.
	 */
	public void setCoins(final double coins) {
		this.coins = coins < 0 ? 0 : coins;
	}

	/**
	 * Set the color of the clan. This method may only be called once.
	 *
	 * @param color May not be null
	 */
	void setColor(final Color color) {
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
	void setFlags(final int flags) {
		this.flags = flags;
	}

	/**
	 * Change the id of the clan. May only be called once.
	 *
	 * @param id Has to be between zero and {@code Byte#MAX_VALUE}.
	 */
	void setId(final int id) {
		if (this.id != -1) {
			throw new UnsupportedOperationException("Can't change id of clan!");
		} else if ((id < 0) || (id > Byte.MAX_VALUE)) {
			throw new IllegalArgumentException("Out of bounds!");
		}
		this.id = id;
	}

	/**
	 * Set the name. May only be called once.
	 *
	 * @param name May not be null
	 */
	void setName(final String name) {
		if (this.name != null) {
			throw new UnsupportedOperationException("Can't change name of clan!");
		} else if (name == null) {
			throw new IllegalArgumentException("name == null");
		}
		this.name = name;
	}

	void setResources(final List<Double> resources) {
		this.resources = resources;
	}

	void setResourceStats(final List<Double> resourceStats) {
		this.resourceStats = resourceStats;
	}

	void setSoldiersDefenseLevel(final int soldiersDefenseLevel) {
		this.soldiersDefenseLevel = soldiersDefenseLevel;
	}

	void setSoldiersDefenseStrength(final double soldiersDefenseStrength) {
		this.soldiersDefenseStrength = soldiersDefenseStrength;
	}

	void setSoldiersLevel(final int soldiersLevel) {
		this.soldiersLevel = soldiersLevel;
	}

	void setSoldiersOffenseLevel(final int soldiersOffenseLevel) {
		this.soldiersOffenseLevel = soldiersOffenseLevel;
	}

	void setSoldiersOffenseStrength(final double soldiersOffenseStrength) {
		this.soldiersOffenseStrength = soldiersOffenseStrength;
	}

	void setSoldiersStrength(final double soldiersStrength) {
		this.soldiersStrength = soldiersStrength;
	}

	void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	void setStrategyData(StrategyData strategyData) {
		this.strategyData = strategyData;
	}

	void update(final int currentRound) {
		if (this.strategyData != null) {
			this.strategyData.update(currentRound);
		}
	}
}
