package conquer.messages;

/**
 * A message that is sent as soon as the relationship between two clans is improved.
 */
public record BetterRelationshipMessage(IClan first, IClan second, double oldValue,
										double newValue) implements Message {
	@Override
	public boolean isPlayerInvolved() {
		return this.first.isPlayerClan() || this.second.isPlayerClan();
	}

	@Override
	public String getMessageText() {
		return Messages.getMessage("Message.betterRelationship", this.first.getName(), this.second.getName(),
			String.format("%.2f", this.newValue - this.oldValue), String.format("%.2f", this.newValue));
	}
}
