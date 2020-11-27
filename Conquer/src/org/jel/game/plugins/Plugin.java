package org.jel.game.plugins;

import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.jel.game.data.City;
import org.jel.game.data.Game;
import org.jel.game.data.Result;
import org.jel.game.utils.Graph;

public interface Plugin {
	default void exit(final Result r) {

	}

	default List<Component> getButtons() {
		return List.of();
	}

	String getName();

	void handle(Graph<City> cities, Context ctx);

	default void init(final PluginInterface pi) {

	}

	default void resume(Game game, InputStream bytes) {

	}

	default void save(OutputStream outputStream) throws IOException {

	}
}
