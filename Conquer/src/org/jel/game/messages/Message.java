package org.jel.game.messages;

import java.util.Optional;

public interface Message {
	String getMessageText();

	default Optional<String> getOptionalIconPath() {
		return Optional.empty();
	}

	default boolean isBadForPlayer() {
		return false;
	}

	default boolean isPlayerInvolved() {
		return false;
	}

	default boolean shouldBeShownToThePlayer() {
		return this.isPlayerInvolved();
	}
}
