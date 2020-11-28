package org.jel.game.init;

import java.io.IOException;
import java.io.OutputStream;

public abstract class ExtendedOutputStream extends OutputStream {
	@Override
	public void write(byte[] bytes) throws IOException {
		this.write(new String(bytes));
	}

	public abstract void write(String s) throws IOException;
}
