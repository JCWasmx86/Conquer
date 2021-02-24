package conquer.plugins.builtins;

public record CiviliansDiedBecauseOfMissingResourcesMessage(long numberOfCiviliansDied, ICity city, IClan clan)
		implements Message {
	@Override
	public boolean isPlayerInvolved() {
		return this.clan.isPlayerClan();
	}

	@Override
	public boolean isBadForPlayer() {
		return this.isPlayerInvolved();
	}

	@Override
	public String getMessageText() {
		return MessageFormat.format(ResourceAnalyzerMessages.getString("Message.civilians"), //$NON-NLS-1$
				this.numberOfCiviliansDied, this.city.getName());
	}
}
