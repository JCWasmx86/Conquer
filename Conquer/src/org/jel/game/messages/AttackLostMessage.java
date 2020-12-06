package org.jel.game.messages;

import org.jel.game.data.City;

public record AttackLostMessage(City src, City destination, long numberOfAttackers) implements Message {
	@Override
	public boolean isPlayerInvolved() {
		return this.src.isPlayerCity() || this.destination.isPlayerCity();
	}

	@Override
	public boolean isBadForPlayer() {
		return this.isPlayerInvolved() && (this.src.getClanId() == 0);
	}

	@Override
	public String getMessageText() {
		return this.src.getName() + " attacked " + this.destination.getName() + " with " + this.numberOfAttackers
				+ " soldiers and lost.";
	}
}
