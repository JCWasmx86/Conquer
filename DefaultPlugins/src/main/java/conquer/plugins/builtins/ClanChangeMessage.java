package conquer.plugins.builtins;

import java.text.MessageFormat;

import conquer.data.ICity;
import conquer.data.IClan;
import conquer.messages.Message;

public record ClanChangeMessage(ICity city, IClan oldClan, IClan newClan) implements Message {
<<<<<<< HEAD
	@Override
	public String getMessageText() {
		return MessageFormat.format(ChangeCitiesMindsMessages.getString("ClanChangeMessage.message"),
			this.city.getName(), this.newClan.getName());
	}
=======
    @Override
    public String getMessageText() {
        return MessageFormat.format(ChangeCitiesMindsMessages.getString("ClanChangeMessage.message"), //$NON-NLS-1$
                this.city.getName(), this.newClan.getName());
    }
>>>>>>> parent of f8bbb68 (Formatting)

    @Override
    public boolean isBadForPlayer() {
        return this.oldClan.isPlayerClan();
    }

    @Override
    public boolean isPlayerInvolved() {
        return this.oldClan().isPlayerClan() || this.newClan().isPlayerClan();
    }
}
