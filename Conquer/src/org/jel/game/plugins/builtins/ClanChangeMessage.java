package org.jel.game.plugins.builtins;

import java.text.MessageFormat;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.messages.Message;

public record ClanChangeMessage(City city, Clan oldClan, Clan newClan) implements Message {
	@Override
	public String getMessageText() {
		return MessageFormat.format(ChangeCitiesMindsMessages.getString("ClanChangeMessage.message"), city.getName(), //$NON-NLS-1$
				this.newClan.getName());
	}

	@Override
	public boolean isBadForPlayer() {
		return this.oldClan.isPlayerClan();
	}

	@Override
	public boolean isPlayerInvolved() {
		return this.oldClan().isPlayerClan() || this.newClan().isPlayerClan();
	}
}
