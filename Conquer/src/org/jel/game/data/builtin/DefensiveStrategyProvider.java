package org.jel.game.data.builtin;

import org.jel.game.data.strategy.Strategy;
import org.jel.game.data.strategy.StrategyProvider;

public final class DefensiveStrategyProvider implements StrategyProvider {

	@Override
	public Strategy buildStrategy() {
		return new DefensiveStrategyImpl();
	}

	@Override
	public byte getId() {
		return 0;
	}

	@Override
	public String getName() {
		return "defensive";
	}

}
