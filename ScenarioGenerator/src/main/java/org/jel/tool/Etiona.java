package org.jel.tool;

import java.awt.Color;
import java.util.List;

public class Etiona {
	public static void main(final String[] args) {
		new DataFile().setBackground("Background2-res.png").addPlayer(2300, "Y alpha", new Color(100, 200, 0), 0)
				.addPlayer(1200, "Valoinitia", new Color(0, 0, 255), 2)
				.addPlayer(3500, "Los alpha", new Color(0, 255, 0), 3)
				.addPlayer(120, "Deomega", new Color(255, 0, 0), 0)
				.addCity(new City(1.001, "CityIcons/medieval(1).png", "San morir", 0, 25000, 2000, 93, 89, 5000, 1.02,
						List.of(0.52, 2.3, 0.1, 0.4, 0.8, 0.8, 1.2, 0.8, 0.1)))
				.addCity(new City(1.02, "CityIcons/monument.png", "Onmaalpha", 0, 2100, 215, 374, 94, 200, 1.01,
						List.of(0.85, 1.4, 0.8, 0.1, 0.2, 0.2, 0.3, 0.2, 0.8)))
				.addCity(new City(1.08, "CityIcons/moscow.png", "Holamorta", 1, 2300, 120, 210, 32, 710, 1.01,
						List.of(1.0, 1.2, 1.3, 0.8, 0.56, 0.23, 0.8, 0.65, 0.2)))
				.addCity(new City(1.2, "CityIcons/medieval.png", "Initia de aqa", 1, 15000, 7500, 43, 173, 210, 1.02,
						List.of(0.8, 3.4, 2.3, 1.2, 0.4, 0.5, 0.23, 0.2, 0.3)))
				.addCity(new City(1.01, "stadt1.png", "Alpha", 2, 55000, 7500, 243, 435, 12500, 1.2,
						List.of(1.1, 0.8, 1.3, 1.0, 1.3, 0.2, 0.4, 0.5, 1.0)))
				.addCity(new City(1.02, "stadt2.png", "Andron", 2, 1240, 240, 141, 338, 1250, 1.07,
						List.of(0.38, 0.1, 0.3, 0.99, 0.1, 0.89, 0.2, 0.3, 5.6)))
				.addCity(new City(0.9, "stadt3.png", "Deomega", 3, 2500, 120, 583, 107, 50, 1.3,
						List.of(0.3, 1.3, 0.5, 0.1, 0.2, 0.02, 0.4, 0.5, 0.2)))
				.addCity(new City(0.8, "stadt4.png", "Sol", 3, 450, 15, 587, 278, 25, 1.001,
						List.of(0.1, 2.4, 0.1, 0.2, 0.3, 0.2, 0.1, 0.5, 0.2)))
				.addCity(new City(0.98, "stadt5.png", "Lua", 3, 540, 45, 595, 335, 120, 1.011,
						List.of(1.0, 3.4, 0.1, 0.3, 0.1, 0.5, 0.2, 0.2, 0.3)))
				.addCityConnection(0, 1, 1.2).addCityConnection(0, 2, 4.5).addCityConnection(2, 3, 3.7)
				.addCityConnection(0, 1, 1.2).addCityConnection(1, 2, 2.1).addCityConnection(1, 3, 2.1)
				.addCityConnection(0, 5, 8.9).addCityConnection(4, 5, 3.4).addCityConnection(1, 6, 9.9)
				.addCityConnection(5, 6, 4.5).addCityConnection(6, 7, 5.8).addCityConnection(7, 8, 4.5)
				.addCityConnection(0, 3, 4.5).dump("Etiona.data");
	}
}
