package conquer.messages;

/**
 * A message that is sent as soon as the relationship between two clans is worsened.
 */
public record WorseRelationshipMessage(IClan first,IClan second,double oldValue,double newValue)implements Message{
@Override
public boolean isPlayerInvolved(){
	return this.first.isPlayerClan()||this.second.isPlayerClan();
	}

@Override
public boolean isBadForPlayer(){
	return this.isPlayerInvolved();
	}

@Override
public String getMessageText(){
	return Messages.getMessage("Message.worseRelationship",this.first.getName(),this.second.getName(),
	String.format("%.2f",this.oldValue-this.newValue),String.format("%.2f",this.newValue));
	}
	}
