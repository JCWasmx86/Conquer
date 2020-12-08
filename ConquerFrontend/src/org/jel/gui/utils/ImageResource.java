package org.jel.gui.utils;

import java.awt.Component;
import java.awt.Graphics;
import java.io.FileNotFoundException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Reprents any image.
 */
public final class ImageResource implements Icon {
	private final Icon icon;

	/**
	 * Constructs a new ImageResource with the specified icon and loads it.
	 *
	 * @param string The icon.
	 */
	public ImageResource(String string) {
		var url = ClassLoader.getSystemResource(string);
		if (url == null) {
			url = ClassLoader.getSystemResource("images/" + string);
		}
		if (url == null) {
			url = ClassLoader.getPlatformClassLoader().getResource(string);
		}
		if (url == null) {
			url = ClassLoader.getPlatformClassLoader().getResource("images/" + string);
		}
		if (url == null) {
			throw new IllegalArgumentException(new FileNotFoundException(string));
		}
		this.icon = new ImageIcon(url);
	}

	@Override
	public int getIconHeight() {
		return this.icon.getIconHeight();
	}

	@Override
	public int getIconWidth() {
		return this.icon.getIconWidth();
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		this.icon.paintIcon(c, g, x, y);
	}
}
