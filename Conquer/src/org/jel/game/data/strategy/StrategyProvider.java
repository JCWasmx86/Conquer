package org.jel.game.data.strategy;

public interface StrategyProvider {
	Strategy buildStrategy();

	int getId();

	String getName();
}
