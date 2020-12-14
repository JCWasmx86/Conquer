package org.jel.game.messages;

import org.jel.game.Messages;
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
		return Messages.getMessage("Message.conquered", this.src.getName(), this.destination.getName(),
				this.numberOfAttackers);
	}
}
