package conquer.data;

import conquer.data.strategy.StrategyProvider;
import conquer.plugins.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Describes a whole context. A context is defined by the available scenarios,
 * plugins, strategies and ConquerInfoReaderFactories. Every returned list is
 * mutable.
 */
public final class GlobalContext {
	private List<InstalledScenario> installedMaps;
	private List<Plugin> plugins;
	private List<StrategyProvider> strategies;
	private List<String> pluginNames;
	private List<String> strategyNames;
	private List<ConquerInfoReaderFactory> readers;
	private List<String> readerNames;

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

	/**
	 * Merges {@code this} with {@code other}. There won't be any duplicate items in
	 * any of the lists.
	 *
	 * @param other The other context. May not be {@code null}, otherwise an
	 *              {@code IllegalArgumentException} will be thrown.
	 */
	public void mergeWith(final GlobalContext other) {
		if (other == null) {
			throw new IllegalArgumentException("other==null");
		}
		this.installedMaps.addAll(other.installedMaps);
		this.installedMaps = this.installedMaps.stream().distinct().toList();
		this.pluginNames.addAll(other.pluginNames);
		this.pluginNames = this.pluginNames.stream().distinct().toList();
		this.strategyNames.addAll(other.strategyNames);
		this.strategyNames = this.strategyNames.stream().distinct().toList();
		this.readerNames.addAll(other.readerNames);
		this.readerNames = this.readerNames.stream().distinct().toList();
		this.plugins.addAll(other.plugins);
		this.plugins = this.plugins.stream().distinct().toList();
		this.strategies.addAll(other.strategies);
		this.strategies = this.strategies.stream().distinct().toList();
		this.readers.addAll(other.readers);
		this.readers = this.readers.stream().distinct().toList();
	}

	/**
	 * Create a game state from a given scenario.
	 *
	 * @param is The scenario to instantiate.May not be {@code null}, otherwise an
	 *           {@code IllegalArgumentException} will be thrown.
	 * @return A game state.
	 * @throws {@code UnsupportedOperationException} if no reader for the file
	 *                format was found.
	 */
	public ConquerInfo loadInfo(final InstalledScenario is) {
		if (is == null) {
			throw new IllegalArgumentException("is==null");
		}
		final var list = this.readers.stream()
				.sorted(Comparator.comparingInt(a -> a.getMagicNumber().length))
				.toList();
		if (list.isEmpty()) {
			throw new UnsupportedOperationException("No reader found");
		}
		final var maxLength = list.get(list.size() - 1).getMagicNumber().length;
		// Reverse to start with the longest first.
		// So, if you have two readers: One with [aa,ee,ff] and one with [aa,ee,ff,11],
		// this
		// would match the first one first. This would be quite bad, as it might not
		// support it entirely.
		Collections.reverse(list);
		final var bytes = this.obtainBytes(is, maxLength);
		for (final var factory : list) {
			final var magic = factory.getMagicNumber();
			if ((magic.length <= bytes.length) && Arrays.equals(bytes, 0, magic.length, magic, 0, magic.length)) {
				final var reader = factory.getForFile(is);
				return reader.build();
			}
		}
		//Second, slower attempt
		final byte[] entireFile;
		try {
			entireFile = this.getAllBytes(is);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		for (final var factory : list) {
			if (factory.accepts(entireFile)) {
				return factory.getForBytes(entireFile).build();
			}
		}
		throw new UnsupportedOperationException("No supported file format");
	}

	private byte[] getAllBytes(final InstalledScenario is) throws IOException {
		if (is.file() != null) {
			try (final var stream = Files.newInputStream(Paths.get(new File(is.file()).toURI()))) {
				return stream.readAllBytes();
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			return is.in().readAllBytes();
		}
	}

	@Override
	public String toString() {
		return "GlobalContext [installedMaps=" + this.installedMaps + ", plugins=" + this.plugins + ", strategies="
				+ this.strategies + ", pluginNames=" + this.pluginNames + ", strategyNames=" + this.strategyNames
				+ ", readers=" + this.readers + ", readerNames=" + this.readerNames + "]";
	}

	private byte[] obtainBytes(final InstalledScenario is, final int maxLength) {
		if (is.file() != null) {
			try (final var stream = Files.newInputStream(Paths.get(new File(is.file()).toURI()))) {
				final var b = new byte[maxLength];
				stream.read(b);
				return b;
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			return is.in().getMagicNumber(maxLength);
		}
	}
}
