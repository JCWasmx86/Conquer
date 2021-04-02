package conquer.gui;

final class Utils {
	private Utils() {

	}

	public static String format(final double d) {
		return String.format("%.2f", d);
	}

	static int getRefreshRate() {
		//17 means 60 FPS
		return Integer.getInteger("conquer.frontend.rate", 17);
	}
}
