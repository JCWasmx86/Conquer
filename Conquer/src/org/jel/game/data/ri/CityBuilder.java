package org.jel.game.data.ri;

import java.awt.Image;
import java.util.List;

import org.jel.game.data.Clan;
import org.jel.game.data.ConquerInfo;

public class CityBuilder {
	private City tmp;

	public CityBuilder(ConquerInfo info) {
		this.tmp = new City(info);
	}

	public CityBuilder setImage(Image image) {
		tmp.setImage(image);
		return this;
	}

	public CityBuilder setClan(Clan clan) {
		tmp.setClan(clan);
		return this;
	}

	public CityBuilder setNumberOfPeople(long n) {
		tmp.setNumberOfPeople(n);
		return this;
	}

	public CityBuilder setNumberOfSoldiers(long n) {
		tmp.setNumberOfSoldiers(n);
		return this;
	}

	public CityBuilder setX(int x) {
		tmp.setX(x);
		return this;
	}

	public CityBuilder setY(int x) {
		tmp.setY(x);
		return this;
	}

	public CityBuilder setDefense(double defense) {
		tmp.setDefense(defense);
		return this;
	}

	public CityBuilder setDefenseBonus(double defense) {
		tmp.setDefenseBonus(defense);
		return this;
	}

	public CityBuilder setProductionRates(List<Double> productions) {
		tmp.setProductionRates(productions);
		return this;
	}

	public CityBuilder setName(String name) {
		tmp.setName(name);
		return this;
	}

	public CityBuilder setGrowth(double growth) {
		tmp.setGrowth(growth);
		return this;
	}

	public CityBuilder setId(int id) {
		tmp.setId(id);
		return this;
	}

	public City build() {
		return this.tmp;
	}

	public CityBuilder setLevels(List<Integer> levels) {
		tmp.setLevels(levels);
		return this;
	}

	public CityBuilder setAttacksOfPlayer(long num) {
		tmp.setAttacksOfPlayer(num);
		return this;
	}

	public CityBuilder setNumberOfRoundsWithZeroPeople(int num) {
		tmp.setNumberOfRoundsWithZeroPeople(num);
		return this;
	}

	public CityBuilder setOldValue(double value) {
		tmp.setOldValue(value);
		return this;
	}
}
