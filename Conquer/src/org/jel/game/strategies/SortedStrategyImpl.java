package org.jel.game.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleConsumer;
import java.util.stream.Collectors;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.data.Gift;
import org.jel.game.data.Resource;
import org.jel.game.data.Shared;
import org.jel.game.data.StreamUtils;
import org.jel.game.data.strategy.Strategy;
import org.jel.game.data.strategy.StrategyData;
import org.jel.game.data.strategy.StrategyObject;
import org.jel.game.utils.Graph;
import org.jel.game.utils.Pair;

public final class SortedStrategyImpl implements Strategy {
	private static final double MAXIMUM_VARIANCE = 0.3;
	private static final double FIFTY_FIFTY_PROBABILITY = 0.5;
	private List<City> cities;
	private final Map<City, Pair<Double, Double>> values = new HashMap<>();
	private final List<Clan> gifts;
	private int counter;

	public SortedStrategyImpl() {
		this.cities = new ArrayList<>();
		this.gifts = new ArrayList<>();
	}

	@Override
	public boolean acceptGift(Clan sourceClan, Clan destinationClan, Gift gift, double oldValue,
			DoubleConsumer newValue, StrategyObject strategyObject) {
		if ((this.gifts.contains(sourceClan) && (Math.random() < 0.8)) || (Math.random() < 0.1)) {
			return false;
		}
		var preference = strategyObject.getRelationship(sourceClan, destinationClan) * 0.01;
		preference += (destinationClan.getCoins() == 0 ? 0.5 : gift.getNumberOfCoins() / destinationClan.getCoins());
		for (final var v : gift.getMap().entrySet()) {
			final var own = destinationClan.getResources().get(v.getKey().getIndex());
			if (own < v.getValue()) {
				preference++;
			} else if (v.getValue() != 0) {
				preference += (own / v.getValue());
			}
		}
		Shared.logLevel1("SortedStrategy: Preference: " + String.format("%.2f", preference));
		newValue.accept(oldValue + preference);
		if (!this.gifts.contains(sourceClan)) {
			this.gifts.add(sourceClan);
		}
		return true;
	}

	@Override
	public void applyStrategy(final Clan clan, final byte clanId, final Graph<City> cities, final StrategyObject obj) {
		if (this.counter == 7) {
			this.counter = 0;
			this.gifts.clear();
		} else {
			this.counter++;
		}
		this.refreshList(clanId, cities);
		this.attack(cities, obj, clanId, clan);
		this.upgradeCities(cities, clanId, obj);
		this.upgradeClan(clanId, obj);
	}

	private void attack(final Graph<City> graph, final StrategyObject obj, final byte id, final Clan clan) {
		for (final City target : this.cities) {
			final var own = StreamUtils.getCitiesAsStream(graph, a -> graph.isConnected(a, target))
					.sorted((a, b) -> Long.compare(a.getNumberOfSoldiers(), b.getNumberOfSoldiers()))
					.collect(Collectors.toList());
			own.forEach(ownCity -> {
				final var pair = this.values.get(target);
				final var second = (double) pair.second();
				final var ownCitySoldiers = ownCity.getNumberOfSoldiers();
				if (second > (ownCitySoldiers * clan.getSoldiersOffenseStrength() * clan.getSoldiersStrength())) {
					obj.recruitSoldiers(clan.getCoins() * 0.25, id, ownCity, true, ownCity.getNumberOfPeople());
					if (second > (ownCitySoldiers * clan.getSoldiersOffenseStrength() * clan.getSoldiersStrength())) {
						return;
					}
				}
				long numberOfSoldiersUsed;
				if ((ownCitySoldiers > second) || ((ownCitySoldiers * clan.getSoldiersOffenseStrength()
						* clan.getSoldiersStrength()) > second)) {
					numberOfSoldiersUsed = second > ownCitySoldiers ? ownCitySoldiers : (long) second;
				} else {
					return;
				}
				obj.attack(ownCity, target, id, true, numberOfSoldiersUsed, false);
			});
		}
	}

	@Override
	public StrategyData getData() {
		return null;
	}

	private void refreshList(final byte clanId, final Graph<City> cities2) {
		this.values.clear();
		StreamUtils.getCitiesAsStreamNot(cities2, clanId).forEach(a -> {
			// Make the strategy a bit wrong to make it possible for the player to win.
			final var soldiersA = a.getNumberOfSoldiers() * (Math.random() > SortedStrategyImpl.FIFTY_FIFTY_PROBABILITY
					? (1 + (Math.random() % SortedStrategyImpl.MAXIMUM_VARIANCE))
					: (1 - (Math.random() % SortedStrategyImpl.MAXIMUM_VARIANCE)));
			final var peopleA = a.getNumberOfPeople() * (Math.random() > SortedStrategyImpl.FIFTY_FIFTY_PROBABILITY
					? (1 + (Math.random() % SortedStrategyImpl.MAXIMUM_VARIANCE))
					: (1 - (Math.random() % SortedStrategyImpl.MAXIMUM_VARIANCE)));
			this.values.put(a, new Pair<>(peopleA, soldiersA));
		});
		this.cities = StreamUtils
				.getCitiesAsStreamNot(cities2, clanId,
						a -> cities2.getConnected(a).stream().filter(b -> b.getClan() == clanId).count() > 0)
				.sorted((a, b) -> {
					final var pA = this.values.get(a);
					final var pB = this.values.get(b);
					final var ratioA = pA.first() / (pA.second() == 0 ? 1 : pA.second());
					final var ratioB = pB.first() / (pB.second() == 0 ? 1 : pB.second());
					return Double.compare(ratioB, ratioA);// The cities with a high people/soldiers ratio come first.
				}).collect(Collectors.toList());
	}

	private void upgradeCities(final Graph<City> cities, final byte clanId, final StrategyObject obj) {
		var didUpgrade = true;
		final var ownCities = StreamUtils.getCitiesAsStream(cities, clanId).collect(Collectors.toList());
		while (didUpgrade) {
			var num = 0;
			for (final City c : ownCities) {
				var flag = false;
				flag |= obj.upgradeDefense(clanId, c);
				for (final Resource r : Resource.values()) {
					flag |= obj.upgradeResource(clanId, r, c);
				}
				if (flag) {
					num++;
				}
			}
			if (num == 0) {
				didUpgrade = false;
			}
		}
	}

	private void upgradeClan(final byte clanId, final StrategyObject obj) {
		while (true) {
			var flag = false;
			flag |= obj.upgradeDefense(clanId);
			flag |= obj.upgradeOffense(clanId);
			flag |= obj.upgradeSoldiers(clanId);
			if (!flag) {
				break;
			}
		}
	}

}
