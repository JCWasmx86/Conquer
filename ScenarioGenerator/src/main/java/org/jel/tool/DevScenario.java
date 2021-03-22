package org.jel.tool;

import java.awt.Color;
import java.util.List;

public class DevScenario {
	public static void main(final String[] args) {
		final var list = List.of(1.01, 1.01, 1.01, 1.01, 1.01, 1.01, 1.01, 1.01, 1.01);
		new DataFile().setBackground("devBackground.png")
				.addPlayer(1000, "Player", new Color(50, 50, 50), 0)
				.addPlayer(1000, "CPU0", new Color(255, 0, 0), 1)
				.addPlayer(1000, "CPU1", new Color(0, 255, 0), 2)
				.addPlayer(1000, "CPU2", new Color(0, 0, 255), 3)
				.addCity(new City(1.01, "dev/player.png", "Player0",
						0, 5000, 500, 0, 40, 500, 1.03, list))
				.addCity(new City(1.01, "dev/player.png", "Player1", 0, 5000,
						500, 40, 40, 500, 1.03, list))
				.addCity(new City(1.01, "dev/player.png", "Player2", 0, 5000,
						500, 40, 0, 500, 1.03, list))
				.addCity(new City(1.01, "dev/player.png", "Player3", 0, 5000,
						500, 35, 120, 500, 1.03, list))
				.addCity(new City(1.01, "dev/player.png", "Player4", 0, 5000,
						500, 120, 220, 500, 1.03, list))
				.addCity(new City(1.01, "dev/player.png", "Player5", 0, 5000,
						500, 120, 170, 500, 1.03, list))
				.addCity(new City(1.01, "dev/player.png", "Player6", 0, 5000,
						500, 80, 140, 500, 1.03, list))
				.addCity(new City(1.01, "dev/player.png", "Player7", 0, 5000,
						500, 230, 140, 500, 1.03, list))
				.addCity(new City(1.01, "dev/player.png", "Player8", 0, 5000,
						500, 230, 240, 500, 1.03, list))
				.addCityConnection(0, 1, 1)
				.addCityConnection(1, 2, 1)
				.addCityConnection(0, 2, 1)
				.addCityConnection(2, 3, 1)
				.addCityConnection(1, 4, 1)
				.addCityConnection(2, 5, 1)
				.addCityConnection(3, 6, 1)
				.addCityConnection(6, 7, 1)
				.addCityConnection(7, 8, 1)
				.addCityConnection(6, 8, 1)
				.addCityConnection(4, 8, 1)
				.addCityConnection(0, 4, 1)
				.dump("dev.data");
	}
}
