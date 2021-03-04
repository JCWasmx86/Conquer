package conquer.frontend.spi;

import javax.swing.JButton;
import javax.swing.JMenuItem;

public interface GUIMenuPlugin {
    /**
     * Gives a {@link JMenuItem} that will be either in the settings, in a menu or
     * somewhere else. This allows giving the user more options to configure.
     *
     * @return A menuitem.
     */
    JMenuItem getMenuItem();

    /**
     * This will add a button on a implementation defined position.
     *
     * @return A button.
     */
    JButton getButton();
}
