package org.jel.game.data.strategy;

import org.jel.game.data.Version;

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
	 * @return {@code true} if compatible, {@code false} otherwise.
	 */
	default boolean compatibleTo(final Version version) {
		return true;
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
