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
	public boolean acceptGift(Clan sourceClan, Clan destinationClan, Gift gift, double oldValue,
			DoubleConsumer newValue, StrategyObject strategyObject) {
		final var own = BuiltinShared.sum(destinationClan);
		final var giftValue = BuiltinShared.sum(gift);
		final var prop = own == 0 ? Math.random() * 2 : (giftValue / own);
		newValue.accept(oldValue + (prop * 0.05 * strategyObject.getRelationship(sourceClan, destinationClan)));
		return true;
	}

	@Override
	public void applyStrategy(final Clan clan, final byte clanId, final Graph<City> cities, final StrategyObject obj) {
		this.object = obj;
		this.graph = cities;
		final var dt = clan.getData();
		if (dt instanceof DefensiveStrategyData dsd) {
			final var ds = dsd.getStrategy();
			if (ds == DefensiveStrategy.EXPAND) {
				if (Math.random() > 0.5) {
					this.tryAttacking(clanId, clan);
					BuiltinShared.offensiveAttack(clanId, clan, cities, obj);
				} else {
					BuiltinShared.offensiveAttack(clanId, clan, cities, obj);
					this.tryAttacking(clanId, clan);
				}
			} else if (ds == DefensiveStrategy.FORTIFYANDUPGRADE) {
				if (Math.random() > 0.5) {
					BuiltinShared.moderatePlay(clanId, cities, obj, clan);
				} else {
					this.defensiveUpgrades(clanId);
					this.defensiveCityUpgrades(clanId);
					BuiltinShared.moderateResourcesUpgrade(cities, obj, clanId, clan);
				}
			} else if (ds == DefensiveStrategy.RECRUIT) {
				BuiltinShared.offensiveRecruiting(clanId, cities, obj, clan);
			}
		} else {
			throw new InternalError();
		}
	}

	private void defensiveCityUpgrades(final byte i) {
		StreamUtils.getCitiesAsStream(this.graph, i, (a, b) -> {
			final Predicate<City> predicate = c -> c.getClan() != i;
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
				b = this.object.upgradeDefense(i, a);
				cnter++;
			}
		});
	}

	private void defensiveUpgrades(final byte i) {
		var b = true;
		var cnter = 0;
		while (b && (cnter < DefensiveStrategyImpl.MAX_ITERATIONS)) {
			b = this.object.upgradeDefense(i);
			cnter++;
		}
		b = true;
		cnter = 0;
		while (b && (cnter < DefensiveStrategyImpl.MAX_ITERATIONS)) {
			b = this.object.upgradeSoldiers(i);
			cnter++;
		}
		b = true;
		cnter = 0;
		while (b && (cnter < DefensiveStrategyImpl.MAX_ITERATIONS)) {
			b = this.object.upgradeOffense(i);
			cnter++;
		}
	}

	@Override
	public StrategyData getData() {
		return new DefensiveStrategyData();
	}

	private void tryAttacking(final byte i, final Clan clan) {
		StreamUtils.forEach(this.graph, i, ownCity -> StreamUtils
				.getCitiesAroundCityNot(this.graph, ownCity, ownCity.getClan()).sorted().forEach(enemy -> {
					final var dOwn = ownCity.getNumberOfSoldiers() * clan.getSoldiersOffenseStrength()
							* clan.getSoldiersStrength();
					final var dTwo = enemy.getDefense() + (enemy.getNumberOfSoldiers() * enemy.getBonus());
					if (dOwn > (dTwo * 1.1)) {
						this.object.attack(ownCity, enemy, (byte) ownCity.getClan(), false, 0, false);
					}
				}));
	}
}
