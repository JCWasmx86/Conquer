package org.jel.game.data;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a city.
 */
public final class City implements Comparable<City> {
	private double bonus;
	private byte clan;
	private double defense;
	private final Game game;
	private double growth;
	private Image image;
	private List<Integer> levels = new ArrayList<>();
	private String name;
	private long numAttacksOfPlayer = 0;
	private long numberOfPeople;
	private long numberOfSoldiers;
	private List<Double> productions;
	private int numberOfRoundsWithZeroPeople = 0;
	private int x;

	private int y;

	private double oldOne = 1d;

	/**
	 * Create a new City.
	 *
	 * @param game A handle to the game object
	 */
	public City(final Game game) {
		if (game == null) {
			throw new IllegalArgumentException("game==null");
		}
		for (var i = 0; i < (Resource.values().length + 1); i++) {
			this.levels.add(0);
		}
		this.game = game;
	}

	/**
	 * Increases the number of attacks of the player.
	 */
	void attackByPlayer() {
		this.numAttacksOfPlayer++;
	}

	@Override
	public int compareTo(final City other) {
		if (other == null) {
			throw new IllegalArgumentException("other==null");
		}
		return Double.compare((this.getNumberOfSoldiers() * this.getBonus()) + this.getDefense(),
				(other.getNumberOfSoldiers() * other.getBonus()) + other.getDefense());
	}

	void endOfRound() {
		if (this.numberOfPeople <= 50) {
			this.numberOfRoundsWithZeroPeople++;
			if (this.numberOfRoundsWithZeroPeople == 3) {
				this.numberOfRoundsWithZeroPeople = 0;
				this.setNumberOfPeople(100L + Shared.getRandomNumber(101));
			}
		} else {
			this.numberOfRoundsWithZeroPeople = 0;
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof City)) {
			return false;
		}
		return ((City) obj).name.equals(this.name);
	}

	/**
	 * Gives the defensebonus: Suppose there are 500 Soldiers in the city and there
	 * is a defense bonus of 1.03. Now they have the same power as 515 soldiers.
	 *
	 * @return The defensebonus
	 */
	public double getBonus() {
		return this.bonus;
	}

	/**
	 * Returns the clan id.
	 *
	 * @return The clan id
	 */
	public int getClan() {
		return this.clan;
	}

	/**
	 * Get the difference between the production of coins per round and the use of
	 * coins per round.
	 *
	 * @return The difference
	 */
	public double getCoinDiff() {
		return (this.numberOfPeople * Shared.COINS_PER_PERSON_PER_ROUND)
				- (this.numberOfSoldiers * Shared.COINS_PER_SOLDIER_PER_ROUND);
	}

	/**
	 * Gives the base defense: The base defense means, that even if a city has no
	 * soldiers, it has a bit of defense. (Like towers and walls)
	 *
	 * @return The base defense
	 */
	public double getDefense() {
		return this.defense;
	}

	public double getDefenseStrength(Clan clan) {
		if (clan == null) {
			throw new IllegalArgumentException("clan == null");
		}
		if (clan.getId() != this.clan) {
			throw new IllegalArgumentException("Wrong clan passed!");
		}
		return this.getDefense() + (this.getNumberOfSoldiers() * this.getBonus() * clan.getSoldiersStrength()
				* clan.getSoldiersDefenseStrength());
	}

	/**
	 * Returns a handle to the game object
	 *
	 * @return A handle to the game object.
	 */
	public Game getGame() {
		return this.game;
	}

	/**
	 * Returns the growth of a city. The growth of a city is the factor, for which
	 * the number of persons increases every round. For example: 1000 Persons and a
	 * growth of 1.01: Next round: 1010 persons Next round: 1020 persons
	 *
	 * @return The growth of the city.
	 */
	public double getGrowth() {
		return this.growth;
	}

	/**
	 *
	 * @return The icon of the city.
	 */
	public Image getImage() {
		return this.image;
	}

	/**
	 * Returns the number of levels of each resource of the city.
	 *
	 * @return The levels
	 */
	public List<Integer> getLevels() {
		return this.levels;
	}

	/**
	 * @return The name of the city.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the number of attacks from the player
	 */
	public long getNumberAttacksOfPlayer() {
		return this.numAttacksOfPlayer;
	}

	/**
	 * Returns the number of persons in the city
	 */
	public long getNumberOfPeople() {
		return this.numberOfPeople;
	}

	int getNumberOfRoundsWithZeroPeople() {
		return this.numberOfRoundsWithZeroPeople;
	}

	/**
	 * Returns the number of soldiers in the city.
	 */
	public long getNumberOfSoldiers() {
		return this.numberOfSoldiers;
	}

	/**
	 * Gives the how much every resource is produced every round.
	 */
	public List<Double> getProductions() {
		return this.productions;
	}

	/**
	 * Returns the x-Position
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Returns the y-Position.
	 */
	public int getY() {
		return this.y;
	}

	@Override
	public int hashCode() {
		final var prime = 31;
		var result = 1;
		result = (prime * result) + this.x;
		result = (prime * result) + this.y;
		return result;
	}

	double oldOne() {
		return this.oldOne;
	}

	/**
	 * Returns the production rate per round for a specified resource
	 *
	 * @param idx The index of the resource.
	 * @return The production rate of the resource at {@code idx}
	 */
	public double productionPerRound(final int idx) {
		if ((idx < 0) || (idx >= this.productions.size())) {
			throw new IllegalArgumentException("Invalid argument!");
		}
		return this.productions.get(idx) * this.numberOfPeople;
	}

	void setAttacksOfPlayer(long num) {
		this.numAttacksOfPlayer = num;
	}

	public void setClan(final int id) {
		this.clan = (byte) id;
	}

	public void setDefense(final double base) {
		if (this.defense != 0) {
			this.defense /= this.oldOne;
			this.defense *= (base < 1 ? 1 / base : base);
			this.oldOne = base;
		} else {
			this.defense = base;
		}
		this.defense = base;
	}

	public void setDefenseBonus(final double bonus) {
		this.bonus = bonus;
	}

	public void setGrowth(final double growth) {
		this.growth = growth;
	}

	public void setImage(final Image image) {
		this.image = image;
	}

	public void setLevels(final List<Integer> levels) {
		this.levels = levels;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setNumberOfPeople(final long num) {
		if (num < 0) {
			throw new IllegalArgumentException("num < 0 : " + num);
		}
		this.numberOfPeople = num;
	}

	void setNumberOfRoundsWithZeroPeople(int num) {
		this.numberOfRoundsWithZeroPeople = num;
	}

	public void setNumberOfSoldiers(final long num) {
		if (num < 0) {
			throw new IllegalArgumentException("num < 0 : " + num);
		}
		this.numberOfSoldiers = num;
	}

	void setOldValue(double value) {
		this.oldOne = value;
	}

	public void setProductionRates(final List<Double> productions) {
		this.productions = productions;
	}

	public void setX(final int x) {
		this.x = x;
	}

	public void setY(final int y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "City [image=" + this.image + ", clan=" + this.clan + ", numberOfPeople=" + this.numberOfPeople
				+ ", numberOfSoldiers=" + this.numberOfSoldiers + ", y=" + this.y + ", x=" + this.x + ", defense="
				+ this.defense + ", bonus=" + this.bonus + ", name=" + this.name + ", growth=" + this.growth + "]";
	}
}
