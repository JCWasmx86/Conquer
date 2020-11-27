package org.jel.game.utils;

import java.io.IOException;

import org.jel.game.data.Shared;

public final class ShutdownThread extends Thread {
	@Override
	public void run() {
		try {
			Shared.LOGGER.close();
		} catch (final IOException e) {
			throw new InternalError(e);
		}
	}
}
