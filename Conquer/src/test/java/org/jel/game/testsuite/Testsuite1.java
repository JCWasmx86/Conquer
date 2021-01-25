package org.jel.game.testsuite;

import java.io.IOException;

import org.jel.game.data.GlobalContext;
import org.jel.game.data.InstalledScenario;
import org.jel.game.data.XMLReader;
import org.jel.game.init.ExtendedOutputStream;
import org.jel.game.init.Initializer;
import org.jel.game.init.Installer;

/**
 * This will run as first test in github actions. It will just install the game
 * and then exit. Another testsuite will then run with all other tests. This has
 * to be done in order to activate all plugins.
 */
public final class Testsuite1 extends Testsuite {
	public static void main(final String[] args) {
		final var suite = new Testsuite1();
		final var numErrors = suite.start();
		System.out.println(numErrors + " errors");
		System.exit(numErrors == 0 ? 0 : 1);
	}

	private void checkContextWithoutInstantiation(final GlobalContext ctx) {
		if (ctx == null) {
			this.error("readInfo(false) returned null!");
			return;
		}
		if (ctx.getPlugins() == null) {
			this.error("readInfo(false).getPlugins() returned null!");
			return;
		}
		if (ctx.getStrategies() == null) {
			this.error("readInfo(false).getStrategies() returned null!");
			return;
		}
		if (ctx.getInstalledMaps() == null) {
			this.error("readInfo(false).getInstalledMaps() returned null!");
			return;
		}
		if (ctx.getPluginNames() == null) {
			this.error("readInfo(false).getPluginNames() returned null!");
			return;
		}
		if (ctx.getStrategyNames() == null) {
			this.error("readInfo(false).getStrategyNames() returned null!");
			return;
		}
		if (!ctx.getPlugins().isEmpty()) {
			this.error("The number of plugins in an uninstantiated context is not zero!");
		}
		if (!ctx.getStrategies().isEmpty()) {
			this.error("The number of plugins in an uninstantiated context is not zero!");
		}
		ctx.getInstalledMaps().forEach(this::checkScenario);
		ctx.getPluginNames().forEach(a -> {
			if (a == null) {
				this.error("Pluginname is null");
			}
		});
		ctx.getStrategyNames().forEach(a -> {
			if (a == null) {
				this.error("Strategyname is null");
			}
		});
	}

	private void checkScenario(final InstalledScenario is) {
		if (is == null) {
			this.error("InstalledScenario is null");
			return;
		}
		if (is.file() == null) {
			this.error("InstalledScenario.file() is null");
		}
		if (is.name() == null) {
			this.error("InstalledScenario.name() is null");
		}
		if (is.thumbnail() == null) {
			this.error("InstalledScenario.thumbnail() is null");
		}
	}

	private int start() {
		// Just a normal installation
		this.testChooserEqualsNull();
		// -2, because -1 would install the entire music and this would take too long.
		new Installer(a -> a.length - 2, new ExtendedOutputStream() {

			@Override
			public void write(final int b) throws IOException {
				System.out.write(b);
			}

			@Override
			public void write(final String s) throws IOException {
				System.out.println(s);
			}
		}, this::throwable).run();
		try {
			Initializer.INSTANCE().initialize(this::throwable);
		} catch (final Throwable t) {
			this.throwable(t);
		}
		var didThrow = false;
		try {
			Initializer.INSTANCE().initialize(this::throwable);
		} catch (final Throwable t) {
			this.success("Got an exception while initialising a second time: " + t.getMessage());
			didThrow = true;
		}
		if (!didThrow) {
			this.error("Didn't throw an exception while initialising a second time, although it was expected!");
		}
		final var xmlReader = XMLReader.getInstance();
		XMLReader.setThrowableConsumer(this::throwable);
		this.checkContextWithoutInstantiation(xmlReader.readInfo(false));
		return this.numberOfErrors;
	}

	private void testChooserEqualsNull() {
		try {
			new Installer(null, null, null);
		} catch (final Throwable t) {
			this.success("Got expected exception while instantiating Installer with a null-Chooser: " + t.getMessage());
			return;
		}
		this.error("Didn't get any exception while instantiating Installer with a null-Chooser");
	}

}
