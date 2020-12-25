package org.jel.game.plugins;

import java.util.Collections;
import java.util.List;

import org.jel.game.data.EventList;
import org.jel.game.data.IClan;
import org.jel.game.messages.Message;

/**
 * A collection of data passed to plugins.
 */
public final class Context {
	private final EventList events;
	private final List<String> clanNames;
	private final List<IClan> clans;

	/**
	 * Shouldn't be used from the outside.
	 */
	public Context(final EventList events, final List<String> clanNames, final List<IClan> clans) {
		this.events = events;
		this.clanNames = clanNames;
		this.clans = clans;
	}

	/**
	 * Add an event to the eventlist notifying all handlers
	 *
	 * @param message The message to add. May not be null.
	 */
	public void appendToEventList(final Message message) {
		this.events.add(message);
	}

	/**
	 * Get a reference to a clan
	 *
	 * @param index The index
	 * @return The clan at the specified index.
	 */
	public IClan getClan(final int index) {
		return this.clans.get(index);
	}

	public List<String> getClanNames() {
		return Collections.unmodifiableList(this.clanNames);
	}
}
