package org.jel.game.data.builtin;

import java.util.Random;

import org.jel.game.data.Shared;
import org.jel.game.data.strategy.StrategyData;

public final class OffensiveStrategyData implements StrategyData {
	private static final int MAX_ROUND_NUMBER = 20;
	private int counter;
	private OffensiveStrategy action;
	private final Random random;

	public OffensiveStrategyData() {
		this.random = new Random(System.nanoTime());
		this.counter = Math.abs(this.random.nextInt(OffensiveStrategyData.MAX_ROUND_NUMBER)) + 1;
		this.action = OffensiveStrategy.EXPAND;
	}

	public OffensiveStrategy getAction() {
		return this.action;
	}

	@Override
	public void update(final int currentRound) {
		this.counter--;
		if (this.counter == 0) {
			if (this.action == OffensiveStrategy.EXPAND) {
				this.action = OffensiveStrategy.UPGRADE;
				this.counter = Math.abs(this.random.nextInt(OffensiveStrategyData.MAX_ROUND_NUMBER)) + 1;
				Shared.logLevel1(
						"Offensive-strategy: " + OffensiveStrategy.UPGRADE + " for " + this.counter + " rounds");

			} else {
				this.action = OffensiveStrategy.EXPAND;
				this.counter = Math.abs(this.random.nextInt(OffensiveStrategyData.MAX_ROUND_NUMBER)) + 1;
				Shared.logLevel1(
						"Offensive-strategy: " + OffensiveStrategy.EXPAND + " for " + this.counter + " rounds");
			}
		}
	}
}
