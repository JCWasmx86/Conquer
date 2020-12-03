package org.jel.game.messages;

import org.jel.game.data.Clan;
import org.jel.game.data.Shared;

public record BetterRelationshipMessage(Clan first, Clan second, double oldValue, double newValue) implements Message {
	@Override
	public boolean isPlayerInvolved() {
		return (this.first.getId() == Shared.PLAYER_CLAN) || (this.second.getId() == Shared.PLAYER_CLAN);
	}

	@Override
	public String getMessageText() {
		return this.first.getName() + " and " + this.second.getName() + " improved their relationship by "
				+ String.format("%.2f", this.newValue - this.oldValue) + " points (Now "
				+ String.format("%.2f", this.newValue) + " points)";
	}
}
