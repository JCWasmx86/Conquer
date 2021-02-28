package conquer.plugins;

import conquer.data.ICity;
import conquer.data.IClan;

import java.util.List;

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
