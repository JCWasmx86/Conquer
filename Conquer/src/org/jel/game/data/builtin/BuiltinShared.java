package org.jel.game.data.builtin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.data.Gift;
import org.jel.game.data.Resource;
import org.jel.game.data.Shared;
import org.jel.game.data.StreamUtils;
import org.jel.game.data.strategy.StrategyObject;
import org.jel.game.utils.Graph;

public final class BuiltinShared {

	static void moderateAttack(final byte clan, final City source, final Graph<City> graph,
			final StrategyObject object) {
		// Find all cities around the source of clans with a relationship<75
		final var citiesOfEnemies = StreamUtils
				.getCitiesAroundCityNot(graph, source, a -> object.getRelationship(clan, a.getClan()) < 75)
				.collect(Collectors.toList());
		if (!citiesOfEnemies.isEmpty()) {
			// Get weakest city
			final var weakestCity = citiesOfEnemies.get(0);
			final var powerOfDefenderEstimated = weakestCity.getNumberOfSoldiers();
			if ((powerOfDefenderEstimated >= source.getNumberOfSoldiers()) || (source.getNumberOfSoldiers() == 0)) {
				return;
			}
			// Attack the weakest city
			object.attack(source, weakestCity, clan, false, 0, false);
		}
	}

	static void moderatePlay(final byte clanId, final Graph<City> graph, final StrategyObject object, final Clan clan) {
		final var citiesOfClan = StreamUtils.getCitiesAsStream(graph, clanId).collect(Collectors.toList());
		StreamUtils.getCitiesAsStream(graph, clanId).forEach(c -> {
			// If there are too many soldiers in a city, try to move them, else recruit
			// some.
			final var diff = (Shared.COINS_PER_PERSON_PER_ROUND * c.getNumberOfPeople())
					- (Shared.COINS_PER_SOLDIER_PER_ROUND * c.getNumberOfSoldiers());
			if ((diff < -20) && (citiesOfClan.size() > 1)) {
				object.moveSoldiers(c, object.reachableCities(c), clanId, false, null, 0);
			} else if (diff > 20) {
				object.recruitSoldiers(diff - 20, clanId, c, false, 0);
			}
			// Do some expansion
			BuiltinShared.moderateAttack(clanId, c, graph, object);
		});
		// Upgrade resources
		BuiltinShared.moderateResourcesUpgrade(graph, object, clanId, clan);
	}

	static void moderateResourcesUpgrade(final Graph<City> graph, final StrategyObject object, final byte clanId,
			final Clan clan) {
		if (clanId == 0) {
			return;
		}
		final List<Double> resources = new ArrayList<>(clan.getResourceStats());
		final var map = new HashMap<Integer, Double>();
		for (var i = 0; i < resources.size(); i++) {
			map.put(i, resources.get(i));
		}
		Collections.sort(resources);
		if (resources.get(0) > 0) {
			// Update defense, because there are too much resources produced
			BuiltinShared.tryUpdatingDefense(graph, object, clanId);
		} else {
			BuiltinShared.tryUpdatingResources(graph, object, clanId, map);
		}
	}

	static void offensiveAttack(final byte i, final Clan clan, final Graph<City> graph, final StrategyObject object) {
		// Predicate to filter for all own cities that have neighbours, that are not of
		// clan i.
		final Predicate<City> pre = a -> StreamUtils.getCitiesAroundCityNot(graph, a, i).count() > 0;
		StreamUtils
				.getCitiesAsStream(graph,
						a -> StreamUtils.getCitiesAroundCity(graph, a).filter(b -> b.getClan() == clan.getId())
								.count() > 0)
				.forEach(enemy -> StreamUtils.getCitiesAroundCity(graph, enemy, i).sorted((a, b) -> {
					final var cnt1 = StreamUtils.getCitiesAroundCity(graph, a, pre).count();
					final var cnt2 = StreamUtils.getCitiesAroundCity(graph, b, pre).count();
					if (cnt1 == cnt2) {
						final var compared = Long.compare(a.getNumberOfSoldiers(), b.getNumberOfSoldiers());
						if (compared == 0) {
							return Double.compare(object.getRelationship(i, a.getClan()),
									object.getRelationship(i, b.getClan()));
						}
						return compared;
					}
					return Long.compare(cnt1, cnt2);
					// Attack them, starting with the weakest.
				}).forEach(own -> {
					final var cnt = object.maximumNumberToMove(i, graph.getWeight(own, enemy),
							own.getNumberOfSoldiers());
					if (own.getNumberOfSoldiers() < enemy.getDefense()) {
						object.recruitSoldiers(clan.getCoins(), i, own, false, 0);
					}
					if (!graph.isConnected(own, enemy) || (own.getNumberOfSoldiers() < enemy.getDefense())
							|| (cnt == 0)) {
						return;
					}
					object.attack(own, enemy, i, true, cnt, false);
				}));
	}

