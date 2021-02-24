package conquer.frontend.spi;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

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
	 * Return some arbitray string to identify this component.
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
}
