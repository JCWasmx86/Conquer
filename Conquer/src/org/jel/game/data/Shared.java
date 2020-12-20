package org.jel.game.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import org.jel.game.utils.Logger;

/**
 * A class with some shared utility methods.
 */
public final class Shared {
	/**
	 * Describes the directory with all data of the game. (E.g. configuration files,
	 * plugins, strategies,...)
	 */
	public static final String BASE_DIRECTORY = System.getProperty("os.name").toLowerCase().contains("windows")
			? System.getenv("APPDATA") + "\\.conquer\\"
			: System.getProperty("user.home") + "/.config/.conquer/";

	public static final String SAVE_DIRECTORY = Shared.BASE_DIRECTORY + System.getProperty("file.separator") + "saves";

	/**
	 * The maximum level of defense, resource production,...
	 */
	public static final int MAX_LEVEL = 1000;
	/**
	 * The id of the clan of the player.
	 */
	public static final int PLAYER_CLAN = 0;
	/**
	 * The default logger.
	 */
	public static final Logger LOGGER;
	/**
	 * Another logger that only logs, if the property {@code conquer.logging.level1}
	 * is set.
	 */
	private static final Logger LOGGER_LEVEL1;
	/**
	 * Another logger that only logs, if the property {@code conquer.logging.level2}
	 * is set.
	 */
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
	/**
	 * Describes the usage of coins for each soldier for each distance-unit at every
	 * move. (Additional to {@link Shared#COINS_PER_MOVE_OF_SOLDIER_BASE})
	 */
	public static final double COINS_PER_MOVE_OF_SOLDIER = 1.8;
	/**
	 * For every soldier, exactly this amount of coins has to be spent per move as a
	 * base.
	 */
	public static final double COINS_PER_MOVE_OF_SOLDIER_BASE = 4.0;
	/**
	 * Describes how many coins a person produces per round.
	 */
	public static final double COINS_PER_PERSON_PER_ROUND = 0.75;
	/**
	 * Initial costs for recruiting one soldier.
	 */
	public static final double COINS_PER_SOLDIER_INITIAL = 150.0;
	/**
	 * The amount of coins a soldier uses every round.
	 */
	public static final double COINS_PER_SOLDIER_PER_ROUND = 6;
	/**
	 * The amount of food every person uses per round.
	 */
	public static final double FOOD_PER_PERSON_PER_ROUND = 0.8;
	/**
	 * The amount of food every soldier uses per round.
	 */
	public static final double FOOD_PER_SOLDIER_PER_ROUND = 2.2;
	/**
	 * Initial use of iron for recruiting one soldier.
	 */
	public static final double IRON_PER_SOLDIER_INITIAL = 5;
	/**
	 * The amount of iron every soldier uses per round.
	 */
	public static final double IRON_PER_SOLDIER_PER_ROUND = 4;
	/**
	 * The amount of leather every person uses per round.
	 */
	public static final double LEATHER_PER_PERSON_PER_ROUND = 0.4;
	/**
	 * The amount of leather every soldier uses per round.
	 */
	public static final double LEATHER_PER_SOLDIER_PER_ROUND = 4;
	/**
	 * The amount of stone every person uses per round.
	 */
	public static final double STONE_PER_PERSON_PER_ROUND = 0.1;
	/**
	 * Initial use of stone for recruiting one soldier.
	 */
	public static final double STONE_PER_SOLDIER_INITIAL = 14.8;
	/**
	 * The amount of stone every soldier uses per round.
	 */
	public static final double STONE_PER_SOLDIER_PER_ROUND = 2;
	/**
	 * The amount of textiles every person uses per round.
	 */
	public static final double TEXTILES_PER_PERSON_PER_ROUND = 0.5;
	/**
	 * The amount of textiles every soldier uses per round.
	 */
	public static final double TEXTILES_PER_SOLDIER_PER_ROUND = 2;
	/**
	 * The amount of wood every person uses per round.
	 */
	public static final double WOOD_PER_PERSON_PER_ROUND = 0.01;
	/**
	 * Initial use of wood for recruiting one soldier.
	 */
	public static final double WOOD_PER_SOLDIER_INITIAL = 41.2;
	/**
	 * The amount of wood every soldier uses per round.
	 */
	public static final double WOOD_PER_SOLDIER_PER_ROUND = 1.5;
	/**
	 * The amount of coal every person uses per round.
	 */
	public static final double COAL_PER_PERSON_PER_ROUND = 1;
	/**
	 * The amount of coal every soldier uses per round.
	 */
	public static final double COAL_PER_SOLDIER_PER_ROUND = 1.5;
	/**
	 * The amount of wheat every soldier uses per round.
	 */
	public static final double WHEAT_PER_SOLDIER_PER_ROUND = 1.5;
	/**
	 * The amount of wheat every person uses per round.
	 */
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

	/**
	 * Calculate the cost of upgrading the resource to the specified level (The next
	 * level)
	 *
	 * @param level The next level
	 * @return The costs in coins for upgrading.
	 */
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

