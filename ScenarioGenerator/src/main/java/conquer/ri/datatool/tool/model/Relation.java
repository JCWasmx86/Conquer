package conquer.ri.datatool.tool.model;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Relation {
	private final String first;
	private final String second;
	private final int relation;

	public Relation(final String first, final String second, final int relation) {
		this.first = first;
		this.second = second;
		this.relation = relation;
	}

	public void validate(final Player[] players) {
		final var set = Arrays.stream(players).map(Player::name).collect(Collectors.toSet());
		if (!set.contains(this.first)) {
			throw new IllegalArgumentException(this.first + " is no available clan!");
		}
		if (!set.contains(this.second)) {
			throw new IllegalArgumentException(this.second + " is no available clan!");
		}
		ValidatorUtils.throwIfNegative(this.relation, "Relation between " + this.first + " and " + this.second + " " +
			"may" +
			" not be negative!");
	}

	public String first() { return this.first; }

	public String second() { return this.second; }

	public int relation() { return this.relation; }

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		final var that = (Relation) obj;
		return Objects.equals(this.first, that.first) &&
			Objects.equals(this.second, that.second) &&
			this.relation == that.relation;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.first, this.second, this.relation);
	}

	@Override
	public String toString() {
		return "Relation[" +
			"first=" + this.first + ", " +
			"second=" + this.second + ", " +
			"relation=" + this.relation + ']';
	}

}
