package org.jel.tool;

import java.util.ArrayList;
import java.util.List;

public class City {
	private double growth;

	private String background, name;

	private int clan, numberOfPeople, numberOfSoldiers, x, y, Defense;

	private double defenseBonus;

	private List<Double> productions = new ArrayList<>();

	public City(double growth, String cityIcon, String name, int clanId, int numberOfPeople, int numberOfSoldiers,
			int x, int y, int defense, double defenseBonus, List<Double> productions) {
		if (productions.size() != 9) {
			throw new IllegalArgumentException("productions.size()!=9");
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

	public int getClan() {
		return this.clan;
	}

	public int getDefense() {
		return this.Defense;
	}

	public double getDefenseBonus() {
		return this.defenseBonus;
	}

	public double getGrowth() {
		return this.growth;
	}

	public String getName() {
		return this.name;
	}

	public int getNumberOfPeople() {
		return this.numberOfPeople;
	}

	public int getNumberOfSoldiers() {
		return this.numberOfSoldiers;
	}

	public List<Double> getProductions() {
		return this.productions;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public void setClan(int clan) {
		this.clan = clan;
	}

	public void setDefense(int defense) {
		this.Defense = defense;
	}

	public void setDefenseBonus(double defenseBonus) {
		this.defenseBonus = defenseBonus;
	}

	public void setGrowth(double growth) {
		this.growth = growth;
	}

	public void setName(String name) {
		this.name = name;

	}

	public void setNumberOfPeople(int numberOfPeople) {
		this.numberOfPeople = numberOfPeople;
	}

	public void setNumberOfSoldiers(int numberOfSoldiers) {
		this.numberOfSoldiers = numberOfSoldiers;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
}
