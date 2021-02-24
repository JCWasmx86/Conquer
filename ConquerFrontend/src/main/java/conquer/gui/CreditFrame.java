package conquer.gui;

import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent.EventType;

import conquer.gui.utils.ImageResource;
import conquer.gui.utils.LoopPlayer;

/**
 * Shows credits and licensing information.
 */
final class CreditFrame extends JFrame implements WindowListener, ActionListener {
	private static final long serialVersionUID = -4549305902050012246L;
	private final LoopPlayer player;
	private volatile boolean switchToMainScreen = false;

	// Construct a new CreditFrame
	CreditFrame() {
		this.player = new LoopPlayer().addSong("Credits");
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		this.player.abort();
		this.switchToMainScreen = true;
		MainScreen.forward(this.getLocation(), false);
		this.dispose();
	}

	private String generateHtml() {
		final var sb = new StringBuilder("<html>");
		sb.append("<h3>Used libraries</h3><ul>");
		sb.append(
				"<li><a href=\"https://wiki.gnome.org/Projects/Vala\">Vala (GNU Lesser General Public License v2.1)</a></li>");
		sb.append("<li><a href=\"https://www.gtk.org/\">GTK (GNU Lesser General Public License v2)</a></li>");
		sb.append("<li><a href=\"https://libarchive.org/\">Libarchive (New BSD License)</a></li>");
		sb.append("<li><a href=\"https://curl.se/libcurl/\">libcurl (MIT/X derivate)</a></li>");
		sb.append(
				"<li><a href=\"https://wiki.gnome.org/Projects/Libgee\">libgee (GNU Lesser General Public License v2.1)</a></li>");
		sb.append(
				"<li><a href=\"https://wiki.gnome.org/Projects/JsonGlib\">JSON-GLib (GNU Lesser General Public License v2.1)</a></li>");
		sb.append("</ul><h3>Used libraries for sound</h3>");
		sb.append(
				"All of these were repackaged and published on maven. (<a href=\"https://github.com/pdudits/soundlibs\">Github repo</a><br>");
		sb.append("<ul>");
		sb.append(
				"<li> <a href=\"https://mvnrepository.com/artifact/com.googlecode.soundlibs/tritonus-share/0.3.7.4\">tritonus-share (GNU Lesser General Public License v2.1)</a>");
		sb.append(
				"<li> <a href=\"https://mvnrepository.com/artifact/com.googlecode.soundlibs/mp3spi/1.9.5.4\">MP3SPI (GNU Lesser General Public License v2.1)</a>");
		sb.append(
				"<li> <a href=\"https://mvnrepository.com/artifact/com.googlecode.soundlibs/jlayer/1.0.1.4\">JLayer (GNU Lesser General Public License v2.1)</a>");
		sb.append(
				"<li> <a href=\"https://mvnrepository.com/artifact/com.googlecode.soundlibs/vorbisspi/1.0.3.3\">VorbisSPI (GNU Lesser General Public License v2.1)</a>");
		sb.append(
				"<li> <a href=\"https://mvnrepository.com/artifact/com.googlecode.soundlibs/jorbis/0.0.17.4\">JOrbis (GNU Lesser General Public License v2.1)</a>");
		sb.append("</ul><h3>Other legal things</h3><ul>");
		sb.append(
				"<li><a href=\"https://github.com/B00merang-Project/Windows-10\">Windows-10 theme for the launcher on windows (GNU General Public License v3.0)</a></li>");
		sb.append(
				"<li><a href=\"https://gitlab.gnome.org/GNOME/adwaita-icon-theme\">Adwaita icon theme is distributed on windows with the launcher (GNU Lesser General Public License v3; artwork by the </a><a href=\"http://www.gnome.org\">GNOME Project</a>; <a href=\"http://creativecommons.org/licenses/by-sa/3.0\">CC BY-SA3.0</a>)</li>");
		sb.append(
				"<li><a href=\"https://www.freedesktop.org/wiki/Software/icon-theme/\">Hicolor icon theme is distributed on windows with the launcher (GNU General Public License v2)</a></li>");
		sb.append(
				"</ul>These are not compiled directly, but are downloaded at the build process from the <a href=\"https://www.msys2.org/\">MSYS2</a> project: <br><ul>");
		sb.append(
				"<li><a href=\"https://repo.msys2.org/mingw/x86_64/mingw-w64-x86_64-adwaita-icon-theme-3.38.0-3-any.pkg.tar.zst\">Adwaita icon theme</a></li>");
		sb.append(
				"<li><a href=\"https://repo.msys2.org/mingw/x86_64/mingw-w64-x86_64-hicolor-icon-theme-0.17-2-any.pkg.tar.zst\">Hicolor icon theme</a></li>");
		// TODO: Check whether a license for every distributed DLL has to be added.
		sb.append("</ul><h3>Notable links</h3>");
		sb.append(
				"<i><a href=\"https://github.com/musescore/MuseScore\">Musescore</a></i> was used to generate the music for the <i>DefaultMusic</i> plugin and the GUI.<br>");
		sb.append(
				"The names \"Belenos\", \"Etiona\" and \"Slaine\" are from <i><a href=\"https://trisquel.info/en\">Trisquel Linux</a></i>.<br>");
		sb.append(
				"The name \"Freedo\" is from the <i><a href=\"https://directory.fsf.org/wiki/Linux-libre\">Linux-libre project</a></i><br>");
		return sb.append("</html>").toString();
	}

	/**
	 * Initialize and show this frame at the specified location
	 *
	 * @param location The location, where the frame will appear
	 */
	void init(final Point location) {
		this.setLocation(location);
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.addWindowListener(this);
		final JButton button = new RoundButton(new ImageResource("back.png"));
		this.add(button);
		button.addActionListener(this);
		this.player.start();
		final var jep = new JEditorPane("text/html", this.generateHtml());
		jep.addHyperlinkListener(a -> {
			if (a.getEventType() == EventType.ACTIVATED) {
				try {
					Desktop.getDesktop().browse(a.getURL().toURI());
				} catch (IOException | URISyntaxException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", ImageObserver.ERROR);
				}
			}
		});
		jep.setEditable(false);
		final var scrollPane = new JScrollPane(jep);
		this.add(scrollPane);
		this.pack();
		this.setVisible(true);
	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowActivated(final WindowEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowClosed(final WindowEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowClosing(final WindowEvent e) {
		if (this.switchToMainScreen) {
			MainScreen.forward(this.getLocation(), false);
		} else {
			System.exit(0);
		}
	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowDeactivated(final WindowEvent e) {
	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowDeiconified(final WindowEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowIconified(final WindowEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowOpened(final WindowEvent e) {

	}
}
