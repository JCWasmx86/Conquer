package org.jel.tool;

import java.util.ArrayList;
import java.util.List;

public class City {
	private double growth;

	public void setGrowth(double growth) {
		this.growth = growth;
	}

	private String background, name;

	public String getBackground() {
		return this.background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
		
	}

	public int getClan() {
		return this.clan;
	}

	public void setClan(int clan) {
		this.clan = clan;
	}

	public int getNumberOfPeople() {
		return this.numberOfPeople;
	}

	public void setNumberOfPeople(int numberOfPeople) {
		this.numberOfPeople = numberOfPeople;
	}

	public int getNumberOfSoldiers() {
		return this.numberOfSoldiers;
	}

	public void setNumberOfSoldiers(int numberOfSoldiers) {
		this.numberOfSoldiers = numberOfSoldiers;
	}

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public City(double growth, String background, String name, int clan, int numberOfPeople, int numberOfSoldiers,
			int x, int y, int defense, double defenseBonus, List<Double> list) {
		this.growth = growth;
		this.background = background;
		this.name = name;
		this.clan = clan;
		this.numberOfPeople = numberOfPeople;
		this.numberOfSoldiers = numberOfSoldiers;
		this.x = x;
		this.y = y;
		this.Defense = defense;
		this.defenseBonus = defenseBonus;
		this.productions = list;
	}

	public int getDefense() {
		return this.Defense;
	}

	public void setDefense(int defense) {
		this.Defense = defense;
	}

	public double getDefenseBonus() {
		return this.defenseBonus;
	}

	public void setDefenseBonus(double defenseBonus) {
		this.defenseBonus = defenseBonus;
	}

	private int clan, numberOfPeople, numberOfSoldiers, x, y, Defense;
	private double defenseBonus;
	private List<Double> productions = new ArrayList<>();

	public double getGrowth() {
		return this.growth;
	}

	public List<Double> getProductions() {
		return this.productions;
	}
}
