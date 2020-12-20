package org.jel.game.data.builtin;

import org.jel.game.data.strategy.Strategy;
import org.jel.game.data.strategy.StrategyProvider;

public final class OffensiveStrategyProvider implements StrategyProvider {

	@Override
	public Strategy buildStrategy() {
		return new OffensiveStrategyImpl();
	}

	@Override
	public int getId() {
		return 2;
	}

	@Override
	public String getName() {
		return "offensive";
	}

}
