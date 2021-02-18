package conquer.plugins.builtins;

import java.text.MessageFormat;

import conquer.data.ICity;
import conquer.data.IClan;
import conquer.messages.Message;

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
