package org.jel.game.data;

import java.awt.Color;
import java.util.List;

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

}