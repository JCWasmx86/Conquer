package org.jel.game.data.builtin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jel.game.data.Gift;
import org.jel.game.data.ICity;
import org.jel.game.data.IClan;
import org.jel.game.data.Resource;
import org.jel.game.data.StreamUtils;
import org.jel.game.data.strategy.StrategyObject;
import org.jel.game.utils.Graph;

final class BuiltinShared {

	private static final int GOOD_RELATION = 75;
	private static final int MAX_ITERATIONS = 100;
	private static final int COINS_TO_RETAIN = 20;

	static void moderateAttack(final IClan clan, final ICity source, final Graph<ICity> graph,
			final StrategyObject object) {
		// Find all cities around the source of clans with a relationship < 75
		final var citiesOfEnemies = StreamUtils
				.getCitiesAroundCityNot(graph, source,
						a -> object.getRelationship(clan, a) < BuiltinShared.GOOD_RELATION)
				.collect(Collectors.toList());
		if (!citiesOfEnemies.isEmpty()) {
			// Get weakest city
			final var weakestCity = citiesOfEnemies.get(0);
			final var estimatedPowerOfDefender = weakestCity.getNumberOfSoldiers();
			// If this clan may lose the attack cancel it.
			final var numberOfSoldiersInSource = source.getNumberOfSoldiers();
			if ((estimatedPowerOfDefender >= numberOfSoldiersInSource) || (numberOfSoldiersInSource == 0)) {
				return;
			}
			// Attack the weakest city
			object.attack(source, weakestCity, false, 0, false);
		}
	}

	static void moderatePlay(final Graph<ICity> graph, final StrategyObject object, final IClan clan) {
		final var citiesOfClan = StreamUtils.getCitiesAsStream(graph, clan).collect(Collectors.toList());
		StreamUtils.forEach(graph, clan, c -> {
			// If there are too many soldiers in a city, try to move them, else recruit
			// some.
			final var diff = c.getCoinDiff();
			if ((diff < -BuiltinShared.COINS_TO_RETAIN) && (citiesOfClan.size() > 1)) {
				object.moveSoldiers(c, object.reachableCities(c), false, null, 0);
			} else if (diff > BuiltinShared.COINS_TO_RETAIN) {
				object.recruitSoldiers(diff - BuiltinShared.COINS_TO_RETAIN, c, false, 0);
			}
			// Do some expansion
			BuiltinShared.moderateAttack(clan, c, graph, object);
		});
		// Upgrade resources
		BuiltinShared.moderateResourcesUpgrade(graph, object, clan);
	}

	static void moderateResourcesUpgrade(final Graph<ICity> graph, final StrategyObject object, final IClan clan) {
		if (clan.isPlayerClan()) {
			return;
		}
		final List<Double> resources = new ArrayList<>(clan.getResourceStats());
		final var map = new HashMap<Integer, Double>();
		for (var i = 0; i < resources.size(); i++) {
			map.put(i, resources.get(i));
		}
		Collections.sort(resources);
		if (resources.get(0) > 0) {
			// Update defense, because there are too much resources produced.
			BuiltinShared.tryUpdatingDefense(graph, object, clan);
		} else {
			// Else update the resources
			BuiltinShared.tryUpdatingResources(graph, object, clan, map);
		}
	}

	static void offensiveAttack(final IClan clan, final Graph<ICity> cityGraph, final StrategyObject object) {
		final Predicate<ICity> pre = city -> StreamUtils.getCitiesAroundCityNot(cityGraph, city, clan).count() > 0;
		StreamUtils
				.getCitiesAsStream(cityGraph,
						city -> (StreamUtils.getCitiesAroundCity(cityGraph, city, b -> b.getClan() == clan)
								.count() > 0) && (city.getClanId() != clan.getId()))
				.forEach(enemy -> StreamUtils.getCitiesAroundCity(cityGraph, enemy, clan).sorted((a, b) -> {
					final var cnt1 = StreamUtils.getCitiesAroundCity(cityGraph, a, pre).count();
					final var cnt2 = StreamUtils.getCitiesAroundCity(cityGraph, b, pre).count();
					if (cnt1 == cnt2) {
						final var compared = Long.compare(a.getNumberOfSoldiers(), b.getNumberOfSoldiers());
						if ((compared == 0) && (a.getClan() != clan) && (b.getClan() != clan)) {
							return Double.compare(object.getRelationship(clan, a), object.getRelationship(clan, b));
						}
						return compared;
					}
					return Long.compare(cnt1, cnt2);
					// Attack them, starting with the weakest.
				}).forEach(own -> {
					final var cnt = object.maximumNumberToMove(clan, own, enemy, own.getNumberOfSoldiers());
					if (own.getNumberOfSoldiers() < enemy.getDefense()) {
						object.recruitSoldiers(clan.getCoins(), own, false, 0);
					}
					if (!cityGraph.isConnected(own, enemy) || (own.getNumberOfSoldiers() < enemy.getDefense())
							|| (cnt == 0)) {
						return;
					}
					object.attack(own, enemy, true, cnt, false);
				}));
	}

