package org.jel.tool;

import java.util.List;

public final class City {
	private final List<Double> productions;
	private double growth;
	private String background, name;
	private int clan, numberOfPeople, numberOfSoldiers, x, y, Defense;
	private double defenseBonus;

	public City(final double growth, final String cityIcon, final String name, final int clanId,
				final int numberOfPeople, final int numberOfSoldiers, final int x, final int y, final int defense,
				final double defenseBonus, final List<Double> productions) {
		if (productions.size() != 9) {
			throw new IllegalArgumentException("productions.size() != 9: " + productions.size());
		}
		this.growth = growth;
		this.background = cityIcon;
		this.name = name;
		this.clan = clanId;
		this.numberOfPeople = numberOfPeople;
		this.numberOfSoldiers = numberOfSoldiers;
		this.x = x;
		this.y = y;
		this.Defense = defense;
		this.defenseBonus = defenseBonus;
		this.productions = productions;
	}

	public String getBackground() {
		return this.background;
	}

	public void setBackground(final String background) {
		this.background = background;
	}

	public int getClan() {
		return this.clan;
	}

	public void setClan(final int clan) {
		this.clan = clan;
	}

	public int getDefense() {
		return this.Defense;
	}

	public void setDefense(final int defense) {
		this.Defense = defense;
	}

	public double getDefenseBonus() {
		return this.defenseBonus;
	}

	public void setDefenseBonus(final double defenseBonus) {
		this.defenseBonus = defenseBonus;
	}

	public double getGrowth() {
		return this.growth;
	}

	public void setGrowth(final double growth) {
		this.growth = growth;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;

	}

	public int getNumberOfPeople() {
		return this.numberOfPeople;
	}

	public void setNumberOfPeople(final int numberOfPeople) {
		this.numberOfPeople = numberOfPeople;
	}

	public int getNumberOfSoldiers() {
		return this.numberOfSoldiers;
	}

	public void setNumberOfSoldiers(final int numberOfSoldiers) {
		this.numberOfSoldiers = numberOfSoldiers;
	}

	public List<Double> getProductions() {
		return this.productions;
	}

	public int getX() {
		return this.x;
	}

	public void setX(final int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(final int y) {
		this.y = y;
	}

}
