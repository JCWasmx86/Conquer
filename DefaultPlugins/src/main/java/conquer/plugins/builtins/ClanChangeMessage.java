package conquer.plugins.builtins;

import conquer.data.ICity;
import conquer.data.IClan;
import conquer.messages.Message;

import java.text.MessageFormat;

public record ClanChangeMessage(ICity city, IClan oldClan, IClan newClan) implements Message {
    @Override
    public String getMessageText() {
        return MessageFormat.format(ChangeCitiesMindsMessages.getString("ClanChangeMessage.message"), //$NON-NLS-1$
                this.city.getName(), this.newClan.getName());
    }

    @Override
    public boolean isBadForPlayer() {
        return this.oldClan.isPlayerClan();
    }

    @Override
    public boolean isPlayerInvolved() {
        return this.oldClan().isPlayerClan() || this.newClan().isPlayerClan();
    }
}
