package org.jel.game.plugins.builtins;

import java.text.MessageFormat;

import org.jel.game.data.Clan;
import org.jel.game.data.Shared;
import org.jel.game.messages.Message;

public record SoldiersDesertedBecauseOfMissingMoneyMessage(Clan clan, long numberOfDesertedSoldiers)
		implements Message {
	@Override
	public boolean isBadForPlayer() {
		return this.clan.getId() == Shared.PLAYER_CLAN;
	}

	@Override
	public boolean isPlayerInvolved() {
		return this.clan.getId() == Shared.PLAYER_CLAN;
	}

	@Override
	public String getMessageText() {
		return MessageFormat.format(MoneyAnalyzerMessages.getString("MoneyAnalyzer.message"),
				this.numberOfDesertedSoldiers);
	}
}
