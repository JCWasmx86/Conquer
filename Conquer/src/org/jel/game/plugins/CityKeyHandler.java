package org.jel.game.plugins;

import org.jel.game.data.City;

/**
 * A callback that is called as soon as a city is focused and the specified key
 * is pressed.
 */
@FunctionalInterface
public interface CityKeyHandler {
	/**
	 * Called as soon as a city is focused and the specified key is pressed
	 *
	 * @param key  The key that was pressed
	 * @param city The focused city.
	 */
	void handle(String key, City city);
}
