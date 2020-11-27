package org.jel.game.plugins.builtins;

import org.jel.game.data.Clan;
import org.jel.game.messages.Message;

public record SoldiersDesertedBecauseOfMissingMoneyMessage(Clan clan, long numberOfDesertedSoldiers)
		implements Message {
	@Override
	public boolean isBadForPlayer() {
		return this.clan.getId() == 0;
	}

	@Override
	public boolean isPlayerInvolved() {
		return this.clan.getId() == 0;
	}

	@Override
	public String getMessageText() {
		return this.numberOfDesertedSoldiers + " soldiers deserted because they weren't paid!";
	}
}
