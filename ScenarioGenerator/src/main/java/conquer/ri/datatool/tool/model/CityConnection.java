package conquer.ri.datatool.tool.model;

import java.util.Arrays;
import java.util.Objects;

public final class CityConnection {
	private final String from;
	private final String to;
	private final double distance;

	public CityConnection(String from, String to, double distance) {
		this.from = from;
		this.to = to;
		this.distance = distance;
	}

	void validate(City[] cities) {
		final var from = Arrays.stream(cities).filter(a -> a.name().equals(this.from)).toList();
		if (from.isEmpty()) {
			throw new IllegalArgumentException(this.from + " doesn't exist!");
		}
		final var to = Arrays.stream(cities).filter(a -> a.name().equals(this.to)).toList();
		if (to.isEmpty()) {
			throw new IllegalArgumentException(this.to + " doesn't exist!");
		}
		ValidatorUtils.throwIfBad(this.distance, "Connection between " + to + " and " + from + " is negative, " +
			"infinite" +
			" or NaN!");
	}

	public String from() { return this.from; }

	public String to() { return this.to; }

	public double distance() { return this.distance; }

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (CityConnection) obj;
		return Objects.equals(this.from, that.from) &&
			Objects.equals(this.to, that.to) &&
			Double.doubleToLongBits(this.distance) == Double.doubleToLongBits(that.distance);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.from, this.to, this.distance);
	}

	@Override
	public String toString() {
		return "CityConnection[" +
			"from=" + this.from + ", " +
			"to=" + this.to + ", " +
			"distance=" + this.distance + ']';
	}

}
