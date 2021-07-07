package conquer.plugins;

import java.util.List;

import conquer.data.ICity;
import conquer.data.IClan;

/**
 * Callback that is called when all cities of a clan produced money in a round
 */
@FunctionalInterface
public interface MoneyHook {
	/**
	 * @param cities All cities of the clan
	 * @param clan   The clan
	 */
	void moneyPaid(List<? extends ICity> cities, IClan clan);
}
