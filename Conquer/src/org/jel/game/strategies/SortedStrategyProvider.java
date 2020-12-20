package org.jel.game.strategies;

import org.jel.game.data.strategy.Strategy;
import org.jel.game.data.strategy.StrategyProvider;

public final class SortedStrategyProvider implements StrategyProvider {

	@Override
	public Strategy buildStrategy() {
		return new SortedStrategyImpl();
	}

	@Override
	public int getId() {
		return (byte) 1000;
	}

	@Override
	public String getName() {
		return "Sorted";
	}

}
