package conquer.data;

import conquer.InternalUseOnly;
import conquer.data.strategy.Strategy;
import conquer.data.strategy.StrategyData;
import conquer.data.strategy.StrategyProvider;

import java.awt.Color;
import java.util.List;

public interface IClan {

	/**
	 * Get the number of coins this clan has
	 *
	 * @return Number of coins.
	 */
	double getCoins();

	/**
	 * Set the coins.
	 *
	 * @param coins If {@code coins} is smaller than 0, the new amount of coins is
	 *              0.
	 */
	void setCoins(double coins);

	/**
	 * Returns the color associated with this clan
	 *
	 * @return Clan color
	 */
	Color getColor();

	/**
	 * Set the color of the clan. This method may only be called once.
	 *
	 * @param color May not be null
	 */
	void setColor(Color color);

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
	 * Set the flags of a clan.
	 *
	 * @param flags Some unspecified value.
	 */
	void setFlags(int flags);

	/**
	 * Return the id of the clan.
	 *
	 * @return Clan id.
	 */
	int getId();

	/**
	 * Change the id of the clan. May only be called once.
	 *
	 * @param id Has to be positive or zero.
	 */
	void setId(int id);

	/**
	 * Returns the name of the clan
	 *
	 * @return Name of the clan
	 */
	String getName();

	/**
	 * Set the name. May only be called once.
	 *
	 * @param name May not be null
	 */
	void setName(String name);

	/**
	 * Returns a mutable list of the amount of all resources. The index for a
	 * resource is obtained by {@link Resource#getIndex()}
	 *
	 * @return List of resources.
	 */
	List<Double> getResources();

	/**
	 * Set all resources. May only be called while initializing the clan.
	 *
	 * @param resources May not be {@code null} or have the wrong length, otherwise
	 *                  an {@code IllegalArgumentException} shall be thrown.
	 */
	void setResources(List<Double> resources);

	/**
	 * Returns a mutable list of the production of all resources. The index for a
	 * resource is obtained by {@link Resource#getIndex()}
	 *
	 * @return Production of resources.
	 */
	List<Double> getResourceStats();

	/**
	 * Set all resource stats. May only be called by the corresponding
	 * {@code ConquerInfo}-object
	 *
	 * @param resourceStats May not be {@code null} or have the wrong length, otherwise
	 *                      an {@code IllegalArgumentException} shall be thrown.
	 */
	void setResourceStats(List<Double> resourceStats);

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
	 * Set the strategy. May only be called once and shouldn't be treated as public.
	 *
	 * @param strategy May not be null
	 */
	void setStrategy(Strategy strategy);

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
	 * Upgrade the defense strength of the soldiers.
	 *
	 * @return {@code true}, if enough coins were available and the level wasn't
	 * equals to the maximum level, {@code false} otherwise.
	 */
	boolean upgradeSoldiersDefense();

	/**
	 * Upgrade the strength of the soldiers.
	 *
	 * @return {@code true}, if enough coins were available and the level wasn't
	 * equals to the maximum level, {@code false} otherwise.
	 */
	boolean upgradeSoldiers();

	/**
	 * Upgrade the offensive strength of the soldiers.
	 *
	 * @return {@code true}, if enough coins were available and the level wasn't
	 * equals to the maximum level, {@code false} otherwise.
	 */
	boolean upgradeSoldiersOffense();

	/**
	 * Set the strategy data. Shouldn't be treated as public.
	 *
	 * @param strategyData May be null
	 */
	void setStrategyData(StrategyData strategyData);

	/**
	 * Called every round. Should only be called by the corresponding
	 * {@code ConquerInfo}-object
	 *
	 * @param currentRound Current round.
	 */
	void update(int currentRound);

