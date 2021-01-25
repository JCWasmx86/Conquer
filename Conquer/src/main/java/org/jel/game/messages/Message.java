package org.jel.game.messages;

import java.util.Optional;

/**
 * This interface is there for providing messages. Messages are events that will
 * be received by every registered handler.
 */
@FunctionalInterface
public interface Message {
	/**
	 * Return a human-readable text representation of the message.
	 *
	 * @return Message text.
	 */
	String getMessageText();

	/**
	 * Returns an optional icon path.
	 *
	 * @return Optional icon path.
	 */
	default Optional<String> getOptionalIconPath() {
		return Optional.empty();
	}

	/**
	 * Returns if the message is bad for the player or good.
	 *
	 * @return {@code true}, if it is bad for the player, e.g. a city was conquered.
	 */
	default boolean isBadForPlayer() {
		return false;
	}

	/**
	 * Return whether the player was involved in this message. (E.g. as attacker or
	 * defender).
	 *
	 * @return {@code true} if the player was involved.
	 */
	default boolean isPlayerInvolved() {
		return false;
	}

	/**
	 * Useful for GUIs, that show messages. Messages that return {@code true}, can
	 * be hidden, as they would for example give too much information.
	 *
	 * @return {@code true} if the message shouldn't be shown to the player.
	 */
	default boolean shouldBeShownToThePlayer() {
		return this.isPlayerInvolved();
	}
}
