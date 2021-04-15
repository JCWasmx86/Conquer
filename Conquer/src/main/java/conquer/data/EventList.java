package conquer.data;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import conquer.messages.Message;
import conquer.plugins.MessageListener;

/**
 * A list that allows adding listeners that will be called as soon as an element
 * is added or removed.
 */
public final class EventList extends ArrayList<Message> {
	@Serial
	private static final long serialVersionUID = -3059648150677032552L;

	private final List<MessageListener> listeners = new ArrayList<>(50);

	/**
	 * If {@code element} is {@code null}, an {@link IllegalArgumentException} will
	 * be thrown.
	 */
	@Override
	public void add(final int index, final Message element) {
		if (element == null) {
			throw new IllegalArgumentException("message==null");
		}
		this.listeners.forEach(a -> a.added(element));
		super.add(index, element);
	}

	/**
	 * If {@code element} is {@code null}, an {@link IllegalArgumentException} will
	 * be thrown.
	 */
	@Override
	public boolean add(final Message message) {
		if (message == null) {
			throw new IllegalArgumentException("message==null");
		}
		this.listeners.forEach(a -> a.added(message));
		return super.add(message);
	}

	/**
	 * If {@code listener} is {@code null}, an {@link IllegalArgumentException} will
	 * be thrown.
	 */
	public void addListener(final MessageListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("listener==null");
		}
		this.listeners.add(listener);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		} else {
			return super.equals(o);
		}
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ this.listeners.hashCode();
	}

	/**
	 * If {@code o} is {@code null} or is not an instance of {@link Message} an
	 * {@link IllegalArgumentException} will be thrown.
	 */
	@Override
	public boolean remove(final Object o) {
		if (o == null) {
			throw new IllegalArgumentException("o==null");
		} else if (!(o instanceof Message)) {
			throw new IllegalArgumentException("o has to be an instanceof Message");
		}
		this.listeners.forEach(a -> a.removed((Message) o));
		return super.remove(o);
	}
}
