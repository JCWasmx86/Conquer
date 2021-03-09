package conquer.data;

/**
 * A factory to allow recognizing an unknown file and load it, if it is
 * supported.
 */
public interface ConquerInfoReaderFactory {

	/**
	 * Build a {@link ConquerInfoReader} for a given file.
	 *
	 * @param is The scenario. May not be {@code null}.
	 * @return A reader that is able to construct a {@link ConquerInfo} from
	 * {@code is}.
	 */
	ConquerInfoReader getForFile(InstalledScenario is);

	/**
	 * Gives the magic number for the file it is able to read.
	 *
	 * @return Magic number. May never be null.
	 */
	byte[] getMagicNumber();
}
