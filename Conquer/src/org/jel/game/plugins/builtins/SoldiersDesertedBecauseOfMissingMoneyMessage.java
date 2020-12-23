package org.jel.game.plugins.builtins;

import java.text.MessageFormat;

import org.jel.game.data.Clan;
import org.jel.game.messages.Message;

public record SoldiersDesertedBecauseOfMissingMoneyMessage(Clan clan, long numberOfDesertedSoldiers)
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
		return MessageFormat.format(MoneyAnalyzerMessages.getString("MoneyAnalyzer.message"),
				this.numberOfDesertedSoldiers);
	}
}
