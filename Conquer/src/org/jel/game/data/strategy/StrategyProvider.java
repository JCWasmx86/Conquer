package org.jel.game.data.strategy;

import org.jel.game.data.Version;

public interface StrategyProvider {
	Strategy buildStrategy();

	default boolean compatibleTo(final Version version) {
		return true;
	}

	int getId();

	String getName();
}
