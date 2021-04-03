package conquer.init;

import conquer.Messages;
import conquer.data.Shared;

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

/**
 * Automatically restores missing files, but ignores all other files. It may be
 * run in a separate thread.
 */
/// Why is this here? This is really weird.
public class Installer implements Runnable {
	// The default info.xml file.
	private static final String DEFAULT_XML = "<!--Info file for conquer. Edit to add new scenarios, plugins and " +
			"strategies -->" //$NON-NLS-1$
			+ System.lineSeparator() + "<info>" + System.lineSeparator() //$NON-NLS-1$
			+ "\t<!-- Add a new scenario. name is the name of the scenario, file points to the scenario file and " +
			"thumbnail points to an image show while selecting a scenario -->" //$NON-NLS-1$
			+ System.lineSeparator() + "\t<scenarios>" + System.lineSeparator() //$NON-NLS-1$
			+ "\t\t<!-- <scenario name=\"foo.bar.Baz\" file=\"file.data\" thumbnail=\"thumb.png\" /> -->" //$NON-NLS-1$
			+ System.lineSeparator() + "\t</scenarios>" + System.lineSeparator() //$NON-NLS-1$
			+ "\t<!-- Add a new plugin. className is the name of the class implementing Plugin -->" //$NON-NLS-1$
			+ System.lineSeparator() + "\t<plugins>" + System.lineSeparator() //$NON-NLS-1$
			+ "\t\t<!-- <plugin className=\"foo.bar.some.class.implementing.Plugin\" /> -->" + System.lineSeparator()
			//$NON-NLS-1$
			+ "\t</plugins>" + System.lineSeparator() //$NON-NLS-1$
			+ "\t<!-- Add a new strategy. className is the name of the class implementing StrategyProvider -->" //$NON
			// -NLS-1$
			+ System.lineSeparator() + "\t<strategies>" + System.lineSeparator() //$NON-NLS-1$
			+ "\t\t<!-- <strategy className=\"foo.bar.some.class.implementing.StrategyProvider\" /> -->" //$NON-NLS-1$
			+ System.lineSeparator() + "\t</strategies>" + System.lineSeparator() + "</info>" + System.lineSeparator(); //$NON-NLS-1$ //$NON-NLS-2$
	// The default properties
	private static final String DEFAULT_PROPERTIES = "# Settings for conquer" + System.lineSeparator();
	private static final File BASE_FILE = new File(Shared.BASE_DIRECTORY).getAbsoluteFile();
	private final OptionChooser chooser;
	private final ExtendedOutputStream stream;
	private final Consumer<Exception> onError;

	/**
	 * Constructs a new Installer
	 *
	 * @param chooser Returns the option to selected. May not be {@code null},
	 *                otherwise an {@link IllegalArgumentException} will be thrown.
	 * @param writeTo Outputstream for status messages. May be {@code null}.
	 * @param onError Consumer for occurring exceptions. May be {@code null}.
	 */
	public Installer(final OptionChooser chooser, final ExtendedOutputStream writeTo,
					 final Consumer<Exception> onError) {
		if (chooser == null) {
			throw new IllegalArgumentException("chooser==null"); //$NON-NLS-1$
		}
		this.chooser = chooser;
		this.stream = writeTo;
		this.onError = onError;
	}

	// Checks, whether the game is already installed.
	private boolean alreadyInstalled() {
		final var baseFile = new File(Shared.BASE_DIRECTORY).getAbsoluteFile();
		return baseFile.exists() && new File(baseFile, "info.xml").exists(); //$NON-NLS-1$
	}

	private int askQuestion() {
		return this.chooser.choose(new String[]{Messages.getString("Installer.minimalInstallation"), //$NON-NLS-1$
				Messages.getString("Installer.standardInstallation"), //$NON-NLS-1$
				Messages.getString("Installer.extendedInstallation")}); //$NON-NLS-1$
	}

