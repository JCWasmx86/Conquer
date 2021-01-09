package org.jel.game.data;

import java.awt.Color;
import java.util.List;

import org.jel.game.InternalUseOnly;
import org.jel.game.data.strategy.Strategy;
import org.jel.game.data.strategy.StrategyData;
import org.jel.game.data.strategy.StrategyProvider;

public interface IClan {

	/**
	 * Get the number of coins this clan has
	 *
	 * @return Number of coins.
	 */
	double getCoins();

	/**
	 * Returns the color associated with this clan
	 *
	 * @return Clan color
	 */
	Color getColor();

	/**
	 * Returns optional data for the strategy.
	 *
	 * @return Optional Data. May be null
	 */
	StrategyData getData();

	/**
	 * Returns some unspecified, implementation defined integer
	 *
	 * @return Some integer
	 */
	int getFlags();

	/**
	 * Return the id of the clan.
	 *
	 * @return Clan id.
	 */
	int getId();

	/**
	 * Returns the name of the clan
	 *
	 * @return Name of the clan
	 */
	String getName();

	/**
	 * Returns a mutable list of the amount of all resources. The index for a
	 * resource is obtained by {@link Resource#getIndex()}
	 *
	 * @return List of resources.
	 */
	List<Double> getResources();

	/**
	 * Returns a mutable list of the production of all resources. The index for a
	 * resource is obtained by {@link Resource#getIndex()}
	 *
	 * @return Production of resources.
	 */
	List<Double> getResourceStats();

	/**
	 * Returns the defenselevel of the soldiers of the clan
	 *
	 * @return Defenselevel
	 */
	int getSoldiersDefenseLevel();

	/**
	 * Returns the defense strength of the soldiers of the clan
	 *
	 * @return Defensestrength
	 */
	double getSoldiersDefenseStrength();

	/**
	 * Returns the level of the soldiers of the clan
	 *
	 * @return Level
	 */
	int getSoldiersLevel();

	/**
	 * Returns the offensivelevel of the soldiers of the clan
	 *
	 * @return Offensivelevel
	 */
	int getSoldiersOffenseLevel();

	/**
	 * Returns the offense strength of the soldiers of the clan
	 *
	 * @return Offensestrength
	 */
	double getSoldiersOffenseStrength();

	/**
	 * Returns the strength of the soldiers of the clan.
	 *
	 * @return Strength
	 */
	double getSoldiersStrength();

	/**
	 * Returns the Strategy this clan uses
	 *
	 * @return The strategy of the clan.
	 */
	Strategy getStrategy();

	/**
	 * Initialises the clan.
	 *
	 * @param strategies An array of all strategies available.
	 */
	void init(StrategyProvider[] strategies, Version version);

	/**
	 * Returns whether the clan is played by the human player or not
	 *
	 * @return {@code true} if the clan is played by the human.
	 */
	boolean isPlayerClan();

	/**
	 * Set the coins.
	 *
	 * @param coins If {@code coins} is smaller than 0, the new amount of coins is
	 *              0.
	 */
	void setCoins(double coins);

	/**
	 * Set the color of the clan. This method may only be called once.
	 *
	 * @param color May not be null
	 */
	void setColor(Color color);

	/**
	 * Set the flags of a clan.
	 *
	 * @param flags Some unspecified value.
	 */
	void setFlags(int flags);

	/**
	 * Change the id of the clan. May only be called once.
	 *
	 * @param id Has to be positive or zero.
	 */
	void setId(int id);

	/**
	 * Set the name. May only be called once.
	 *
	 * @param name May not be null
	 */
	void setName(String name);

	void setResources(List<Double> resources);

	void setResourceStats(List<Double> resourceStats);

	boolean upgradeSoldiersDefense();

	boolean upgradeSoldiers();

	boolean upgradeSoldiersOffense();

	void setStrategy(Strategy strategy);

	void setStrategyData(StrategyData strategyData);

	void update(int currentRound);

