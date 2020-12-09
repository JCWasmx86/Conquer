package org.jel.game.testsuite;

import java.io.File;

import org.jel.game.data.GlobalContext;
import org.jel.game.data.InstalledScenario;
import org.jel.game.data.Reader;
import org.jel.game.data.XMLReader;

public final class Testsuite2 {
	public static void main(String[] args) {
		final var suite = new Testsuite2();
		final var numErrors = suite.start();
		System.out.println(numErrors + " errors");
		System.exit(numErrors == 0 ? 0 : 1);
	}

	private int numErrors = 0;

	private void buildGame(InstalledScenario scenario, GlobalContext context) {
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
		while (!game.onlyOneClanAlive()) {
			game.executeActions();
			if (game.currentRound() == 10000) {
				break;
			}
		}
	}

	private void buildGames(GlobalContext context) {
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

	private void error(String message) {
		System.err.println("[ERROR] " + message);
		this.numErrors++;
	}

	private void injectPlugin(GlobalContext context) {
		final var maliciousPlugin = new MaliciousPlugin();
		context.getPlugins().add(maliciousPlugin);
		context.getPluginNames().add(maliciousPlugin.getClass().getCanonicalName());
	}

	private int start() {
		XMLReader.setThrowableConsumer(this::throwable);
		final var context = XMLReader.getInstance().readInfo(true);
		this.checkContext(context);
		this.buildGames(context);
		return this.numErrors;
	}

	private void success(String message) {
		System.out.println("[SUCCESS] " + message);
	}

	private void throwable(Throwable t) {
		System.err.println("Unexpected throwable: ");
		t.printStackTrace();
		this.numErrors++;
	}
}
