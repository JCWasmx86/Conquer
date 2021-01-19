package org.jel.game.data;

/**
 * A factory to allow recognizing an unknown file and load it, if it is
 * supported.
 */
public interface ConquerInfoReaderFactory {

	/**
	 * Build a {@link ConquerInfoReader} for a given file.
	 *
	 * @param file The file. May not be {@code null}.
	 * @return A reader that is able to construct a {@link ConquerInfo} from
	 *         {@code file}.
	 */
	ConquerInfoReader getForFile(InstalledScenario is);

	/**
	 * Gives the magic number for the file it is able to read.
	 *
	 * @return Magic number. May not be null.
	 */
	byte[] getMagicNumber();
}
