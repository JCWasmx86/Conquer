package org.jel.game.plugins.builtins;

import java.text.MessageFormat;

import org.jel.game.data.Clan;
import org.jel.game.data.ICity;
import org.jel.game.messages.Message;

public record CiviliansDiedBecauseOfMissingResourcesMessage(long numberOfCiviliansDied, ICity city, Clan clan)
		implements Message {
	@Override
	public boolean isPlayerInvolved() {
		return this.clan.isPlayerClan();
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
