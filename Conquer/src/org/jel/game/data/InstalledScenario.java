package org.jel.game.data;

/**
 * Represents a scenario.<br>
 * {@code name} is the name of the scenario, {@code file} is the corresponding
 * file. {@code thumbnail} is an optional picture.
 */
public final record InstalledScenario(String name, String file, String thumbnail) {
}
