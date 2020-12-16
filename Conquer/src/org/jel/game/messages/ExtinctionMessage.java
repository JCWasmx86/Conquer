package org.jel.game.messages;

import org.jel.game.Messages;
import org.jel.game.data.Clan;
import org.jel.game.data.Shared;

public record ExtinctionMessage(Clan clan) implements Message {

	@Override
	public String getMessageText() {
		return Messages.getMessage("Message.extincted", this.clan.getName());
	}

	@Override
	public boolean isBadForPlayer() {
		return this.clan.getId() == Shared.PLAYER_CLAN;
	}

	@Override
	public boolean isPlayerInvolved() {
		return this.isBadForPlayer();
	}

	@Override
	public boolean shouldBeShownToThePlayer() {
		return this.clan.getId() == Shared.PLAYER_CLAN;
	}
}
