package org.jel.game.plugins;

import java.util.List;

import org.jel.game.data.ICity;
import org.jel.game.data.IClan;

/**
 * Callback that is called when all cities of a clan produced money in a round
 */
@FunctionalInterface
public interface MoneyHook {
	/**
	 * @param cities All cities of the clan
	 * @param clan   The clan
	 */
	void moneyPaid(List<ICity> cities, IClan clan);
}
