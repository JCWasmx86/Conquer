package org.jel.gui;

import java.awt.Graphics2D;

final class DashedLine {
	private final CityLabel cityA;
	private final CityLabel cityB;

	DashedLine(CityLabel a, CityLabel b) {
		this.cityA = a;
		this.cityB = b;
	}

	void draw(Graphics2D g) {
		g.drawLine(this.cityA.getPreferredX(), this.cityA.getPreferredY(), this.cityB.getPreferredX(),
				this.cityB.getPreferredY());
	}
}