	private void baseInstallation() throws IOException {
		Installer.BASE_FILE.mkdirs();
		final var xml = new File(Installer.BASE_FILE, "info.xml"); //$NON-NLS-1$
		if (!xml.exists()) {
			Files.write(Paths.get(xml.toURI()), Installer.DEFAULT_XML.getBytes(), StandardOpenOption.CREATE);
		}
		final var props = new File(Installer.BASE_FILE, "game.properties"); //$NON-NLS-1$
		if (!props.exists()) {
			Files.write(Paths.get(props.toURI()), Installer.DEFAULT_PROPERTIES.getBytes(), StandardOpenOption.CREATE);
		}
	}

	private boolean connected() {
		try (final var testStream = new URL("https://www.example.com").openStream()) { //$NON-NLS-1$
			return true;
		} catch (final IOException e) {
			return false;
		}
	}

	private void extendedBaseInstallation() throws IOException {
		this.baseInstallation();
		try (final var zipStream = ClassLoader.getSystemResource("data/conquer.zip").openStream()) { //$NON-NLS-1$
			Installer.BASE_FILE.mkdirs();
			this.unzipFile(zipStream);
		} catch (final IOException e) {
			this.writeError("Wasn't able to find music/conquer.zip"); //$NON-NLS-1$
			if (this.stream != null) {
				e.printStackTrace(new PrintStream(this.stream));
			}
			Shared.LOGGER.exception(e);
		}
	}

	private void installMusic() throws IOException {
		if (!this.connected()) {
			this.writeError(Messages.getString("Installer.noInternetConnection")); //$NON-NLS-1$
			return;
		}
		this.write(Messages.getString("Installer.pleaseWait")); //$NON-NLS-1$
		final var url = new URL(
				"https://raw.githubusercontent.com/JCWasmx86/JCWasmx86.github.io/master/conquer-data/Music.zip");
		//$NON-NLS-1$
		final var messageString = Messages.getMessage("Installer.downloading", url.toString()); //$NON-NLS-1$
		this.write(messageString); // $NON-NLS-1$
		try (final var urlStream = url.openStream()) {
			this.unzipFile(urlStream);
		} catch (final IOException ioe) {
			this.writeError(Messages.getString("Installer.downloadFailed") + url); //$NON-NLS-1$
		}
		final var info = new File(Installer.BASE_FILE, "info.xml").getAbsoluteFile(); //$NON-NLS-1$
		final String newContents;
		try (final var stream2 = Files.newInputStream(Paths.get(new File(Installer.BASE_FILE, "info.xml").toString()))) { //$NON-NLS-1$
			final var contents = new String(stream2.readAllBytes(), StandardCharsets.UTF_8);
			newContents = contents.replace("<!--<plugin className=\"conquer.plugins.builtins.DefaultMusic\" />-->",
					//$NON-NLS-1$
					"<plugin className=\"conquer.plugins.builtins.DefaultMusic\" />"); //$NON-NLS-1$
		}
		Files.writeString(Paths.get(info.toURI()), newContents, StandardCharsets.UTF_8);
	}

	@Override
	public void run() {
		if (!this.alreadyInstalled()) {
			this.write(Messages.getString("Installer.installingConquer")); //$NON-NLS-1$
			try {
				Initializer.installing = true;
				this.startInstalling();
				Initializer.installing = false;
			} catch (final IOException e) {
				this.writeError(Messages.getString("Installer.installationFailed")); //$NON-NLS-1$
				if (this.stream != null) {
					e.printStackTrace(new PrintStream(this.stream));
				}
				Shared.LOGGER.exception(e);
				if (this.onError != null) {
					this.onError.accept(e);
				}
				return;
			}
			this.write(Messages.getString("Installer.installedSuccessfully")); //$NON-NLS-1$
			this.write(Messages.getString("Installer.pleaseRestart")); //$NON-NLS-1$
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
			final var props = new File(Installer.BASE_FILE, "game.properties"); //$NON-NLS-1$
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
				this.write(Messages.getString("Installer.unzipping") + ze.getName()); //$NON-NLS-1$
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
