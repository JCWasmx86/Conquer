package org.jel.game.plugins;

import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.jel.game.data.City;
import org.jel.game.data.Result;
import org.jel.game.utils.Graph;

/**
 * The interface every plugin has to implement.
 */
public interface Plugin {
	/**
	 * Called when the game ended
	 *
	 * @param result The result
	 */
	default void exit(final Result result) {

	}

	/**
	 * An optional list of buttons/labels/... that can be registered
	 *
	 * @return A list of components.
	 */
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
	void handle(final Graph<City> cities, final Context ctx);

	/**
	 * Called at the start of the game. Can be used for adding handlers.
	 *
	 * @param pluginInterface
	 */
	default void init(final PluginInterface pluginInterface) {

	}

	/**
	 * Called when a saved game is restored
	 *
	 * @param game
	 * @param bytes The written bytes
	 * @throws IOException In case of an IO-Error
	 */
	default void resume(final PluginInterface game, final InputStream bytes) throws IOException {

	}

	/**
	 * In case the game is stored, any data may be written to the outputstream.
	 *
	 * @param outputStream Target stream.
	 * @throws IOException
	 */
	default void save(final OutputStream outputStream) throws IOException {

	}
}
