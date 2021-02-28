package conquer.messages;

import conquer.Messages;
import conquer.data.ICity;

/**
 * A message that is sent as soon as an attack occurs and the city was
 * conquered.
 */
public record ConquerMessage(ICity src, ICity destination, long numberOfAttackers) implements Message {
	@Override
	public boolean isPlayerInvolved() {
		return this.src.isPlayerCity() || this.destination.isPlayerCity();
	}

	@Override
	public boolean isBadForPlayer() {
		return this.destination.getClan().isPlayerClan();
	}

	@Override
	public String getMessageText() {
		return Messages.getMessage("Message.conquered", this.src.getName(), this.destination.getName(),
				this.numberOfAttackers);
	}
}
