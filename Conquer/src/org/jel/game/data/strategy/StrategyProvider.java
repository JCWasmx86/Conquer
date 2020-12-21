package org.jel.game.data.strategy;

import org.jel.game.data.Version;

public interface StrategyProvider {
	Strategy buildStrategy();

	int getId();

	String getName();
	
	default boolean compatibleTo(final Version version) {
		return true;
	}
}
