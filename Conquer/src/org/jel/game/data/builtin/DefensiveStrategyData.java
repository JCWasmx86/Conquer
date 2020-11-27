package org.jel.game.data.builtin;

import java.util.Random;

import org.jel.game.data.Shared;
import org.jel.game.data.strategy.StrategyData;

public final class DefensiveStrategyData implements StrategyData {
	private int counter;
	private final Random random;
	private DefensiveStrategy strategy;

	public DefensiveStrategyData() {
		this.random = new Random(System.nanoTime());
		this.counter = Math.abs(this.random.nextInt(20)) + 1;
		this.strategy = DefensiveStrategy.values()[Math.abs(this.random.nextInt(DefensiveStrategy.values().length))];
	}

	public DefensiveStrategy getStrategy() {
		return this.strategy;
	}

	@Override
	public void update(final int currentRound) {
		this.counter--;
		if (this.counter == 0) {
			switch (this.strategy) {
			case EXPAND:
				this.strategy = DefensiveStrategy.FORTIFYANDUPGRADE;
				this.counter = Math.abs(this.random.nextInt(40)) + 1;
				Shared.logLevel1("Defensive-Strategy: " + DefensiveStrategy.FORTIFYANDUPGRADE + " for " + this.counter
						+ " rounds");
				break;
			case FORTIFYANDUPGRADE:
				this.strategy = DefensiveStrategy.RECRUIT;
				this.counter = Math.abs(this.random.nextInt(30)) + 1;
				Shared.logLevel1(
						"Defensive-Strategy: " + DefensiveStrategy.RECRUIT + " for " + this.counter + " rounds");
				break;
			case RECRUIT:
				this.strategy = DefensiveStrategy.EXPAND;
				this.counter = Math.abs(this.random.nextInt(5)) + 1;
				Shared.logLevel1(
						"Defensive-Strategy: " + DefensiveStrategy.EXPAND + " for " + this.counter + " rounds");
				break;
			default:
				break;

			}
		}
	}
}
