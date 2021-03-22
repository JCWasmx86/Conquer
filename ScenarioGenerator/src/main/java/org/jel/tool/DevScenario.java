package org.jel.tool;

import java.awt.Color;
import java.util.ArrayList;

public class DevScenario {
	public static void main(final String[] args) {
		new DataFile().setBackground("devBackground.png").addPlayer(1000, "Player", new Color(50, 50, 50), 0)
				.addPlayer(1000, "CPU0", new Color(255, 0, 0), 1).addPlayer(1000, "CPU1", new Color(0, 255, 0), 2)
				.addPlayer(1000, "CPU2", new Color(0, 0, 255), 3).addCity(new City(1.01, "dev/player.png", "Player0",
				0, 5000, 500, 20, 20, 500, 1.03, new ArrayList<>()))
				.dump("dev.data");
	}
}
