package org.jel.game.plugins;

import org.jel.game.data.ICity;

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
	void handleMove(ICity src, ICity dest, long numberOfSoldiers);
}
