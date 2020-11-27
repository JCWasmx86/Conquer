package org.jel.game.plugins;

import org.jel.game.data.City;

@FunctionalInterface
public interface CityKeyHandler {
	void handle(String key, City c);
}
