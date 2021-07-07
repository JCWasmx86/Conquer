package conquer.gui;

import java.awt.Graphics;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import conquer.data.ConquerInfo;
import conquer.data.IClan;

record Statistic(Map<IClan, Double> map) {
	static Statistic build(final ConquerInfo info, final BiFunction<ConquerInfo, ? super IClan, Double> valueFunction) {
		final var list =
			info.getClans().stream().sorted(Comparator.comparingDouble(a -> valueFunction.apply(info, a))).collect(Collectors.toList());
		final var totalStrength = list.stream().mapToDouble(a -> valueFunction.apply(info, a)).sum();
		final var map = new HashMap<IClan, Double>();
		list.forEach(a -> map.put(a, valueFunction.apply(info, a) / totalStrength));
		return new Statistic(map);
	}

	void draw(final Graphics g, final int width, final int height) {
		final var startX = (int) Math.rint(width * 0.2);
		final var startY = (int) Math.rint(height * 0.2);
		final var endX = (int) Math.rint(width * 0.8);
		final var endY = (int) Math.rint(height * 0.8);
		var currY = startY;
		final var diffY = endY - startY;
		for (final var entry :
			this.map.entrySet().stream().sorted((a, b) -> Double.compare(b.getValue(), a.getValue())).collect(Collectors.toList())) {
			g.setColor(entry.getKey().getColor());
			final var heightOfRectangle = (int) Math.rint(diffY * entry.getValue());
			g.fillRect(startX, currY, endX - startX, heightOfRectangle);
			currY += heightOfRectangle;
		}
	}
}
