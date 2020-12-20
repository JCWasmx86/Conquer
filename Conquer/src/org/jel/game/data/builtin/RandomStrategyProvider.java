package org.jel.game.data.builtin;

import org.jel.game.data.Shared;
import org.jel.game.data.strategy.Strategy;
import org.jel.game.data.strategy.StrategyProvider;

public final class RandomStrategyProvider implements StrategyProvider {

	@Override
	public Strategy buildStrategy() {
		return switch (Shared.getRandomNumber(3)) {
		case 0:
			yield new DefensiveStrategyImpl();
		case 2:
			yield new OffensiveStrategyImpl();
		case 1:
		default:
			yield new ModerateStrategyImpl();
		};
	}

	@Override
	public int getId() {
		return 3;
	}

	@Override
	public String getName() {
		return "random";
	}

}