	static void offensiveRecruiting(final Graph<ICity> graph, final StrategyObject object, final IClan clan) {
		// Find all own cities that are on the border to another clan.
		StreamUtils.getCitiesAsStream(graph, clan, a -> StreamUtils.getCitiesAroundCityNot(graph, a, clan).count() > 0)
				.sorted((a, b) -> {
					// Sort them using the defense strength
					final var defenseStrengthA = object.defenseStrengthOfCity(a);
					final var defenseStrengthB = object.defenseStrengthOfCity(b);
					return Double.compare(defenseStrengthA, defenseStrengthB);
					// Make them stronger, starting with the weakest city
				}).forEach(a -> object.recruitSoldiers(clan.getCoins(), a, false, 0));
	}

	static double sum(final IClan clan) {
		return clan.getCoins() + clan.getResources().stream().mapToDouble(Double::doubleValue).sum();
	}

	static double sum(final Gift gift) {
		return gift.getNumberOfCoins() + gift.getMap().values().stream().mapToDouble(Double::doubleValue).sum();
	}

	private static void tryUpdatingDefense(final Graph<ICity> graph, final StrategyObject object, final IClan clan) {
		// Sort all cities using the basedefense as key
		final var sortedListOfCities = StreamUtils
				.getCitiesAsStream(graph, clan, Comparator.comparing(ICity::getDefense)).collect(Collectors.toList());
		// Find the average basedefense
		final var average = sortedListOfCities.stream().mapToDouble(ICity::getDefense).average();
		var avg = average.getAsDouble();
		avg = avg <= 0 ? 1 : avg;
		for (final var city : sortedListOfCities) {
			// Skip cities, that are above the average
			if (city.getDefense() > avg) {
				break;
			}
			// Upgrade cities, that are below the average
			while (city.getDefense() < avg) {
				final var b = object.upgradeDefense(city);
				if (!b) {
					break;
				}
			}
		}
		// Now find the cities, that are at the border==> Make these cities stronger.
		object.getWeakestCityInRatioToSurroundingEnemyCities(StreamUtils.getCitiesAsStream(graph, clan)).forEach(a -> {
			var cnter = 0;
			while (true) {
				final var b = object.upgradeDefense(a);
				cnter++;
				if ((!b) || (cnter >= BuiltinShared.MAX_ITERATIONS)) {
					break;
				}
			}
		});
	}

	static void tryUpdatingResources(final Graph<ICity> graph, final StrategyObject object, final IClan clan,
			final Map<Integer, Double> map) {
		// Try to upgrade all resources with negative production
		map.entrySet().stream().filter(a -> a.getValue() < 0).forEach(a -> {
			final var aKey = a.getKey();
			// Sort using the production values, or the level of each resource
			final var sortedListOfCities = StreamUtils.getCitiesAsStream(graph, clan, (o1, o2) -> {
				final var productions1 = o1.getProductions();
				final var productions2 = o2.getProductions();
				final var i = Double.compare(productions1.get(aKey), productions2.get(aKey));
				return i == 0 ? Double.compare(o1.getLevels().get(aKey), o2.getLevels().get(aKey)) : i;
			}).collect(Collectors.toList());
			var cnter = 0;
			// Upgrade resource
			while (cnter != sortedListOfCities.size()) {
				final var b = object.upgradeResource(Resource.values()[a.getKey()], sortedListOfCities.get(cnter));
				if (!b) {
					cnter++;
				}
			}
		});
	}

	private BuiltinShared() {

	}
}
