package org.jel.game.strategies;

import org.jel.game.data.strategy.Strategy;
import org.jel.game.data.strategy.StrategyProvider;

public final class SortedStrategyProvider implements StrategyProvider {

	@Override
	public Strategy buildStrategy() {
		return new SortedStrategyImpl();
	}

	@Override
	public byte getId() {
		return (byte) 126;
	}

	@Override
	public String getName() {
		return "Sorted";
	}

}
