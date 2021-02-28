package conquer.testsuite;

import conquer.data.*;
import conquer.utils.Graph;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class saves and restores Game-objects and compares them.
 */
public final class Testsuite3 extends Testsuite {
    private static final PlayerGiftCallback CALLBACK = (a, b, c, d, e, f) -> false;

    public static void main(final String[] args) {
        final var suite = new Testsuite3();
        final var numErrors = suite.start();
        System.out.println(numErrors + " errors");
        System.exit(numErrors == 0 ? 0 : 1);
    }

    private void checkCities(final Graph<ICity> cities, final Graph<ICity> cities2) {
        final var n = StreamUtils.getCitiesAsStream(cities).count();
        final var n1 = StreamUtils.getCitiesAsStream(cities2).count();
        if (n != n1) {
            this.error("n != n1");
        }
        final var max = Math.min(n, n1);
        for (var i = 0; i < max; i++) {
            for (var j = 0; j < max; j++) {
                if (i == j) {
                    continue;
                }
                final var cityA = cities.getValue(i);
                final var cityA2 = cities.getValue(j);
                final var cityB = cities2.getValue(i);
                final var cityB2 = cities2.getValue(j);
                if (cities.isConnected(cityA, cityA2) && !cities.isConnected(cityB, cityB2)) {
                    this.error("cities.isConnected(cityA, cityA2)&&!cities.isConnected(cityB, cityB2)");
                }
                if (!cities.isConnected(cityA, cityA2) && cities.isConnected(cityB, cityB2)) {
                    this.error("!cities.isConnected(cityA, cityA2)&&cities.isConnected(cityB, cityB2)");
                }
                if (!cities.isConnected(cityA, cityA2) && !cities.isConnected(cityB, cityB2)) {
                    continue;
                }
                final var weightA = cities.getWeight(cityA, cityA2);
                final var weightB = cities2.getWeight(cityB, cityB2);
                if (!this.nearlyEquals(weightA, weightB)) {
                    this.error(cityA.getName() + "<=>" + cityA2.getName() + ": " + weightA + "//" + weightB);
                }
            }
        }
        final var listA = StreamUtils.getCitiesAsStream(cities).collect(Collectors.toList());
        final var listB = StreamUtils.getCitiesAsStream(cities2).collect(Collectors.toList());
        for (var i = 0; i < max; i++) {
            this.compareCity(listA.get(i), listB.get(i));
        }
    }

    private void checkClans(final List<IClan> clans, final List<IClan> clans2) {
        for (var i = 0; i < clans.size(); i++) {
            final var oldClan = clans.get(i);
            final var newClan = clans2.get(i);
            this.compare(oldClan, newClan);
        }
    }

    private void checkRelations(final int numPlayers, final Graph<Integer> old, final Graph<Integer> restored) {
        for (var i = 0; i < numPlayers; i++) {
            for (var j = 0; j < numPlayers; j++) {
                try {
                    if ((i != j) && (old.getWeight(i, j) != restored.getWeight(i, j))) {
                        this.error(old.getWeight(i, j) + "//" + restored.getWeight(i, j));
                    }
                } catch (final Throwable t) {
                    this.throwable(t);
                }
            }
        }
    }

    private void checkResources(final List<Double> resources, final List<Double> resources2) {
        for (var i = 0; i < Math.min(resources.size(), resources2.size()); i++) {
            if (!this.nearlyEquals(resources.get(i), resources2.get(i))) {
                this.error("resources.get(i)!=resources2.get(i)");
            }
        }
    }

