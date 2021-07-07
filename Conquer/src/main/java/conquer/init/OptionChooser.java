package conquer.init;

/**
 * An interface returning the index for the selected option.
 */
@FunctionalInterface
public interface OptionChooser {
	/**
	 * Returns the selected index.
	 *
	 * @param options All options. Will never be {@code null}.
	 *
	 * @return Selected index.
	 */
	int choose(String... options);
}
