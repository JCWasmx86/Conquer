package org.jel.game.data.builtin;

import java.util.function.DoubleConsumer;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.data.Gift;
import org.jel.game.data.strategy.Strategy;
import org.jel.game.data.strategy.StrategyData;
import org.jel.game.data.strategy.StrategyObject;
import org.jel.game.utils.Graph;

public final class ModerateStrategyImpl implements Strategy {
	@Override
	public boolean acceptGift(Clan sourceClan, Clan destinationClan, Gift gift, double oldValue,
			DoubleConsumer newValue, StrategyObject strategyObject) {
		if (Math.random() < (1 - (strategyObject.getRelationship(sourceClan, destinationClan) * 0.1))) {
			return false;
		}
		final var pref = gift.getNumberOfCoins()
				+ gift.getMap().values().stream().mapToDouble(Double::doubleValue).sum();
		final var own = destinationClan.getCoins()
				+ destinationClan.getResources().stream().mapToDouble(Double::doubleValue).sum();
		if (pref > own) {
			newValue.accept(oldValue + ((pref / own) * 5));
		} else {
			newValue.accept(oldValue + ((pref / own) * 7.3));
		}
		return true;
	}

	@Override
	public void applyStrategy(final Clan clan, final byte clanId, final Graph<City> cities,
			final StrategyObject object) {
		BuiltinShared.moderatePlay(clanId, cities, object, clan);
	}

	@Override
	public StrategyData getData() {
		return null;
	}
}
