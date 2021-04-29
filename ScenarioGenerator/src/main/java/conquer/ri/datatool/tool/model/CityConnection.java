package conquer.ri.datatool.tool.model;

import java.util.Arrays;

public record CityConnection(String from, String to, double distance) {
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
}
