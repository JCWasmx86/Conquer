package org.jel.game.plugins.builtins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.data.EventList;
import org.jel.game.data.Shared;
import org.jel.game.plugins.Context;
import org.jel.game.plugins.Plugin;
import org.jel.game.plugins.PluginInterface;
import org.jel.game.plugins.ResourceHook;
import org.jel.game.utils.Graph;

public final class ResourceAnalyzer implements Plugin, ResourceHook {

	private EventList events;
	private int currentRound = 0;

	@Override
	public void analyzeStats(final City city, final List<Double> statistics, final Clan clan) {
		if (this.currentRound < Integer.getInteger("resource.analyzer.delay", 10)) {
			return;
		}
		final List<Double> killedSoldiers = new ArrayList<>();
		final List<Double> killedCivilians = new ArrayList<>();
		for (var idx = 0; idx < statistics.size(); idx++) {
			final var resources = clan.getResources();
			if (statistics.get(idx) < 0) {
				double saved = clan.getResources().get(idx);
				saved += statistics.get(idx);
				if (saved >= 0) {
					resources.set(idx, saved);
					statistics.set(idx, 0.0);
				} else {
					resources.set(idx, 0.0);
					statistics.set(idx, -(-statistics.get(0) - clan.getResources().get(idx)));
				}
			}
			if (statistics.get(idx) < 0) {
				final double d = statistics.get(idx);
				final var numSoldiersToGetToZero = ((-d) / Shared.getDataValues()[idx][1]);
				if (numSoldiersToGetToZero < city.getNumberOfSoldiers()) {
					killedSoldiers.add(numSoldiersToGetToZero);
					killedCivilians.add(0.0);
				} else {
					final var d1 = d + (city.getNumberOfSoldiers() + Shared.getDataValues()[idx][1]);
					final var numCiviliansToGetToZero = (-d1) / Shared.getDataValues()[idx][0];
					killedSoldiers.add((double) city.getNumberOfSoldiers());
					if (!Double.isNaN(numCiviliansToGetToZero)) {
						killedCivilians
								.add(numCiviliansToGetToZero > city.getNumberOfPeople() ? city.getNumberOfPeople()
										: numCiviliansToGetToZero);
					} else {
						killedCivilians.add(0.0);
					}
				}
			} else {
				killedCivilians.add(0.0);
				killedSoldiers.add(0.0);
			}
		}
		Collections.sort(killedSoldiers);
		Collections.sort(killedCivilians);
		final var maxSoldiers = (long) Math.floor(killedSoldiers.get(killedSoldiers.size() - 1));
		final var maxCivilians = (long) Math.floor(killedCivilians.get(killedCivilians.size() - 1));
		if (maxSoldiers > 0) {
			final var tmp = city.getNumberOfSoldiers();
			city.setNumberOfSoldiers(city.getNumberOfSoldiers() - maxSoldiers);
			this.events.add(new SoldiersDesertedBecauseOfMissingResourcesMessage(maxSoldiers, city, clan));
			final var tmp2 = city.getNumberOfSoldiers();
			final var tmp3 = tmp > 100 ? 100 : tmp2;
			city.setNumberOfSoldiers(tmp2 == 0 ? tmp3 : tmp2);
		}
		if (maxCivilians > 0) {
			final var tmp = city.getNumberOfPeople();
			city.setNumberOfPeople(city.getNumberOfPeople() - maxCivilians);
			this.events.add(new CiviliansDiedBecauseOfMissingResourcesMessage(maxCivilians, city, clan));
			final var tmp2 = city.getNumberOfPeople();
			final var tmp3 = tmp > 100 ? 100 : tmp2;
			city.setNumberOfPeople(tmp2 == 0 ? tmp3 : tmp2);
		}
	}

	@Override
	public String getName() {
		return "ResourceAnalyzer";
	}

	@Override
	public void handle(final Graph<City> cities, final Context ctx) {
		this.currentRound++;

	}

	@Override
	public void init(final PluginInterface pi) {
		pi.addResourceHook(this);
		this.events = pi.getEventList();
	}
}
