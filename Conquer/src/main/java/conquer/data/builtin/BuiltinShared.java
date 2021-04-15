package conquer.data.builtin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import conquer.data.Gift;
import conquer.data.ICity;
import conquer.data.IClan;
import conquer.data.Resource;
import conquer.data.StreamUtils;
import conquer.data.strategy.StrategyObject;
import conquer.utils.Graph;

final class BuiltinShared {

	private static final int GOOD_RELATION = 75;
	private static final int MAX_ITERATIONS = 100;
	private static final int COINS_TO_RETAIN = 20;

	private BuiltinShared() {

	}

	// Method for checking things that MUST be true.
	static void assertThat(final boolean b, final String message) {
		if (!b) {
			throw new InternalError("Assertion failed: " + message);
		}
	}

	static void moderateAttack(final IClan clan, final ICity source, final Graph<ICity> graph,
							   final StrategyObject object) {
		BuiltinShared.assertThat(clan != null, "clan==null");
		BuiltinShared.assertThat(source != null, "source==null");
		BuiltinShared.assertThat(graph != null, "graph==null");
		BuiltinShared.assertThat(object != null, "object==null");
		// Find all cities around the source of clans with a relationship < GOOD_RELATION
		final var citiesOfEnemies = StreamUtils
			.getCitiesAroundCityNot(object, graph, source,
				a -> object.getRelationship(clan, a) < BuiltinShared.GOOD_RELATION)
			.toList();
		// Peace!
		if (citiesOfEnemies.isEmpty()) {
			return;
		}
		// Get weakest city of all cities of the enemy.
		final var weakestCity = citiesOfEnemies.get(0);
		final var estimatedPowerOfDefender = weakestCity.getNumberOfSoldiers();
		// If this clan may lose the attack, cancel it.
		final var numberOfSoldiersInSource = source.getNumberOfSoldiers();
		if ((estimatedPowerOfDefender >= numberOfSoldiersInSource) || (numberOfSoldiersInSource == 0)) {
			return;
		}
		// Attack the weakest city
		object.attack(source, weakestCity, false, 0);
	}

