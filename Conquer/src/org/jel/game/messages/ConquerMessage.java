package org.jel.game.messages;

import org.jel.game.data.City;
import org.jel.game.data.Shared;

public record ConquerMessage(City src, City destination, long numberOfAttackers) implements Message {
	@Override
	public boolean isPlayerInvolved() {
		return (this.src.getClan() == Shared.PLAYER_CLAN) || (this.destination.getClan() == Shared.PLAYER_CLAN);
	}

	@Override
	public boolean isBadForPlayer() {
		return this.destination.getClan() == 0;
	}

	@Override
	public String getMessageText() {
		return this.src.getName() + " conquered " + this.destination.getName() + " with " + this.numberOfAttackers
				+ " soldiers.";
	}
}
