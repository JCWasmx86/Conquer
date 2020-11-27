package org.jel.game.plugins.builtins;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.messages.Message;

public record CiviliansDiedBecauseOfMissingResourcesMessage(long numberOfCiviliansDied, City city, Clan clan)
		implements Message {
	@Override
	public boolean isPlayerInvolved() {
		return this.clan.getId() == 0;
	}

	@Override
	public boolean isBadForPlayer() {
		return this.isPlayerInvolved();
	}

	@Override
	public String getMessageText() {
		return this.numberOfCiviliansDied + " residents of " + this.city.getName()
				+ " died because of missing resources";
	}
}
