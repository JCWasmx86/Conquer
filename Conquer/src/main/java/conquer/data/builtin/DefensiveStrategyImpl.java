package conquer.data.builtin;

import conquer.data.Gift;
import conquer.data.ICity;
import conquer.data.IClan;
import conquer.data.StreamUtils;
import conquer.data.strategy.Strategy;
import conquer.data.strategy.StrategyData;
import conquer.data.strategy.StrategyObject;
import conquer.utils.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.DoubleConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class DefensiveStrategyImpl implements Strategy {
    private static final int MAX_ITERATIONS = 100;
    private Graph<ICity> graph;
    private StrategyObject object;

    @Override
    public boolean acceptGift(final IClan sourceClan, final IClan destinationClan, final Gift gift,
                              final double oldValue, final DoubleConsumer newValue, final StrategyObject strategyObject) {
        BuiltinShared.assertThat(sourceClan != null, "sourceClan==null");
        BuiltinShared.assertThat(destinationClan != null, "destinationClan==null");
        BuiltinShared.assertThat(gift != null, "gift==null");
        BuiltinShared.assertThat(newValue != null, "newValue==null");
        BuiltinShared.assertThat(strategyObject != null, "strategyObject==null");
        BuiltinShared.assertThat(oldValue >= 0, "oldValue<0: " + oldValue);
        final var own = BuiltinShared.sum(destinationClan);
        final var giftValue = BuiltinShared.sum(gift);
        final var prop = own == 0 ? Math.random() * 2 : (giftValue / own);
        newValue.accept(oldValue + (prop * 0.05 * strategyObject.getRelationship(sourceClan, destinationClan)));
        return true;
    }

    @Override
    public void applyStrategy(final IClan clan, final Graph<ICity> cities, final StrategyObject obj) {
        BuiltinShared.assertThat(clan != null, "clan==null");
        BuiltinShared.assertThat(cities != null, "cities==null");
        BuiltinShared.assertThat(obj != null, "obj==null");
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
            this.sendGift(clan);
        } else {
            throw new InternalError();
        }
    }

    private void sendGift(final IClan clan) {
        if (StreamUtils.getCitiesAsStream(this.graph, clan).count() < 2) {
            return;// We can't waste our power.
        }
        // Create a working copy.
        final var list = new ArrayList<>(clan.getResources());
        Collections.sort(list);
        if (list.get(0) < 1500) {
            // Don't waste resources, we may need them.
        }
        final var giftedResources = new ArrayList<Double>();
        for (final var totalResource : clan.getResources()) {
            // Give up to 50% of a resource as gift.
            final var amount = Math.random() * 0.5 * totalResource;
            giftedResources.add(Math.min(totalResource, amount));
        }
        final var totalCoins = clan.getCoins() * Math.random() * 0.33;
        final var gift = new Gift(giftedResources, totalCoins);
        final var clans = StreamUtils.getCitiesAsStream(this.graph).map(ICity::getClan).distinct()
                .filter(a -> a != clan).sorted(Comparator.comparingDouble(a -> this.object.getRelationship(a, clan)))
                .collect(Collectors.toList());
        // Improve relationship, start from the one with the worst relationship.
        for (final var otherClan : clans) {
            if (Math.random() < 0.75) {
                continue;
            }
            if (this.object.sendGift(clan, otherClan, gift)) {
                break;
            }
        }
    }

    private void defensiveCityUpgrades(final IClan clan) {
        // Try to build fortresses that are difficult to conquer, starting with
        // upgrading the weakest cities.
        StreamUtils.getCitiesAsStream(this.graph, clan, (a, b) -> {
            final Predicate<ICity> predicate = c -> c.getClan() != clan;
            final var cnt1 = StreamUtils.getCitiesAroundCity(this.object, this.graph, a, predicate).count();
            final var cnt2 = StreamUtils.getCitiesAroundCity(this.object, this.graph, b, predicate).count();
            if ((cnt1 == cnt2) || ((cnt1 == 0) && (cnt2 == 0))) {
                return Double.compare(a.getDefense(), b.getDefense());
            } else {
                return Long.compare(cnt1, cnt2);
            }
        }).forEach(a -> {
            var b = true;
            var cnter = 0;
            while (b && (cnter < DefensiveStrategyImpl.MAX_ITERATIONS)) {
                b = this.object.upgradeDefense(a);
                cnter++;
            }
        });
    }

    private void defensiveUpgrades(final IClan clan) {
        var b = true;
        var cnter = 0;
        while (b && (cnter < DefensiveStrategyImpl.MAX_ITERATIONS)) {
            b = clan.upgradeSoldiersDefense();
            cnter++;
        }
        b = true;
        cnter = 0;
        while (b && (cnter < DefensiveStrategyImpl.MAX_ITERATIONS)) {
            b = clan.upgradeSoldiers();
            cnter++;
        }
        b = true;
        cnter = 0;
        while (b && (cnter < DefensiveStrategyImpl.MAX_ITERATIONS)) {
            b = clan.upgradeSoldiersOffense();
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

    private void tryAttacking(final IClan clan) {
        StreamUtils.forEach(this.graph, clan,
                ownCity -> StreamUtils.getCitiesAroundCityNot(this.object, this.graph, ownCity, ownCity.getClan())
                        .sorted().forEach(enemy -> {
                            // Strength of the own soldiers
                            final var dOwn = ownCity.getNumberOfSoldiers() * clan.getSoldiersOffenseStrength()
                                    * clan.getSoldiersStrength();
                            // Estimated strength of the enemy
                            final var dTwo = enemy.getDefense() + (enemy.getNumberOfSoldiers() * enemy.getBonus());
                            // Only attack, if a victory is very probable.
                            if (dOwn > (dTwo * 1.1)) {
                                this.object.attack(ownCity, enemy, false, 0);
                            }
                        }));
    }
}
