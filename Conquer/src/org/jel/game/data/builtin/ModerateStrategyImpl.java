package org.jel.game.data.builtin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleConsumer;
import java.util.stream.Collectors;

import org.jel.game.data.Gift;
import org.jel.game.data.ICity;
import org.jel.game.data.IClan;
import org.jel.game.data.StreamUtils;
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
		this.sendGift(clan, cities, object);
	}

	private void sendGift(final IClan clan, final Graph<ICity> cities, final StrategyObject object) {
		final var map = new HashMap<IClan, Integer>();
		StreamUtils.forEach(cities, a -> {
			final var clanObject = a.getClan();
			if (!map.containsKey(clanObject)) {
				map.put(clanObject, 1);
			} else {
				map.put(clanObject, map.get(clanObject) + 1);
			}
		});
		final var clansSortedByDescendingSize = map.entrySet().stream().filter(a -> a.getKey() != clan)
				.sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
				.filter(otherClan -> object.getRelationship(clan, otherClan.getKey()) < 65).map(Map.Entry::getKey)
				.collect(Collectors.toList());
		// Try to get relationships with the strongest clans.
		for (final var otherClan : clansSortedByDescendingSize) {
			final var resourcesToGive = new ArrayList<Double>();
			final var stats = otherClan.getResourceStats();
			for (var i = 0; i < stats.size(); i++) {
				final double value = stats.get(i);
				final double ownValue = clan.getResourceStats().get(i);
				if ((ownValue < 0) || (value > 0) || (value > ownValue)) {
					resourcesToGive.add(0d);
				} else {
					final var numRoundsOfResourceStored = clan.getResources().get(i) / ownValue;
					final var roundsToGive = Math.random() * 0.5 * numRoundsOfResourceStored;
					resourcesToGive.add(Double.isNaN(roundsToGive) || Double.isInfinite(roundsToGive) ? 0
							: roundsToGive * ownValue);
				}
			}
			final var ownCoins = clan.getCoins();
			final var coins = otherClan.getCoins();
			if ((ownCoins < coins) || (ownCoins == 0)) {
				continue;
			}
			final var coinsToGive = Math.random() * 0.25 * ownCoins;
			final var gift = new Gift(resourcesToGive, coinsToGive);
			if (Math.random() < 0.2) {
				object.sendGift(clan, otherClan, gift);
			}
		}
	}

	@Override
	public StrategyData getData() {
		return null;
	}
}
