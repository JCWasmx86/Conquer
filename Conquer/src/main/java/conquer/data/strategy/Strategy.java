package conquer.data.strategy;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.DoubleConsumer;

import conquer.data.Gift;
import conquer.data.ICity;
import conquer.data.IClan;
import conquer.utils.Graph;

/**
 * A strategy are the instructions for each clan. In every round
 * {@link Strategy#applyStrategy(IClan, Graph, StrategyObject)} is called. Then
 * the CPU/strategy executes its actions.
 */
public interface Strategy {
<<<<<<< HEAD
	/**
	 * Called, if {@code sourceClan} is giving {@code destinationClan} a gift. If
	 * the gift was accepted, {@code newValue} has to be applied with the new
	 * relationship between those two clans and {@code true} has to be returned.
	 *
	 * @param sourceClan      Source clan.
	 * @param destinationClan Destination clan.
	 * @param gift            The gift to give.
	 * @param oldValue        Old relationship value
	 * @param newValue        Can be applied to the new relationship value.
	 * @param strategyObject  An object giving more information.
	 *
	 * @return {@code true} if the gift was accepted, false otherwise.
	 */
	boolean acceptGift(IClan sourceClan, IClan destinationClan, Gift gift, double oldValue, DoubleConsumer newValue,
					   StrategyObject strategyObject);
=======
    /**
     * Called, if {@code sourceClan} is giving {@code destinationClan} a gift. If
     * the gift was accepted, {@code newValue} has to be applied with the new
     * relationship between those two clans and {@code true} has to be returned.
     *
     * @param sourceClan      Source clan.
     * @param destinationClan Destination clan.
     * @param gift            The gift to give.
     * @param oldValue        Old relationship value
     * @param newValue        Can be applied to the new relationship value.
     * @param strategyObject  An object giving more information.
     * @return {@code true} if the gift was accepted, false otherwise.
     */
    boolean acceptGift(IClan sourceClan, IClan destinationClan, Gift gift, double oldValue, DoubleConsumer newValue,
                       StrategyObject strategyObject);
>>>>>>> parent of f8bbb68 (Formatting)

    /**
     * Called every round. In this method the strategy should execute all actions,
     * like attacking, recruiting, upgrading,...
     *
     * @param clan   The clan.
     * @param cities All cities on the map.
     * @param obj    The object with methods that allow to recruit, upgrade, ...
     */
    void applyStrategy(IClan clan, Graph<ICity> cities, StrategyObject obj);

    /**
     * Returns an optional Strategydata.
     *
     * @return Data or {@code null}.
     */
    StrategyData getData();

<<<<<<< HEAD
	/**
	 * Resume after saving.
	 *
	 * @param strategyObject  Reference object, may be useful
	 * @param bytes           Bytes of the internal state of the strategy.
	 * @param hasStrategyData {@code true}, if {@link Strategy#getData()} returned a
	 *                        non-null value, {@code false} otherwise.
	 * @param dataBytes       If {@code hasStrategyData} is true, then the bytes of
	 *                        the StrategyData, otherwise the arrays is undefined.
	 *
	 * @return Return the constructed strategydata or {@code null}.
	 */
	default StrategyData resume(final StrategyObject strategyObject, final byte[] bytes, final boolean hasStrategyData,
								final byte[] dataBytes) {
		return null;
	}

	/**
	 * Optionally save the internal data of the strategy. The corresponding
	 * StrategyData shouldn't be saved by this method.
	 *
	 * @param out The outputstream to write to.
	 *
	 * @throws IOException If some I/O error occurred.
	 */
	default void save(final OutputStream out) throws IOException {
		//Empty as not every strategy has to save something
	}
=======
    /**
     * Resume after saving.
     *
     * @param strategyObject  Reference object, may be useful
     * @param bytes           Bytes of the internal state of the strategy.
     * @param hasStrategyData {@code true}, if {@link Strategy#getData()} returned a
     *                        non-null value, {@code false} otherwise.
     * @param dataBytes       If {@code hasStrategyData} is true, then the bytes of
     *                        the StrategyData, otherwise the arrays is undefined.
     * @return Return the constructed strategydata or {@code null}.
     */
    default StrategyData resume(final StrategyObject strategyObject, final byte[] bytes, final boolean hasStrategyData,
                                final byte[] dataBytes) {
        return null;
    }

    /**
     * Optionally save the internal data of the strategy. The corresponding
     * StrategyData shouldn't be saved by this method.
     *
     * @param out The outputstream to write to.
     * @throws IOException If some I/O error occurred.
     */
    default void save(final OutputStream out) throws IOException {

    }
>>>>>>> parent of f8bbb68 (Formatting)
}
