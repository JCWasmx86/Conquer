package conquer.gui.debug;

import conquer.data.ICity;
import conquer.plugins.Context;
import conquer.utils.Graph;

public class DebugPlugin implements conquer.plugins.Plugin {
	@Override
	public String getName() {
		return "debug";
	}

	@Override
	public void handle(Graph<ICity> cities, Context ctx) {

	}
}
