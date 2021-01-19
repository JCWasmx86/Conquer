package org.jel.game.init;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Some special output stream.
 */
public abstract class ExtendedOutputStream extends OutputStream {
	@Override
	public void write(final byte[] bytes) throws IOException {
		this.write(new String(bytes));
	}

	/**
	 * Write a string.
	 *
	 * @param s The string to write
	 * @throws IOException If an exception occurs.
	 */
	public abstract void write(String s) throws IOException;
}
