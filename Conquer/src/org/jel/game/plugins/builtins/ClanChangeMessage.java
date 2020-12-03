package org.jel.game.plugins.builtins;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.data.Shared;
import org.jel.game.messages.Message;

public record ClanChangeMessage(City city, Clan oldClan, Clan newClan) implements Message {
	@Override
	public String getMessageText() {
		return city.getName() + " is now part of " + newClan.getName() + "!";
	}

	@Override
	public boolean isBadForPlayer() {
		return oldClan.getId() == Shared.PLAYER_CLAN;
	}

	@Override
	public boolean isPlayerInvolved() {
		return oldClan.getId() == Shared.PLAYER_CLAN || newClan.getId() == Shared.PLAYER_CLAN;
	}
}
