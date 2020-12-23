package org.jel.game.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jel.game.data.strategy.StrategyProvider;
import org.jel.game.plugins.Plugin;

/**
 * Describes the whole info.xml file.
 */
public final class GlobalContext {
	private final List<InstalledScenario> installedMaps;
	private final List<Plugin> plugins;
	private final List<StrategyProvider> strategies;
	private final List<String> pluginNames;
	private final List<String> strategyNames;
	private final List<ConquerInfoReaderFactory> readers;

	private final List<String> readerNames;

	GlobalContext(final List<InstalledScenario> installedMaps, final List<Plugin> plugins,
			final List<StrategyProvider> strategies, final List<ConquerInfoReaderFactory> readers,
			final List<String> pluginNames, final List<String> strategyNames, final List<String> readerNames) {
		this.installedMaps = installedMaps;
		this.plugins = plugins;
		this.strategies = strategies;
		this.pluginNames = pluginNames;
		this.strategyNames = strategyNames;
		this.readers = readers;
		this.readerNames = readerNames;
	}

	public List<InstalledScenario> getInstalledMaps() {
		return this.installedMaps;
	}

	public List<String> getPluginNames() {
		return this.pluginNames;
	}

	public List<Plugin> getPlugins() {
		return this.plugins;
	}

	public List<String> getReaderNames() {
		return this.readerNames;
	}

	public List<ConquerInfoReaderFactory> getReaders() {
		return this.readers;
	}

	public List<StrategyProvider> getStrategies() {
		return this.strategies;
	}

	public List<String> getStrategyNames() {
		return this.strategyNames;
	}

	public ConquerInfo loadInfo(final InstalledScenario is) {
		final var list = this.readers.stream()
				.sorted((a, b) -> Integer.compare(a.getMagicNumber().length, b.getMagicNumber().length))
				.collect(Collectors.toList());
		final var maxLength = list.get(list.size() - 1).getMagicNumber().length;
		try (var stream = Files.newInputStream(Paths.get(new File(is.file()).toURI()))) {
			final var b = new byte[maxLength];
			final var n = stream.read(b);
			for (final var factory : list) {
				final var magic = factory.getMagicNumber();
				if (magic.length > n) {
					continue;
				}
				if (Arrays.equals(b, 0, magic.length, magic, 0, magic.length)) {
					final var reader = factory.getForFile(is.file());
					return reader.build();
				}
			}
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		throw new UnsupportedOperationException("No supported file format");
	}
}
