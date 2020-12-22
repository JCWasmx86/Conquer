package org.jel.game.data;

public interface ConquerInfoReader {

	/**
	 * Read the inputfile and build an uninitialized ConquerInfo.
	 *
	 * @return An uninitialized info object.
	 */
	ConquerInfo build();
}
