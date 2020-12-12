package org.jel.game.plugins.builtins;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.data.Shared;
import org.jel.game.messages.Message;

public record SoldiersDesertedBecauseOfMissingResourcesMessage(final long numberOfSoldiersDeserted, final City city,
		final Clan clan) implements Message {
	@Override
	public boolean isBadForPlayer() {
		return this.clan.getId() == Shared.PLAYER_CLAN;
	}

	@Override
	public boolean isPlayerInvolved() {
		return this.clan.getId() == Shared.PLAYER_CLAN;
	}

	@Override
	public String getMessageText() {
		return this.numberOfSoldiersDeserted + " soldiers deserted in " + this.city.getName()
				+ " because of missing resources";
	}
}
