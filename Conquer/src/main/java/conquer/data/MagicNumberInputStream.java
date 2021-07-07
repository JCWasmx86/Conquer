package conquer.data;

import java.io.InputStream;

public abstract class MagicNumberInputStream extends InputStream {

	/**
	 * Returns the start of the input stream and goes back to position 0.
	 *
	 * @param maxLength Number of bytes to read
	 *
	 * @return A byte array of size {@code maxLength}.
	 */
	public abstract byte[] getMagicNumber(int maxLength);
}
