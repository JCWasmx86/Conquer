package org.jel.game.data.strategy;

public interface StrategyProvider {
	Strategy buildStrategy();

	byte getId();

	String getName();
}
