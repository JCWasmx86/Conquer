package conquer.init;

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
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import conquer.Messages;
import conquer.data.Shared;

/**
 * Automatically restores missing files, but ignores all other files. It may be
 * run in a separate thread.
 */
/// Why is this here? This is really weird.
public class Installer implements Runnable {
	// The default info.xml file.
	private static final String DEFAULT_XML = "<!--Info file for conquer. Edit to add new scenarios, plugins and " +
		"strategies -->"
		+ System.lineSeparator() + "<info>" + System.lineSeparator()
		+ "\t<!-- Add a new scenario. name is the name of the scenario, file points to the scenario file and " +
		"thumbnail points to an image show while selecting a scenario -->"
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
	private static final String DEFAULT_PROPERTIES = "# Settings for conquer" + System.lineSeparator();
	private static final File BASE_FILE = new File(Shared.BASE_DIRECTORY).getAbsoluteFile();
	private final OptionChooser chooser;
	private final ExtendedOutputStream stream;
	private final Consumer<? super Exception> onError;

	/**
	 * Constructs a new Installer
	 *
	 * @param chooser Returns the option to selected. May not be {@code null},
	 *                otherwise an {@link IllegalArgumentException} will be thrown.
	 * @param writeTo Outputstream for status messages. May be {@code null}.
	 * @param onError Consumer for occurring exceptions. May be {@code null}.
	 */
	public Installer(final OptionChooser chooser, final ExtendedOutputStream writeTo,
					 final Consumer<? super Exception> onError) {
		if (chooser == null) {
			throw new IllegalArgumentException("chooser==null");
		}
		this.chooser = chooser;
		this.stream = writeTo;
		this.onError = onError;
	}

	// Checks, whether the game is already installed.
	private boolean alreadyInstalled() {
		final var baseFile = new File(Shared.BASE_DIRECTORY).getAbsoluteFile();
		return baseFile.exists() && new File(baseFile, "info.xml").exists();
	}

	private int askQuestion() {
		return this.chooser.choose(Messages.getString("Installer.minimalInstallation"),
			Messages.getString("Installer.standardInstallation"),
			Messages.getString("Installer.extendedInstallation"));
	}

	private void baseInstallation() throws IOException {
		Installer.BASE_FILE.mkdirs();
		final var xml = new File(Installer.BASE_FILE, "info.xml");
		if (!xml.exists()) {
			Files.write(Paths.get(xml.toURI()), Installer.DEFAULT_XML.getBytes(), StandardOpenOption.CREATE);
		}
		final var props = new File(Installer.BASE_FILE, "game.properties");
		if (!props.exists()) {
			Files.write(Paths.get(props.toURI()), Installer.DEFAULT_PROPERTIES.getBytes(), StandardOpenOption.CREATE);
		}
	}

	private boolean connected() {
		try (final var testStream = new URL("https://www.example.com").openStream()) {
			return true;
		} catch (final IOException e) {
			return false;
		}
	}

	private void extendedBaseInstallation() throws IOException {
		this.baseInstallation();
		try (final var zipStream = ClassLoader.getSystemResource("data/conquer.zip").openStream()) {
			Installer.BASE_FILE.mkdirs();
			this.unzipFile(zipStream);
		} catch (final IOException e) {
			this.writeError("Wasn't able to find music/conquer.zip");
			if (this.stream != null) {
				e.printStackTrace(new PrintStream(this.stream));
			}
			Shared.LOGGER.exception(e);
		}
	}

	private void installMusic() throws IOException {
		if (!this.connected()) {
			this.writeError(Messages.getString("Installer.noInternetConnection"));
			return;
		}
		this.write(Messages.getString("Installer.pleaseWait"));
		final var url = new URL(
			"https://raw.githubusercontent.com/JCWasmx86/JCWasmx86.github.io/master/conquer-data/Music.zip");

		final var messageString = Messages.getMessage("Installer.downloading", url.toString());
		this.write(messageString);
		try (final var urlStream = url.openStream()) {
			this.unzipFile(urlStream);
		} catch (final IOException ioe) {
			this.writeError(Messages.getString("Installer.downloadFailed") + url);
		}
		final var info = new File(Installer.BASE_FILE, "info.xml").getAbsoluteFile();
		final String newContents;
		try (final var stream2 =
				 Files.newInputStream(Paths.get(new File(Installer.BASE_FILE, "info.xml").toString()))) {
			final var contents = new String(stream2.readAllBytes(), StandardCharsets.UTF_8);
			newContents = contents.replace("<!--<plugin className=\"conquer.plugins.builtins.DefaultMusic\" />-->",

				"<plugin className=\"conquer.plugins.builtins.DefaultMusic\" />");
		}
		Files.writeString(Paths.get(info.toURI()), newContents, StandardCharsets.UTF_8);
	}

	@Override
	public void run() {
		if (!this.alreadyInstalled()) {
			this.write(Messages.getString("Installer.installingConquer"));
			try {
				Initializer.installing = true;
				this.startInstalling();
				Initializer.installing = false;
			} catch (final IOException e) {
				this.writeError(Messages.getString("Installer.installationFailed"));
				if (this.stream != null) {
					e.printStackTrace(new PrintStream(this.stream));
				}
				Shared.LOGGER.exception(e);
				if (this.onError != null) {
					this.onError.accept(e);
				}
				return;
			}
			this.write(Messages.getString("Installer.installedSuccessfully"));
			this.write(Messages.getString("Installer.pleaseRestart"));
		}
	}

	// Installs everything
	private void startInstalling() throws IOException {
		final var n = this.askQuestion();
		if (n == 0) {
			this.baseInstallation();
		} else {
			this.extendedBaseInstallation();
			if (n == 2) {
				this.installMusic();
			}
			final var props = new File(Installer.BASE_FILE, "game.properties");
			if (!props.exists()) {
				Files.write(Paths.get(props.toURI()), Installer.DEFAULT_PROPERTIES.getBytes(),
					StandardOpenOption.CREATE);
			}
		}
	}

	private void unzipFile(final InputStream zipStream) {
		try (final var zin = new ZipInputStream(zipStream)) {
			ZipEntry ze;
			while ((ze = zin.getNextEntry()) != null) {
				this.write(Messages.getString("Installer.unzipping") + ze.getName());
				if (ze.isDirectory()) {
					new File(Installer.BASE_FILE, ze.getName()).mkdirs();
				} else {
					this.writeEntry(zin, ze);
				}
			}
		} catch (final IOException e) {
			if (this.stream != null) {
				e.printStackTrace(new PrintStream(this.stream));
			}
			Shared.LOGGER.exception(e);
		}
	}

	private void write(final String s) {
		Shared.LOGGER.message(s);
		if (this.stream != null) {
			try {
				this.stream.write(s);
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void writeEntry(final ZipInputStream zin, final ZipEntry ze) {
		if (ze.getName().contains("/")) {
			new File(Installer.BASE_FILE, ze.getName()).getParentFile().mkdirs();
		}
		try (final var fos = new FileOutputStream(new File(Installer.BASE_FILE, ze.getName()))) {
			int read;
			final var bytes = new byte[1024];
			while ((read = zin.read(bytes)) != -1) {
				fos.write(bytes, 0, read);
			}
		} catch (final IOException e) {
			if (this.stream != null) {
				e.printStackTrace(new PrintStream(this.stream));
			}
			Shared.LOGGER.exception(e);
		}
	}

	private void writeError(final String s) {
		Shared.LOGGER.error(s);
		if (this.stream != null) {
			try {
				this.stream.write(s);
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
