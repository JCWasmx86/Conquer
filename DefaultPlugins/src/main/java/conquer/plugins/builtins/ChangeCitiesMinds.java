package conquer.plugins.builtins;

import conquer.data.ICity;
import conquer.data.IClan;
import conquer.data.Shared;
import conquer.data.StreamUtils;
import conquer.plugins.Context;
import conquer.plugins.Plugin;
import conquer.utils.Graph;

import java.util.Random;

public final class ChangeCitiesMinds implements Plugin {
	private static final double INSTANT_CLAN_CHANGE = 0.05;
	private static final int PROBABILITY_NO_CHANGE_OF_CLAN = 75;
	private final Random random = new Random();

	private void change(final ICity c, final Context ctx, final IClan oldClan, final IClan otherClan) {
		var changedClan = false;
		var soldiersToCivilians = 1D;
		try {
			soldiersToCivilians = (double) c.getNumberOfSoldiers() / (double) c.getNumberOfPeople();
		} catch (final ArithmeticException ae) {
			Shared.LOGGER.exception(ae);
		}
		if (soldiersToCivilians < ChangeCitiesMinds.INSTANT_CLAN_CHANGE) {
			c.setClan(otherClan);
			changedClan = true;
		} else {
			changedClan = this.evalClanChange(soldiersToCivilians, c, otherClan);
		}
		if (changedClan) {
			ctx.appendToEventList(new ClanChangeMessage(c, oldClan, c.getClan()));
		}
	}

	private boolean evalClanChange(final double soldiersToCivilians, final ICity c, final IClan otherClan) {
		if (this.random.nextInt(100) > 90) {
			if ((soldiersToCivilians < 0.15) && (Math.random() > 0.85)) {
				c.setClan(otherClan);
				c.setNumberOfPeople((long) (c.getNumberOfPeople() * Shared.randomPercentage(90, 98)));
				c.setNumberOfSoldiers((long) (c.getNumberOfSoldiers() * Shared.randomPercentage(45, 90)));
				return true;
			} else if ((soldiersToCivilians < 0.25) && (Math.random() > 0.9)) {
				c.setClan(otherClan);
				c.setNumberOfPeople((long) (c.getNumberOfPeople() * Shared.randomPercentage(60, 88)));
				c.setNumberOfSoldiers((long) (c.getNumberOfSoldiers() * Shared.randomPercentage(55, 90)));
				return true;
			} else if ((soldiersToCivilians < 0.35) && (Math.random() > 0.98)) {
				c.setClan(otherClan);
				c.setNumberOfPeople((long) (c.getNumberOfPeople() * Shared.randomPercentage(20, 60)));
				c.setNumberOfSoldiers((long) (c.getNumberOfSoldiers() * Shared.randomPercentage(80, 98)));
				return true;
			}
		}
		return false;
	}

	@Override
	public String getName() {
		return "ChangeCitiesMinds";
	}

	@Override
	public void handle(final Graph<ICity> cities, final Context ctx) {
		StreamUtils.forEach(cities, c -> {
			// Only continue, if the loss of one city may not extinct one entire clan.
			if (StreamUtils.getCitiesAsStream(cities, c.getClan()).count() == 1) {
				return;
			}
			final var oldClan = c.getClan();
			final var list = StreamUtils.getCitiesAroundCity(cities, c).map(ICity::getClan).distinct()
										.toList();
			final var canChangeClan = list.size() == 1;
			final var otherClan = list.get(0);
			if ((!canChangeClan || (otherClan == c.getClan()))
					|| (this.random.nextInt(100) < ChangeCitiesMinds.PROBABILITY_NO_CHANGE_OF_CLAN)) {
				return;
			}
			this.change(c, ctx, oldClan, otherClan);
		});
	}
}
