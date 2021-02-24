package conquer.messages;

/**
 * A message that will be sent when the clan was extincted. (=No cities left).
 */
public record ExtinctionMessage(IClan clan)implements Message{

@Override
public String getMessageText(){
	return Messages.getMessage("Message.extincted",this.clan.getName());
	}

@Override
public boolean isBadForPlayer(){
	return this.clan.isPlayerClan();
	}

@Override
public boolean isPlayerInvolved(){
	return this.isBadForPlayer();
	}
	}
