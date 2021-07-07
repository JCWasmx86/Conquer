package conquer.messages;

import conquer.Messages;
import conquer.data.ICity;

/**
 * A message that is sent as soon as an attack occurs and at the end all
 * soldiers on both sides are dead.
 */
public record AnnihilationMessage(ICity src, ICity destination, long numberOfAttackers) implements Message {
	@Override
	public boolean isPlayerInvolved() {
		return this.src.isPlayerCity() || this.destination.isPlayerCity();
	}

	@Override
	public String getMessageText() {
		return Messages.getMessage("Message.annihilation", this.src.getName(), this.destination.getName(),
			this.numberOfAttackers);
	}
}
