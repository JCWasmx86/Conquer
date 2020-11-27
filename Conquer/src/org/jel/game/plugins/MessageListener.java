package org.jel.game.plugins;

import org.jel.game.messages.Message;

public interface MessageListener {
	void added(Message s);

	void removed(Message s);
}
