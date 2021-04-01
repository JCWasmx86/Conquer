package conquer.data.builtin;

import conquer.data.strategy.Strategy;
import conquer.data.strategy.StrategyProvider;

public final class DefensiveStrategyProvider implements StrategyProvider {

	@Override
	public Strategy buildStrategy() {
		return new DefensiveStrategyImpl();
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public String getName() {
		return "defensive";
	}

}
