package conquer.messages;

import conquer.Messages;
import conquer.data.ICity;

/**
 * Sent when a random event occurs. {@code factorOfPeople},
 * {@code factorOfSoldiers} and {@code growthFactor} are the factors for
 * calculating the new number of soldiers/people or growth.
 */
public record RandomEventMessage(RandomEvent randomEvent, double factorOfPeople, double factorOfSoldiers,
								 double growthFactor, ICity city) implements Message {
	@Override
	public boolean isBadForPlayer() {
		if (!this.isPlayerInvolved()) {
			return false;
		}
		return switch (this.randomEvent) {
			case ACCIDENT:
			case CIVIL_WAR:
			case CROP_FAILURE:
			case FIRE:
			case PANDEMIC:
			case PESTILENCE:
			case REBELLION:
			case SABOTAGE:
				yield true;
			case ECONOMIC_GROWTH:
			case GROWTH:
			case MIGRATION:
				yield false;
		};
	}

	@Override
	public boolean isPlayerInvolved() {
		return this.city.isPlayerCity();
	}

	@Override
	public String getMessageText() {
		return Messages.getMessage(this.randomEvent.getMessage(), this.city.getName());
	}
}
