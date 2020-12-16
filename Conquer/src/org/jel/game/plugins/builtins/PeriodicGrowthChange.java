package org.jel.game.plugins.builtins;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jel.game.data.City;
import org.jel.game.plugins.Context;
import org.jel.game.plugins.Plugin;
import org.jel.game.plugins.PluginInterface;
import org.jel.game.utils.Graph;

public final class PeriodicGrowthChange implements Plugin {
	private static final Random RANDOM = new Random(System.nanoTime());

	private static double newGrowth(final int currValue) {
		return 0.1 * Math.sin((0.3 * currValue) % (2 * Math.PI));
	}

	private List<Integer> values;

	@Override
	public String getName() {
		return "PeriodicGrowthChange";
	}

	@Override
	public void handle(final Graph<City> cities, final Context ctx) {
		this.initList(cities);
		final var cityArr = cities.getValues(new City[0]);
		for (var i = 0; i < cityArr.length; i++) {
			final var currGrowth = cityArr[i].getGrowth();
			final var cleanedGrowth = currGrowth / (1 + PeriodicGrowthChange.newGrowth(this.values.get(i)));
			final var newGrowth = cleanedGrowth * (1 + PeriodicGrowthChange.newGrowth(this.values.get(i) + 1));
			this.values.set(i, this.values.get(i) + 1);
			cityArr[i].setGrowth(newGrowth);
		}
	}

	@Override
	public void init(final PluginInterface pi) {
		this.values = null;
	}

	private void initList(final Graph<City> cities) {
		if (this.values == null) {
			this.values = new ArrayList<>();
			final var cityArr = cities.getValues(new City[0]);
			for (var i = 0; i < cityArr.length; i++) {
				this.values.add(PeriodicGrowthChange.RANDOM.nextInt(37));
				cityArr[i].setGrowth(cityArr[i].getGrowth() * (1 + PeriodicGrowthChange.newGrowth(this.values.get(i))));
			}
		}
	}
}
