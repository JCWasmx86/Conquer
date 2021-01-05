package org.jel.game.testsuite;

import java.io.File;

import org.jel.game.data.Shared;
import org.jel.game.data.XMLReader;
import org.jel.game.init.Initializer;

public final class PerformanceTest {
	public static void main(String[] args) {
		new File(Shared.BASE_DIRECTORY, "logs.log").delete();
		Shared.LOGGER.reopen();
		Initializer.INSTANCE().initialize(a -> {
			a.printStackTrace();
			throw new RuntimeException(a);
		});
		XMLReader.setThrowableConsumer(a -> {
			a.printStackTrace();
			throw new RuntimeException(a);
		});
		final var info = XMLReader.getInstance().readInfo();
		var currentRound = 0;
		final var MAX_ROUNDS = 1000000;
		long start = System.nanoTime();
		while (currentRound < MAX_ROUNDS) {
			for (final var s : info.getInstalledMaps()) {
				final var game = info.loadInfo(s);
				game.addContext(info);
				game.setPlayerGiftCallback((a, b, c, d, e, f) -> false);
				game.init();
				while (!game.onlyOneClanAlive() && currentRound < MAX_ROUNDS) {
					game.executeActions();
					currentRound++;
				}
			}
		}
		final var end = System.nanoTime();
		final var diff = end - start;
		final var nsPerRound = ((double) diff) / MAX_ROUNDS;
		System.out.println("ns/round: " + String.format("%.2f", nsPerRound));
	}
}
