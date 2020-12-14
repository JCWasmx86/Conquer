package org.jel.gui;

public final class Utils {
	private Utils() {

	}

	public static double format(double d) {
		return Double.parseDouble(String.format("%.2f", d));
	}
}
