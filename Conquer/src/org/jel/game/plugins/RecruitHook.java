package org.jel.game.plugins;

import org.jel.game.data.City;

@FunctionalInterface
public interface RecruitHook {
	void recruited(City c, long n);
}
