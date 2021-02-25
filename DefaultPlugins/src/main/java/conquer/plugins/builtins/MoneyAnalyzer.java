package conquer.plugins.builtins;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;

import conquer.data.EventList;
import conquer.data.ICity;
import conquer.data.IClan;
import conquer.plugins.Context;
import conquer.plugins.MoneyHook;
import conquer.plugins.Plugin;
import conquer.plugins.PluginInterface;
import conquer.utils.Graph;

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
	public void handle(final Graph<ICity> cities, final Context ctx) {
		this.currentRound++;
	}

	@Override
	public void init(final PluginInterface pi) {
		pi.addMoneyHook(this);
		this.events = pi.getEventList();
	}

	@Override
	public void moneyPaid(final List<ICity> cities, final IClan clan) {
		if (this.currentRound < Integer.getInteger("money.analyzer.delay", 10)) { //$NON-NLS-1$
			return;
		}
		if (clan.getCoins() > 0) {
			return;
		}
		final var costs = clan.getInfo().getSoldierCosts(clan).coinsPerSoldierPerRound();
		final var soldiers = Math.abs(clan.getCoins()) / costs;
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
		clan.setCoins(clan.getCoins() + (soldiers * costs));
		if (soldiers > 0) {
			this.events.add(new SoldiersDesertedBecauseOfMissingMoneyMessage(clan, num));
		}
	}

	@Override
	public void resume(final PluginInterface game, final InputStream bytes) throws IOException {
		try (var dis = new DataInputStream(bytes)) {
			this.currentRound = dis.readInt();
		}
		this.events = game.getEventList();
	}

	@Override
	public void save(final OutputStream outputStream) throws IOException {
		try (var dos = new DataOutputStream(outputStream)) {
			dos.writeInt(this.currentRound);
		}
	}
}
