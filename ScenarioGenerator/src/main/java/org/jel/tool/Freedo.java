package org.jel.tool;

import java.awt.Color;
import java.util.List;

final class Freedo {
	private Freedo() {
		//Empty
	}

	public static void main(final String[] args) {
		new DataFile().setBackground("Background4-res.png").addPlayer(4500, "Rhomortina", new Color(255, 255, 200), 0)
					  .addPlayer(3500, "Epsilamda", new Color(255, 0, 0), 3)
					  .addPlayer(1200, "Medianes", new Color(0, 255, 0), 1)
					  .addPlayer(12000, "Initia e finitia", new Color(0, 0, 255), 2)
					  .addPlayer(2500, "Denargo", new Color(255, 255, 0), 2)
					  .addCity(new City(1.03, "CityIcons/architecture-and-city.png", "Rhomortina", 0, 230000, 2300,
							  140, 65,
							  2700, 1.05, List.of(1.1, 0.8, 1.3, 1.1, 0.8, 1.1, 0.9, 0.5, 1.4)))
					  .addCity(new City(1.07, "CityIcons/fortress.png", "Nisentara", 0, 14000, 12000, 126, 226, 1240,
							  1.01,
							  List.of(1.1, 1.3, 0.95, 0.45, 0.4, 0.23, 0.8, 0.7, 0.9)))
					  .addCity(new City(1.05, "CityIcons/medieval.png", "Epsi", 1, 55000, 2200, 315, 293, 2500, 1.02,
							  List.of(0.9, 0.2, 1.3, 1.0, 1.3, 1.4, 1.2, 1.0, 1.7)))
					  .addCity(new City(1.1, "stadt1.png", "Lamda", 1, 23000, 1200, 138, 420, 2500, 1.001,
							  List.of(1.0, 0.9, 1.2, 0.96, 0.8, 1.0, 1.2, 1.1, 1.8)))
					  .addCity(new City(1.02, "CityIcons/medieval(1).png", "Medianes", 2, 2000, 40, 173, 515, 120,
							  1.001,
							  List.of(1.0, 0.98, 1.0, 0.2, 1.1, 0.1, 1.1, 0.9, 0.2)))
					  .addCity(new City(1.02, "stadt2.png", "Initia", 3, 25000, 2500, 418, 330, 4500, 1.03,
							  List.of(1.0, 1.2, 1.0, 0.8, 1.1, 0.7, 1.2, 1.1, 0.7)))//
					  .addCity(new City(1.03, "CityIcons/fortress(1).png", "Finitia", 3, 45000, 4500, 555, 380, 4500,
							  1.13,
							  List.of(1.1, 1.5, 1.2, 0.9, 1.1, 0.7, 1.3, 1.1, 0.5)))//
					  .addCity(new City(1.05, "CityIcons/fortress(2).png", "Alexandrjna", 3, 12500, 120, 558, 525,
							  4500,
							  1.01,
							  List.of(1.1, 1.2, 1.0, 0.9, 1.0, 0.8, 1.1, 1.05, 0.8)))
					  .addCity(new City(1.01, "stadt3.png", "Terre", 1, 240000, 1200, 345, 502, 120, 1.01,
							  List.of(1.0, 1.1, 1.0, 1.0, 1.0, 1.01, 0.9, 00.8, 1.9)))
					  .addCity(new City(1.03, "stadt4.png", "Den", 4, 125000, 7500, 644, 137, 1000, 1.001,
							  List.of(0.8, 0.2, 0.5, 0.4, 1.1, 0.3, 1.15, 1.1, 0.4)))
					  .addCity(new City(1.02, "stadt5.png", "Aurora", 4, 12000, 250, 183, 24, 980, 1.012,
							  List.of(0.8, 0.6, 1.3, 1.4, 0.87, 1.045, 0.8, 0.7, 1.8)))
					  .addCity(new City(1.03, "CityIcons/fortress(1).png", "Argo", 4, 62500, 7500, 381, 120, 920,
							  1.003,
							  List.of(1.02, 0.98, 1.3, 1.1, 0.8, 0.7, 0.8, 0.5, 1.9)))
					  .addCityConnection(0, 1, 4.5).addCityConnection(1, 2, 2.3).addCityConnection(2, 3, 2.6)
					  .addCityConnection(1, 3, 4.8).addCityConnection(3, 4, 2.5).addCityConnection(2, 5, 4.9)
					  .addCityConnection(5, 6, 4.5).addCityConnection(5, 7, 8.5).addCityConnection(5, 8, 2.3)
					  .addCityConnection(8, 7, 3.0).addCityConnection(5, 9, 5.6).addCityConnection(6, 9, 5.4)
					  .addCityConnection(9, 10, 12.7).addCityConnection(9, 11, 5.6).addCityConnection(10, 11, 6.5)
					  .dump("Freedo.data");
	}

}
