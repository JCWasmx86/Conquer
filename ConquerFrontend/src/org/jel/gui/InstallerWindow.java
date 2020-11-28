package org.jel.gui;

import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.jel.game.init.ExtendedOutputStream;

public class InstallerWindow extends ExtendedOutputStream {
	private String tmp;
	private JFrame jframe;
	private JPanel jpanel;

	InstallerWindow() {
		jframe = new JFrame();
		jpanel = new JPanel();
		jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
		jframe.add(new JScrollPane(jpanel));
		jframe.setTitle("Installing conquer...");
		jframe.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	}

	@Override
	public void write(String s) throws IOException {
		if (tmp != null) {
			this.jpanel.add(new JLabel(tmp));
			tmp = null;
		}
		this.jpanel.add(new JLabel(s));
		if (!jframe.isVisible()) {
			this.jframe.setVisible(true);
		}
		this.jframe.pack();
	}

	@Override
	public void write(int b) throws IOException {
		tmp += (char) b;
		if (b == '\n') {
			this.jpanel.add(new JLabel(tmp));
			tmp = null;
			this.jframe.pack();
		}
		if (!jframe.isVisible()) {
			this.jframe.setVisible(true);
		}
	}

	void dispose() {
		this.jframe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

}
