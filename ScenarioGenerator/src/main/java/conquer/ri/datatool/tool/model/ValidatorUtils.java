package conquer.ri.datatool.tool.model;

final class ValidatorUtils {
	private ValidatorUtils() {
		//Empty
	}

	static void throwIfNull(final Object obj, final String s) {
		if (obj == null) {
			throw new IllegalArgumentException(s);
		}
	}

	static void throwIfBad(final double value, final String s) {
		if (value < 0 || Double.isNaN(value) || Double.isInfinite(value)) {
			throw new IllegalArgumentException(s);
		}
	}

	static void throwIfNegative(final int n, final String s) {
		if (n < 0) {
			throw new IllegalArgumentException(s);
		}
	}
}
