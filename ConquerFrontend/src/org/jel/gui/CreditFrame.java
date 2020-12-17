package org.jel.gui;

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

import org.jel.gui.utils.ImageResource;
import org.jel.gui.utils.LoopPlayer;

/**
 * Shows credits and licensing information.
 */
final class CreditFrame extends JFrame implements WindowListener, ActionListener {
	private static final long serialVersionUID = -4549305902050012246L;
	private final LoopPlayer player;
	private volatile boolean switchToMainScreen = false;

	// Construct a new CreditFrame
	CreditFrame() {
		this.player = new LoopPlayer().addSong("Credits.wav");
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		this.player.abort();
		this.switchToMainScreen = true;
		MainScreen.forward(this.getLocation(), false);
		this.dispose();
	}

	private String generateHtml() {
		final var sb = new StringBuilder("<html><center>");
		sb.append("<h1>Image sources</h1>");
		sb.append("<h2>Image sources for the game</h2>");
		sb.append(
				"<b>back.png</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>ban.png</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>coal.png</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>cotton.png</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>defenseUpgrade.png (Renamed from \"shield.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/good-ware\" title=\"Good Ware\">Good Ware</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append("<b>down.png</b>: <div>Same as back.png, but mirrored by me.</div><br>");
		sb.append(
				"<b>fish.png</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/kiranshastry\" title=\"Kiranshastry\">Kiranshastry</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append("<b>forward.png</b>: <div>Same as back.png, but mirrored by me.</div><br>");
		sb.append(
				"<b>hourglass.png</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>iron.png (Renamed from \"steel.png\")</b>: <div>Icons made by <a href=\"https://smashicons.com/\" title=\"Smashicons\">Smashicons</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>leather.png</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/good-ware\" title=\"Good Ware\">Good Ware</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>max.png (Renamed from \"upload.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/kiranshastry\" title=\"Kiranshastry\">Kiranshastry</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div> Furthermore the color of the arrow was changed to green.<br>");
		sb.append(
				"<b>meat.png</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/flat-icons\" title=\"Flat Icons\">Flat Icons</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>messagebox.png (Renamed from \"mail.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/pixel-perfect\" title=\"Pixel perfect\">Pixel perfect</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>moneygift.png (Renamed from \"agreement.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/pixel-perfect\" title=\"Pixel perfect\">Pixel perfect</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>stone.png</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>textile.png (Renamed from \"fur.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append("<b>up.png</b>: <div>Same as back.png, but mirrored by me.</div><br>");
		sb.append(
				"<b>wheat.png (Renamed from \"cereal.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>wood.png (Renamed from \"trees.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/good-ware\" title=\"Good Ware\">Good Ware</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append("<h2>Image sources for the scenario \"Belenos\"</h2>");
		sb.append(
				"<b>architecture-and-city.png</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\"> www.flaticon.com</a></div> <br>");
		sb.append(
				"<b>castle.png </b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>castle(1).png (Renamed from \"castle.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>castle(2).png (Renamed from \"castle.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/eucalyp\" title=\"Eucalyp\">Eucalyp</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>castle(3).png (Renamed from \"castle.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>cultures.png </b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/surang\" title=\"surang\">surang</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>fortress.png </b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>fortress(1).png (Renamed from \"fortress.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>fortress(2).png (Renamed from \"fortress.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>medieval.png</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>medieval(1).png (Renamed from \"medieval.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/good-ware\" title=\"Good Ware\">Good Ware</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div></br>");
		sb.append("<h2>Image sources for the scenario \"Etiona\"</h2>");
		sb.append(
				"<b>medieval(1).png (Renamed from \"medieval.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/good-ware\" title=\"Good Ware\">Good Ware</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div></br>");
		sb.append(
				"<b>monument.png</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\"> www.flaticon.com</a></div>");
		sb.append(
				"<b>moscow.png</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/nikita-golubev\" title=\"Nikita Golubev\">Nikita Golubev</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>medieval.png</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append("<h2>Image sources for the scenario \"Slaine\"</h2>");
		sb.append(
				"<b>castle.png </b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>castle(1).png (Renamed from \"castle.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>castle(2).png (Renamed from \"castle.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/eucalyp\" title=\"Eucalyp\">Eucalyp</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>moscow.png</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/nikita-golubev\" title=\"Nikita Golubev\">Nikita Golubev</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>fortress.png </b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>fortress(1).png (Renamed from \"fortress.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>fortress(2).png (Renamed from \"fortress.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>medieval.png</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>medieval(1).png (Renamed from \"medieval.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/good-ware\" title=\"Good Ware\">Good Ware</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div></br>");
		sb.append("<h2>Image sources for the scenario \"Freedo\"</h2>");
		sb.append(
				"<b>fortress.png </b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>medieval.png</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>medieval(1).png (Renamed from \"medieval.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/good-ware\" title=\"Good Ware\">Good Ware</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div></br>");
		sb.append(
				"<b>fortress(1).png (Renamed from \"fortress.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append(
				"<b>fortress(2).png (Renamed from \"fortress.png\")</b>: <div>Icons made by <a href=\"https://www.flaticon.com/authors/freepik\" title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" title=\"Flaticon\">www.flaticon.com</a></div><br>");
		sb.append("<h1>Used libraries for the native launcher</h1>");
		sb.append("<h2><a href=\"https://libarchive.org/\">libarchive</a></h2>");
		sb.append("Copyright (c) 2003-2018<br>" + "All rights reserved.<br>" + "<br>"
				+ "Redistribution and use in source and binary forms, with or without<br>"
				+ "modification, are permitted provided that the following conditions<br>" + "are met:<br>"
				+ "1. Redistributions of source code must retain the above copyright<br>"
				+ "   notice, this list of conditions and the following disclaimer<br>"
				+ "   in this position and unchanged.<br>"
				+ "2. Redistributions in binary form must reproduce the above copyright<br>"
				+ "   notice, this list of conditions and the following disclaimer in the<br>"
				+ "   documentation and/or other materials provided with the distribution.<br>" + "<br>"
				+ "THIS SOFTWARE IS PROVIDED BY THE AUTHOR(S) ``AS IS'' AND ANY EXPRESS OR<br>"
				+ "IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES<br>"
				+ "OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.<br>"
				+ "IN NO EVENT SHALL THE AUTHOR(S) BE LIABLE FOR ANY DIRECT, INDIRECT,<br>"
				+ "INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT<br>"
				+ "NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,<br>"
				+ "DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY<br>"
				+ "THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT<br>"
				+ "(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF<br>"
				+ "THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.");
		sb.append("<h2><a href=\"https://github.com/DaveGamble/cJSON\">cJSON</a></h2>");
		sb.append("Copyright (c) 2009-2017 Dave Gamble and cJSON contributors<br>" + "<br>"
				+ "Permission is hereby granted, free of charge, to any person obtaining a copy<br>"
				+ "of this software and associated documentation files (the \"Software\"), to deal<br>"
				+ "in the Software without restriction, including without limitation the rights<br>"
				+ "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell<br>"
				+ "copies of the Software, and to permit persons to whom the Software is<br>"
				+ "furnished to do so, subject to the following conditions:<br>" + "<br>"
				+ "The above copyright notice and this permission notice shall be included in<br>"
				+ "all copies or substantial portions of the Software.<br>" + "<br>"
				+ "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR<br>"
				+ "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,<br>"
				+ "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE<br>"
				+ "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER<br>"
				+ "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,<br>"
				+ "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN<br>" + "THE SOFTWARE.");
		sb.append("<h2><a href=\"https://curl.se/libcurl/\">libcurl</a></h2>");
		sb.append("COPYRIGHT AND PERMISSION NOTICE<br>" + "<br>"
				+ "Copyright (c) 1996 - 2020, Daniel Stenberg, daniel@haxx.se, and many contributors, see the THANKS file.<br>"
				+ "<br>" + "All rights reserved.<br>" + "<br>"
				+ "Permission to use, copy, modify, and distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.<br>"
				+ "<br>"
				+ "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.<br>"
				+ "<br>"
				+ "Except as contained in this notice, the name of a copyright holder shall not be used in advertising or otherwise to promote the sale, use or other dealings in this Software without prior written authorization of the copyright holder.<br>");
		sb.append("<h1>Notable links</h1>");
		sb.append(
				"<i><a href=\"https://github.com/musescore/MuseScore\">Musescore</a></i> was used to generate the music for the <i>DefaultMusic</i> plugin.<br>");
		sb.append(
				"The names \"Belenos\", \"Etiona\" and \"Slaine\" are from <i><a href=\"https://trisquel.info/en\">Trisquel Linux</a></i>.<br>");
		sb.append(
				"The name \"Freedo\" is from the <i><a href=\"https://directory.fsf.org/wiki/Linux-libre\">Linux-libre project</a></i><br>");
		return sb.append("</center></html>").toString();
	}

	/**
	 * Initialise and show this frame at the specified location
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
