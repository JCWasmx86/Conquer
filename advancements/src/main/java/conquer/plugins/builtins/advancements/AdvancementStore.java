package conquer.plugins.builtins.advancements;

import conquer.data.AttackResult;
import conquer.data.ICity;
import conquer.messages.Message;
import conquer.plugins.AttackHook;
import conquer.plugins.MessageListener;
import conquer.plugins.MoveHook;
import conquer.plugins.RecruitHook;

public class AdvancementStore implements MessageListener, AttackHook, MoveHook, RecruitHook {
	@Override
	public void added(Message message) {

	}

	@Override
	public void removed(Message message) {

	}

	@Override
	public void after(ICity src, ICity destination, long survivingSoldiers, AttackResult result) {

	}

	@Override
	public void before(ICity src, ICity destination, long numberOfSoldiersMoved) {

	}

	@Override
	public void handleMove(ICity src, ICity dest, long numberOfSoldiers) {

	}

	@Override
	public void recruited(ICity city, long numberOfSoldiers) {

	}
}
