package conquer.plugins;

import conquer.data.ICity;
import conquer.data.Result;
import conquer.data.Version;
import conquer.utils.Graph;

import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * The interface every plugin has to implement.
 */
public interface Plugin {
	/**
	 * Returns whether this plugin is compatible to the version of the engine.
	 *
	 * @param version May not be {@code null}. Version of the game engine
	 * @return {@code true} if compatible, {@code false} otherwise.
	 */
	default boolean compatibleTo(final Version version) {
		return true;
	}

	/**
	 * Returns all plugins that conflict with this plugin.
	 *
	 * @return Conflicting plugins.
	 */
	default List<Class<? extends Plugin>> getConflictingPlugins() {
		return List.of();
	}

	/**
	 * Get all dependencies of this plugin.
	 *
	 * @return All dependencies of this plugin.
	 */
	default List<Class<? extends Plugin>> getDependencies() {
		return List.of();
	}

	/**
	 * Called when the game ended
	 *
	 * @param result The result
	 */
	default void exit(final Result result) {
		//Not every plugin has to react
	}

	/**
	 * An optional list of buttons/labels/... that can be registered
	 *
	 * @return A list of components.
	 * @deprecated In the retrospective, it was a bad decision to mix UI and logic.
	 */
	@Deprecated(forRemoval = true)
	default List<Component> getButtons() {
		return List.of();
	}

	/**
	 * Returns the name of the plugin. Has to be unique.
	 *
	 * @return Name of the plugin.
	 */
	String getName();

	/**
	 * Called at the end of every round.
	 *
	 * @param cities All cities
	 * @param ctx    The context with some data.
	 */
	void handle(final Graph<ICity> cities, final Context ctx);

	/**
	 * Called at the start of the game. Can be used for adding handlers.
	 *
	 * @param pluginInterface
	 */
	default void init(final PluginInterface pluginInterface) {
		//Not every plugin needs an initialization
	}

	/**
	 * Called when a saved game is restored
	 *
	 * @param game
	 * @param bytes The written bytes
	 * @throws IOException In case of an IO-Error
	 */
	default void resume(final PluginInterface game, final InputStream bytes) throws IOException {
		//Avoid breaking the interface contract
	}

	/**
	 * In case the game is stored, any data may be written to the outputstream.
	 *
	 * @param outputStream Target stream.
	 * @throws IOException
	 */
	default void save(final OutputStream outputStream) throws IOException {
		//Avoid breaking the interface contract
	}
}
