package org.jel.game.data.builtin;

import org.jel.game.data.strategy.Strategy;
import org.jel.game.data.strategy.StrategyProvider;

public final class ModerateStrategyProvider implements StrategyProvider {

	@Override
	public Strategy buildStrategy() {
		return new ModerateStrategyImpl();
	}

	@Override
	public int getId() {
		return 1;
	}

	@Override
	public String getName() {
		return "moderate";
	}

}
