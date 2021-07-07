package conquer.plugins.builtins;

import conquer.data.ICity;
import conquer.data.StreamUtils;
import conquer.plugins.Context;
import conquer.plugins.Plugin;
import conquer.utils.Graph;

public final class IncreaseGrowth implements Plugin {

	private static final double MAXIMUM_INCREASE = 0.1;

	@Override
	public String getName() {
		return "IncreaseGrowth";
	}

	@Override
	public void handle(final Graph<? extends ICity> cities, final Context ctx) {
		StreamUtils.forEach(cities, a -> {
			if (a.getGrowth() < 1) {
				a.setGrowth(a.getGrowth() * (1 + (Math.random() % IncreaseGrowth.MAXIMUM_INCREASE)));
			}
		});
	}

}
