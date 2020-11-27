package org.jel.game.data.strategy;

import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
public interface StrategyData {
	default void save(OutputStream out) throws IOException {

	}

	void update(int currentRound);
}
