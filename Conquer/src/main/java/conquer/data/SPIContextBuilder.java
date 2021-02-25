package conquer.data;

import java.util.ArrayList;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import conquer.data.strategy.StrategyProvider;
import conquer.plugins.Plugin;

/**
 * A context builder that uses SPI to discover plugins, strategies, readers and
 * installed scenarios.
 */
public class SPIContextBuilder {
	/**
	 * Build the context.
	 *
	 * @return The newly built context.
	 */
	public GlobalContext buildContext() {
		final var installedScenarios = new ArrayList<InstalledScenario>();
		final var providers = ServiceLoader.load(InstalledScenarioProvider.class);
		providers.forEach(a -> {
			try {
				final var scenarios = a.getScenarios();
				if (scenarios == null) {
					return;
				}
				for (final var b : scenarios) {
					if (b != null) {
						installedScenarios.add(b);
					}
				}
			} catch (final Exception e) {
				Shared.LOGGER.exception(e);
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
