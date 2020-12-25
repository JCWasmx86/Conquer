package org.jel.game.messages;

import org.jel.game.Messages;
import org.jel.game.data.IClan;

public record ExtinctionMessage(IClan clan) implements Message {

	@Override
	public String getMessageText() {
		return Messages.getMessage("Message.extincted", this.clan.getName());
	}

	@Override
	public boolean isBadForPlayer() {
		return this.clan.isPlayerClan();
	}

	@Override
	public boolean isPlayerInvolved() {
		return this.isBadForPlayer();
	}
}
