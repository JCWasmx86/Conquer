package conquer.data.registries;

import conquer.data.strategy.StrategyProvider;

import java.util.List;

/**
 * Interface to support custom StrategyProvider-registrations. (E.g. plugins implemented in scripting languages)
 */
public interface StrategyRegistry {
	/**
	 * Get the registered strategy providers.
	 *
	 * @return List of registered {@link StrategyProvider}s. None may be null.
	 */
	List<StrategyProvider> findProviders();
}
