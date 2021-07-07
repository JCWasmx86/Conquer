package conquer.init;

/**
 * An interface returning the index for the selected option.
 */
@FunctionalInterface
public interface OptionChooser {
<<<<<<< HEAD
	/**
	 * Returns the selected index.
	 *
	 * @param options All options. Will never be {@code null}.
	 *
	 * @return Selected index.
	 */
	int choose(String... options);
=======
    /**
     * Returns the selected index.
     *
     * @param options All options. Will never be {@code null}.
     * @return Selected index.
     */
    int choose(String[] options);
>>>>>>> parent of f8bbb68 (Formatting)
}
