package conquer.data.registries;

import java.util.List;

import conquer.data.strategy.StrategyProvider;

/**
 * Interface to support custom StrategyProvider-registrations. (E.g. providers/strategies implemented in scripting
 * languages)
 */
public interface StrategyRegistry {
	/**
	 * Get the registered strategy providers.
	 *
	 * @return List of registered {@link StrategyProvider}s. None may be null.
	 */
	List<StrategyProvider> findProviders();
}
