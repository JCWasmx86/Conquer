package conquer.plugins.builtins.advancements;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import conquer.data.AttackResult;
import conquer.data.ICity;
import conquer.data.Shared;
import conquer.messages.Message;
import conquer.plugins.AttackHook;
import conquer.plugins.MessageListener;
import conquer.plugins.MoveHook;
import conquer.plugins.PluginInterface;
import conquer.plugins.RecruitHook;

public class AdvancementStore implements MessageListener, AttackHook, MoveHook, RecruitHook {
	@Override
	public void added(Message message) {
		Advancements.availableAdvancements.forEach(a -> a.added(message));
	}

	@Override
	public void removed(Message message) {
	}

	@Override
	public void after(ICity src, ICity destination, long survivingSoldiers, AttackResult result) {
		Advancements.availableAdvancements.forEach(a -> a.after(src, destination, survivingSoldiers, result));
	}

	@Override
	public void before(ICity src, ICity destination, long numberOfSoldiersMoved) {
		Advancements.availableAdvancements.forEach(a -> a.before(src, destination, numberOfSoldiersMoved));
	}

	@Override
	public void handleMove(ICity src, ICity dest, long numberOfSoldiers) {
		Advancements.availableAdvancements.forEach(a -> a.handleMove(src, dest, numberOfSoldiers));
	}

	@Override
	public void recruited(ICity city, long numberOfSoldiers) {
		Advancements.availableAdvancements.forEach(a -> a.recruited(city, numberOfSoldiers));
	}

	public void nextRound(int round) {
		Advancements.availableAdvancements.forEach(a -> a.nextRound(round));
	}

	void save(final OutputStream outputStream) throws IOException {
		try (final var oos = new ObjectOutputStream(outputStream)) {
			oos.writeObject(Advancements.gainedAdvancements);
		}
	}

	void resume(final PluginInterface game, InputStream bytes) throws IOException {
		try (final var ois = new ObjectInputStream(bytes)) {
			Advancements.gainedAdvancements = (List<Advancement>) ois.readObject();
		} catch (ClassNotFoundException e) {
			Shared.LOGGER.exception(e);
			Advancements.gainedAdvancements = new ArrayList<>();
		}
	}
}
