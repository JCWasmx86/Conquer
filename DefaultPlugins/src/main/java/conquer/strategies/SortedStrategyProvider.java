package conquer.strategies;

import conquer.data.strategy.Strategy;
import conquer.data.strategy.StrategyProvider;

public final class SortedStrategyProvider implements StrategyProvider {

	@Override
	public Strategy buildStrategy() {
		return new SortedStrategyImpl();
	}

	@Override
	public int getId() {
		return 1000;
	}

	@Override
	public String getName() {
		return "Sorted";
	}

}
