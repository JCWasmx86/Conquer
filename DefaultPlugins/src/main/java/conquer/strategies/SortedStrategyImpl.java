package conquer.strategies;

import conquer.data.Gift;
import conquer.data.ICity;
import conquer.data.IClan;
import conquer.data.Resource;
import conquer.data.Shared;
import conquer.data.StreamUtils;
import conquer.data.strategy.Strategy;
import conquer.data.strategy.StrategyData;
import conquer.data.strategy.StrategyObject;
import conquer.utils.Graph;
import conquer.utils.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleConsumer;
import java.util.stream.Collectors;

public final class SortedStrategyImpl implements Strategy {
    private static final double MAXIMUM_VARIANCE = 0.3;
    private static final double FIFTY_FIFTY_PROBABILITY = 0.5;
    private List<ICity> cities;
    private final Map<ICity, Pair<Double, Double>> values = new HashMap<>();
    private final List<IClan> gifts;
    private int counter;

    public SortedStrategyImpl() {
        this.cities = new ArrayList<>();
        this.gifts = new ArrayList<>();
    }

    @Override
    public boolean acceptGift(final IClan sourceClan, final IClan destinationClan, final Gift gift,
                              final double oldValue, final DoubleConsumer newValue, final StrategyObject strategyObject) {
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
    public void applyStrategy(final IClan clan, final Graph<ICity> cities, final StrategyObject obj) {
        if (this.counter == 7) {
            this.counter = 0;
            this.gifts.clear();
        } else {
            this.counter++;
        }
        this.refreshList(clan, cities);
        this.attack(cities, obj, clan);
        this.upgradeCities(cities, clan, obj);
        this.upgradeClan(clan);
    }

    private void attack(final Graph<ICity> graph, final StrategyObject obj, final IClan clan) {
        this.cities.forEach(target -> {
            final var own = StreamUtils.getCitiesAsStream(graph, a -> graph.isConnected(a, target))
                    .sorted(Comparator.comparingLong(ICity::getNumberOfSoldiers))
                    .collect(Collectors.toList());
            own.forEach(ownCity -> {
                final var pair = this.values.get(target);
                final var second = pair.second();
                final var ownCitySoldiers = ownCity.getNumberOfSoldiers();
                final var factor = clan.getSoldiersOffenseStrength() * clan.getSoldiersStrength();
                if (this.tryRecruiting(clan, factor, second, ownCity, ownCitySoldiers, obj)) {
                    return;// We are too weak to attack.
                }
                final long numberOfSoldiersUsed;
                if ((ownCitySoldiers > second) || ((ownCitySoldiers * factor) > second)) {
                    numberOfSoldiersUsed = second > ownCitySoldiers ? ownCitySoldiers : second.longValue();
                } else {
                    return;
                }
                obj.attack(ownCity, target, true, numberOfSoldiersUsed);
            });
        });
    }

    @Override
    public StrategyData getData() {
        return null;
    }

    private void refreshList(final IClan clan, final Graph<ICity> cities2) {
        this.values.clear();
        StreamUtils.getCitiesAsStreamNot(cities2, clan).forEach(a -> {
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
                .getCitiesAsStreamNot(cities2, clan,
                        a -> cities2.getConnected(a).stream().anyMatch(b -> b.getClan() == clan))
                .sorted((a, b) -> {
                    final var pA = this.values.get(a);
                    final var pB = this.values.get(b);
                    final var ratioA = pA.first() / (pA.second() == 0 ? 1 : pA.second());
                    final var ratioB = pB.first() / (pB.second() == 0 ? 1 : pB.second());
                    return Double.compare(ratioB, ratioA);// The cities with a high people/soldiers ratio come first.
                }).collect(Collectors.toList());
    }

    private boolean tryRecruiting(final IClan clan, final double factor, final Double second, final ICity ownCity,
                                  final long ownCitySoldiers, final StrategyObject obj) {
        if (second > (ownCitySoldiers * factor)) {
            obj.recruitSoldiers(clan.getCoins() * 0.25, ownCity, true, ownCity.getNumberOfPeople());
            return second > (ownCitySoldiers * factor);
        }
        return false;
    }

    private void upgradeCities(final Graph<ICity> cities, final IClan clan, final StrategyObject obj) {
        var didUpgrade = true;
        final var ownCities = StreamUtils.getCitiesAsStream(cities, clan).collect(Collectors.toList());
        while (didUpgrade) {
            var num = 0;
            for (final var c : ownCities) {
                var flag = false;
                flag = obj.upgradeDefense(c);
                for (final Resource r : Resource.values()) {
                    flag |= obj.upgradeResource(r, c);
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

    private void upgradeClan(final IClan clan) {
        while (true) {
            var flag = false;
            flag = clan.upgradeSoldiersDefense();
            flag |= clan.upgradeSoldiersOffense();
            flag |= clan.upgradeSoldiers();
            if (!flag) {
                break;
            }
        }
    }

}
