package org.jel.game.data;

import java.util.ArrayList;
import java.util.List;

import org.jel.game.messages.Message;
import org.jel.game.plugins.MessageListener;

public final class EventList extends ArrayList<Message> {
	private static final long serialVersionUID = -3059648150677032552L;

	private final List<MessageListener> listeners = new ArrayList<>(50);

	@Override
	public void add(int index, Message element) {
		this.listeners.forEach(a -> a.added(element));
		super.add(index, element);
	}

	@Override
	public boolean add(Message e) {
		this.listeners.forEach(a -> a.added(e));
		return super.add(e);
	}

	void addListener(MessageListener ml) {
		this.listeners.add(ml);
	}

	@Override
	public boolean remove(Object o) {
		if (!(o instanceof Message)) {
			throw new IllegalArgumentException("o has to be an instanceof Message");
		}
		this.listeners.forEach(a -> a.added((Message) o));
		return super.remove(o);
	}

}
