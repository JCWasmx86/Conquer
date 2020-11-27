package org.jel.game.data;

import java.util.function.Consumer;

import org.jel.game.data.strategy.StrategyObject;

public interface PlayerGiftCallback {

	boolean acceptGift(Clan source, Clan destination, Gift gift, double oldValue, Consumer<Double> newValue,
			StrategyObject strategyObject);

}
