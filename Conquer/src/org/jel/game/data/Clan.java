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
	private int id;
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

	public Clan() {

	}

	public Clan(final int id, final double coins, final String name, final Color color, final List<Double> resources,
			final List<Double> resourceStats, final Strategy pt) {
		if ((id < 0) || (coins < 0) || (name == null) || (color == null) || (resources == null)
				|| (resources.size() != Resource.values().length) || (resourceStats == null)
				|| (resourceStats.size() != (Resource.values().length + 1)) || (pt == null)) {
			throw new IllegalArgumentException("Invalid argument!");
		}
		this.id = id;
		this.coins = coins;
		this.name = name;
		this.color = color;
		this.resources = resources;
		this.resourceStats = resourceStats;
		this.strategy = pt;
	}

	public double getCoins() {
		return this.coins;
	}

	public Color getColor() {
		return this.color;
	}

	public StrategyData getData() {
		return this.strategyData;
	}

	public int getFlags() {
		return this.flags;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public List<Double> getResources() {
		return this.resources;
	}

	public List<Double> getResourceStats() {
		return this.resourceStats;
	}

	public int getSoldiersDefenseLevel() {
		return this.soldiersDefenseLevel;
	}

	public double getSoldiersDefenseStrength() {
		return this.soldiersDefenseStrength;
	}

	public int getSoldiersLevel() {
		return this.soldiersLevel;
	}

	public int getSoldiersOffenseLevel() {
		return this.soldiersOffenseLevel;
	}

	public double getSoldiersOffenseStrength() {
		return this.soldiersOffenseStrength;
	}

	public double getSoldiersStrength() {
		return this.soldiersStrength;
	}

	public Strategy getStrategy() {
		return this.strategy;
	}

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

	public void setCoins(final double coins) {
		this.coins = coins < 0 ? 0 : coins;
	}

	public void setColor(final Color color2) {
		this.color = color2;
	}

	public void setFlags(final int flags) {
		this.flags = flags;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setResources(final List<Double> resources) {
		this.resources = resources;
	}

	public void setResourceStats(final List<Double> resourceStats) {
		this.resourceStats = resourceStats;
	}

	public void setSoldiersDefenseLevel(final int soldiersDefenseLevel) {
		this.soldiersDefenseLevel = soldiersDefenseLevel;
	}

	public void setSoldiersDefenseStrength(final double soldiersDefenseStrength) {
		this.soldiersDefenseStrength = soldiersDefenseStrength;
	}

	public void setSoldiersLevel(final int soldiersLevel) {
		this.soldiersLevel = soldiersLevel;
	}

	public void setSoldiersOffenseLevel(final int soldiersOffenseLevel) {
		this.soldiersOffenseLevel = soldiersOffenseLevel;
	}

	public void setSoldiersOffenseStrength(final double soldiersOffenseStrength) {
		this.soldiersOffenseStrength = soldiersOffenseStrength;
	}

	public void setSoldiersStrength(final double soldiersStrength) {
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
