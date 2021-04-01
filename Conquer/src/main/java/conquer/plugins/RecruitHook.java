package conquer.plugins;

import conquer.data.ICity;

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
	void recruited(ICity city, long numberOfSoldiers);
}
