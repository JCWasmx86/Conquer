package org.jel.game.messages;

import org.jel.game.Messages;
import org.jel.game.data.Clan;
import org.jel.game.data.Shared;

public record WorseRelationshipMessage(Clan first, Clan second, double oldValue, double newValue) implements Message {
	@Override
	public boolean isPlayerInvolved() {
		return this.first.isPlayerClan() || this.second.isPlayerClan();
	}

	@Override
	public boolean isBadForPlayer() {
		return this.isPlayerInvolved();
	}

	@Override
	public String getMessageText() {
		return Messages.getMessage("Message.worseRelationship", this.first.getName(), this.second.getName(),
				String.format("%.2f", this.oldValue - this.newValue), String.format("%.2f", this.newValue));
	}
}
