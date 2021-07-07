package conquer.data;

/**
 * An interface for a class that is able to save and restore a
 * {@link ConquerInfo} It may be bound to a special implementation. Every class
 * that implements this interface must provide an accessible constructor taking
 * a {@link String} as argument.
 */
public interface ConquerSaver {
<<<<<<< HEAD
	/**
	 * Restore the game state.
	 *
	 * @return The restored game state.
	 *
	 * @throws Exception If an undefined exception occurs.
	 */
	ConquerInfo restore() throws Exception;

	/**
	 * Save the game state.
	 *
	 * @param info Game state. May not be {@code null}.
	 *
	 * @throws Exception If an undefined exception occurs.
	 */
	void save(ConquerInfo info) throws Exception;
=======
    /**
     * Restore the game state.
     *
     * @return The restored game state.
     * @throws Exception If an undefined exception occurs.
     */
    ConquerInfo restore() throws Exception;

    /**
     * Save the game state.
     *
     * @param info Game state. May not be {@code null}.
     * @throws Exception If an undefined exception occurs.
     */
    void save(ConquerInfo info) throws Exception;
>>>>>>> parent of f8bbb68 (Formatting)
}
