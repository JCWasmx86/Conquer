package org.jel.game.plugins.builtins;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jel.game.data.ICity;
import org.jel.game.plugins.Context;
import org.jel.game.plugins.Plugin;
import org.jel.game.plugins.PluginInterface;
import org.jel.game.utils.Graph;

import conquer.frontend.spi.MusicProvider;

public final class DefaultMusic implements Plugin, MusicProvider {

	@Override
	public String getName() {
		return "DefaultMusic";
	}

	@Override
	public void handle(final Graph<ICity> cities, final Context ctx) {
		// Do nothing
	}

	@Override
	public void init(final PluginInterface pi) {
		for (var i = 0; i < 27; i++) {
			pi.addMusic("Battle" + i);
		}
	}

	@Override
	public void resume(final PluginInterface pi, final InputStream bytes) throws IOException {
		for (var i = 0; i < 27; i++) {
			pi.addMusic("Battle" + i);
		}
	}

	@Override
	public List<String> getMusic() {
		final List<String> ret = new ArrayList<>();
		for (var i = 0; i < 27; i++) {
			ret.add("Battle" + i);
		}
		return ret;
	}

}
