package conquer.data;

import conquer.data.strategy.StrategyObject;
import conquer.data.strategy.StrategyProvider;
import conquer.plugins.Plugin;
import conquer.plugins.PluginInterface;

import java.awt.Color;
import java.awt.Image;
import java.util.List;
import java.util.function.Consumer;

/**
 * This interface is the interface to the entire engine. It provides everything
 * to allow writing a good GUI/CLI to it.
 */
public interface ConquerInfo extends StrategyObject, PluginInterface {

	/**
	 * Bind a context to this instance. This method may be called as often as you
	 * want, until {@link #init()} was called. After calling {@link #init()}, the
	 * behavior is undefined
	 *
	 * @param context The context to bind. May not be {@code null}
	 * @throws IllegalArgumentException If the context is {@code null}
	 */
	void addContext(final GlobalContext context);

	/**
	 * Calculate the result of the game. If more than one clan is left (Check by
	 * calling {@link #onlyOneClanAlive()} the behavior is undefined.
	 *
	 * @return The result, if only one clan is left. {@link Result#CPU_WON}, if the
	 * clan of the player is extincted, otherwise {@link Result#PLAYER_WON}.
	 */
	Result calculateResult();

	/**
	 * Returns the number of the current round. It is positive.
	 *
	 * @return The current round.
	 */
	int currentRound();

	/**
	 * Executes all strategies of the CPU. Furthermore, the current round counter is
	 * incremented, resources are produced and used, ...
	 */
	void executeActions();

	/**
	 * Exit this game with the specified result.
	 *
	 * @param calculateResult The result to exit with.
	 * @deprecated Replaced by {@link #exit()}, to improve readability.
	 */
	@Deprecated
	void exit(final Result calculateResult);

	/**
	 * Exit the game.
	 */
	default void exit() {
		this.exit(this.calculateResult());
	}

	/**
	 * Get the background image of the game.
	 *
	 * @return Background image. May be {@code null}.
	 */
	Image getBackground();

	/**
	 * Get a clan, based on the id. This id is obtained by calling
	 * {@link IClan#getId()}.
	 *
	 * @param index An unique id.
	 * @return The clan
	 * @throws {@link IllegalArgumentException} if the id has no matching clan.
	 */
	IClan getClan(final int index);

	/**
	 * Get a list of clan names. This list should be immutable.
	 *
	 * @return An immutable list of clannames. It should be sorted by the ascending
	 * id.
	 */
	List<String> getClanNames();

	/**
	 * A list of all clans. This list should be immutable.
	 *
	 * @return An immutable list of clans. It should be sorted by the ascending id.
	 */
	List<IClan> getClans();

	/**
	 * A list of the amount of coins of all clans. This list should be immutable.
	 *
	 * @return An immutable list of coins of all clans. It should be sorted by the
	 * ascending id.
	 */
	List<Double> getCoins();

	/**
	 * A list of the amount of colors of all clans. This list should be immutable.
	 *
	 * @return An immutable list of colors of all clans. It should be sorted by the
	 * ascending id.
	 */
	List<Color> getColors();

	/**
	 * Returns a list of all extra music, that was e.g. added by plugins.
	 *
	 * @return A list of all music pieces that were registered by e.g. plugins.
	 */
	@Deprecated(forRemoval = true)
	List<String> getExtraMusic();

	/**
	 * Returns the number of players (Human player+CPU players) in this scenario.
	 *
	 * @return Number of players/clans in this scenario
	 */
	int getNumPlayers();

	/**
	 * Returns all registered plugins.
	 *
	 * @return List of registered plugins
	 */
	List<Plugin> getPlugins();

	/**
	 * Returns a saver, that allows to save the game state with the specified name.
	 *
	 * @param name The name to use for saving this game state. May not be
	 *             {@code null} or empty, otherwise an
	 *             {@link IllegalArgumentException} shall be thrown.
	 * @return A {@link ConquerSaver} that allows saving the game state.
	 */
	ConquerSaver getSaver(final String name);

	/**
	 * Returns the version of the engine.
	 *
	 * @return Version of the engine.
	 */
	Version getVersion();

	/**
	 * Initializes everything.
	 */
	void init();

	/**
	 * Returns whether a clan is extincted (==No cities left)
	 *
	 * @param clan The clan. May not be {@code null}, otherwise an
	 *             {@link IllegalArgumentException} shall be thrown.
	 * @return {@code true} if the clan is dead, {@code false} otherwise.
	 */
	boolean isDead(final IClan clan);

	/**
	 * Returns whether the human player can play. This method can be quite redundant
	 * in a single thread, but may be useful in multithreaded scenarios.
	 *
	 * @return {@code true} if it is the players' turn, {@code false} otherwise.
	 */
	boolean isPlayersTurn();

