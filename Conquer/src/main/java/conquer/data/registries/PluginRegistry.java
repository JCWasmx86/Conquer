package conquer.data.registries;

import java.util.List;

import conquer.plugins.Plugin;

/**
 * Interface to support custom plugin-registrations. (E.g. plugins implemented in scripting languages)
 */
public interface PluginRegistry {
	/**
	 * Get the registered plugins.
	 *
	 * @return List of registered plugins. None may be null.
	 */
	List<Plugin> findPlugins();
}
