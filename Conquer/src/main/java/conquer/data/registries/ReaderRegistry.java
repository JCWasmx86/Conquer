package conquer.data.registries;

import java.util.List;

import conquer.data.ConquerInfoReaderFactory;

/**
 * Interface to support custom reader factory-registrations. (E.g. factories implemented in scripting languages)
 */
public interface ReaderRegistry {
	/**
	 * Get the registered factories.
	 *
	 * @return List of registered factories. None may be null.
	 */
	List<ConquerInfoReaderFactory> findFactories();
}