	@Deprecated
	void setPlayersTurn(boolean b);

	/**
	 * Calculates the maximum number of soldiers a clan could recruit.
	 *
	 * @param clan  The clan. May not be {@code null}, otherwise an
	 *              {@link IllegalArgumentException} shall be thrown.
	 * @param limit The absolute limit,may not be negative, otherwise an
	 *              {@link IllegalArgumentException} shall be thrown.
	 * @return
	 */
	long maximumNumberOfSoldiersToRecruit(final IClan clan, final long limit);

	/**
	 * Returns whether only one clan is alive,
	 *
	 * @return {@code true} if only one clan is left, otherwise {@code false}.
	 */
	default boolean onlyOneClanAlive() {
		return StreamUtils.getCitiesAsStream(this.getCities()).map(ICity::getClan).distinct().count() == 1;
	}

	/**
	 * Sets an error handler that is called for every uncaught exception. May be
	 * ignored.
	 *
	 * @param onError A consumer that will be applied to every uncaught exception.
	 */
	default void setErrorHandler(final Consumer<Throwable> onError) {
		//Empty to avoid breaking the interface
	}

	/**
	 * Set a callback function that is called as soon as the player is gifted by
	 * another clan.
	 *
	 * @param giftCallback The callback. May not be {@code null}.
	 */
	void setPlayerGiftCallback(PlayerGiftCallback giftCallback);

	/**
	 * Upgrade the defense of the given city, until the maximum level is reached or
	 * not enough coins are available.
	 *
	 * @param city The city to upgrade. May not be {@code null}.
	 */
	void upgradeDefenseFully(final ICity city);

	/**
	 * Upgrade the resource production of the given city, until the maximum level is
	 * reached or not enough coins are available.
	 *
	 * @param resources The resource to upgrade. May not be {@code null}.
	 * @param city      The city to upgrade. May not be {@code null}.
	 */
	void upgradeResourceFully(final Resource resources, final ICity city);

	/**
	 * Returns the clan of the player.
	 *
	 * @return Clan of the player.
	 */
	IClan getPlayerClan();

	/**
	 * Return the usage and production of the resources. The default implementation
	 * is here as a replacement for e.g. {@link Shared#STONE_PER_PERSON_PER_ROUND}.
	 *
	 * @return An object describing the production and usage of all resources.
	 */
	@SuppressWarnings("deprecation")
	default ResourceUsage getResourceUsage() {
		return new ResourceUsage(Shared.getDataValues(), Shared.COINS_PER_PERSON_PER_ROUND);
	}

	/**
	 * Returns the usage and production of the resources for the given clan.
	 *
	 * @param clan The clan.
	 * @return ResourceUsage for the given clan.
	 */
	default ResourceUsage getResourceUsage(final IClan clan) {
		return this.getResourceUsage();
	}

	/**
	 * Return an object describing the costs for soldiers. The default
	 * implementation is here as a replacement for e.g.
	 * {@link Shared#COINS_PER_MOVE_OF_SOLDIER}.
	 *
	 * @return An object describing the costs for moving/recruiting soldiers.
	 */
	@SuppressWarnings("deprecation")
	default SoldierCosts getSoldierCosts() {
		return new SoldierCosts(Shared.COINS_PER_MOVE_OF_SOLDIER, Shared.COINS_PER_MOVE_OF_SOLDIER_BASE,
				Shared.COINS_PER_SOLDIER_INITIAL, Shared.COINS_PER_SOLDIER_PER_ROUND, Shared.IRON_PER_SOLDIER_INITIAL,
				Shared.STONE_PER_SOLDIER_INITIAL, Shared.WOOD_PER_SOLDIER_INITIAL);
	}

	/**
	 * Return an object describing the costs for soldiers for this clan.
	 *
	 * @param clan The costs for this clan.
	 * @return SoldierCosts object
	 */
	default SoldierCosts getSoldierCosts(final IClan clan) {
		return this.getSoldierCosts();
	}

	/**
	 * Gives the maximum level for resource production, soldier levels and so on.
	 * The default implementation is here as a replacement for
	 * {@link Shared#MAX_LEVEL}.
	 *
	 * @return Maximum level.
	 */
	@SuppressWarnings("deprecation")
	default int getMaximumLevel() {
		return Shared.MAX_LEVEL;
	}

	/**
	 * Returns a list of required plugins to function.
	 *
	 * @return List of required plugins.
	 */
	default List<Class<Plugin>> requiredPlugins() {
		return List.of();
	}

	/**
	 * Returns a list of required strategyproviders to function.
	 *
	 * @return List of required strategyproviders.
	 */
	default List<Class<StrategyProvider>> requiredStrategyProviders() {
		return List.of();
	}
}