	default void upgradeFully(SoldierUpgrade upgradeType) {
		switch (upgradeType) {
		case BOTH:
			upgradeSoldiersFully();
			break;
		case DEFENSE:
			upgradeSoldiersDefenseFully();
			break;
		case OFFENSE:
			upgradeSoldiersOffenseFully();
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + upgradeType);
		}
	}

	@InternalUseOnly
	default void upgradeSoldiersDefenseFully() {
		var b = true;
		while (b) {
			b = this.upgradeSoldiersDefense();
		}
	}

	@InternalUseOnly
	default void upgradeSoldiersFully() {
		var b = true;
		while (b) {
			b = this.upgradeSoldiers();
		}
	}

	@InternalUseOnly
	default void upgradeSoldiersOffenseFully() {
		var b = true;
		while (b) {
			b = this.upgradeSoldiersOffense();
		}
	}

	default double upgradeCosts(SoldierUpgrade upgrade, int x) {
		switch (upgrade) {
		case BOTH:
			return upgradeCostsForSoldiers(x);
		case DEFENSE:
			return upgradeCostsForDefense(x);
		case OFFENSE:
			return upgradeCostsForOffense(x);
		default:
			throw new IllegalArgumentException("Unexpected value: " + upgrade);
		}
	}

	default double upgradeCostsForOffense(int x) {
		// Duplicated, as it would otherwise depend on another method.
		if (x == 0) {
			return 40;
		}
		return Math.sqrt(Math.pow(Math.log(x), 3)) * x * x * Math.sqrt(x) * Math.log(x);
	}

	default double upgradeCostsForDefense(int x) {
		if (x == 0) {
			return 40;
		}
		return Math.sqrt(Math.pow(Math.log(x), 3)) * x * x * Math.sqrt(x) * Math.log(x);
	}

	@Deprecated
	default int maxLevelsAddOffenseDefenseUpgrade(final int x, final double coins) {
		return maxLevelsAddDefenseUpgrade(x, coins);
	}

	@InternalUseOnly
	@Deprecated
	default double upgradeCostsForOffenseAndDefense(final int x) {
		if (x == 0) {
			return 40;
		}
		return Math.sqrt(Math.pow(Math.log(x), 3)) * x * x * Math.sqrt(x) * Math.log(x);
	}

	@InternalUseOnly
	@Deprecated
	default double upgradeCostsForSoldiers(final int x) {
		return upgradeCostsForOffenseAndDefense(x) * 10;
	}

	@InternalUseOnly
	@Deprecated
	default int maxLevelsAddDefenseUpgrade(final int currLevel, double coins) {
		var cnt = 0;
		while (true) {
			final var costs = upgradeCosts(SoldierUpgrade.DEFENSE, currLevel + cnt);
			if (costs > coins) {
				break;
			}
			coins -= costs;
			cnt++;
			if ((cnt + currLevel) == Shared.MAX_LEVEL) {
				return cnt;
			}
		}
		return cnt;
	}

	@InternalUseOnly
	@Deprecated
	default int maxLevelsAddOffenseUpgrade(final int currLevel, double coins) {
		var cnt = 0;
		while (true) {
			final var costs = upgradeCosts(SoldierUpgrade.OFFENSE, currLevel + cnt);
			if (costs > coins) {
				break;
			}
			coins -= costs;
			cnt++;
			if ((cnt + currLevel) == Shared.MAX_LEVEL) {
				return cnt;
			}
		}
		return cnt;
	}

	default int maxLevelsAddResourcesUpgrade(final int currLevel, double coins) {
		var cnt = 0;
		while (true) {
			final var costs = costs(currLevel + cnt);
			if (costs > coins) {
				break;
			}
			coins -= costs;
			cnt++;
			if ((cnt + currLevel) == Shared.MAX_LEVEL) {
				return cnt;
			}
		}
		return cnt;
	}

	@Deprecated
	@InternalUseOnly
	default int maxLevelsAddSoldiersUpgrade(final int currLevel, double coins) {
		var cnt = 0;
		while (true) {
			final var costs = upgradeCosts(SoldierUpgrade.BOTH, currLevel + cnt);
			if (costs > coins) {
				break;
			}
			coins -= costs;
			cnt++;
			if ((cnt + currLevel) == Shared.MAX_LEVEL) {
				return cnt;
			}
		}
		return cnt;
	}

	default double newPower(SoldierUpgrade upgrade, int x) {
		switch (upgrade) {
		case BOTH:
			return newPowerForSoldiers(x);
		case DEFENSE:
			return newPowerOfSoldiersForDefense(x);
		case OFFENSE:
			return newPowerOfSoldiersForOffense(x);
		default:
			throw new IllegalArgumentException("Unexpected value: " + upgrade);
		}
	}

	// All of those are repeated, so you can override only one without effecting
	// (probably) others.
	@InternalUseOnly
	@Deprecated
	default double newPowerOfSoldiersForDefense(int level) {
		return Math.sqrt(Math.log(level) + (4 * level)) / 50;
	}

	@InternalUseOnly
	@Deprecated
	default double newPowerOfSoldiersForOffense(int level) {
		return Math.sqrt(Math.log(level) + (4 * level)) / 50;
	}

	@InternalUseOnly
	@Deprecated
	default double newPowerForSoldiers(final int level) {
		return Math.sqrt(Math.log(level) + (4 * level)) / 100;
	}

	@InternalUseOnly
	@Deprecated
	default double newPowerOfSoldiersForOffenseAndDefense(final int level) {
		return Math.sqrt(Math.log(level) + (4 * level)) / 50;
	}

	default double newPowerOfUpdate(final int level, final double oldValue) {
		return (1.01 * oldValue) + (level / (double) Shared.MAX_LEVEL);
	}

	default double costs(final int level) {
		var ret = Math.pow(level, Math.E);
		if (level != 0) {
			ret = Math.pow(ret, 1.0d / 8.0d) * Math.pow(level, 1.0d / 3.0d);
		}
		ret = Math.pow(ret, (Math.PI / Math.E) + Math.pow(level, 1.0d / 40.0d));
		ret *= Math.toRadians(level);
		ret *= Math.toDegrees(level / 360.0d) / Math.PI;
		return ret;
	}

	default int maxLevels(SoldierUpgrade upgrade, int level, double coins) {
		switch (upgrade) {
		case BOTH:
			return maxLevelsAddSoldiersUpgrade(level, coins);
		case DEFENSE:
			return maxLevelsAddDefenseUpgrade(level, coins);
		case OFFENSE:
			return maxLevelsAddOffenseUpgrade(level, coins);
		default:
			throw new IllegalArgumentException("Unexpected value: " + upgrade);
		}
	}
}