package conquer.messages;

import conquer.Messages;
import conquer.data.ICity;

/**
 * A message that is sent as soon as an attack occurs and the attacker was
 * defeated.
 */
public record AttackLostMessage(ICity src, ICity destination, long numberOfAttackers) implements Message {
    @Override
    public boolean isPlayerInvolved() {
        return this.src.isPlayerCity() || this.destination.isPlayerCity();
    }

    @Override
    public boolean isBadForPlayer() {
        return this.isPlayerInvolved() && this.src.getClan().isPlayerClan();
    }

<<<<<<< HEAD
	@Override
	public String getMessageText() {
		return Messages.getMessage("Message.attackLost", this.src.getName(), this.destination.getName(),
			this.numberOfAttackers);
	}
=======
    @Override
    public String getMessageText() {
        return Messages.getMessage("Message.attackLost", this.src.getName(), this.destination.getName(),
                this.numberOfAttackers);
    }
>>>>>>> parent of f8bbb68 (Formatting)
}
