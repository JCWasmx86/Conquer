package org.jel.game.plugins.builtins;

import java.text.MessageFormat;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.data.Shared;
import org.jel.game.messages.Message;

public record CiviliansDiedBecauseOfMissingResourcesMessage(long numberOfCiviliansDied, City city, Clan clan)
		implements Message {
	@Override
	public boolean isPlayerInvolved() {
		return this.clan.getId() == Shared.PLAYER_CLAN;
	}

	@Override
	public boolean isBadForPlayer() {
		return this.isPlayerInvolved();
	}

	@Override
	public String getMessageText() {
		return MessageFormat.format(ResourceAnalyzerMessages.getString("Message.civilians"), //$NON-NLS-1$
				this.numberOfCiviliansDied, this.city.getName());
	}
}
