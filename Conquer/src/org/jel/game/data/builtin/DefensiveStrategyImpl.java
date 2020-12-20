package org.jel.game.data.builtin;

import java.util.function.DoubleConsumer;
import java.util.function.Predicate;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.data.Gift;
import org.jel.game.data.StreamUtils;
import org.jel.game.data.strategy.Strategy;
import org.jel.game.data.strategy.StrategyData;
import org.jel.game.data.strategy.StrategyObject;
import org.jel.game.utils.Graph;

public final class DefensiveStrategyImpl implements Strategy {
	private static final int MAX_ITERATIONS = 100;
	private Graph<City> graph;
	private StrategyObject object;

	@Override
	public boolean acceptGift(final Clan sourceClan, final Clan destinationClan, final Gift gift, final double oldValue,
			final DoubleConsumer newValue, final StrategyObject strategyObject) {
		final var own = BuiltinShared.sum(destinationClan);
		final var giftValue = BuiltinShared.sum(gift);
		final var prop = own == 0 ? Math.random() * 2 : (giftValue / own);
		newValue.accept(oldValue + (prop * 0.05 * strategyObject.getRelationship(sourceClan, destinationClan)));
		return true;
	}

	@Override
	public void applyStrategy(final Clan clan, final Graph<City> cities, final StrategyObject obj) {
		this.object = obj;
		this.graph = cities;
		final var dt = clan.getData();
		if (dt instanceof DefensiveStrategyData dsd) {
			final var ds = dsd.getStrategy();
			if (ds == DefensiveStrategy.EXPAND) {
				if (Math.random() > 0.5) {
					this.tryAttacking(clan);
					BuiltinShared.offensiveAttack(clan, cities, obj);
				} else {
					BuiltinShared.offensiveAttack(clan, cities, obj);
					this.tryAttacking(clan);
				}
			} else if (ds == DefensiveStrategy.FORTIFYANDUPGRADE) {
				if (Math.random() > 0.5) {
					BuiltinShared.moderatePlay(cities, obj, clan);
				} else {
					this.defensiveUpgrades(clan);
					this.defensiveCityUpgrades(clan);
					BuiltinShared.moderateResourcesUpgrade(cities, obj, clan);
				}
			} else if (ds == DefensiveStrategy.RECRUIT) {
				BuiltinShared.offensiveRecruiting(cities, obj, clan);
			}
		} else {
			throw new InternalError();
		}
	}

	private void defensiveCityUpgrades(final Clan clan) {
		StreamUtils.getCitiesAsStream(this.graph, clan, (a, b) -> {
			final Predicate<City> predicate = c -> c.getClanId() != clan.getId();
			final var cnt1 = StreamUtils.getCitiesAroundCity(this.graph, a, predicate).count();
			final var cnt2 = StreamUtils.getCitiesAroundCity(this.graph, b, predicate).count();
			if ((cnt1 == cnt2) || ((cnt1 == 0) && (cnt2 == 0))) {
				return Double.compare(a.getDefense(), b.getDefense());
			} else {
				return Long.compare(cnt1, cnt2);
			}
		}).forEach(a -> {
			var b = true;
			var cnter = 0;
			while (b && (cnter < DefensiveStrategyImpl.MAX_ITERATIONS)) {
				b = this.object.upgradeDefense(clan, a);
				cnter++;
			}
		});
	}

	private void defensiveUpgrades(final Clan clan) {
		var b = true;
		var cnter = 0;
		while (b && (cnter < DefensiveStrategyImpl.MAX_ITERATIONS)) {
			b = this.object.upgradeDefense(clan);
			cnter++;
		}
		b = true;
		cnter = 0;
		while (b && (cnter < DefensiveStrategyImpl.MAX_ITERATIONS)) {
			b = this.object.upgradeSoldiers(clan);
			cnter++;
		}
		b = true;
		cnter = 0;
		while (b && (cnter < DefensiveStrategyImpl.MAX_ITERATIONS)) {
			b = this.object.upgradeOffense(clan);
			cnter++;
		}
	}

	@Override
	public StrategyData getData() {
		return new DefensiveStrategyData();
	}

	@Override
	public StrategyData resume(final StrategyObject strategyObject, final byte[] bytes, final boolean hasStrategyData,
			final byte[] dataBytes) {
		this.object = strategyObject;
		if (hasStrategyData) {
			return new DefensiveStrategyData(dataBytes);
		} else {
			return null;
		}
	}

	private void tryAttacking(final Clan clan) {
		StreamUtils.forEach(this.graph, clan, ownCity -> StreamUtils
				.getCitiesAroundCityNot(this.graph, ownCity, ownCity.getClanId()).sorted().forEach(enemy -> {
					final var dOwn = ownCity.getNumberOfSoldiers() * clan.getSoldiersOffenseStrength()
							* clan.getSoldiersStrength();
					final var dTwo = enemy.getDefense() + (enemy.getNumberOfSoldiers() * enemy.getBonus());
					if (dOwn > (dTwo * 1.1)) {
						this.object.attack(ownCity, enemy, ownCity.getClanId(), false, 0, false);
					}
				}));
	}
}
