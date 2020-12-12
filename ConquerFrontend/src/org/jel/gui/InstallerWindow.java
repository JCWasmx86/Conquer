package org.jel.gui;

import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.jel.game.init.ExtendedOutputStream;

/**
 * A windows for the installer. It just consists of a list of JLabels.
 */
final class InstallerWindow extends ExtendedOutputStream {
	private String tmp;
	private final JFrame jframe;
	private final JPanel jpanel;

	/**
	 * Creates a new InstallerWindow.
	 */
	InstallerWindow() {
		this.jframe = new JFrame();
		this.jpanel = new JPanel();
		this.jpanel.setLayout(new BoxLayout(this.jpanel, BoxLayout.Y_AXIS));
		this.jframe.add(new JScrollPane(this.jpanel));
		this.jframe.setTitle(Messages.getString("InstallerWindow.installingConquer")); //$NON-NLS-1$
		this.jframe.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	}

	/**
	 * Signals that the windows is not needed anymore.
	 */
	void dispose() {
		this.jframe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	@Override
	public void write(int b) throws IOException {
		this.tmp += (char) b;
		if (b == '\n') {// If the string had a newline, add a new JLabel.
			this.jpanel.add(new JLabel(this.tmp));
			this.tmp = null;
			this.jframe.pack();
		}
		if (!this.jframe.isVisible()) {
			this.jframe.setVisible(true);
		}
	}

	@Override
	public void write(String s) throws IOException {
		if (this.tmp != null) {
			this.jpanel.add(new JLabel(this.tmp));
			this.tmp = null;
		}
		this.jpanel.add(new JLabel(s));
		if (!this.jframe.isVisible()) {
			this.jframe.setVisible(true);
		}
		this.jframe.pack();
	}

}
