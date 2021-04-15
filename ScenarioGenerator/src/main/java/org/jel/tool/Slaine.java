package org.jel.tool;

import java.awt.Color;
import java.util.List;

final class Slaine {
	private Slaine() {
		//Empty
	}

	public static void main(final String[] args) {
		new DataFile().setBackground("Background3-res.png").addPlayer(1200, "Nidispera", new Color(123, 234, 12), 0)
			.addPlayer(120, "Rho", new Color(255, 0, 0), 2).addPlayer(550, "Lambdia", new Color(0, 255, 100)
			, 3)
			.addPlayer(4500, "Niomega", new Color(255, 255, 205), 1)
			.addPlayer(120, "Dealpha", new Color(0, 225, 225), 3)
			.addPlayer(45000, "Deomega", new Color(123, 123, 223), 1)
			.addPlayer(23000, "Finite", new Color(100, 100, 100), 2)
			.addCity(new City(1.01, "CityIcons/castle.png", "Nidispera", 0, 45000, 1200, 57, 54, 1250, 1.01,
				List.of(1.02, 1.5, 1.4, 1.1, 1.3, 1.5, 1.2, 1.1, 1.18)))
			.addCity(new City(1.02, "CityIcons/castle(1).png", "Rhonda", 1, 1250, 250, 177, 252, 250, 1.01,
				List.of(1.01, 1.02, 0.98, 1.02, 1.1, 0.99, 1.04, 1.4, 0.7)))
			.addCity(new City(1.04, "CityIcons/castle(2).png", "Laminitia", 2, 4300, 453, 342, 194, 2750,
				1.01,
				List.of(1.1, 0.8, 0.6, 0.7, 0.8, 0.1, 0.45, 1.2, 0.8)))
			.addCity(new City(1.005, "CityIcons/moscow.png", "Nisa", 3, 68000, 4500, 138, 479, 2500, 1.01,
				List.of(1.0, 1.3, 1.2, 0.56, 0.8, 1.2, 0.98, 0.8, 0.9)))
			.addCity(new City(1.1, "CityIcons/fortress.png", "Dealpha", 4, 1200, 23, 321, 51, 400, 1.2,
				List.of(0.98, 0.87, 1.2, 1.2, 1.03, 1.1, 1.0, 1.02, 1.0)))
			.addCity(new City(1.02, "CityIcons/fortress(1).png", "Deomega", 5, 85000, 4500, 476, 239, 2150,
				1.05,
				List.of(0.2, 0.3, 1.2, 0.8, 1.3, 0.7, 0.3, 0.7, 1.0)))
			.addCity(new City(1.02, "CityIcons/fortress(2).png", "Deomega II", 5, 25000, 1250, 534, 334, 340
				, 1.02,
				List.of(1.02, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 0.8)))
			.addCity(new City(1.0002, "CityIcons/medieval.png", "Initia", 6, 125000, 40000, 564, 112, 10000,
				1.002,
				List.of(1.2, 1.3, 1.2, 1.0, 1.25, 1.17, 1.2, 1.3, 0.8)))
			.addCity(new City(1.02, "CityIcons/medieval(1).png", "Finitia", 6, 12500, 4500, 661, 285, 500,
				1.02,
				List.of(1.1, 1.2, 1.1, 1.14, 1.1, 1.0, 1.0, 0.98, 1.2)))
			.addCityConnection(0, 1, 12.5).addCityConnection(0, 2, 10.5).addCityConnection(1, 2, 9.75)
			.addCityConnection(1, 3, 8.9).addCityConnection(0, 4, 12.0).addCityConnection(4, 5, 6.7)
			.addCityConnection(5, 6, 13.4).addCityConnection(2, 5, 5.6).addCityConnection(5, 7, 5.45)
			.addCityConnection(7, 8, 12.5).addCityConnection(6, 8, 12.0).dump("Slaine.data");
	}
}
