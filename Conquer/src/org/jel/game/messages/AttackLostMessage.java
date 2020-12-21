package org.jel.game.messages;

import org.jel.game.Messages;
import org.jel.game.data.ICity;

public record AttackLostMessage(ICity src, ICity destination, long numberOfAttackers) implements Message {
	@Override
	public boolean isPlayerInvolved() {
		return this.src.isPlayerCity() || this.destination.isPlayerCity();
	}

	@Override
	public boolean isBadForPlayer() {
		return this.isPlayerInvolved() && this.src.getClan().isPlayerClan();
	}

	@Override
	public String getMessageText() {
		return Messages.getMessage("Message.attackLost", this.src.getName(), this.destination.getName(),
				this.numberOfAttackers);
	}
}