	static void offensiveRecruiting(final byte clanId, final Graph<City> graph, final StrategyObject object,
			final Clan clan) {
		// Find all own cities that are on the border to another clan.
		StreamUtils
				.getCitiesAsStream(graph, clanId, a -> StreamUtils.getCitiesAroundCityNot(graph, a, clanId).count() > 0)
				.sorted((a, b) -> {
					// Sort them using the defense strength
					final var defenseStrengthA = object.defenseStrengthOfCity(a);
					final var defenseStrengthB = object.defenseStrengthOfCity(b);
					return Double.compare(defenseStrengthA, defenseStrengthB);
					// Make them stronger, starting with the weakest city
				}).forEach(a -> object.recruitSoldiers(clan.getCoins(), clanId, a, false, 0));
	}

	private static void tryUpdatingDefense(final Graph<City> graph, final StrategyObject object, final byte clan) {
		// Sort all cities using the basedefense as key
		final var sortedListOfCities = StreamUtils
				.getCitiesAsStream(graph, clan, Comparator.comparing(City::getDefense)).collect(Collectors.toList());
		// Find the average basedefense
		final var average = sortedListOfCities.stream().mapToDouble(City::getDefense).average();
		var avg = average.getAsDouble();
		avg = avg <= 0 ? 1 : avg;
		for (final City city : sortedListOfCities) {
			// Skip cities, that are above the average
			if (city.getDefense() > avg) {
				break;
			}
			// Upgrade cities, that are below the average
			while (city.getDefense() < avg) {
				final var b = object.upgradeDefense(clan, city);
				if (!b) {
					break;
				}
			}
		}
		// Now find the cities, that are at the border==> Make these cities stronger.
		object.getWeakestCityInRatioToSurroundingEnemyCities(StreamUtils.getCitiesAsStream(graph, clan)).forEach(a -> {
			var cnter = 0;
			while (true) {
				final var b = object.upgradeDefense(clan, a);
				cnter++;
				if ((!b) || (cnter >= 100)) {
					break;
				}
			}
		});
	}

	static void tryUpdatingResources(final Graph<City> graph, final StrategyObject object, final byte clan,
			final Map<Integer, Double> map) {
		// Try to upgrade all resources with negative production
		map.entrySet().stream().filter(a -> a.getValue() < 0).forEach(a -> {
			final var aKey = a.getKey();
			// Sort using the production values, or the level of each resource
			final var sortedListOfCities = StreamUtils.getCitiesAsStream(graph, clan, (o1, o2) -> {
				final var i = Double.compare(o1.getProductions().get(aKey), o2.getProductions().get(aKey));
				return i == 0 ? i : Double.compare(o1.getLevels().get(aKey), o2.getLevels().get(aKey));
			}).collect(Collectors.toList());
			var cnter = 0;
			// Upgrade resource
			while (cnter != sortedListOfCities.size()) {
				final var b = object.upgradeResource(clan, Resource.values()[a.getKey()],
						sortedListOfCities.get(cnter));
				if (!b) {
					cnter++;
				}
			}
		});
	}

	static double sum(Clan clan) {
		return clan.getCoins() + clan.getResources().stream().mapToDouble(Double::doubleValue).sum();
	}

	private BuiltinShared() {

	}

	static double sum(Gift gift) {
		return gift.getNumberOfCoins() + gift.getMap().values().stream().mapToDouble(Double::doubleValue).sum();
	}
}
