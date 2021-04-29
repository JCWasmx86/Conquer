package conquer.ri.datatool.tool.model;

import java.util.Arrays;

public record City(String name, String icon, String clan, double growth, int numberOfPeople, int numberOfSoldiers,
				   int x, int y, int defense, double defenseBonus, Productions productions) {
	void validate(final Player[] players) {
		ValidatorUtils.throwIfNull(name, "Cityname is missing!");
		ValidatorUtils.throwIfNull(icon, "Icon of " + this.name + " is missing!");
		ValidatorUtils.throwIfNull(clan, "Clan of city " + this.name + " is missing!");
		final var clan = Arrays.stream(players).filter(a -> a.name().equals(this.clan)).findFirst();
		if (clan.isEmpty()) {
			throw new IllegalArgumentException("Clan " + this.clan + " of city " + this.name + " doesn't exist!");
		}
		ValidatorUtils.throwIfBad(this.growth, "Growth of " + this.name + " mustn't be negative, infinite or NaN!");
		ValidatorUtils.throwIfNegative(this.numberOfPeople, "Number of people in " + this.name + " mustn't be " +
			"negative!");
		ValidatorUtils.throwIfNegative(this.numberOfSoldiers, "Number of soldiers in " + this.name + " mustn't be " +
			"negative!");
		ValidatorUtils.throwIfNegative(this.x, "x-Position of " + this.name + " mustn't be negative!");
		ValidatorUtils.throwIfNegative(this.y, "y-Position of " + this.name + " mustn't be negative!");
		ValidatorUtils.throwIfBad(this.defenseBonus, "Defense bonus of " + this.name + " mustn't be negative, infinite" +
			" or NaN!");
		ValidatorUtils.throwIfNull(this.productions, "Productions of " + this.name + " are missing!");
		this.productions.validate();
	}
}
