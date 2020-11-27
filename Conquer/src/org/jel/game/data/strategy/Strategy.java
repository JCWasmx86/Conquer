package org.jel.game.data.strategy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.DoubleConsumer;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.data.Gift;
import org.jel.game.utils.Graph;

public interface Strategy {
	boolean acceptGift(Clan sourceClan, Clan destinationClan, Gift gift, double oldValue, DoubleConsumer newValue,
			StrategyObject strategyObject);

	void applyStrategy(Clan clan, byte clanId, Graph<City> cities, StrategyObject obj);

	StrategyData getData();

	default void init(InputStream in) throws IOException {

	}

	default StrategyData resume(StrategyObject strategyObject, byte[] bytes, boolean hasStrategyData,
			byte[] dataBytes) {
		return null;
	}

	default void save(OutputStream out) throws IOException {

	}
}
