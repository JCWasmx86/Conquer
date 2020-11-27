package org.jel.game.plugins;

import java.util.List;

import org.jel.game.data.Clan;
import org.jel.game.data.EventList;
import org.jel.game.messages.Message;

public final class Context {
	private final EventList events;
	private final List<String> clanNames;
	private final List<Clan> clans;

	public Context(final EventList events2, final List<String> clanNames, final List<Clan> clans) {
		this.events = events2;
		this.clanNames = clanNames;
		this.clans = clans;
	}

	public void appendToEventList(Message message) {
		this.events.add(message);
	}

	public Clan getClan(int index) {
		return this.clans.get(index);
	}

	public List<String> getClanNames() {
		return this.clanNames;
	}
}
