import conquer.data.ConquerInfoReaderFactory;
import conquer.data.DefaultScenarioProvider;
import conquer.data.InstalledScenarioProvider;
import conquer.data.builtin.DefensiveStrategyProvider;
import conquer.data.builtin.ModerateStrategyProvider;
import conquer.data.builtin.OffensiveStrategyProvider;
import conquer.data.builtin.RandomStrategyProvider;
import conquer.data.ri.ScenarioFileReaderFactory;
import conquer.data.strategy.StrategyProvider;

/**
 * The module exporting all required packages.
 */
module conquer {
	requires transitive java.desktop;

	exports conquer.init;
	exports conquer.data;
	exports conquer.utils;
	exports conquer.plugins;
	exports conquer.data.strategy;
	exports conquer.messages;

	uses conquer.data.InstalledScenarioProvider;
	uses conquer.data.strategy.StrategyProvider;
	uses conquer.plugins.Plugin;
	uses conquer.data.ConquerInfoReaderFactory;
	uses conquer.init.InitTask;
	uses conquer.data.registries.ReaderRegistry;
	uses conquer.data.registries.PluginRegistry;
	uses conquer.data.registries.StrategyRegistry;

	provides ConquerInfoReaderFactory with ScenarioFileReaderFactory;
	provides StrategyProvider
		with OffensiveStrategyProvider, DefensiveStrategyProvider, ModerateStrategyProvider,
			RandomStrategyProvider;
	provides InstalledScenarioProvider with DefaultScenarioProvider;
}