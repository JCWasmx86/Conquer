package org.jel.game.plugins.builtins;

import java.io.IOException;
import java.io.InputStream;

import org.jel.game.data.City;
import org.jel.game.plugins.Context;
import org.jel.game.plugins.Plugin;
import org.jel.game.plugins.PluginInterface;
import org.jel.game.utils.Graph;

public final class DefaultMusic implements Plugin {

	@Override
	public String getName() {
		return "DefaultMusic";
	}

	@Override
	public void handle(final Graph<City> cities, final Context ctx) {
		// Do nothing
	}

	@Override
	public void init(final PluginInterface pi) {
		for (var i = 0; i < 27; i++) {
			pi.addMusic("Battle" + i + (Boolean.getBoolean("conquer.useMP3") ? ".mp3" : ".wav"));
		}
	}

	@Override
	public void resume(final PluginInterface pi, final InputStream bytes) throws IOException {
		for (var i = 0; i < 27; i++) {
			pi.addMusic("Battle" + i + (Boolean.getBoolean("conquer.useMP3") ? ".mp3" : ".wav"));
		}
	}

}
