package conquer.plugins.builtins.advancements;

import java.io.Serializable;
import java.util.Optional;

import conquer.data.AttackResult;
import conquer.data.ICity;
import conquer.messages.Message;
import conquer.plugins.AttackHook;
import conquer.plugins.MessageListener;
import conquer.plugins.MoveHook;
import conquer.plugins.RecruitHook;

interface Advancement extends MessageListener, AttackHook, MoveHook, RecruitHook, Serializable {

	String title();

	String extendedTitle();

	void init(AdvancementCallBack ifGained);

	default Optional<Class<? extends Advancement>> getDependency() {
		return Optional.empty();
	}

	default void after(ICity src, ICity destination, long survivingSoldiers, AttackResult result) {

	}

	default void before(ICity src, ICity destination, long numberOfSoldiersMoved) {

	}

	default void added(Message message) {

	}

	default void removed(Message message) {

	}

	default void handleMove(ICity src, ICity dest, long numberOfSoldiers) {

	}

	default void recruited(ICity city, long numberOfSoldiers) {

	}

	default void nextRound(int round) {

	}
}
