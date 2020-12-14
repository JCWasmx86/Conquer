package org.jel.game.plugins.builtins;

import java.util.List;
import java.util.Random;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.data.EventList;
import org.jel.game.data.Shared;
import org.jel.game.plugins.Context;
import org.jel.game.plugins.MoneyHook;
import org.jel.game.plugins.Plugin;
import org.jel.game.plugins.PluginInterface;
import org.jel.game.utils.Graph;

public final class MoneyAnalyzer implements Plugin, MoneyHook {
	private static final int MAX_ITERATIONS = 12000;
	private final Random random = new Random();
	private EventList events;
	private int currentRound = 0;

	@Override
	public String getName() {
		return "MoneyAnalyzer"; //$NON-NLS-1$
	}

	@Override
	public void handle(final Graph<City> cities, final Context ctx) {
		this.currentRound++;
	}

	@Override
	public void init(final PluginInterface pi) {
		pi.addMoneyHook(this);
		this.events = pi.getEventList();
	}

	@Override
	public void moneyPaid(final List<City> cities, final Clan clan) {
		if (this.currentRound < Integer.getInteger("money.analyzer.delay", 10)) { //$NON-NLS-1$
			return;
		}
		if (clan.getCoins() > 0) {
			return;
		}
		final var soldiers = Math.abs(clan.getCoins()) / Shared.COINS_PER_SOLDIER_PER_ROUND;
		var num = 0L;
		var cnter = 0;
		while ((num < soldiers) && (cnter < MoneyAnalyzer.MAX_ITERATIONS)) {
			final var idx = this.random.nextInt(cities.size());
			final var c = cities.get(idx);
			final var percentage = this.random.nextDouble();
			final var desertingSoldiers = (long) (percentage * c.getNumberOfSoldiers());
			num += desertingSoldiers;
			c.setNumberOfSoldiers(c.getNumberOfSoldiers() - desertingSoldiers);
			cnter++;
		}
		clan.setCoins(clan.getCoins() + (soldiers * Shared.COINS_PER_SOLDIER_PER_ROUND));
		if (soldiers > 0) {
			this.events.add(new SoldiersDesertedBecauseOfMissingMoneyMessage(clan, num));
		}
	}
}
