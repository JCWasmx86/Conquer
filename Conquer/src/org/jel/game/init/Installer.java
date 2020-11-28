package org.jel.game.init;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jel.game.data.Shared;

/**
 * May be called, while the Intro is running. Automatically restores missing
 * files, but ignores all other files. It may be run in a separate thread.
 */
public class Installer implements Runnable {
	// The default info.xml file.
	private static final String DEFAULT_XML = "<!--Info file for conquer. Edit to add new scenarios, plugins and strategies -->"
			+ System.lineSeparator() + "<info>" + System.lineSeparator()
			+ "\t<!-- Add a new scenario. name is the name of the scenario, file points to the scenario file and thumbnail points to an image show while selecting a scenario -->"
			+ System.lineSeparator() + "\t<scenarios>" + System.lineSeparator()
			+ "\t\t<!-- <scenario name=\"foo.bar.Baz\" file=\"file.data\" thumbnail=\"thumb.png\" /> -->"
			+ System.lineSeparator() + "\t</scenarios>" + System.lineSeparator()
			+ "\t<!-- Add a new plugin. className is the name of the class implementing Plugin -->"
			+ System.lineSeparator() + "\t<plugins>" + System.lineSeparator()
			+ "\t\t<!-- <plugin className=\"foo.bar.some.class.implementing.Plugin\" /> -->" + System.lineSeparator()
			+ "\t</plugins>" + System.lineSeparator()
			+ "\t<!-- Add a new strategy. className is the name of the class implementing StrategyProvider -->"
			+ System.lineSeparator() + "\t<strategies>" + System.lineSeparator()
			+ "\t\t<!-- <strategy className=\"foo.bar.some.class.implementing.StrategyProvider\" /> -->"
			+ System.lineSeparator() + "\t</strategies>" + System.lineSeparator() + "</info>" + System.lineSeparator();
	// The default properties
	private static final String DEFAULT_PROPERTIES = "# Settings for conquer" + System.lineSeparator()
			+ "# If set to true, use .mp3 files==>Worse audioquality (Except for the intro,credits and mainscreen, but mp3 files may not be available)"
			+ System.lineSeparator() + "conquer.useMP3 = false" + System.lineSeparator()
			+ "# If set to true, no sound will be played, while playing." + System.lineSeparator()
			+ "conquer.nosound = false" + System.lineSeparator() + "# Add your own properties below"
			+ System.lineSeparator();
	private static final File BASE_FILE = new File(Shared.BASE_DIRECTORY).getAbsoluteFile();
	private OptionChooser chooser;
	private ExtendedOutputStream stream;

	// Checks, whether the game is already installed.
	private boolean alreadyInstalled() {
		final var baseFile = new File(Shared.BASE_DIRECTORY).getAbsoluteFile();
		return baseFile.exists() && new File(baseFile, "info.xml").exists();
	}

	/**
	 * Checks whether the game is installed. If no, it is installed/repaired.
	 *
	 * @implNote If the installation fails because of any reason, the JVM is killed
	 *           with the exit code -127!
	 */
	public Installer(OptionChooser chooser, ExtendedOutputStream writeTo) {
		if (chooser == null) {
			throw new IllegalArgumentException("chooser==null");
		}
		this.chooser = chooser;
		this.stream = writeTo;
	}

	@Override
	public void run() {
		if (!this.alreadyInstalled()) {
			write("Installing conquer!");
			try {
				Initializer.installing = true;
				this.startInstalling();
				Initializer.installing = false;
			} catch (final IOException e) {
				writeError("Installation failed");
				if (this.stream != null)
					e.printStackTrace(new PrintStream(this.stream));
				Shared.LOGGER.exception(e);
				System.exit(-127);// Exit, can't be resolved
			}
			write("Conquer was installed successfully!");
			write("Please restart Conquer!");
		}
	}

	private int askQuestion() {
		final String[] options = { "Minimal installation", "Standard installation (Scenarios and some plugins)",
				"Extended installation (Scenarios, plugins and music)" };
		return chooser.choose(options);
	}

