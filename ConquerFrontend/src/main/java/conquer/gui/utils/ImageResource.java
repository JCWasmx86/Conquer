package conquer.gui.utils;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;

/**
 * Represents any image.
 */
public final class ImageResource implements Icon {
    private final Icon icon;

    /**
     * Constructs a new ImageResource with the specified icon and loads it.
     *
     * @param string The icon. If it wasn't found, an
     *               {@link IllegalArgumentException} will be thrown.
     */
    public ImageResource(final String string) {
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
            url = ClassLoader.getSystemResource("resources/" + string);
        }
        if (url == null) {
            url = ClassLoader.getSystemResource("resources/images/" + string);
        }
        if (url == null) {
            url = ClassLoader.getPlatformClassLoader().getResource("resources/" + string);
        }
        if (url == null) {
            url = ClassLoader.getPlatformClassLoader().getResource("resources/images/" + string);
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
    public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
        this.icon.paintIcon(c, g, x, y);
    }
}
