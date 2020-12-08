package org.jel.game.testsuite;

import java.io.File;

import org.jel.game.data.GlobalContext;
import org.jel.game.data.InstalledScenario;
import org.jel.game.data.Reader;
import org.jel.game.data.XMLReader;

public final class Testsuite2 {
	private int numErrors = 0;

	public static void main(String[] args) {
		final var suite = new Testsuite2();
		final var numErrors = suite.start();
		System.exit(numErrors == 0 ? 0 : 1);
	}

	private int start() {
		XMLReader.setThrowableConsumer(this::throwable);
		final var context = XMLReader.getInstance().readInfo(true);
		checkContext(context);
		buildGames(context);
		return numErrors;
	}

	private void buildGames(GlobalContext context) {
		if (context.getInstalledMaps().isEmpty()) {
			error("No scenarios found!");
		}
		injectPlugin(context);
		for (var scenario : context.getInstalledMaps()) {
			buildGame(scenario, context);
		}
	}

	private void injectPlugin(GlobalContext context) {
		var maliciousPlugin = new MaliciousPlugin();
		context.getPlugins().add(maliciousPlugin);
		context.getPluginNames().add(maliciousPlugin.getClass().getCanonicalName());
	}

	private void buildGame(InstalledScenario scenario, GlobalContext context) {
		if (scenario == null) {
			error("scenario==null");
			return;
		}
		var file = new File(scenario.file());
		if (!file.exists()) {
			error(file + " doesn\'t exist!");
			return;
		}
		var thumbnail = new File(scenario.thumbnail());
		if (!thumbnail.exists()) {
			error(thumbnail + " doesn\'t exist!");
			return;
		}
		if (scenario.name() == null) {
			error("scenario.name()==null");
			return;
		}
		final var reader = new Reader(scenario.file());
		final var game = reader.buildGame();
		if (game == null) {
			error("game==null");
			return;
		} else {
			success("" + scenario.name() + " is correct!");
		}
		game.addContext(context);
		game.init();
		while(!game.onlyOneClanAlive()) {
			game.executeActions();
		}
	}

	private void checkContext(final GlobalContext context) {
		if (context == null) {
			error("context==null");
			return;
		}
		if (context.getPluginNames().size() != context.getPlugins().size()) {
			error("context.getPluginNames().size()!=context.getPlugins().size()");
			return;
		} else {
			success("context.getPluginNames().size()==context.getPlugins().size()");
		}
		if (context.getStrategyNames().size() != context.getStrategies().size()) {
			error("context.getStrategyNames().size()!=context.getStrategies().size()");
			return;
		} else {
			success("context.getStrategyNames().size()==context.getStrategies().size()");
		}
		for (var i = 0; i < context.getPlugins().size(); i++) {
			var plugin = context.getPlugins().get(i);
			var name = context.getPluginNames().get(i);
			if (plugin == null) {
				error("plugin==null");
				continue;
			}
			if (name == null) {
				error("name==null");
				continue;
			}
			if (!plugin.getClass().getCanonicalName().equals(name)) {
				error("plugin.getClass().getCanonicalName()!=name");
			} else {
				success("plugin.getClass().getCanonicalName()==name");
			}
		}
		for (var i = 0; i < context.getStrategies().size(); i++) {
			var strategy = context.getStrategies().get(i);
			var name = context.getStrategyNames().get(i);
			if (strategy == null) {
				error("strategy==null");
				continue;
			}
			if (name == null) {
				error("name==null");
				continue;
			}
			if (!strategy.getClass().getCanonicalName().equals(name)) {
				error("strategy.getClass().getCanonicalName()!=name");
			} else {
				success("strategy.getClass().getCanonicalName()==name");
			}
		}
	}

	private void error(String message) {
		System.err.println("[ERROR] " + message);
		numErrors++;
	}

	private void throwable(Throwable t) {
		System.err.println("Unexpected throwable: ");
		t.printStackTrace();
		numErrors++;
	}

	private void success(String message) {
		System.out.println("[SUCCESS] " + message);
	}
}
