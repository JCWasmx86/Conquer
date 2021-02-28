package conquer.data;

/**
 * An interface describing a reader for building an uninitialized
 * {@link ConquerInfo} object.
 */
public interface ConquerInfoReader {

    /**
     * Read the inputfile and build an uninitialized ConquerInfo.
     *
     * @return An uninitialized {@link ConquerInfo} object.
     */
    ConquerInfo build();
}
