package org.jel.game.init;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

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
	public void initialize(Consumer<Exception> onError) {
		if (Initializer.initialized) {
			throw new IllegalStateException("Can't initialize more than once!");
		}
		Initializer.initialized = true;
		final var file = new File(Shared.BASE_DIRECTORY);
		final var propertiesFile = new File(file, "game.properties");
		try {
			Thread.sleep(500);
			while (Initializer.installing) {
				// Do nothing, as we are waiting for the end of the installation process
			}
			System.getProperties().load(Files.newInputStream(Paths.get(propertiesFile.toURI())));
		} catch (final IOException | RuntimeException | InterruptedException e) {
			Shared.LOGGER.error("Initialization failed!");
			Shared.LOGGER.exception(e);
			if (onError != null) {
				onError.accept(e);
			}
		}
	}
}