    private void checkSoldiers(final IClan oldClan, final IClan newClan) {
        if (oldClan.getSoldiersLevel() != newClan.getSoldiersLevel()) {
            this.error("oldClan.getSoldiersLevel()!=newClan.getSoldiersLevel()");
        }
        if (oldClan.getSoldiersDefenseLevel() != newClan.getSoldiersDefenseLevel()) {
            this.error("oldClan.getSoldiersDefenseLevel()!=newClan.getSoldiersDefenseLevel()");
        }
        if (oldClan.getSoldiersOffenseLevel() != newClan.getSoldiersOffenseLevel()) {
            this.error("oldClan.getSoldiersOffenseLevel()!=newClan.getSoldiersOffenseLevel()");
        }
        if (!this.nearlyEquals(oldClan.getSoldiersStrength(), newClan.getSoldiersStrength())) {
            this.error("!nearlyEquals(oldClan.getSoldiersStrength(), newClan.getSoldiersStrength())");
        }
        if (!this.nearlyEquals(oldClan.getSoldiersDefenseStrength(), newClan.getSoldiersDefenseStrength())) {
            this.error("!nearlyEquals(oldClan.getSoldiersDefenseStrength(), newClan.getSoldiersDefenseStrength())");
        }
        if (!this.nearlyEquals(oldClan.getSoldiersOffenseStrength(), newClan.getSoldiersOffenseStrength())) {
            this.error("!nearlyEquals(oldClan.getSoldiersOffenseStrength(), newClan.getSoldiersOffenseStrength())");
        }
    }

    private void checkStats(final List<Double> resourceStats, final List<Double> resourceStats2) {
        for (var i = 0; i < Math.min(resourceStats.size(), resourceStats2.size()); i++) {
            if (!this.nearlyEquals(resourceStats.get(i), resourceStats2.get(i))) {
                this.error("resourceStats.get(i)!=resourceStats.get(i)" + "//" + resourceStats.get(i) + "//"
                        + resourceStats2.get(i));
            }
        }
    }

    private void compare(final IClan oldClan, final IClan newClan) {
        final var tmp = this.numberOfErrors;
        if (!oldClan.getName().equals(newClan.getName())) {
            this.error("oldClan.name!=newClan.name");
        }
        if (oldClan.getCoins() != newClan.getCoins()) {
            this.error("oldClan.coins!=newClan.coins");
        }
        if (!oldClan.getColor().equals(newClan.getColor())) {
            this.error("oldClan.color!=newClan.color");
        }
        if (oldClan.getResources().size() != newClan.getResources().size()) {
            this.error("oldClan.getResources().size()!=newClan.getResources().size()");
        }
        this.checkResources(oldClan.getResources(), newClan.getResources());
        if (oldClan.getResourceStats().size() != newClan.getResourceStats().size()) {
            this.error("oldClan.getResourceStats().size()!=newClan.getResourceStats().size()");
        }
        this.checkStats(oldClan.getResourceStats(), newClan.getResourceStats());
        if (oldClan.getStrategy().getClass() != newClan.getStrategy().getClass()) {
            this.error("oldClan.getStrategy().getClass()!=newClan.getStrategy().getClass()");
        }
        if (oldClan.getFlags() != newClan.getFlags()) {
            this.error("oldClan.flags!=newClan.flags");
        }
        this.checkSoldiers(oldClan, newClan);
        if (tmp == this.numberOfErrors) {
            this.success("The clan " + oldClan.getName() + " was restored successfully!");
        }
    }

    private void compare(final ConquerInfo game, final ConquerInfo restoredGame, final String name) {
        final var tmp = this.numberOfErrors;
        if (game.getNumPlayers() != restoredGame.getNumPlayers()) {
            this.error("game.numPlayers!=restored.numPlayers");
        }
        if (game.getClans().size() != restoredGame.getClans().size()) {
            this.error("game.getClans().size()!=restoredGame.getClans().size()");
        }
        if (game.getNumPlayers() != restoredGame.getNumPlayers()) {
            this.error("game.getNumPlayers()!=restoredGame.getNumPlayers()");
        }
        if (game.currentRound() != restoredGame.currentRound()) {
            this.error("game.currentRound()!=restoredGame.currentRound()");
        }
        this.checkRelations(game.getNumPlayers(), game.getRelations(), restoredGame.getRelations());
        this.checkClans(game.getClans(), restoredGame.getClans());
        this.checkCities(game.getCities(), restoredGame.getCities());
        if (tmp == this.numberOfErrors) {
            this.success("The game " + name + " was restored successfully!");
        }
    }

