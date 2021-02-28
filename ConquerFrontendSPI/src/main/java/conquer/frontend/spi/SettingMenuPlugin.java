package conquer.frontend.spi;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.Properties;

/**
 * An interface providing everything needed for settings.
 */
public interface SettingMenuPlugin {
    /**
     * The main component to show
     *
     * @return Some component
     */
    Component getComponent();

    /**
     * Return some arbitrary string to identify this component.
     *
     * @return Some title
     */
    String getTitle();

    /**
     * An optional icon, that may be used by the implementation.
     *
     * @return
     */
    Optional<Icon> getIcon();

    /**
     * Save the state of the settings in the properties
     *
     * @param properties Storage for your settings
     */
    void save(Properties properties);

    /**
     * Restore the state of the settings from the properties.
     *
     * @param properties State of properties.
     */
    void restore(Properties properties);

    /**
     * Reset the state of the settings.
     */
    void reset();
}
