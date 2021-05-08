package conquer.plugins.builtins.advancements;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import conquer.data.ICity;
import conquer.data.IClan;
import conquer.plugins.Context;
import conquer.plugins.Plugin;
import conquer.plugins.PluginInterface;
import conquer.utils.Graph;

public class AdvancementsPlugin implements Plugin {

	private AdvancementStore store = new AdvancementStore();
	private int round = 0;
	@Override
	public void init(final PluginInterface pluginInterface) {
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
		round++;
		store.nextRound(round);
	}

	@Override
	public void save(OutputStream outputStream) throws IOException {
		this.store.save(outputStream);
	}

	@Override
	public void resume(PluginInterface game, InputStream bytes) throws IOException {
		this.store = new AdvancementStore();
		this.store.resume(game,bytes);
	}
}
