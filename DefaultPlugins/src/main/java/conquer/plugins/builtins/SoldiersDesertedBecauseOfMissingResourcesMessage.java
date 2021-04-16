package conquer.plugins.builtins;

import java.text.MessageFormat;

import conquer.data.ICity;
import conquer.data.IClan;
import conquer.messages.Message;

public record SoldiersDesertedBecauseOfMissingResourcesMessage(long numberOfSoldiersDeserted, ICity city, IClan clan)
	implements Message {
	@Override
	public boolean isBadForPlayer() {
		return this.clan.isPlayerClan();
	}

	@Override
	public boolean isPlayerInvolved() {
		return this.isBadForPlayer();
	}

	@Override
	public String getMessageText() {
		return MessageFormat.format(ResourceAnalyzerMessages.getString("Message.soldiers"),
			this.numberOfSoldiersDeserted, this.city.getName());
	}
}