	default void upgradeFully(final SoldierUpgrade upgradeType) {
		switch (upgradeType) {
			case BOTH -> this.upgradeSoldiersFully();
			case DEFENSE -> this.upgradeSoldiersDefenseFully();
			case OFFENSE -> this.upgradeSoldiersOffenseFully();
			default -> throw new IllegalArgumentException("Unexpected value: " + upgradeType);
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

	/**
	 * Get the costs for upgrading {@code upgrade} Replaces
	 * {@link IClan#upgradeCostsForDefense(int)},{@link IClan#upgradeCostsForSoldiers(int)}
	 * and {@link IClan#upgradeCostsForOffense(int)}
	 *
	 * @param upgrade The upgrade. May not be {@code null}, otherwise an
	 *                {@code IllegalArgumentException} will be thrown.
	 * @param x       Current level.
	 * @return The costs for upgrading {@code upgrade}.
	 */
	default double upgradeCosts(final SoldierUpgrade upgrade, final int x) {
		return switch (upgrade) {
			case BOTH -> this.upgradeCostsForSoldiers(x);
			case DEFENSE -> this.upgradeCostsForDefense(x);
			case OFFENSE -> this.upgradeCostsForOffense(x);
		};
	}

	/**
	 * Deprecated. But should still be overridden in case an implementation wants to
	 * provide another calculation.
	 *
	 * @param x Current level
	 * @return Costs for upgrading the offense strength of the clan.
	 */
	@Deprecated
	default double upgradeCostsForOffense(final int x) {
		return this.upgradeCosts0(x);
	}

	private double upgradeCosts0(final int x) {
		if (x == 0) {
			return 40;
		}
		return Math.sqrt(Math.pow(Math.log(x), 3)) * x * x * Math.sqrt(x) * Math.log(x);
	}

	/**
	 * Deprecated. But should still be overridden in case an implementation wants to
	 * provide another calculation.
	 *
	 * @param x Current level
	 * @return Costs for upgrading the defense strength of the clan.
	 */
	@Deprecated
	default double upgradeCostsForDefense(final int x) {
		return this.upgradeCosts0(x);
	}

	/**
	 * Deprecated. But should still be overridden in case an implementation wants to
	 * provide another calculation.
	 *
	 * @param currLevel Current level
	 * @param coins     Maximum coins to give.
	 * @return Returns the maximum number of upgrades, until {@code coins} is not
	 * enough anymore.
	 */
	@Deprecated
	default int maxLevelsAddOffenseDefenseUpgrade(final int currLevel, double coins) {
		return this.maxLevels0(currLevel, coins, SoldierUpgrade.BOTH);
	}

	private int maxLevels0(final int currLevel, double coins, final SoldierUpgrade upgrade) {
		var cnt = 0;
		do {
			final var costs = this.upgradeCosts(upgrade, currLevel + cnt);
			if (costs > coins) {
				break;
			}
			coins -= costs;
			cnt++;
		} while ((cnt + currLevel) != this.getInfo().getMaximumLevel());
		return cnt;
	}

	/**
	 * Deprecated. But should still be overridden in case an implementation wants to
	 * provide another calculation.
	 *
	 * @param x Current level
	 * @return Costs for upgrading the offense/defense strength of the clan.
	 */
	@InternalUseOnly
	@Deprecated
	default double upgradeCostsForOffenseAndDefense(final int x) {
		return this.upgradeCosts0(x);
	}

	/**
	 * Deprecated. But should still be overridden in case an implementation wants to
	 * provide another calculation.
	 *
	 * @param x Current level
	 * @return Costs for upgrading the soldiers strength of the clan.
	 */
	@InternalUseOnly
	@Deprecated
	default double upgradeCostsForSoldiers(final int x) {
		return this.upgradeCostsForOffenseAndDefense(x) * 10;
	}

	/**
	 * Deprecated. But should still be overridden in case an implementation wants to
	 * provide another calculation.
	 *
	 * @param currLevel Current level
	 * @param coins     Maximum coins to give.
	 * @return Returns the maximum number of upgrades, until {@code coins} is not
	 * enough anymore.
	 */
	@InternalUseOnly
	@Deprecated
	default int maxLevelsAddDefenseUpgrade(final int currLevel, double coins) {
		return this.maxLevels0(currLevel, coins, SoldierUpgrade.DEFENSE);
	}

	/**
	 * Deprecated. But should still be overridden in case an implementation wants to
	 * provide another calculation.
	 *
	 * @param currLevel Current level
	 * @param coins     Maximum coins to give.
	 * @return Returns the maximum number of upgrades, until {@code coins} is not
	 * enough anymore.
	 */

	@InternalUseOnly
	@Deprecated
	default int maxLevelsAddOffenseUpgrade(final int currLevel, double coins) {
		return this.maxLevels0(currLevel, coins, SoldierUpgrade.OFFENSE);
	}

	default int maxLevelsAddResourcesUpgrade(final int currLevel, double coins) {
		var cnt = 0;
		do {
			final var costs = this.costs(currLevel + cnt);
			if (costs > coins) {
				break;
			}
			coins -= costs;
			cnt++;
		} while ((cnt + currLevel) != this.getInfo().getMaximumLevel());
		return cnt;
	}

	/**
	 * Deprecated. But should still be overridden in case an implementation wants to
	 * provide another calculation.
	 *
	 * @param currLevel Current level
	 * @param coins     Maximum coins to give.
	 * @return Returns the maximum number of upgrades, until {@code coins} is not
	 * enough anymore.
	 */
	@Deprecated
	@InternalUseOnly
	default int maxLevelsAddSoldiersUpgrade(final int currLevel, double coins) {
		return this.maxLevels0(currLevel, coins, SoldierUpgrade.BOTH);
	}

	/**
	 * Returns the new power for {@code upgrade} for level {@code x}. Replaces the
	 * deprecated
	 * {@link IClan#newPowerForSoldiers(int)},{@link IClan#newPowerOfSoldiersForDefense(int)}
	 * and {@link IClan#newPowerOfSoldiersForOffense(int)}.
	 *
	 * @param upgrade Which upgrade to make
	 * @param x       Current level
	 * @return New power.
	 */
	default double newPower(final SoldierUpgrade upgrade, final int x) {
		return switch (upgrade) {
			case BOTH -> this.newPowerForSoldiers(x);
			case DEFENSE -> this.newPowerOfSoldiersForDefense(x);
			case OFFENSE -> this.newPowerOfSoldiersForOffense(x);
		};
	}

	// All of those are repeated, so you can override only one without effecting
	// (probably) others.

	private double newPower(final int level) {
		return Math.sqrt(Math.log(level) + (4 * level)) / 50;
	}

	/**
	 * Shouldn't be used directly, but can still be overwritten. Replacement for
	 * {@link Shared#newPowerOfSoldiersForOffenseAndDefense(int)}
	 *
	 * @param level
	 * @return
	 */
	@InternalUseOnly
	@Deprecated
	default double newPowerOfSoldiersForDefense(final int level) {
		return this.newPower(level);
	}

	/**
	 * Shouldn't be used directly, but can still be overwritten. Replacement for
	 * {@link Shared#newPowerOfSoldiersForOffenseAndDefense(int)}
	 *
	 * @param level
	 * @return
	 */
	@InternalUseOnly
	@Deprecated
	default double newPowerOfSoldiersForOffense(final int level) {
		return this.newPower(level);
	}

	/**
	 * Shouldn't be used directly, but can still be overwritten. Replacement for
	 * {@link Shared#newPowerForSoldiers(int)}
	 *
	 * @param level
	 * @return
	 */
	@InternalUseOnly
	@Deprecated
	default double newPowerForSoldiers(final int level) {
		return this.newPower(level) / 2;
	}

	/**
	 * Shouldn't be used directly, but can still be overwritten. Replacement for
	 * {@link Shared#newPowerOfSoldiersForOffenseAndDefense(int)}
	 *
	 * @param level
	 * @return
	 */
	@InternalUseOnly
	@Deprecated
	default double newPowerOfSoldiersForOffenseAndDefense(final int level) {
		return this.newPower(level);
	}

	/**
	 * Replacement for {@link Shared#newPowerOfUpdate(int, double)}.
	 *
	 * @param level    Current level
	 * @param oldValue Old value of production.
	 * @return New production rate.
	 */
	default double newPowerOfUpdate(final int level, final double oldValue) {
		return (1.01 * oldValue) + (level / (double) this.getInfo().getMaximumLevel());
	}

	/**
	 * Replacement for {@link Shared#costs(int)}.
	 *
	 * @param level Current level.
	 * @return Costs in coins for upgrading to next level.
	 */
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

	/**
	 * Returns the maximum number of levels to upgrade, until {@code coins} is not
	 * enough, starting from {@code level} for the upgrade {@code upgrade}. Replaces
	 * {@link IClan#maxLevelsAddSoldiersUpgrade(int, double)},{@link IClan#maxLevelsAddDefenseUpgrade}
	 * and {@link IClan#maxLevelsAddOffenseUpgrade(int, double)}
	 *
	 * @param upgrade The upgrade. May not be {@code null}.
	 * @param level   Current level
	 * @param coins   Maximum coins to give.
	 * @return Number of levels.
	 */
	default int maxLevels(final SoldierUpgrade upgrade, final int level, final double coins) {
		return switch (upgrade) {
			case BOTH -> this.maxLevelsAddSoldiersUpgrade(level, coins);
			case DEFENSE -> this.maxLevelsAddDefenseUpgrade(level, coins);
			case OFFENSE -> this.maxLevelsAddOffenseUpgrade(level, coins);
		};
	}

	/**
	 * Return the corrseponding game state. The default implementation throws an
	 * {@code UnsupportedOperationException}.
	 *
	 * @return Corresponding game state.
	 */
	default ConquerInfo getInfo() {
		throw new UnsupportedOperationException("Didn't have access to a matching ConquerInfo");
	}
}