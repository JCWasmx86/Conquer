package conquer.plugins.builtins.advancements;

import java.util.List;

import conquer.data.ICity;
import conquer.data.IClan;
import conquer.plugins.Context;
import conquer.plugins.Plugin;
import conquer.plugins.PluginInterface;
import conquer.utils.Graph;

public class AdvancementsPlugin implements Plugin {

	private List<IClan> clans;
	private AdvancementStore store = new AdvancementStore();

	@Override
	public void init(final PluginInterface pluginInterface) {
		this.clans = pluginInterface.getClans();
		pluginInterface.addMessageListener(store);
		pluginInterface.addAttackHook(store);
		pluginInterface.addMoveHook(store);
		pluginInterface.addRecruitHook(store);
	}

	@Override
	public String getName() {
		return "Advancements";
	}

	@Override
	public void handle(final Graph<ICity> cities, final Context ctx) {

	}
}
