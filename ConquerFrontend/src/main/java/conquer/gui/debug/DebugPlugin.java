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
	public void init(PluginInterface pluginInterface) {
		pluginInterface.addAttackHook(new AttackHook() {
			@Override
			public void after(ICity src, ICity destination, long survivingSoldiers, AttackResult result) {
				System.out.println("Attack: " + src.getName() + " -> " + destination.getName() + "; " + survivingSoldiers + " soldiers survived; result: " + result);
			}

			@Override
			public void before(ICity src, ICity destination, long numberOfSoldiersMoved) {
				System.out.println("Attack: " + src.getName() + " -> " + destination.getName() + "; " + numberOfSoldiersMoved + " soldiers //Strategy: " + src.getClan().getStrategy().getClass().getCanonicalName());
			}
		});
		pluginInterface.addMessageListener(new MessageListener() {
			@Override
			public void added(Message message) {
				System.out.println("Received message: " + message.getMessageText());
			}

			@Override
			public void removed(Message message) {
				//Empty
			}
		});
		pluginInterface.addMoveHook(((src, dest, numberOfSoldiers) -> System.out.println("Moving: " + src.getName() +
			" -> " + dest.getName() + "; " + numberOfSoldiers + " soldiers // Strategy: " + src.getClan().getStrategy().getClass().getCanonicalName())));
		pluginInterface.addRecruitHook(((city, numberOfSoldiers) -> System.out.println("Recruit: " + city.getName() +
			"; " + numberOfSoldiers +
			" soldiers // Strategy: " + city.getClan().getStrategy().getClass().getCanonicalName())));
	}

	@Override
	public String getName() {
		return "debug";
	}

	@Override
	public void handle(Graph<ICity> cities, Context ctx) {
	}
}
