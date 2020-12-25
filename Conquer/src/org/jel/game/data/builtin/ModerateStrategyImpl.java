package org.jel.game.data.builtin;

import java.util.function.DoubleConsumer;

import org.jel.game.data.Gift;
import org.jel.game.data.ICity;
import org.jel.game.data.IClan;
import org.jel.game.data.strategy.Strategy;
import org.jel.game.data.strategy.StrategyData;
import org.jel.game.data.strategy.StrategyObject;
import org.jel.game.utils.Graph;

public final class ModerateStrategyImpl implements Strategy {
	private static final double BIG_RELATIONSHIP_INCREASE_FACTOR = 7.3;
	private static final int SMALL_RELATIONSHIP_INCREASE_FACTOR = 5;

	@Override
	public boolean acceptGift(final IClan sourceClan, final IClan destinationClan, final Gift gift,
			final double oldValue, final DoubleConsumer newValue, final StrategyObject strategyObject) {
		if (Math.random() < (1 - (strategyObject.getRelationship(sourceClan, destinationClan) * 0.1))) {
			return false;
		}
		final var pref = BuiltinShared.sum(gift);
		final var own = BuiltinShared.sum(destinationClan);
		if (pref > own) {
			newValue.accept(oldValue + ((pref / own) * ModerateStrategyImpl.SMALL_RELATIONSHIP_INCREASE_FACTOR));
		} else {
			newValue.accept(oldValue + ((pref / own) * ModerateStrategyImpl.BIG_RELATIONSHIP_INCREASE_FACTOR));
		}
		return true;
	}

	@Override
	public void applyStrategy(final IClan clan, final Graph<ICity> cities, final StrategyObject object) {
		BuiltinShared.moderatePlay(cities, object, clan);
	}

	@Override
	public StrategyData getData() {
		return null;
	}
}