	// Installs everything
	private void startInstalling() throws IOException {
		final var n = askQuestion();
		switch (n) {
		default:
		case 0:
			baseInstallation();
			break;
		case 1:
		case 2:
			extendedBaseInstallation();
			if (n == 2) {
				installMusic();
			}
			final var props = new File(BASE_FILE, "game.properties");
			if (!props.exists()) {
				Files.write(Paths.get(props.toURI()), Installer.DEFAULT_PROPERTIES.getBytes(),
						StandardOpenOption.CREATE);
			}
			break;
		}
	}

	private void installMusic() throws IOException {
		if (!connected()) {
			writeError("No internet connection!");
			return;
		}
		write("Please wait (~2 minutes)");
		for (var i = 1; i < 6; i++) {
			final var url = new URL(
					"https://raw.githubusercontent.com/JCWasmx86/JCWasmx86.github.io/master/zips/Music" + i + ".zip");
			write("Downloading: " + url);
			try (var urlStream = url.openStream()) {
				unzipFile(urlStream);
			} catch (IOException ioe) {
				writeError("Download failed: " + url);
			}
		}
		final var info = new File(BASE_FILE, "info.xml").getAbsoluteFile();
		String newContents = null;
		try (var stream2 = Files.newInputStream(Paths.get(new File(BASE_FILE, "info.xml").toString()))) {
			final var contents = new String(stream2.readAllBytes(), StandardCharsets.UTF_8);
			newContents = contents.replace("<!--<plugin className=\"org.jel.game.plugins.DefaultMusic\" />-->",
					"<plugin className=\"org.jel.game.plugins.DefaultMusic\" />");
		}
		Files.writeString(Paths.get(info.toURI()), newContents, StandardCharsets.UTF_8);
	}

	private boolean connected() {
		try (var testStream = new URL("https://www.example.com").openStream()) {
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private void write(String s) {
		Shared.LOGGER.message(s);
		if (stream != null) {
			try {
				stream.write(s);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void writeError(String s) {
		Shared.LOGGER.error(s);
		if (stream != null) {
			try {
				stream.write(s);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void unzipFile(InputStream zipStream) {
		try (var zin = new ZipInputStream(zipStream)) {
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null) {
				write("Unzipping: " + ze.getName());
				if (ze.isDirectory()) {
					new File(BASE_FILE, ze.getName()).mkdirs();
				} else {
					writeEntry(zin, ze);
				}
			}
		} catch (IOException e) {
			if (this.stream != null)
				e.printStackTrace(new PrintStream(this.stream));
			Shared.LOGGER.exception(e);
		}
	}

	private void writeEntry(ZipInputStream zin, ZipEntry ze) {
		try (var fos = new FileOutputStream(new File(BASE_FILE, ze.getName()))) {
			int read;
			final var bytes = new byte[1024];
			while ((read = zin.read(bytes)) != -1) {
				fos.write(bytes, 0, read);
			}
		} catch (IOException e) {
			if (this.stream != null)
				e.printStackTrace(new PrintStream(this.stream));
			Shared.LOGGER.exception(e);
		}
	}

	private void extendedBaseInstallation() throws IOException {
		try (final var zipStream = ClassLoader.getSystemResource("music/conquer.zip").openStream()) {
			BASE_FILE.mkdirs();
			unzipFile(zipStream);
		} catch (IOException e) {
			writeError("Wasn't able to find music/conquer.zip");
			if (this.stream != null)
				e.printStackTrace(new PrintStream(this.stream));
			Shared.LOGGER.exception(e);
		}
	}

	private void baseInstallation() throws IOException {
		BASE_FILE.mkdirs();
		final var xml = new File(BASE_FILE, "info.xml");
		if (!xml.exists()) {
			Files.write(Paths.get(xml.toURI()), Installer.DEFAULT_XML.getBytes(), StandardOpenOption.CREATE);
		}
		final var props = new File(BASE_FILE, "game.properties");
		if (!props.exists()) {
			Files.write(Paths.get(props.toURI()), Installer.DEFAULT_PROPERTIES.getBytes(), StandardOpenOption.CREATE);
		}
	}

}
