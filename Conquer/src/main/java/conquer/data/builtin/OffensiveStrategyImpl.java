package conquer.data.builtin;

import java.util.function.DoubleConsumer;
import java.util.stream.Collectors;

import conquer.data.Gift;
import conquer.data.ICity;
import conquer.data.IClan;
import conquer.data.Resource;
import conquer.data.StreamUtils;
import conquer.data.strategy.Strategy;
import conquer.data.strategy.StrategyData;
import conquer.data.strategy.StrategyObject;
import conquer.utils.Graph;

public final class OffensiveStrategyImpl implements Strategy {
	private static final double OFFENSIVE_UPGRADE_PROBABILITY = 0.15;
	private static final double DECLINE_GIFT_PROBABILITY = 0.875;
	private static final int MAX_ITERATIONS_PER_ROUND = 100;
	private StrategyObject object;
	private Graph<ICity> graph;

	@Override
	public boolean acceptGift(final IClan sourceClan, final IClan destinationClan, final Gift gift,
			final double oldValue, final DoubleConsumer newValue, final StrategyObject strategyObject) {
		BuiltinShared.assertThat(sourceClan != null, "sourceClan==null");
		BuiltinShared.assertThat(destinationClan != null, "destinationClan==null");
		BuiltinShared.assertThat(gift != null, "gift==null");
		BuiltinShared.assertThat(newValue != null, "newValue==null");
		BuiltinShared.assertThat(strategyObject != null, "strategyObject==null");
		BuiltinShared.assertThat(oldValue >= 0, "oldValue<0: " + oldValue);
		if (Math.random() > OffensiveStrategyImpl.DECLINE_GIFT_PROBABILITY) {
			return false;
		} else {
			final var cities = strategyObject.getCities();
			final var numOwn = StreamUtils.getCitiesAsStream(cities, destinationClan).count();
			final var numOther = StreamUtils.getCitiesAsStream(cities, sourceClan).count();
			if (numOwn < numOther) {// "Alliance" with stronger clans
				newValue.accept(oldValue + (Math.random() * 120 * (numOther / (double) numOwn)));
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public void applyStrategy(final IClan clan, final Graph<ICity> cities, final StrategyObject obj) {
		BuiltinShared.assertThat(clan != null, "clan==null");
		BuiltinShared.assertThat(cities != null, "cities==null");
		BuiltinShared.assertThat(obj != null, "obj==null");
		this.object = obj;
		this.graph = cities;
		final var data = clan.getData();
		if (data instanceof OffensiveStrategyData strategyData) {
			final var action = strategyData.getAction();
			if (action == OffensiveStrategy.EXPAND) {
				BuiltinShared.offensiveAttack(clan, cities, obj);
			} else {// Upgrades
				if (Math.random() < OffensiveStrategyImpl.OFFENSIVE_UPGRADE_PROBABILITY) {
					// Rarely, only the resources for recruiting soldiers upgraded
					this.offensiveResourcesUpgrade(clan);
				} else {
					BuiltinShared.moderateResourcesUpgrade(cities, obj, clan);
				}
				// Recruit as many soldiers as possible
				BuiltinShared.offensiveRecruiting(cities, obj, clan);
				// Strengthen the soldiers
				this.offensiveSoldierUpgrading(clan);
			}
			// Move troops to the borders
			this.moveTroops(clan);
		} else {
			throw new InternalError();
		}
	}

	@Override
	public StrategyData getData() {
		return new OffensiveStrategyData();
	}

	private void moveTroops(final IClan clan) {
		// All cities, of the own clan, that are not at the border (=Are only surrounded
		// by cities of the same clan), and have soldiers. (Set W in the following)
		final var citiesWithoutBordersWithSoldiers = StreamUtils
				.getCitiesAsStream(this.graph, clan, a -> a.getNumberOfSoldiers() > 0)
				.filter(a -> StreamUtils.getCitiesAroundCityNot(this.object, this.graph, a, clan).count() == 0)
				.collect(Collectors.toList());
		// All cities that are adjacent to another clan. (Set B in the following)
		final var citiesOnBorder = StreamUtils
				.getCitiesAsStream(this.graph, clan, a -> !citiesWithoutBordersWithSoldiers.contains(a))
				.collect(Collectors.toList());
		// For each city in W, now all adjacent cities from B are sorted, first by the
		// number of soldiers, then by
		// the distance (A city with less soldiers will be supported earlier)
		citiesWithoutBordersWithSoldiers
				.forEach(city -> citiesOnBorder.stream().filter(a -> this.graph.isConnected(a, city)).sorted((a, b) -> {
					final var i = Long.compare(a.getNumberOfSoldiers(), b.getNumberOfSoldiers());
					if (i != 0) {
						return i;
					} else {
						return Double.compare(this.graph.getWeight(city, a), this.graph.getWeight(city, b));
					}
				}).forEach(a -> {
					// And then move the maximum amount of soldiers to every city.
					final var numberOfSoldiersToMove = this.object.maximumNumberToMove(clan, city, a,
							city.getNumberOfSoldiers());
					this.object.moveSoldiers(city, null, true, a, numberOfSoldiersToMove);
				}));
	}

	// Only upgrade the essential resources that are needed for recruiting soldiers
	private void offensiveResourcesUpgrade(final IClan clan) {
		final double iron = clan.getResourceStats().get(Resource.IRON.getIndex());
		final double stone = clan.getResourceStats().get(Resource.STONE.getIndex());
		final double wood = clan.getResourceStats().get(Resource.WOOD.getIndex());
		if (iron < 0) {
			this.upgradeResourcesForClan(clan, Resource.IRON);
		}
		if (stone < 0) {
			this.upgradeResourcesForClan(clan, Resource.STONE);
		}
		if (wood < 0) {
			this.upgradeResourcesForClan(clan, Resource.WOOD);
		}

	}

	// Upgrade the only the soldiers strength and the soldiers offense strength.
	// Upgrading the defense is a "waste of money" for this algorithm.
	private void offensiveSoldierUpgrading(final IClan clan) {
		var b = true;
		var cnter = 0;
		while (b && (cnter < OffensiveStrategyImpl.MAX_ITERATIONS_PER_ROUND)) {
			b = clan.upgradeSoldiersOffense();
			cnter++;
		}
		b = true;
		cnter = 0;
		while (b && (cnter < OffensiveStrategyImpl.MAX_ITERATIONS_PER_ROUND)) {
			b = clan.upgradeSoldiers();
			cnter++;
		}
	}

	@Override
	public StrategyData resume(final StrategyObject strategyObject, final byte[] bytes, final boolean hasStrategyData,
			final byte[] dataBytes) {
		this.object = strategyObject;
		if (hasStrategyData) {
			return new OffensiveStrategyData(dataBytes);
		} else {
			return null;
		}
	}

	// If there are no cities that don't have any connection to another clan,
	// all cities are used for upgrading the resource production.
	// The cities are upgraded in ascending order.
	private void upgradeResourcesForClan(final IClan clan, final Resource resc) {
		final var citiesWithoutBorders = StreamUtils
				.getCitiesAsStream(this.graph, clan,
						a -> StreamUtils.getCitiesAroundCityNot(this.object, this.graph, a, clan).count() == 0)
				.collect(Collectors.toList());
		final var cityStream = (citiesWithoutBorders.isEmpty() ? StreamUtils.getCitiesAsStream(this.graph, clan)
				: citiesWithoutBorders.stream());
		cityStream.sorted((a, b) -> {
			final var index = resc.getIndex();
			final double resA = a.getProductions().get(index);
			final double resB = b.getProductions().get(index);
			return Double.compare(resA, resB);
		}).forEach(a -> {
			while (this.object.upgradeResource(resc, a)) {
				// Empty
			}
		});
	}
}
