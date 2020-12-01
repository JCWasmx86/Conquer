package org.jel.game.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import org.jel.game.utils.Logger;

public final class Shared {
	public static final String BASE_DIRECTORY = System.getProperty("os.name").toLowerCase().contains("windows")
			? System.getProperty("user.home") + "\\Appdata\\Roaming\\.conquer\\"
			: System.getProperty("user.home") + "/.config/.conquer/";
	public static final int PLAYER_CLAN = 0;
	public static final Logger LOGGER;
	private static final Logger LOGGER_LEVEL1;
	private static final Logger LOGGER_LEVEL2;
	static {
		new File(Shared.BASE_DIRECTORY).mkdirs();
		LOGGER = new Logger(Shared.BASE_DIRECTORY + "/logs.log");
		LOGGER_LEVEL1 = new Logger(Shared.BASE_DIRECTORY + "/1.log");
		LOGGER_LEVEL2 = new Logger(Shared.BASE_DIRECTORY + "/2.log");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				Shared.LOGGER_LEVEL1.close();
				Shared.LOGGER_LEVEL2.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}));
	}
	public static final double COINS_PER_MOVE_OF_SOLDIER = 1.8;
	public static final double COINS_PER_MOVE_OF_SOLDIER_BASE = 4.0;
	public static final double COINS_PER_PERSON_PER_ROUND = 0.75;
	public static final double COINS_PER_SOLDIER_INITIAL = 150.0;
	public static final double COINS_PER_SOLDIER_PER_ROUND = 6;
	public static final double FOOD_PER_PERSON_PER_ROUND = 0.8;
	public static final double FOOD_PER_SOLDIER_PER_ROUND = 2.2;
	public static final double IRON_PER_SOLDIER_INITIAL = 5;
	public static final double IRON_PER_SOLDIER_PER_ROUND = 4;
	public static final double LEATHER_PER_PERSON_PER_ROUND = 0.4;
	public static final double LEATHER_PER_SOLDIER_PER_ROUND = 4;
	public static final double STONE_PER_PERSON_PER_ROUND = 0.1;
	public static final double STONE_PER_SOLDIER_INITIAL = 14.8;
	public static final double STONE_PER_SOLDIER_PER_ROUND = 2;
	public static final double TEXTILES_PER_PERSON_PER_ROUND = 0.5;
	public static final double TEXTILES_PER_SOLDIER_PER_ROUND = 2;
	public static final double WOOD_PER_PERSON_PER_ROUND = 0.01;
	public static final double WOOD_PER_SOLDIER_INITIAL = 41.2;
	public static final double WOOD_PER_SOLDIER_PER_ROUND = 1.5;
	public static final double COAL_PER_PERSON_PER_ROUND = 1;
	public static final double COAL_PER_SOLDIER_PER_ROUND = 1.5;
	public static final double WHEAT_PER_SOLDIER_PER_ROUND = 1.5;
	public static final double WHEAT_PER_PERSON_PER_ROUND = 0.94;
	private static final double[][] DATA_VALUES = {
			{ Shared.WHEAT_PER_PERSON_PER_ROUND, Shared.WHEAT_PER_SOLDIER_PER_ROUND },
			{ Shared.FOOD_PER_PERSON_PER_ROUND, Shared.FOOD_PER_SOLDIER_PER_ROUND },
			{ Shared.WOOD_PER_PERSON_PER_ROUND, Shared.WOOD_PER_SOLDIER_PER_ROUND },
			{ Shared.COAL_PER_PERSON_PER_ROUND, Shared.COAL_PER_SOLDIER_PER_ROUND },
			{ Shared.FOOD_PER_PERSON_PER_ROUND, Shared.FOOD_PER_SOLDIER_PER_ROUND },
			{ 0, Shared.IRON_PER_SOLDIER_PER_ROUND },
			{ Shared.TEXTILES_PER_PERSON_PER_ROUND, Shared.TEXTILES_PER_SOLDIER_PER_ROUND },
			{ Shared.LEATHER_PER_PERSON_PER_ROUND, Shared.LEATHER_PER_SOLDIER_PER_ROUND },
			{ Shared.STONE_PER_PERSON_PER_ROUND, Shared.STONE_PER_SOLDIER_PER_ROUND } };

	private static final Random RANDOM = new Random(System.nanoTime());

	public static double costs(final int level) {
		var ret = Math.pow(level, Math.E);
		if (level != 0) {
			ret = Math.pow(ret, 1.0d / 8.0d) * Math.pow(level, 1.0d / 3.0d);
		}
		ret = Math.pow(ret, (Math.PI / Math.E) + Math.pow(level, 1.0d / 40.0d));
		ret *= Math.toRadians(level);
		ret *= Math.toDegrees(level / 360.0d) / Math.PI;
		return ret;
	}

	public static void deleteDirectory(File file) throws IOException {
		final var list = file.listFiles();
		if (list != null) {
			for (final var temp : list) {
				Shared.deleteDirectory(temp);
			}
		}
		Files.delete(Paths.get(file.toURI()));
	}

	public static void errorLevel1(String error) {
		if (System.getProperty("conquer.logging.level1") != null) {
			Shared.LOGGER_LEVEL1.error(error);
		}
	}

	public static void errorLevel2(String error) {
		if (System.getProperty("conquer.logging.level2") != null) {
			Shared.LOGGER_LEVEL1.error(error);
		}
	}

	public static double[][] getDataValues() {
		return Arrays.copyOf(Shared.DATA_VALUES, Shared.DATA_VALUES.length);
	}

	public static int getRandomNumber(int i) {
		return Shared.RANDOM.nextInt(i);
	}

	public static boolean isBetween(final int x, final int lower, final int upper) {
		return (lower <= x) && (x <= upper);
	}

	public static void logLevel1(String message) {
		if (System.getProperty("conquer.logging.level1") != null) {
			Shared.LOGGER_LEVEL1.message(message);
		}
	}

	public static void logLevel2(String message) {
		if (System.getProperty("conquer.logging.level2") != null) {
			Shared.LOGGER_LEVEL1.message(message);
		}
	}

	public static int maxLevelsAddOffenseDefenseUpgrade(final int currLevel, double coins) {
		var cnt = 0;
		while (true) {
			final var costs = Shared.upgradeCostsForOffenseAndDefense(currLevel + cnt);
			if (costs > coins) {
				break;
			}
			coins -= costs;
			cnt++;
		}
		return cnt;
	}

	public static int maxLevelsAddResourcesUpgrade(final int currLevel, double coins) {
		var cnt = 0;
		while (true) {
			final var costs = Shared.costs(currLevel + cnt);
			if (costs > coins) {
				break;
			}
			coins -= costs;
			cnt++;
		}
		return cnt;
	}

	public static int maxLevelsAddSoldiersUpgrade(final int currLevel, double coins) {
		var cnt = 0;
		while (true) {
			final var costs = Shared.upgradeCostsForSoldiers(currLevel + cnt);
			if (costs > coins) {
				break;
			}
			coins -= costs;
			cnt++;
		}
		return cnt;
	}

	public static double newPowerForSoldiers(final int level) {
		return Shared.upgradeCostsForSoldiers(level) / 25000;
	}

	public static double newPowerOfSoldiersForOffenseAndDefense(final int x) {
		return Math.sqrt(Math.log(x) + (4 * x)) / 50;
	}

	public static double newPowerOfUpdate(final int level, final double oldValue) {
		if (level == 0) {
			return oldValue * 1.3;
		}
		var c = ((level + 1.0) / level);
		while (c > Math.E) {
			c = Math.log(c);
		}
		while (c > 1.002) {
			c = Math.pow(c, 1 / (c * c * c));
		}
		return oldValue * Math.pow(c, 6);
	}

	public static double randomPercentage(final double down, final double upper) {
		return (Math.random() * (upper / 100)) + (down / 100);
	}

	public static String[] savedGames() {
		final var saves = new File(Shared.BASE_DIRECTORY, "saves");
		if (!saves.exists()) {
			return new String[0];
		}
		return Arrays.stream(saves.list()).collect(Collectors.toList()).toArray(new String[0]);
	}

	public static double upgradeCostsForOffenseAndDefense(final int x) {
		if (x == 0) {
			return 40.02;
		}
		return Math.pow(Math.log(x) + (4 * x), 3);
	}

	public static double upgradeCostsForSoldiers(final int x) {
		if (x == 0) {
			return 230.02;
		}
		return Shared.upgradeCostsForSoldiers0(Shared.upgradeCostsForSoldiers0((0.1 * x) + 3)) * 1000;
	}

	private static double upgradeCostsForSoldiers0(final double x) {
		return x - (0.8 * Math.sin(x));
	}

	private Shared() {

	}
}
