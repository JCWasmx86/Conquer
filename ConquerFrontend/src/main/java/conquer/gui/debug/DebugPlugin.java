package conquer.gui.debug;

import conquer.data.AttackResult;
import conquer.data.ICity;
import conquer.messages.Message;
import conquer.plugins.AttackHook;
import conquer.plugins.Context;
import conquer.plugins.MessageListener;
import conquer.plugins.Plugin;
import conquer.plugins.PluginInterface;
import conquer.utils.Graph;

public class DebugPlugin implements Plugin {

	@Override
	public void init(final PluginInterface pluginInterface) {
		pluginInterface.addAttackHook(new AttackHook() {
			@Override
			public void after(final ICity src, final ICity destination, final long survivingSoldiers, final AttackResult result) {
				System.out.println("Attack: " + src.getName() + " -> " + destination.getName() + "; " + survivingSoldiers + " soldiers survived; result: " + result);
			}

			@Override
			public void before(final ICity src, final ICity destination, final long numberOfSoldiersMoved) {
				System.out.println("Attack: " + src.getName() + " -> " + destination.getName() + "; " + numberOfSoldiersMoved + " soldiers //Strategy: " + src.getClan().getStrategy().getClass().getCanonicalName());
			}
		});
		pluginInterface.addMessageListener(new MessageListener() {
			@Override
			public void added(final Message message) {
				System.out.println("Received message: " + message.getMessageText());
			}

			@Override
			public void removed(final Message message) {
				//Empty
			}
		});
		pluginInterface.addMoveHook(((src, dest, numberOfSoldiers) -> System.out.println("Moving: " + src.getName() +
			" -> " + dest.getName() + "; " + numberOfSoldiers + " soldiers // Strategy: " + src.getClan().getStrategy().getClass().getCanonicalName())));
		pluginInterface.addRecruitHook(((city, numberOfSoldiers) -> System.out.println("Recruit: " + city.getName() +
			"; " + numberOfSoldiers +
			" soldiers // Strategy: " + city.getClan().getStrategy().getClass().getCanonicalName())));
		pluginInterface.addKeyHandler("a", a -> System.out.println("Captured keyhandler!"));
		pluginInterface.addCityKeyHandler("b",
			(a, b) -> System.out.println("Captured keyhandler over " + b.getName() + "!"));
	}

	@Override
	public String getName() {
		return "debug";
	}

	@Override
	public void handle(final Graph<ICity> cities, final Context ctx) {
	}
}
