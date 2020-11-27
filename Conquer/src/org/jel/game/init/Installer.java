package org.jel.game.init;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

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
	@Override
	public void run() {
		if (!this.alreadyInstalled()) {
			Shared.LOGGER.message("Installing conquer!");
			try {
				Initializer.installing = true;
				this.startInstalling();
				Initializer.installing = false;
			} catch (final IOException e) {
				Shared.LOGGER.error("Installation failed");
				Shared.LOGGER.exception(e);
				final var f = new JFrame();
				f.setAlwaysOnTop(true);
				JOptionPane.showMessageDialog(f, "Installation failed: " + e.getMessage(), "Installation failed",
						JOptionPane.INFORMATION_MESSAGE);
				f.dispose();
				System.exit(-127);// Exit, can't be resolved
			}
			final var f = new JFrame();
			f.setAlwaysOnTop(true);
			JOptionPane.showMessageDialog(f, "Conquer was installed successfully!", "Installation successful",
					JOptionPane.INFORMATION_MESSAGE);
			f.dispose();
		}
	}

	// Installs everything
	private void startInstalling() throws IOException {
		final String[] options = { "Minimal installation", "Standard installation (Scenarios and some plugins)",
				"Extended installation (Scenarios, plugins and music)" };
		final var n = JOptionPane.showOptionDialog(null, // parent container of JOptionPane
				"Please select the type of installation", "Installation", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
		switch (n) {
		default:
		case 0: {
			final var baseFile = new File(Shared.BASE_DIRECTORY).getAbsoluteFile();
			baseFile.mkdirs();
			final var xml = new File(baseFile, "info.xml");
			if (!xml.exists()) {
				Files.write(Paths.get(xml.toURI()), Installer.DEFAULT_XML.getBytes(), StandardOpenOption.CREATE);
			}
			final var props = new File(baseFile, "game.properties");
			if (!props.exists()) {
				Files.write(Paths.get(props.toURI()), Installer.DEFAULT_PROPERTIES.getBytes(),
						StandardOpenOption.CREATE);
			}
		}
			break;
		case 1:
		case 2: {
			final var stream = ClassLoader.getSystemResource("music/conquer.zip").openStream();
			final var baseFile = new File(Shared.BASE_DIRECTORY).getAbsoluteFile();
			baseFile.mkdirs();
			ZipEntry ze = null;

			try (var zin = new ZipInputStream(stream)) {
				while ((ze = zin.getNextEntry()) != null) {
					Shared.LOGGER.message("Unzipping: " + ze.getName());
					if (ze.isDirectory()) {
						new File(baseFile, ze.getName()).mkdirs();
					} else {
						try (var fos = new FileOutputStream(new File(baseFile, ze.getName()))) {
							int read;
							final var bytes = new byte[1024];
							while ((read = zin.read(bytes)) != -1) {
								fos.write(bytes, 0, read);
							}

						}
					}
				}
			}
			if (n == 2) {
				JOptionPane.showMessageDialog(null, "Please wait (~2 minutes)");
				for (var i = 1; i < 6; i++) {
					final var url = new URL(
							"https://raw.githubusercontent.com/JCWasmx86/JCWasmx86.github.io/master/zips/Music" + i
									+ ".zip");
					try (var zipInputStream = new ZipInputStream(url.openStream())) {
						ze = null;
						while ((ze = zipInputStream.getNextEntry()) != null) {
							Shared.LOGGER.message("Unzipping: " + ze.getName());
							if (ze.isDirectory()) {
								new File(baseFile, ze.getName()).mkdirs();
							} else {
								try (var fos = new FileOutputStream(new File(baseFile, ze.getName()))) {
									int read;
									final var bytes = new byte[1024];
									while ((read = zipInputStream.read(bytes)) != -1) {
										fos.write(bytes, 0, read);
									}

								}
							}
						}
					}
				}
				final var info = new File(baseFile, "info.xml").getAbsoluteFile();
				String newContents = null;
				try (var stream2 = Files.newInputStream(Paths.get(new File(baseFile, "info.xml").toString()))) {
					final var contents = new String(stream2.readAllBytes(), StandardCharsets.UTF_8);
					newContents = contents.replace("<!--<plugin className=\"org.jel.game.plugins.DefaultMusic\" />-->",
							"<plugin className=\"org.jel.game.plugins.DefaultMusic\" />");
				}
				Files.writeString(Paths.get(info.toURI()), newContents, StandardCharsets.UTF_8);
			}
			final var props = new File(baseFile, "game.properties");
			if (!props.exists()) {
				Files.write(Paths.get(props.toURI()), Installer.DEFAULT_PROPERTIES.getBytes(),
						StandardOpenOption.CREATE);
			}
		}
			break;
		}
	}

}
