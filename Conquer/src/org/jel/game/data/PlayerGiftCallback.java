package org.jel.game.data;

import java.util.function.DoubleConsumer;

import org.jel.game.data.strategy.StrategyObject;

public interface PlayerGiftCallback {

	boolean acceptGift(Clan source, Clan destination, Gift gift, double oldValue, DoubleConsumer newValue,
			StrategyObject strategyObject);

}
