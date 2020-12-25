package org.jel.game.plugins.builtins;

import java.text.MessageFormat;

import org.jel.game.data.ICity;
import org.jel.game.data.IClan;
import org.jel.game.messages.Message;

public record SoldiersDesertedBecauseOfMissingResourcesMessage(long numberOfSoldiersDeserted, ICity city, IClan clan)
		implements Message {
	@Override
	public boolean isBadForPlayer() {
		return this.clan.isPlayerClan();
	}

	@Override
	public boolean isPlayerInvolved() {
		return this.isBadForPlayer();
	}

	@Override
	public String getMessageText() {
		return MessageFormat.format(ResourceAnalyzerMessages.getString("Message.soldiers"), //$NON-NLS-1$
				this.numberOfSoldiersDeserted, this.city.getName());
	}
}
