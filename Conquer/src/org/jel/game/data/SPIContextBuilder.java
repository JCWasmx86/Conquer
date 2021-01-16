package org.jel.game.data;

import java.util.ArrayList;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import org.jel.game.data.strategy.StrategyProvider;
import org.jel.game.plugins.Plugin;

public class SPIContextBuilder {
	public GlobalContext buildContext() {
		final var installedScenarios = new ArrayList<InstalledScenario>();
		final var providers = ServiceLoader.load(InstalledScenarioProvider.class);
		providers.forEach(a -> {
			if (a.getScenarios() == null) {
				return;
			}
			for (var b : a.getScenarios()) {
				if (b != null) {
					installedScenarios.add(b);
				}
			}
		});
		final var strategies = ServiceLoader.load(StrategyProvider.class).stream().map(Provider::get)
				.collect(Collectors.toList());
		final var plugins = ServiceLoader.load(Plugin.class).stream().map(Provider::get).collect(Collectors.toList());
		final var readers = ServiceLoader.load(ConquerInfoReaderFactory.class).stream().map(Provider::get)
				.collect(Collectors.toList());
		return new GlobalContext(installedScenarios, plugins, strategies, readers,
				plugins.stream().map(a -> a.getClass().getName()).collect(Collectors.toList()),
				strategies.stream().map(a -> a.getClass().getName()).collect(Collectors.toList()),
				readers.stream().map(a -> a.getClass().getName()).collect(Collectors.toList()));
	}
}
