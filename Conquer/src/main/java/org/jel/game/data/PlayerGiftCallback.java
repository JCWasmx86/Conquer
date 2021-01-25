package org.jel.game.data;

import java.util.function.DoubleConsumer;

import org.jel.game.data.strategy.StrategyObject;

/**
 * A callback that is executed as soon as the player gets a gift.
 */
public interface PlayerGiftCallback {
	/**
	 * Called as soon as the player gets a gift.
	 *
	 * @param source         The source clan.
	 * @param destination    The destination clan - Always the clan with the id
	 *                       {@link Shared#PLAYER_CLAN}
	 * @param gift           The gift object
	 * @param oldValue       The old relationship value.
	 * @param newValue       Call the consumer with the new relationship value.
	 * @param strategyObject A handle to a strategyobject, e.g. {@link Game}.
	 * @return True if the gift has been accepted, false otherwise.
	 */
	boolean acceptGift(IClan source, IClan destination, Gift gift, double oldValue, DoubleConsumer newValue,
			StrategyObject strategyObject);
}
