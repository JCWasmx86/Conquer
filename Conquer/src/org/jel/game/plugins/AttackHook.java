package org.jel.game.plugins;

import org.jel.game.data.AttackResult;
import org.jel.game.data.City;

public interface AttackHook {
	void after(City src, City destination, long survivingSoldiers, AttackResult result);

	void before(City src, City destination, long numberOfSoldiersMoved);
}
