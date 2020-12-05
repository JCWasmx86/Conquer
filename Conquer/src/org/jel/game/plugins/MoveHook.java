package org.jel.game.plugins;

import org.jel.game.data.City;

/**
 * A callback that is invoked when troops are moved. (No attack)
 */
@FunctionalInterface
public interface MoveHook {
	/**
	 * Called when troops are moved
	 *
	 * @param src              Source city
	 * @param dest             Destination city
	 * @param numberOfSoldiers Number of soldiers
	 */
	void handleMove(City src, City dest, long numberOfSoldiers);
}
