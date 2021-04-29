package conquer.ri.datatool.tool.model;

import java.util.Objects;

public final class Color {
	private final int red;
	private final int green;
	private final int blue;

	public Color(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	void validate() {
		new java.awt.Color(this.red, this.green, this.blue);
	}

	public int red() { return this.red; }

	public int green() { return this.green; }

	public int blue() { return this.blue; }

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (Color) obj;
		return this.red == that.red &&
			this.green == that.green &&
			this.blue == that.blue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.red, this.green, this.blue);
	}

	@Override
	public String toString() {
		return "Color[" +
			"red=" + this.red + ", " +
			"green=" + this.green + ", " +
			"blue=" + this.blue + ']';
	}

}
