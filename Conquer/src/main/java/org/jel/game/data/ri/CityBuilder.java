package org.jel.game.data.ri;

import java.awt.Image;
import java.util.List;

import org.jel.game.data.ConquerInfo;
import org.jel.game.data.IClan;

class CityBuilder {
	private final City tmp;

	public CityBuilder(final ConquerInfo info) {
		this.tmp = new City(info);
	}

	public City build() {
		return this.tmp;
	}

	public CityBuilder setAttacksOfPlayer(final long num) {
		this.tmp.setAttacksOfPlayer(num);
		return this;
	}

	public CityBuilder setClan(final IClan clan) {
		this.tmp.setClan(clan);
		return this;
	}

	public CityBuilder setDefense(final double defense) {
		this.tmp.setDefense(defense);
		return this;
	}

	public CityBuilder setDefenseBonus(final double defense) {
		this.tmp.setDefenseBonus(defense);
		return this;
	}

	public CityBuilder setGrowth(final double growth) {
		this.tmp.setGrowth(growth);
		return this;
	}

	public CityBuilder setId(final int id) {
		this.tmp.setId(id);
		return this;
	}

	public CityBuilder setImage(final Image image) {
		this.tmp.setImage(image);
		return this;
	}

	public CityBuilder setLevels(final List<Integer> levels) {
		this.tmp.setLevels(levels);
		return this;
	}

	public CityBuilder setName(final String name) {
		this.tmp.setName(name);
		return this;
	}

	public CityBuilder setNumberOfPeople(final long n) {
		this.tmp.setNumberOfPeople(n);
		return this;
	}

	public CityBuilder setNumberOfRoundsWithZeroPeople(final int num) {
		this.tmp.setNumberOfRoundsWithZeroPeople(num);
		return this;
	}

	public CityBuilder setNumberOfSoldiers(final long n) {
		this.tmp.setNumberOfSoldiers(n);
		return this;
	}

	public CityBuilder setOldValue(final double value) {
		this.tmp.setOldValue(value);
		return this;
	}

	public CityBuilder setProductionRates(final List<Double> productions) {
		this.tmp.setProductionRates(productions);
		return this;
	}

	public CityBuilder setX(final int x) {
		this.tmp.setX(x);
		return this;
	}

	public CityBuilder setY(final int x) {
		this.tmp.setY(x);
		return this;
	}
}
