package conquer.plugins.builtins;

import conquer.data.IClan;
import conquer.messages.Message;

import java.text.MessageFormat;

public record SoldiersDesertedBecauseOfMissingMoneyMessage(IClan clan, long numberOfDesertedSoldiers)
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
