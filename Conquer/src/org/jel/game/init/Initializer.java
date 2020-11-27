package org.jel.game.init;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

import org.jel.game.data.Shared;

/**
 * Loads the properties from .conquer/game.properties
 */
public final class Initializer {
	// Singleton
	private static final Initializer INSTANCE = new Initializer();
	private static boolean initialized = false;
	static boolean installing = false;

	/**
	 * Get the singleton-object of the Initializer
	 */
	public static Initializer INSTANCE() {
		return Initializer.INSTANCE;
	}

	private Initializer() {
	}

	/**
	 * Initialize the required things like properties.
	 */
	public void initialize() {
		if (Initializer.initialized) {
			throw new IllegalStateException("Can't initialize more than once!");
		}
		Initializer.initialized = true;
		final var file = new File(Shared.BASE_DIRECTORY);
		final var propertiesFile = new File(file, "game.properties");
		try {
			Thread.sleep(500);
			while (Initializer.installing) {

			}
			System.getProperties().load(Files.newInputStream(Paths.get(propertiesFile.toURI())));
		} catch (final IOException | RuntimeException | InterruptedException e) {
			Shared.LOGGER.error("Initialization failed!");
			Shared.LOGGER.exception(e);
			JOptionPane.showMessageDialog(null, "Initialization failed!", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
}
