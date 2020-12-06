package org.jel.game.testsuite;

import java.io.IOException;

import org.jel.game.data.GlobalContext;
import org.jel.game.data.InstalledScenario;
import org.jel.game.data.XMLReader;
import org.jel.game.init.ExtendedOutputStream;
import org.jel.game.init.Initializer;
import org.jel.game.init.Installer;

public class Testsuite {
	private int numErrors = 0;

	public static void main(String[] args) {
		final var suite = new Testsuite();
		int numErrors = suite.start();
		System.exit(numErrors == 0 ? 0 : 1);
	}

	private int start() {
		// Just a normal installation
		testChooserEqualsNull();
		new Installer(a -> a.length - 1, new ExtendedOutputStream() {

			@Override
			public void write(int b) throws IOException {
				System.out.write(b);
			}

			@Override
			public void write(String s) throws IOException {
				System.out.println(s);
			}
		}, this::throwable).run();
		try {
			Initializer.INSTANCE().initialize(this::throwable);
		} catch (Throwable t) {
			throwable(t);
		}
		boolean didThrow = false;
		try {
			Initializer.INSTANCE().initialize(this::throwable);
		} catch (Throwable t) {
			success("Got an exception while initialising a second time: " + t.getMessage());
			didThrow = true;
		}
		if (!didThrow) {
			error("Didn't throw an exception while initialising a second time, although it was expected!");
		}
		final var xmlReader = XMLReader.getInstance();
		XMLReader.setThrowableConsumer(this::throwable);
		checkContextWithoutInstantiation(xmlReader.readInfo(false));
		checkContextWithInstantiation(xmlReader.readInfo(true));
		return numErrors;
	}

	private void checkContextWithInstantiation(GlobalContext ctx) {
		if (ctx == null) {
			error("readInfo(true) returned null!");
			return;
		}
		if (ctx.getPlugins() == null) {
			error("readInfo(true).getPlugins() returned null!");
			return;
		}
		if (ctx.getStrategies() == null) {
			error("readInfo(true).getStrategies() returned null!");
			return;
		}
		if (ctx.getInstalledMaps() == null) {
			error("readInfo(true).getInstalledMaps() returned null!");
			return;
		}
		if (ctx.getPluginNames() == null) {
			error("readInfo(true).getPluginNames() returned null!");
			return;
		}
		if (ctx.getStrategyNames() == null) {
			error("readInfo(true).getStrategyNames() returned null!");
			return;
		}
		ctx.getInstalledMaps().forEach(this::checkScenario);
		ctx.getPluginNames().forEach(a -> {
			if (a == null) {
				error("Pluginname is null");
			}
		});
		ctx.getStrategyNames().forEach(a -> {
			if (a == null) {
				error("Strategyname is null");
			}
		});
		ctx.getPlugins().forEach(a -> {
			if (a == null) {
				error("Plugin is null");
			}
		});
		ctx.getStrategies().forEach(a -> {
			if (a == null) {
				error("Strategy is null");
			}
		});
	}

	private void checkContextWithoutInstantiation(GlobalContext ctx) {
		if (ctx == null) {
			error("readInfo(false) returned null!");
			return;
		}
		if (ctx.getPlugins() == null) {
			error("readInfo(false).getPlugins() returned null!");
			return;
		}
		if (ctx.getStrategies() == null) {
			error("readInfo(false).getStrategies() returned null!");
			return;
		}
		if (ctx.getInstalledMaps() == null) {
			error("readInfo(false).getInstalledMaps() returned null!");
			return;
		}
		if (ctx.getPluginNames() == null) {
			error("readInfo(false).getPluginNames() returned null!");
			return;
		}
		if (ctx.getStrategyNames() == null) {
			error("readInfo(false).getStrategyNames() returned null!");
			return;
		}
		if (!ctx.getPlugins().isEmpty()) {
			error("The number of plugins in an uninstantiated context is not zero!");
		}
		if (!ctx.getStrategies().isEmpty()) {
			error("The number of plugins in an uninstantiated context is not zero!");
		}
		ctx.getInstalledMaps().forEach(this::checkScenario);
		ctx.getPluginNames().forEach(a -> {
			if (a == null) {
				error("Pluginname is null");
			}
		});
		ctx.getStrategyNames().forEach(a -> {
			if (a == null) {
				error("Strategyname is null");
			}
		});
	}

	private void checkScenario(InstalledScenario is) {
		if (is == null) {
			error("InstalledScenario is null");
			return;
		}
		if (is.file() == null) {
			error("InstalledScenario.file() is null");
		}
		if (is.name() == null) {
			error("InstalledScenario.name() is null");
		}
		if (is.thumbnail() == null) {
			error("InstalledScenario.thumbnail() is null");
		}
	}

	private void testChooserEqualsNull() {
		try {
			new Installer(null, null, null);
		} catch (Throwable t) {
			success("Got expected exception while instantiating Installer with a null-Chooser: " + t.getMessage());
			return;
		}
		error("Didn't get any exception while instantiating Installer with a null-Chooser");
	}

	private void success(String message) {
		System.out.println("[SUCCESS] " + message);
	}

	private void error(String message) {
		System.err.println("[ERROR] " + message);
		numErrors++;
	}

	private void throwable(Throwable throwable) {
		System.err.println("Unexpected throwable: ");
		throwable.printStackTrace();
		numErrors++;
	}

}
