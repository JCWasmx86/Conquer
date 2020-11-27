package org.jel.game.plugins;

import java.util.List;

import org.jel.game.data.City;
import org.jel.game.data.Clan;

@FunctionalInterface
public interface ResourceHook {

	void analyzeStats(final City city, final List<Double> statistics, Clan clan);

}
