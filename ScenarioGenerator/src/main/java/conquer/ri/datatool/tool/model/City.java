package conquer.ri.datatool.tool.model;

import java.util.Arrays;
import java.util.Objects;

public final class City {
	private final String name;
	private final String icon;
	private final String clan;
	private final double growth;
	private final int numberOfPeople;
	private final int numberOfSoldiers;
	private final int x;
	private final int y;
	private final int defense;
	private final double defenseBonus;
	private final Productions productions;

	public City(String name, String icon, String clan, double growth, int numberOfPeople, int numberOfSoldiers,
				int x, int y, int defense, double defenseBonus, Productions productions) {
		this.name = name;
		this.icon = icon;
		this.clan = clan;
		this.growth = growth;
		this.numberOfPeople = numberOfPeople;
		this.numberOfSoldiers = numberOfSoldiers;
		this.x = x;
		this.y = y;
		this.defense = defense;
		this.defenseBonus = defenseBonus;
		this.productions = productions;
	}

	void validate(final Player[] players) {
		ValidatorUtils.throwIfNull(this.name, "Cityname is missing!");
		ValidatorUtils.throwIfNull(this.icon, "Icon of " + this.name + " is missing!");
		ValidatorUtils.throwIfNull(this.clan, "Clan of city " + this.name + " is missing!");
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
		ValidatorUtils.throwIfBad(this.defenseBonus, "Defense bonus of " + this.name + " mustn't be negative, " +
			"infinite" +
			" or NaN!");
		ValidatorUtils.throwIfNull(this.productions, "Productions of " + this.name + " are missing!");
		this.productions.validate();
	}

	public String name() { return this.name; }

	public String icon() { return this.icon; }

	public String clan() { return this.clan; }

	public double growth() { return this.growth; }

	public int numberOfPeople() { return this.numberOfPeople; }

	public int numberOfSoldiers() { return this.numberOfSoldiers; }

	public int x() { return this.x; }

	public int y() { return this.y; }

	public int defense() { return this.defense; }

	public double defenseBonus() { return this.defenseBonus; }

	public Productions productions() { return this.productions; }

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (City) obj;
		return Objects.equals(this.name, that.name) &&
			Objects.equals(this.icon, that.icon) &&
			Objects.equals(this.clan, that.clan) &&
			Double.doubleToLongBits(this.growth) == Double.doubleToLongBits(that.growth) &&
			this.numberOfPeople == that.numberOfPeople &&
			this.numberOfSoldiers == that.numberOfSoldiers &&
			this.x == that.x &&
			this.y == that.y &&
			this.defense == that.defense &&
			Double.doubleToLongBits(this.defenseBonus) == Double.doubleToLongBits(that.defenseBonus) &&
			Objects.equals(this.productions, that.productions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.icon, this.clan, this.growth, this.numberOfPeople, this.numberOfSoldiers,
			this.x, this.y, this.defense, this.defenseBonus,
			this.productions);
	}

	@Override
	public String toString() {
		return "City[" +
			"name=" + this.name + ", " +
			"icon=" + this.icon + ", " +
			"clan=" + this.clan + ", " +
			"growth=" + this.growth + ", " +
			"numberOfPeople=" + this.numberOfPeople + ", " +
			"numberOfSoldiers=" + this.numberOfSoldiers + ", " +
			"x=" + this.x + ", " +
			"y=" + this.y + ", " +
			"defense=" + this.defense + ", " +
			"defenseBonus=" + this.defenseBonus + ", " +
			"productions=" + this.productions + ']';
	}

}
