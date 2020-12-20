package org.jel.game.testsuite;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.data.ConquerInfo;
import org.jel.game.data.Gift;
import org.jel.game.data.GlobalContext;
import org.jel.game.data.InstalledScenario;
import org.jel.game.data.Reader;
import org.jel.game.data.XMLReader;

/**
 * Passes bad arguments to functions.
 */
public final class Testsuite2 extends Testsuite {
	public static void main(final String[] args) {
		final var suite = new Testsuite2();
		final var numErrors = suite.start();
		System.out.println(numErrors + " errors");
		System.exit(numErrors == 0 ? 0 : 1);
	}

	private void buildGame(final InstalledScenario scenario, final GlobalContext context) {
		if (scenario == null) {
			this.error("scenario==null");
			return;
		}
		final var file = new File(scenario.file());
		if (!file.exists()) {
			this.error(file + " doesn\'t exist!");
			return;
		}
		final var thumbnail = new File(scenario.thumbnail());
		if (!thumbnail.exists()) {
			this.error(thumbnail + " doesn\'t exist!");
			return;
		}
		if (scenario.name() == null) {
			this.error("scenario.name()==null");
			return;
		}
		final var reader = new Reader(scenario.file());
		final var game = reader.buildGame();
		if (game == null) {
			this.error("game==null");
			return;
		} else {
			this.success("" + scenario.name() + " is correct!");
		}
		game.addContext(context);
		game.setErrorHandler(this::throwable);
		game.init();
		for (var i = 0; i < 2; i++) {
			game.executeActions();
		}
		this.tryBadValues(game);
		while (!game.onlyOneClanAlive()) {
			game.executeActions();
			if (game.currentRound() == 10000) {
				break;
			}
		}
	}

	private void buildGames(final GlobalContext context) {
		if (context.getInstalledMaps().isEmpty()) {
			this.error("No scenarios found!");
		}
		this.injectPlugin(context);
		for (final var scenario : context.getInstalledMaps()) {
			this.buildGame(scenario, context);
		}
	}

	private void checkContext(final GlobalContext context) {
		if (context == null) {
			this.error("context==null");
			return;
		}
		if (context.getPluginNames().size() != context.getPlugins().size()) {
			this.error("context.getPluginNames().size()!=context.getPlugins().size()");
			return;
		} else {
			this.success("context.getPluginNames().size()==context.getPlugins().size()");
		}
		if (context.getStrategyNames().size() != context.getStrategies().size()) {
			this.error("context.getStrategyNames().size()!=context.getStrategies().size()");
			return;
		} else {
			this.success("context.getStrategyNames().size()==context.getStrategies().size()");
		}
		for (var i = 0; i < context.getPlugins().size(); i++) {
			final var plugin = context.getPlugins().get(i);
			final var name = context.getPluginNames().get(i);
			if (plugin == null) {
				this.error("plugin==null");
				continue;
			}
			if (name == null) {
				this.error("name==null");
				continue;
			}
			if (!plugin.getClass().getCanonicalName().equals(name)) {
				this.error("plugin.getClass().getCanonicalName()!=name");
			} else {
				this.success("plugin.getClass().getCanonicalName()==name");
			}
		}
		for (var i = 0; i < context.getStrategies().size(); i++) {
			final var strategy = context.getStrategies().get(i);
			final var name = context.getStrategyNames().get(i);
			if (strategy == null) {
				this.error("strategy==null");
				continue;
			}
			if (name == null) {
				this.error("name==null");
				continue;
			}
			if (!strategy.getClass().getCanonicalName().equals(name)) {
				this.error("strategy.getClass().getCanonicalName()!=name");
			} else {
				this.success("strategy.getClass().getCanonicalName()==name");
			}
		}
	}

	private void injectPlugin(final GlobalContext context) {
		final var maliciousPlugin = new MaliciousPlugin();
		context.getPlugins().add(maliciousPlugin);
		context.getPluginNames().add(maliciousPlugin.getClass().getCanonicalName());
	}

	private int start() {
		XMLReader.setThrowableConsumer(this::throwable);
		final var context = XMLReader.getInstance().readInfo(true);
		this.checkContext(context);
		this.buildGames(context);
		return this.numberOfErrors;
	}

