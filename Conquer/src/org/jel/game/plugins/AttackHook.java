package org.jel.game.plugins;

import org.jel.game.data.AttackResult;
import org.jel.game.data.City;

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
	void after(City src, City destination, long survivingSoldiers, AttackResult result);

	/**
	 * Called before the attack
	 *
	 * @param src                   Origin of the attack
	 * @param destination           Attacked city
	 * @param numberOfSoldiersMoved Number of soldiers that attack the destination
	 *                              city.
	 */
	void before(City src, City destination, long numberOfSoldiersMoved);
}
