package org.jel.game.plugins.builtins;

import java.text.MessageFormat;

import org.jel.game.data.ICity;
import org.jel.game.data.IClan;
import org.jel.game.messages.Message;

public record ClanChangeMessage(ICity city, IClan oldClan, IClan newClan) implements Message {
	@Override
	public String getMessageText() {
		return MessageFormat.format(ChangeCitiesMindsMessages.getString("ClanChangeMessage.message"), //$NON-NLS-1$
				this.city.getName(), this.newClan.getName());
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
