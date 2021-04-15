package conquer.data.strategy;

import java.util.List;

import conquer.data.Version;

/**
 * This is a SPI interface for providing a new Strategy.
 */
public interface StrategyProvider {
	/**
	 * Return a new implementation-defined strategy object.
	 *
	 * @return New strategy object.
	 */
	Strategy buildStrategy();

	/**
	 * Returns whether this strategy is compatible to the version of the game
	 * engine.
	 *
	 * @param version Version of the game engine.
	 *
	 * @return {@code true} if compatible, {@code false} otherwise.
	 */
	default boolean compatibleTo(final Version version) {
		return true;
	}

	/**
	 * Returns all strategyproviders that conflict with this.
	 *
	 * @return Conflicting strategyproviders.
	 */
	default List<Class<? extends StrategyProvider>> getConflictingPlugins() {
		return List.of();
	}

	/**
	 * Get all dependencies of this strategyprovider.
	 *
	 * @return All dependencies of this strategyprovider.
	 */
	default List<Class<? extends StrategyProvider>> getDependencies() {
		return List.of();
	}

	/**
	 * Returns some unique identifier.
	 *
	 * @return Unique identifier.
	 */
	int getId();

	/**
	 * Returns a human readable name.
	 *
	 * @return Name.
	 */
	String getName();
}
