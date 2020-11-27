package org.jel.game.plugins.builtins;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.messages.Message;

public record SoldiersDesertedBecauseOfMissingResourcesMessage(long numberOfSoldiersDeserted, City city, Clan clan)
		implements Message {
	@Override
	public boolean isBadForPlayer() {
		return this.clan.getId() == 0;
	}

	@Override
	public boolean isPlayerInvolved() {
		return this.clan.getId() == 0;
	}

	@Override
	public String getMessageText() {
		return this.numberOfSoldiersDeserted + " soldiers deserted in " + this.city.getName()
				+ " because of missing resources";
	}
}
