package conquer.ri.datatool.tool.model;

import java.util.Arrays;
import java.util.stream.Collectors;

public record Relation(String first, String second, int relation) {
	public void validate(final Player[] players) {
		final var set = Arrays.stream(players).map(Player::name).collect(Collectors.toSet());
		if (!set.contains(first)) {
			throw new IllegalArgumentException(first + " is no available clan!");
		}
		if (!set.contains(second)) {
			throw new IllegalArgumentException(second + " is no available clan!");
		}
		ValidatorUtils.throwIfNegative(this.relation, "Relation between " + this.first + " and " + this.second + " may" +
			" not be negative!");
	}
}
