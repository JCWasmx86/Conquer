package org.jel.game.plugins;

import org.jel.game.data.City;

/**
 * Called when soldiers are recruited.
 */
@FunctionalInterface
public interface RecruitHook {
	/**
	 * Called when soldiers are recruited.
	 *
	 * @param city             The specified city
	 * @param numberOfSoldiers The number of soldiers that were recruited.
	 */
	void recruited(City city, long numberOfSoldiers);
}
