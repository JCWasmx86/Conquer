package conquer.ri.datatool.tool.model;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Relation {
	private final String first;
	private final String second;
	private final int relation;

	public Relation(String first, String second, int relation) {
		this.first = first;
		this.second = second;
		this.relation = relation;
	}

	public void validate(final Player[] players) {
		final var set = Arrays.stream(players).map(Player::name).collect(Collectors.toSet());
		if (!set.contains(first)) {
			throw new IllegalArgumentException(first + " is no available clan!");
		}
		if (!set.contains(second)) {
			throw new IllegalArgumentException(second + " is no available clan!");
		}
		ValidatorUtils.throwIfNegative(this.relation, "Relation between " + this.first + " and " + this.second + " " +
			"may" +
			" not be negative!");
	}

	public String first() { return first; }

	public String second() { return second; }

	public int relation() { return relation; }

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (Relation) obj;
		return Objects.equals(this.first, that.first) &&
			Objects.equals(this.second, that.second) &&
			this.relation == that.relation;
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second, relation);
	}

	@Override
	public String toString() {
		return "Relation[" +
			"first=" + first + ", " +
			"second=" + second + ", " +
			"relation=" + relation + ']';
	}

}
