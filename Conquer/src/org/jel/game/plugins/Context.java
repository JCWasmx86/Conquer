package org.jel.game.plugins;

import java.util.List;

import org.jel.game.data.Clan;
import org.jel.game.data.EventList;
import org.jel.game.messages.Message;

/**
 * A collection of data passed to plugins.
 */
public final class Context {
	private final EventList events;
	private final List<String> clanNames;
	private final List<Clan> clans;

	/**
	 * Shouldn't be used from the outside.
	 */
	public Context(final EventList events, final List<String> clanNames, final List<Clan> clans) {
		this.events = events;
		this.clanNames = clanNames;
		this.clans = clans;
	}

	/**
	 * Add an event to the eventlist notifying all handlers
	 * 
	 * @param message The message to add
	 */
	public void appendToEventList(Message message) {
		this.events.add(message);
	}

	/**
	 * Get a reference to a clan
	 * 
	 * @param index The index
	 * @return The clan at the specified index.
	 */
	public Clan getClan(int index) {
		return this.clans.get(index);
	}

	public List<String> getClanNames() {
		return this.clanNames;
	}
}
