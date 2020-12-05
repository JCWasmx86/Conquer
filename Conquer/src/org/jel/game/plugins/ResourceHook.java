package org.jel.game.plugins;

import java.util.List;
import org.jel.game.data.City;
import org.jel.game.data.Clan;

/**
 * Called after resources were produced in a city.
 */
@FunctionalInterface
public interface ResourceHook {

	/**
	 * 
	 * @param city       The city
	 * @param statistics The statistics: &gt; 0 means more was produced
	 * @param clan       The clan of the city.
	 */
	void analyzeStats(final City city, final List<Double> statistics, Clan clan);

}
