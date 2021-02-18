package conquer.plugins;

import conquer.data.AttackResult;
import conquer.data.ICity;

/**
 * Called when before and after an attack was executed.
 */
public interface AttackHook {
	/**
	 * Called after the attack
	 *
	 * @param src               Origin of the attack
	 * @param destination       Attacked city
	 * @param survivingSoldiers Number of soldiers that survived in the attacked
	 *                          city
	 * @param result            Result of the attack
	 */
	void after(ICity src, ICity destination, long survivingSoldiers, AttackResult result);

	/**
	 * Called before the attack
	 *
	 * @param src                   Origin of the attack
	 * @param destination           Attacked city
	 * @param numberOfSoldiersMoved Number of soldiers that attack the destination
	 *                              city.
	 */
	void before(ICity src, ICity destination, long numberOfSoldiersMoved);
}
