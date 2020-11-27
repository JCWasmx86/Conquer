package org.jel.game.plugins;

import org.jel.game.data.City;

@FunctionalInterface
public interface MoveHook {
	void handleMove(City src, City dest, long cnt);
}
