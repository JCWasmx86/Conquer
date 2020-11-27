package org.jel.game.plugins.builtins;

import org.jel.game.data.City;
import org.jel.game.data.StreamUtils;
import org.jel.game.plugins.Context;
import org.jel.game.plugins.Plugin;
import org.jel.game.utils.Graph;

public final class IncreaseGrowth implements Plugin {

	@Override
	public String getName() {
		return "IncreaseGrowth";
	}

	@Override
	public void handle(final Graph<City> cities, final Context ctx) {
		StreamUtils.getCitiesAsStream(cities).forEach(a -> {
			if (a.getGrowth() < 1) {
				a.setGrowth(a.getGrowth() * (1 + (Math.random() % 0.1)));
			}
		});
	}

}
