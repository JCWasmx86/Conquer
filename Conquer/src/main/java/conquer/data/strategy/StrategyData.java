package conquer.data.strategy;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This data is associated with a strategy. It can contain anything.
 */
@FunctionalInterface
public interface StrategyData {
	/**
	 * Save the internal state of the data to {@code out}.
	 *
	 * @param out Destination of the serialized data.
	 * @throws IOException If an I/O error occurs.
	 */
	default void save(final OutputStream out) throws IOException {
		//Empty as not every strategy has data that must be saved
	}

	/**
	 * Update the internal state of the data.
	 *
	 * @param currentRound The current round of the game.
	 */
	void update(int currentRound);
}
