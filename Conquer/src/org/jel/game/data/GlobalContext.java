package org.jel.game.data;

import java.util.List;

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

	GlobalContext(final List<InstalledScenario> installedMaps, final List<Plugin> plugins,
			final List<StrategyProvider> strategies2, final List<String> pluginNames,
			final List<String> strategyNames) {
		this.installedMaps = installedMaps;
		this.plugins = plugins;
		this.strategies = strategies2;
		this.pluginNames = pluginNames;
		this.strategyNames = strategyNames;
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

	public List<StrategyProvider> getStrategies() {
		return this.strategies;
	}

	public List<String> getStrategyNames() {
		return this.strategyNames;
	}
}
