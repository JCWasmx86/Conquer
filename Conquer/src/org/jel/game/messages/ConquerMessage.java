package org.jel.game.messages;

import org.jel.game.data.City;

public record ConquerMessage(City src, City destination, long numberOfAttackers) implements Message {
	@Override
	public boolean isPlayerInvolved() {
		return this.src.isPlayerCity() || this.destination.isPlayerCity();
	}

	@Override
	public boolean isBadForPlayer() {
		return this.destination.getClanId() == 0;
	}

	@Override
	public String getMessageText() {
		return this.src.getName() + " conquered " + this.destination.getName() + " with " + this.numberOfAttackers
				+ " soldiers.";
	}
}