	private void tryBadValues(final ConquerInfo info) {
		// Just some sample values.
		final City a = info.getCities().getValue(0);
		final City b = info.getCities().getValue(1);
		final Clan c = info.getClan(0);
		final Clan d = info.getClan(1);
		this.expect(() -> info.attack(a, null, 0, false, 0), IllegalArgumentException.class);
		this.expect(() -> info.attack(null, b, 0, false, 0), IllegalArgumentException.class);
		this.expect(() -> info.attack(a, b, 1, true, -1), IllegalArgumentException.class);
		this.expect(() -> info.attack(a, b, -1, true, 0), IllegalArgumentException.class);
		this.expect(() -> info.attack(a, b, 120, true, 0), IllegalArgumentException.class);
		this.expect(() -> info.attack(null, b, c, false, 0), IllegalArgumentException.class);
		this.expect(() -> info.attack(a, null, c, false, 0), IllegalArgumentException.class);
		this.expect(() -> info.attack(a, b, null, false, 0), IllegalArgumentException.class);
		this.expect(() -> info.attack(a, b, c, false, -1), IllegalArgumentException.class);
		this.expect(() -> info.attack(a, b, 1, false, 0, true), IllegalArgumentException.class);
		this.expect(() -> info.attack(a, b, -1, false, 0, false), IllegalArgumentException.class);
		this.expect(() -> info.defenseStrengthOfCity(null), IllegalArgumentException.class);
		this.expect(() -> info.getClan(-1), IllegalArgumentException.class);
		this.expect(() -> info.getClan(500), IllegalArgumentException.class);
		this.expect(() -> info.getClanNames().add(""), UnsupportedOperationException.class);
		this.expect(() -> info.getCoins().add(0.0), UnsupportedOperationException.class);
		this.expect(() -> info.getColors().add(null), UnsupportedOperationException.class);
		this.expect(() -> info.getRelationship((Clan) null, (Clan) null), IllegalArgumentException.class);
		this.expect(() -> info.getRelationship((Clan) null, a), IllegalArgumentException.class);
		this.expect(() -> info.getRelationship(-1, -1), IllegalArgumentException.class);
		this.expect(() -> info.getRelationship(500, 500), IndexOutOfBoundsException.class);
		this.expect(() -> info.getWeakestCityInRatioToSurroundingEnemyCities((List<City>) null),
				IllegalArgumentException.class);
		this.expect(() -> info.getWeakestCityInRatioToSurroundingEnemyCities((Stream<City>) null),
				IllegalArgumentException.class);
		this.expect(() -> info.isDead(null), IllegalArgumentException.class);
		this.expect(() -> info.isDead(-1), IllegalArgumentException.class);
		this.expect(() -> info.maximumNumberOfSoldiersToRecruit(null, 5), IllegalArgumentException.class);
		this.expect(() -> info.maximumNumberOfSoldiersToRecruit(c, -5), IllegalArgumentException.class);
		this.expect(() -> info.maximumNumberOfSoldiersToRecruit(-1, 5), IllegalArgumentException.class);
		this.expect(() -> info.maximumNumberOfSoldiersToRecruit(1222, 5), IllegalArgumentException.class);
		this.expect(() -> info.maximumNumberToMove(-1, 0, 0), IllegalArgumentException.class);
		this.expect(() -> info.maximumNumberToMove(127, 0, 0), IllegalArgumentException.class);
		this.expect(() -> info.maximumNumberToMove(0, -1, 0), IllegalArgumentException.class);
		this.expect(() -> info.maximumNumberToMove(0, 0, -1), IllegalArgumentException.class);
		this.expect(() -> info.maximumNumberToMove(null, -1, -1), IllegalArgumentException.class);
		this.expect(() -> info.maximumNumberToMove(c, -1, 0), IllegalArgumentException.class);
		this.expect(() -> info.maximumNumberToMove(c, 0, -1), IllegalArgumentException.class);
		this.expect(() -> info.maximumNumberToMove(c, a, a, 0), IllegalArgumentException.class);
		this.expect(() -> info.maximumNumberToMove(c, a, b, -1), IllegalArgumentException.class);
		this.expect(() -> info.maximumNumberToMove(null, a, b, -1), IllegalArgumentException.class);
		this.expect(() -> info.maximumNumberToMove(c, a, null, -1), IllegalArgumentException.class);
		// TODO: moveSoldiers
		this.expect(() -> info.sendGift(c, d, null), IllegalArgumentException.class);
		this.expect(() -> info.sendGift(c, null, new Gift()), IllegalArgumentException.class);
		this.expect(() -> info.sendGift(null, d, new Gift()), IllegalArgumentException.class);
		this.expect(() -> info.reachableCities(null), IllegalArgumentException.class);
		this.expect(() -> info.recruitSoldiers(0, null, null, false, 0), IllegalArgumentException.class);
		this.expect(() -> info.recruitSoldiers(-1, c, a, false, -1), IllegalArgumentException.class);
		this.expect(() -> info.recruitSoldiers(0, c, b, true, -1), IllegalArgumentException.class);
		this.expect(() -> info.recruitSoldiers(0, c, null, false, 0), IllegalArgumentException.class);
		this.expect(() -> info.upgradeDefense(-1), IllegalArgumentException.class);
		this.expect(() -> info.upgradeDefense(120), IllegalArgumentException.class);
		this.expect(() -> info.upgradeOffense(-1), IllegalArgumentException.class);
		this.expect(() -> info.upgradeOffense(120), IllegalArgumentException.class);
		this.expect(() -> info.upgradeSoldiers(-1), IllegalArgumentException.class);
		this.expect(() -> info.upgradeSoldiers(120), IllegalArgumentException.class);
		this.expect(() -> info.upgradeDefense(-1, a), IllegalArgumentException.class);
		this.expect(() -> info.upgradeDefense(120, a), IllegalArgumentException.class);
	}
}
