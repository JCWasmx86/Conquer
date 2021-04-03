package conquer.plugins.builtins;

import conquer.data.ICity;
import conquer.data.IClan;
import conquer.messages.Message;

import java.text.MessageFormat;

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
		return MessageFormat.format(ResourceAnalyzerMessages.getString("Message.civilians"), 
				this.numberOfCiviliansDied, this.city.getName());
	}
}