    private void compareCity(final ICity city, final ICity city2) {
        final var tmp = this.numberOfErrors;
        if (!this.nearlyEquals(city.getBonus(), city2.getBonus())) {
            this.error("!nearlyEquals(city.getBonus(), city2.getBonus())");
        }
        if (city.getClanId() != city2.getClanId()) {
            this.error("city.getClanId()!=city2.getClanId()");
        }
        if (!this.nearlyEquals(city.getDefense(), city2.getDefense())) {
            this.error("!nearlyEquals(city.getDefense(), city2.getDefense())");
        }
        if (!this.nearlyEquals(city.getGrowth(), city2.getGrowth())) {
            this.error("!nearlyEquals(city.getGrowth(), city2.getGrowth())");
        }
        final var a = city.getLevels();
        final var b = city2.getLevels();
        if (a.size() != b.size()) {
            this.error("a.size()!=b.size()");
        }
        for (var i = 0; i < Math.min(a.size(), b.size()); i++) {
            if (!a.get(i).equals(b.get(i))) {
                this.error("a.get(i)!=b.get(i)");
            }
        }
        if (!city.getName().equals(city2.getName())) {
            this.error("!city.getName()!=city2.getName()");
        }
        if (city.getNumberOfPeople() != city2.getNumberOfPeople()) {
            this.error("city.getNumberOfPeople()!=city2.getNumberOfPeople()");
        }
        if (city.getNumberOfSoldiers() != city2.getNumberOfSoldiers()) {
            this.error("city.getNumberOfSoldiers()!=city2.getNumberOfSoldiers()");
        }
        final var prodA = city.getProductions();
        final var prodB = city2.getProductions();
        if (prodA.size() != prodB.size()) {
            this.error("a.size()!=b.size()");
        }
        for (var i = 0; i < Math.min(prodA.size(), prodB.size()); i++) {
            if (!this.nearlyEquals(prodA.get(i), prodB.get(i))) {
                this.error("prodA.get(i)!=prodB.get(i)");
            }
        }
        if (city.getX() != city2.getX()) {
            this.error("city.getX()!=city2.getX()");
        }
        if (city.getY() != city2.getY()) {
            this.error("city.getY()!=city2.getY()");
        }
        if (tmp == this.numberOfErrors) {
            this.success("The city " + city.getName() + " was restored successfully!");
        }
    }

    private void continueToPlay(ConquerInfo restoredGame, final String name) {
        for (var i = 0; i < 10; i++) {
            for (var j = 0; j < 5; j++) {
                restoredGame.executeActions();
            }
            this.saveGame(restoredGame, name);
            final var restoredGame1 = this.restore(name);
            if (restoredGame1 == null) {
                return;
            }
            restoredGame1.setPlayerGiftCallback(Testsuite3.CALLBACK);
            this.compare(restoredGame, restoredGame1, name);
            restoredGame = restoredGame1;
        }
    }

    // Prevent errors from not matching doubles...
    private boolean nearlyEquals(final double a, final double b) {
        return Math.abs(a - b) < 0.000001;
    }

    private ConquerInfo restore(final String name) {
        try {
            return Shared.restore(name);
        } catch (final Exception e) {
            this.throwable(e);
            return null;
        }
    }

    private void saveGame(final ConquerInfo game, final String name) {
        try {
            Shared.save(name, game);
        } catch (final Exception e) {
            this.throwable(e);
        }
    }

    private int start() {
        final var ctx = XMLReader.getInstance().readInfo();
        for (final var scenario : ctx.getInstalledMaps()) {
            final var game = ctx.loadInfo(scenario);
            game.addContext(ctx);
            game.setPlayerGiftCallback(Testsuite3.CALLBACK);
            game.init();
            for (var i = 0; i < 5; i++) {
                game.executeActions();
            }
            final var name = scenario.name();
            this.saveGame(game, name);
            final var restoredGame = this.restore(name);
            if (restoredGame == null) {
                continue;
            }
            restoredGame.setPlayerGiftCallback(Testsuite3.CALLBACK);
            this.compare(game, restoredGame, name);
            this.continueToPlay(restoredGame, name);
        }
        return this.numberOfErrors;
    }

}
