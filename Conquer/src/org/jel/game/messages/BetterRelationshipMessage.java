package org.jel.game.messages;

import org.jel.game.Messages;
import org.jel.game.data.Clan;

public record BetterRelationshipMessage(Clan first, Clan second, double oldValue,
		double newValue) implements Message {
	@Override
	public boolean isPlayerInvolved() {
		return this.first.isPlayerClan() || this.second.isPlayerClan();
	}

	@Override
	public String getMessageText() {
		return Messages.getMessage("Message.betterRelationship", this.first.getName(), this.second.getName(),
				String.format("%.2f", this.newValue - this.oldValue), String.format("%.2f", this.newValue));
	}
}
