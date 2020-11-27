package org.jel.game.data.builtin;

import java.util.function.DoubleConsumer;
import java.util.stream.Collectors;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.data.Gift;
import org.jel.game.data.Resource;
import org.jel.game.data.StreamUtils;
import org.jel.game.data.strategy.Strategy;
import org.jel.game.data.strategy.StrategyData;
import org.jel.game.data.strategy.StrategyObject;
import org.jel.game.utils.Graph;

public final class OffensiveStrategyImpl implements Strategy {
	private StrategyObject object;
	private Graph<City> graph;

	@Override
	public boolean acceptGift(Clan sourceClan, Clan destinationClan, Gift gift, double oldValue,
			DoubleConsumer newValue, StrategyObject strategyObject) {
		if (Math.random() > 0.875) {
			return false;
		} else {
			final var cities = strategyObject.getCities();
			final var numOwn = StreamUtils.getCitiesAsStream(cities, destinationClan.getId()).count();
			final var numOther = StreamUtils.getCitiesAsStream(cities, sourceClan.getId()).count();
			if (numOwn < numOther) {// "Alliance" with stronger clans
				newValue.accept(oldValue + (Math.random() * 120 * (numOther / (double) numOwn)));
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public void applyStrategy(final Clan clan, final byte clanId, final Graph<City> cities, final StrategyObject obj) {
		this.object = obj;
		this.graph = cities;
		final var data = clan.getData();
		if (data instanceof OffensiveStrategyData strategyData) {
			final var action = strategyData.getAction();
			if (action == OffensiveStrategy.EXPAND) {
				BuiltinShared.offensiveAttack(clanId, clan, cities, obj);
			} else {
				if (Math.random() < 0.15) {
					this.offensiveResourcesUpgrade(clanId, clan);
				} else {
					BuiltinShared.moderateResourcesUpgrade(cities, obj, clanId, clan);
				}
				BuiltinShared.offensiveRecruiting(clanId, cities, obj, clan);
				this.offensiveSoldierUpgrading(clanId);
			}
			this.moveTroops(clanId);
		} else {
			throw new InternalError();
		}
	}

	@Override
	public StrategyData getData() {
		return new OffensiveStrategyData();
	}

	private void moveTroops(byte clanId) {
		final var citiesWithoutBordersWithSoldiers = StreamUtils
				.getCitiesAsStream(this.graph, clanId, a -> a.getNumberOfSoldiers() > 0).filter(a -> StreamUtils
						.getCitiesAroundCity(this.graph, a).filter(b -> b.getClan() != clanId).count() == 0)
				.collect(Collectors.toList());
		final var citiesOnBorder = StreamUtils
				.getCitiesAsStream(this.graph, clanId, a -> !citiesWithoutBordersWithSoldiers.contains(a))
				.collect(Collectors.toList());
		citiesWithoutBordersWithSoldiers.forEach(city -> {
			citiesOnBorder.stream().filter(a -> this.graph.isConnected(a, city)).sorted((a, b) -> {
				final var i = Long.compare(a.getNumberOfSoldiers(), b.getNumberOfSoldiers());
				if (i != 0) {
					return i;
				} else {
					return Double.compare(this.graph.getWeight(city, a), this.graph.getWeight(city, b));
				}
			}).forEach(a -> {
				final var numberOfSoldiersToMove = this.object.maximumNumberToMove(clanId,
						this.graph.getWeight(city, a), city.getNumberOfSoldiers());
				this.object.moveSoldiers(city, null, clanId, true, a, numberOfSoldiersToMove);
			});
		});
	}

	private void offensiveResourcesUpgrade(final byte clan, final Clan clanO) {
		final double iron = clanO.getResourceStats().get(Resource.IRON.getIndex());
		final double stone = clanO.getResourceStats().get(Resource.STONE.getIndex());
		final double wood = clanO.getResourceStats().get(Resource.WOOD.getIndex());
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

	private void offensiveSoldierUpgrading(final byte i) {
		var b = true;
		var cnter = 0;
		while (b && (cnter < 100)) {
			b = this.object.upgradeOffense(i);
			cnter++;
		}
		b = true;
		cnter = 0;
		while (b && (cnter < 100)) {
			b = this.object.upgradeSoldiers(i);
			cnter++;
		}
	}

	private void upgradeResourcesForClan(final byte clan, final Resource resc) {
		StreamUtils.getCitiesAsStream(this.graph, clan).sorted((a, b) -> {
			final var index = resc.getIndex();
			final double resA = a.getProductions().get(index);
			final double resB = b.getProductions().get(index);
			return Double.compare(resA, resB);
		}).collect(Collectors.toList()).forEach(a -> {
			while (this.object.upgradeResource(clan, resc, a)) {
				// Empty
			}
		});
	}
}
