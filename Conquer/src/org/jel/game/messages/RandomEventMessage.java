package org.jel.game.messages;

import org.jel.game.data.City;
import org.jel.game.data.Shared;

public record RandomEventMessage(RandomEvent randomEvent, double factorOfPeople, double factorOfSoldiers,
		double growthFactor, City city) implements Message {
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
		return this.city.getClan() == Shared.PLAYER_CLAN;
	}

	@Override
	public String getMessageText() {
		final var ret = new StringBuilder("The city ").append(this.city.getName()).append(" ");
		switch (this.randomEvent) {
		case ACCIDENT:
			ret.append("had an accident.");
			break;
		case CIVIL_WAR:
			ret.append("fought a civil war.");
			break;
		case CROP_FAILURE:
			ret.append("had crop failure.");
			break;
		case ECONOMIC_GROWTH:
			ret.append("increased the economic growth!");
			break;
		case FIRE:
			ret.append("was ravaged by a great fire.");
			break;
		case GROWTH:
			ret.append("grew!");
			break;
		case MIGRATION:
			ret.append("received new residents due to migration!");
			break;
		case PANDEMIC:
			ret.append(" was hit by a pandemic.");
			break;
		case PESTILENCE:
			ret.append(" was hit by the plague.");
			break;
		case REBELLION:
			ret.append("rebelled.");
			break;
		case SABOTAGE:
			ret.append("was sabotaged by the enemy.");
			break;
		default:
			ret.append("???");
			break;

		}
		return ret.toString();
	}
}
