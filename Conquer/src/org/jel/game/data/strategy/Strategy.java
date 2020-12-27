package org.jel.game.data.strategy;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.DoubleConsumer;

import org.jel.game.data.Gift;
import org.jel.game.data.ICity;
import org.jel.game.data.IClan;
import org.jel.game.utils.Graph;

public interface Strategy {
	boolean acceptGift(IClan sourceClan, IClan destinationClan, Gift gift, double oldValue, DoubleConsumer newValue,
			StrategyObject strategyObject);

	void applyStrategy(IClan clan, Graph<ICity> cities, StrategyObject obj);

	StrategyData getData();

	default StrategyData resume(final StrategyObject strategyObject, final byte[] bytes, final boolean hasStrategyData,
			final byte[] dataBytes) {
		return null;
	}

	default void save(final OutputStream out) throws IOException {

	}
}
