package conquer.testsuite;

import conquer.data.SPIContextBuilder;
import conquer.data.Shared;
import conquer.init.Initializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public final class PerformanceTest {
	private PerformanceTest() {
		//Empty
	}

	public static void main(final String[] args) {
		new File(Shared.BASE_DIRECTORY, "logs.log").delete();
		Shared.LOGGER.reopen();
		Initializer.INSTANCE().initialize(a -> {
			a.printStackTrace();
			throw new RuntimeException(a);
		});
		final var info = new SPIContextBuilder().buildContext();
		var currentRound = 0;
		final var MAX_ROUNDS = 1000000;
		final var start = System.nanoTime();
		while (currentRound < MAX_ROUNDS) {
			for (final var s : info.getInstalledMaps()) {
				final var game = info.loadInfo(s);
				game.addContext(info);
				game.setPlayerGiftCallback((a, b, c, d, e, f) -> false);
				game.init();
				while (!game.onlyOneClanAlive() && (currentRound < MAX_ROUNDS)) {
					game.executeActions();
					currentRound++;
				}
			}
		}
		final var end = System.nanoTime();
		final var diff = end - start;
		final var nsPerRound = (((double) diff) / MAX_ROUNDS);
		final var roundsPerSecond = 1_000_000_000 / nsPerRound;
		System.out.println("ns/round (With loading of the scenarios): " + String.format("%.2f", nsPerRound));
		System.out.println("rounds/s (With loading of the scenarios): " + String.format("%.2f", roundsPerSecond));
		Shared.LOGGER.reopen();// Sync the changes with the filesystem
		try (final var reader = new BufferedReader(new FileReader(new File(Shared.BASE_DIRECTORY, "logs.log")))) {
			var max = 0.0;
			var min = Double.MAX_VALUE;
			var total = 0.0;
			while (reader.ready()) {
				final var line = reader.readLine();
				if (line.contains("CPUPLAY:")) {
					final var number = line.split("CPUPLAY:")[1].trim().replace("ms", "");
					final var value = Double.parseDouble(number);// 10^-6 seconds
					final var valueInNS = value * 1000;
					max = Math.max(max, valueInNS);
					min = Math.min(min, valueInNS);
					total += valueInNS;
				}
			}
			final var adjustedNsPerRound = total / MAX_ROUNDS;
			final var adjustedRoundsPerSecond = 1_000_000_000 / adjustedNsPerRound;
			System.out.println(
					"ns/round (Without loading of the scenarios): " + String.format("%.2f", adjustedNsPerRound));
			System.out.println(
					"rounds/s (Without loading of the scenarios): " + String.format("%.2f", adjustedRoundsPerSecond));
			System.out.println("Maximum length of one round in ns: " + String.format("%.2f", max));
			System.out.println("Minimum length of one round in ns: " + String.format("%.2f", min));
			if (max > 150_000) {
				throw new Error("The maximum length of one round is too big: " + max + " (Expected <=150_000)");
			} else if (adjustedNsPerRound > 150) {
				throw new Error("One round is on average too slow: " + adjustedNsPerRound + " (Expected <=150)");
			}
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