	/**
	 * Utility method to delete an entire directory.
	 *
	 * @param file What directory to delete.
	 * @throws IOException In case of an exception.
	 */
	public static void deleteDirectory(final File file) throws IOException {
		final var list = file.listFiles();
		if (list != null) {
			for (final var temp : list) {
				Shared.deleteDirectory(temp);
			}
		}
		Files.delete(Paths.get(file.toURI()));
	}

	/**
	 * Log an error on loglevel 1
	 *
	 * @param error The error message
	 */
	public static void errorLevel1(final String error) {
		if (System.getProperty("conquer.logging.level1") != null) {
			Shared.LOGGER_LEVEL1.error(error);
		}
	}

	/**
	 * Log an error on loglevel 2
	 *
	 * @param error The error message
	 */
	public static void errorLevel2(final String error) {
		if (System.getProperty("conquer.logging.level2") != null) {
			Shared.LOGGER_LEVEL1.error(error);
		}
	}

	/**
	 * Returns an array describing the use of a resource. Each subarray has the
	 * length 2. {@code subarray[0]} is the use of the resource per person per
	 * round, {@code subarray[1]} is the use of the resource per soldier per round.
	 *
	 * @return The array describing the use of a resource.
	 */
	public static double[][] getDataValues() {
		return Arrays.copyOf(Shared.DATA_VALUES, Shared.DATA_VALUES.length);
	}

	/**
	 * Returns a random number between 0 (inclusive) and {@code i} (exclusive).
	 *
	 * @param i Upper bound, should be positive
	 * @return A random number in [0;i[
	 */
	public static int getRandomNumber(final int i) {
		return Shared.RANDOM.nextInt(i);
	}

	/**
	 * Returns whether {@code x} is greater equals {@code lower} and smaller equals
	 * {@code upper}
	 *
	 * @param x
	 * @param lower
	 * @param upper
	 * @return The result
	 */
	public static boolean isBetween(final int x, final int lower, final int upper) {
		return (lower <= x) && (x <= upper);
	}

	/**
	 * Log a message on loglevel 1
	 *
	 * @param message The message
	 */
	public static void logLevel1(final String message) {
		if (System.getProperty("conquer.logging.level1") != null) {
			Shared.LOGGER_LEVEL1.message(message);
		}
	}

	/**
	 * Log a message on loglevel 2
	 *
	 * @param message The message
	 */
	public static void logLevel2(final String message) {
		if (System.getProperty("conquer.logging.level2") != null) {
			Shared.LOGGER_LEVEL1.message(message);
		}
	}

	/**
	 * Calculates the maximum number of levels that can be bought with a given
	 * startlevel and an amount of coins
	 *
	 * @param currLevel The current level
	 * @param coins     Number of coins
	 * @return Maximum number of levels.
	 */
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

	/**
	 * Calculates the maximum number of levels that can be bought with a given
	 * startlevel and an amount of coins
	 *
	 * @param currLevel The current level
	 * @param coins     Number of coins
	 * @return Maximum number of levels.
	 */
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

	/**
	 * Calculates the maximum number of levels that can be bought with a given
	 * startlevel and an amount of coins
	 *
	 * @param currLevel The current level
	 * @param coins     Number of coins
	 * @return Maximum number of levels.
	 */
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

	/**
	 * Returns the new strength of the soldiers of a clan for a given level
	 *
	 * @param level
	 * @return New strength.
	 */
	public static double newPowerForSoldiers(final int level) {
		return Shared.upgradeCostsForSoldiers(level) / 25000;
	}

	/**
	 * Returns the new offense or defense strength of the soldiers of a clan for a
	 * given level
	 *
	 * @param level
	 * @return New offense or defense strength.
	 */
	public static double newPowerOfSoldiersForOffenseAndDefense(final int level) {
		return Math.sqrt(Math.log(level) + (4 * level)) / 50;
	}

	/**
	 * Returns the new amount, how much of each resource is produced every round
	 * based on the last value and the level
	 *
	 * @param level
	 * @param oldValue
	 * @return New amount.
	 */
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

	/**
	 * Returns a percentage in [down;upper]
	 *
	 * @param down  Lower limit
	 * @param upper Upper limit
	 * @return Random percentage
	 */
	public static double randomPercentage(final double down, final double upper) {
		return (Math.random() * (upper / 100)) + (down / 100);
	}

	/**
	 * Returns a list of all saved games available
	 *
	 * @return
	 */
	public static String[] savedGames() {
		final var saves = new File(Shared.SAVE_DIRECTORY);
		if (!saves.exists()) {
			return new String[0];
		}
		return Arrays.stream(saves.list()).collect(Collectors.toList()).toArray(new String[0]);
	}

	/**
	 * Returns the costs for upgrading the offense/defense strength.
	 *
	 * @param x Current level
	 * @return Costs
	 */
	public static double upgradeCostsForOffenseAndDefense(final int x) {
		if (x == 0) {
			return 40.02;
		}
		return Math.pow(Math.log(x) + (4 * x), 3);
	}

	/**
	 * Returns the costs for upgrading the strength of the soldiers of a clan..
	 *
	 * @param x Current level
	 * @return Costs
	 */
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
