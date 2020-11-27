package org.jel.game.messages;

import java.util.Optional;

public interface Message {
	default String getMessageText() {
		return null;
	}

	default Optional<String> getOptionalIconPath() {
		return Optional.empty();
	}

	default boolean isBadForPlayer() {
		return false;
	}

	default boolean isPlayerInvolved() {
		return false;
	}
}
