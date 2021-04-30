package conquer.ri.datatool.tool.model;


import java.util.Objects;

public final class Player {
	private final String name;
	private final Color clanColor;
	private final double initialCoins;
	private final int flags;

	public Player(final String name, final Color clanColor, final double initialCoins, final int flags) {
		this.name = name;
		this.clanColor = clanColor;
		this.initialCoins = initialCoins;
		this.flags = flags;
	}

	void validate() {
		ValidatorUtils.throwIfNull(this.name, "Playername is missing!");
		ValidatorUtils.throwIfNull(this.clanColor, "Color of " + this.name + " is missing!");
		this.clanColor.validate();
		ValidatorUtils.throwIfBad(this.initialCoins, "Initial coins of " + this.name + " mustn't be negative, " +
			"infinite" +
			" or NaN!");
	}

	public String name() { return this.name; }

	public Color clanColor() { return this.clanColor; }

	public double initialCoins() { return this.initialCoins; }

	public int flags() { return this.flags; }

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		final var that = (Player) obj;
		return Objects.equals(this.name, that.name) &&
			Objects.equals(this.clanColor, that.clanColor) &&
			Double.doubleToLongBits(this.initialCoins) == Double.doubleToLongBits(that.initialCoins) &&
			this.flags == that.flags;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.clanColor, this.initialCoins, this.flags);
	}

	@Override
	public String toString() {
		return "Player[" +
			"name=" + this.name + ", " +
			"clanColor=" + this.clanColor + ", " +
			"initialCoins=" + this.initialCoins + ", " +
			"flags=" + this.flags + ']';
	}

}
