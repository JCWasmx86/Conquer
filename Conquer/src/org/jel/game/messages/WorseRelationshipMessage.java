package org.jel.game.messages;

import org.jel.game.data.Clan;

public record WorseRelationshipMessage(Clan first, Clan second, double oldValue, double newValue) implements Message {
	@Override
	public boolean isPlayerInvolved() {
		return (this.first.getId() == 0) || (this.second.getId() == 0);
	}

	@Override
	public boolean isBadForPlayer() {
		return this.isPlayerInvolved();
	}

	@Override
	public String getMessageText() {
		return this.first.getName() + " and " + this.second.getName() + " worsened their relationship by "
				+ String.format("%.2f", this.oldValue - this.newValue) + " points (Now "
				+ String.format("%.2f", this.newValue) + " points)";
	}
}
