package conquer.ri.datatool.tool.model;

import java.util.List;
import java.util.Objects;

public final class Productions {
	private final double wheat;
	private final double fish;
	private final double wood;
	private final double coal;
	private final double meat;
	private final double iron;
	private final double textile;
	private final double leather;
	private final double stone;

	public Productions(final double wheat, final double fish, final double wood, final double coal, final double meat, final double iron, final double textile,
					   final double leather, final double stone) {
		this.wheat = wheat;
		this.fish = fish;
		this.wood = wood;
		this.coal = coal;
		this.meat = meat;
		this.iron = iron;
		this.textile = textile;
		this.leather = leather;
		this.stone = stone;
	}

	void validate() {
		ValidatorUtils.throwIfBad(this.wheat, "Production for wheat mustn't be negative, infinite or NaN!");
		ValidatorUtils.throwIfBad(this.fish, "Production for fish mustn't be negative, infinite or NaN!");
		ValidatorUtils.throwIfBad(this.wood, "Production for wood mustn't be negative, infinite or NaN!");
		ValidatorUtils.throwIfBad(this.coal, "Production for coal mustn't be negative, infinite or NaN!");
		ValidatorUtils.throwIfBad(this.meat, "Production for meat mustn't be negative, infinite or NaN!");
		ValidatorUtils.throwIfBad(this.iron, "Production for iron mustn't be negative, infinite or NaN!");
		ValidatorUtils.throwIfBad(this.textile, "Production for textile mustn't be negative, infinite or NaN!");
		ValidatorUtils.throwIfBad(this.leather, "Production for leather mustn't be negative, infinite or NaN!");
		ValidatorUtils.throwIfBad(this.stone, "Production for stone mustn't be negative, infinite or NaN!");
	}

	public double wheat() { return this.wheat; }

	public double fish() { return this.fish; }

	public double wood() { return this.wood; }

	public double coal() { return this.coal; }

	public double meat() { return this.meat; }

	public double iron() { return this.iron; }

	public double textile() { return this.textile; }

	public double leather() { return this.leather; }

	public double stone() { return this.stone; }

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		final var that = (Productions) obj;
		return Double.doubleToLongBits(this.wheat) == Double.doubleToLongBits(that.wheat) &&
			Double.doubleToLongBits(this.fish) == Double.doubleToLongBits(that.fish) &&
			Double.doubleToLongBits(this.wood) == Double.doubleToLongBits(that.wood) &&
			Double.doubleToLongBits(this.coal) == Double.doubleToLongBits(that.coal) &&
			Double.doubleToLongBits(this.meat) == Double.doubleToLongBits(that.meat) &&
			Double.doubleToLongBits(this.iron) == Double.doubleToLongBits(that.iron) &&
			Double.doubleToLongBits(this.textile) == Double.doubleToLongBits(that.textile) &&
			Double.doubleToLongBits(this.leather) == Double.doubleToLongBits(that.leather) &&
			Double.doubleToLongBits(this.stone) == Double.doubleToLongBits(that.stone);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.wheat, this.fish, this.wood, this.coal, this.meat, this.iron, this.textile,
			this.leather, this.stone);
	}

	@Override
	public String toString() {
		return "Productions[" +
			"wheat=" + this.wheat + ", " +
			"fish=" + this.fish + ", " +
			"wood=" + this.wood + ", " +
			"coal=" + this.coal + ", " +
			"meat=" + this.meat + ", " +
			"iron=" + this.iron + ", " +
			"textile=" + this.textile + ", " +
			"leather=" + this.leather + ", " +
			"stone=" + this.stone + ']';
	}

	public List<Double> toList() {
		return List.of(this.wheat, this.fish, this.wood, this.coal, this.meat, this.iron, this.textile, this.leather,
			this.stone);
	}
}
