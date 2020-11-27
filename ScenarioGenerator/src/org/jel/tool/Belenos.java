package org.jel.tool;

import java.awt.Color;
import java.util.List;

public class Belenos {
	public static void main(String[] args) {
		new DataFile().setBackground("Background1-res.png").addPlayer(1000, "Deinitia", new Color(255, 0, 120), 0)
				.addPlayer(1000, "Valosepulca", new Color(100, 100, 100), 3)
				.addPlayer(7500, "Deforma", new Color(255, 0, 0), 2).addPlayer(1200, "Invaria", new Color(0, 255, 0), 1)
				.addCity(new City(1.001, "CityIcons/architecture-and-city.png", "Samolacrima", 0, 3000, 100, 40, 98,
						150, 1.001, List.of(1.001, 1.2, 1.1, 1.001, 1.2, 1.3, 1.4, 1.2, 1.01)))
				.addCity(new City(1.02, "CityIcons/castle.png", "Onmadispera", 0, 10276, 2278, 202, 124, 340, 1.01,
						List.of(1.2, 1.3, 1.14, 1.2, 0.3, 0.4, 0.1, 0.4, 1.5)))
				.addCity(new City(1.003, "CityIcons/castle(1).png", "Onmamorta", 0, 230, 12, 85, 225, 120, 1.0,
						List.of(1.1, 0.1, 0.7, 0.1, 1.3, 0.2, 1.2, 1.1, 0.34)))
				.addCity(new City(1.003, "CityIcons/castle(2).png", "Deinitia", 0, 12000, 300, 200, 235, 500, 1.2,
						List.of(0.3, 1.4, 0.2, 0.1, 1.0, 0.1, 0.7, 0.7, 0.1)))
				.addCity(new City(1.02, "CityIcons/castle(3).png", "Sanmaga", 1, 1200, 150, 85, 310, 100, 1.1,
						List.of(1.1, 0.12, 0.8, 0.1, 1.3, 0.2, 1.122, 1.1, 0.4)))
				.addCity(new City(1.1, "CityIcons/cultures.png", "Valosepulca", 1, 11000, 2400, 233, 356, 4500, 1.002,
						List.of(0.1, 0.2, 1.4, 1.7, 0.2, 0.8, 0.3, 0.2, 2.5)))
				.addCity(new City(1.1, "CityIcons/fortress.png", "La uria", 1, 1240, 230, 92, 435, 250, 1.1,
						List.of(0.3, 1.4, 0.2, 0.1, 1.0, 0.1, 0.7, 0.7, 0.1)))
				.addCity(new City(1.02, "CityIcons/fortress(1).png", "Insamoria", 2, 30000, 2000, 331, 259, 1500, 1.01,
						List.of(2.3, 1.2, 1.1, 1.1, 1.8, 1.7, 1.23, 1.23, 1.8)))
				.addCity(new City(1.02, "CityIcons/fortress(2).png", "Invaria", 3, 25000, 7800, 434, 272, 2500, 1.02,
						List.of(0.8, 0.9, 0.2, 0.1, 0.4, 0.08, 0.7, 0.6, 0.8)))
				.addCity(new City(1.03, "CityIcons/medieval.png", "Astandor", 3, 1200, 125, 627, 87, 1250, 1.03,
						List.of(0.8, 0.2, 0.3, 0.4, 0.1, 0.2, 0.01, 0.2, 1.0)))
				.addCity(new City(1.021, "CityIcons/medieval(1).png", "Asanidor", 3, 2300, 812, 643, 387, 120, 1.0,
						List.of(0.1, 0.2, 0.3, 0.1, 0.4, 0.1, 1.5, 1.4, 1.7)))
				.addCityConnection(0, 1, 4.5).addCityConnection(1, 2, 2.3).addCityConnection(0, 2, 2.4)
				.addCityConnection(2, 3, 1.3).addCityConnection(0, 3, 7.6).addCityConnection(3, 4, 3)
				.addCityConnection(2, 4, 3.4).addCityConnection(4, 5, 2.3).addCityConnection(5, 6, 3.4)
				.addCityConnection(4, 6, 2).addCityConnection(1, 3, 4.5).addCityConnection(3, 4, 2.3)
				.addCityConnection(3, 7, 8.9).addCityConnection(7, 8, 9.1).addCityConnection(8, 9, 2.3)
				.addCityConnection(9, 10, 2.3).addCityConnection(8, 10, 2.48).dump("Belenos.data");
	}
}
