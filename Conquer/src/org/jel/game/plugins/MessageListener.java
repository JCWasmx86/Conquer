package org.jel.game.plugins;

import org.jel.game.messages.Message;

/**
 * A listener that reacts to different events of the EventList
 */
public interface MessageListener {
	/**
	 * Called when a message has been added to the list
	 *
	 * @param message The added message
	 */
	void added(Message message);

	/**
	 * Called when a message has been removed from the list
	 *
	 * @param message The removed message
	 */
	void removed(Message message);
}