	static void moderatePlay(final Graph<ICity> graph, final StrategyObject object, final IClan clan) {
		BuiltinShared.assertThat(clan != null, "clan==null");
		BuiltinShared.assertThat(graph != null, "graph==null");
		BuiltinShared.assertThat(object != null, "object==null");
		final var citiesOfClan = StreamUtils.getCitiesAsStream(graph, clan).toList();
		StreamUtils.forEach(graph, clan, c -> {
			// If there are too many soldiers in a city, try to move them, else recruit
			// some.
			final var diff = c.getCoinDiff();
			if ((diff < -BuiltinShared.COINS_TO_RETAIN) && (citiesOfClan.size() > 1)) {
				object.moveSoldiers(c, StreamUtils.getCitiesAroundCity(graph, c), false, null, 0);
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
		BuiltinShared.assertThat(clan != null, "clan==null");
		BuiltinShared.assertThat(graph != null, "graph==null");
		BuiltinShared.assertThat(object != null, "object==null");
		final List<Double> resources = new ArrayList<>(clan.getResourceStats());
		final var map = new HashMap<Integer, Double>();
		for (var i = 0; i < resources.size(); i++) {
			map.put(i, resources.get(i));
		}
		Collections.sort(resources);
		if (resources.get(0) > 0) {
			// Update defense, because there are enough resources produced.
			BuiltinShared.tryUpdatingDefense(graph, object, clan);
		} else {
			// Else update the resources
			BuiltinShared.tryUpdatingResources(graph, object, clan, map);
		}
	}

	static void offensiveAttack(final IClan clan, final Graph<ICity> cityGraph, final StrategyObject object) {
		BuiltinShared.assertThat(clan != null, "clan==null");
		BuiltinShared.assertThat(cityGraph != null, "cityGraph==null");
		BuiltinShared.assertThat(object != null, "object==null");
		final Predicate<ICity> inSafeCountry = city -> StreamUtils.getCitiesAroundCity(object, cityGraph, city, clan)
			.count() > 0;
		final Predicate<ICity> isReachableCityOfTheEnemy = city -> (StreamUtils
			.getCitiesAroundCity(object, cityGraph, city, clan).count() > 0) && (city.getClan() != clan);
		StreamUtils.getCitiesAsStream(cityGraph, isReachableCityOfTheEnemy).distinct().forEach(
			enemyCity -> StreamUtils.getCitiesAroundCity(object, cityGraph, enemyCity, clan).sorted((a, b) -> {
				final var cnt1 = StreamUtils.getCitiesAroundCity(object, cityGraph, a, inSafeCountry).count();
				final var cnt2 = StreamUtils.getCitiesAroundCity(object, cityGraph, b, inSafeCountry).count();
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
				final var cnt = object.maximumNumberToMove(clan, own, enemyCity, own.getNumberOfSoldiers());
				if (own.getNumberOfSoldiers() < enemyCity.getDefense()) {
					object.recruitSoldiers(clan.getCoins(), own, false, 0);
				}
				// The enemy city could already be conquered...
				if ((own.getNumberOfSoldiers() < enemyCity.getDefense()) || (cnt == 0 || cnt < enemyCity.getDefense())
					|| (own.getClan() == enemyCity.getClan())) {
					return;
				}
				object.attack(own, enemyCity, true, cnt);
			}));
	}

	static void offensiveRecruiting(final Graph<ICity> graph, final StrategyObject object, final IClan clan) {
		// Find all own cities that are on the border to another clan.
		BuiltinShared.assertThat(graph != null, "graph==null");
		BuiltinShared.assertThat(object != null, "object==null");
		BuiltinShared.assertThat(clan != null, "clan==null");
		StreamUtils.getCitiesAsStream(graph, clan,
			a -> StreamUtils.getCitiesAroundCityNot(object, graph, a, clan).count() > 0).sorted((a, b) -> {
			// Sort them using the defense strength
			final var defenseStrengthA = a.getDefenseStrength();
			final var defenseStrengthB = b.getDefenseStrength();
			return Double.compare(defenseStrengthA, defenseStrengthB);
			// Make them stronger, starting with the weakest city
		}).forEach(a -> object.recruitSoldiers(clan.getCoins(), a, false, 0));
	}

	static double sum(final IClan clan) {
		BuiltinShared.assertThat(clan != null, "clan==null");
		return clan.getCoins() + clan.getResources().stream().mapToDouble(Double::doubleValue).sum();
	}

	static double sum(final Gift gift) {
		BuiltinShared.assertThat(gift != null, "gift==null");
		return gift.getNumberOfCoins() + gift.getMap().values().stream().mapToDouble(Double::doubleValue).sum();
	}

	private static void tryUpdatingDefense(final Graph<ICity> graph, final StrategyObject object, final IClan clan) {
		BuiltinShared.assertThat(graph != null, "graph==null");
		BuiltinShared.assertThat(object != null, "object==null");
		BuiltinShared.assertThat(clan != null, "clan==null");
		// Sort all cities using the basedefense as key
		final var sortedListOfCities = StreamUtils
			.getCitiesAsStream(graph, clan, Comparator.comparing(ICity::getDefense)).toList();
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
		StreamUtils.getCitiesAsStream(graph, clan).sorted((a, b) -> {
			final var defense = a.getDefenseStrength();
			final var neighbours = StreamUtils.getCitiesAroundCityNot(object, graph, a, a.getClan())
				.toList();
			final var attack = neighbours.stream().mapToDouble(ICity::getNumberOfSoldiers).sum();
			final var defenseB = b.getDefenseStrength();
			final var neighboursB = StreamUtils.getCitiesAroundCityNot(object, graph, b, b.getClan())
				.toList();
			final var attackB = neighboursB.stream().mapToDouble(ICity::getNumberOfSoldiers).sum();
			final var diff = attack - defense;
			final var diff2 = attackB - defenseB;
			return Double.compare(diff, diff2);
		}).forEach(a -> {
			var cnter = 0;
			var b = true;
			while (b && (cnter < BuiltinShared.MAX_ITERATIONS)) {
				b = object.upgradeDefense(a);
				cnter++;
			}
		});
	}

	static void tryUpdatingResources(final Graph<ICity> graph, final StrategyObject object, final IClan clan,
									 final Map<Integer, Double> map) {
		BuiltinShared.assertThat(graph != null, "graph==null");
		BuiltinShared.assertThat(object != null, "object==null");
		BuiltinShared.assertThat(clan != null, "clan==null");
		BuiltinShared.assertThat(map != null, "map==null");
		// Further checks would be nice, but would be too slow.
		// Try to upgrade all resources with negative production
		map.entrySet().stream().filter(a -> a.getValue() < 0).forEach(a -> {
			final var aKey = a.getKey();
			// Now sort all cities. Start with the ones that either have a low production of this resource
			// or have low production levels.
			final var sortedListOfCities = StreamUtils.getCitiesAsStream(graph, clan, (o1, o2) -> {
				final var productions1 = o1.getProductions();
				final var productions2 = o2.getProductions();
				final var i = Double.compare(productions1.get(aKey), productions2.get(aKey));
				return i == 0 ? Double.compare(o1.getLevels().get(aKey), o2.getLevels().get(aKey)) : i;
			}).toList();
			var cnter = 0;
			// In every city upgrade the production.
			while (cnter != sortedListOfCities.size()) {
				final var b = object.upgradeResource(Resource.values()[a.getKey()], sortedListOfCities.get(cnter));
				if (!b) {
					cnter++;
				}
			}
		});
	}
}
