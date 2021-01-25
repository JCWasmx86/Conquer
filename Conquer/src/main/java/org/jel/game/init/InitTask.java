package org.jel.game.init;

/**
 * This task is a implementation defined initialization.
 */
@FunctionalInterface
public interface InitTask {
	/**
	 * Run some init actions.
	 */
	void initialize();
}
