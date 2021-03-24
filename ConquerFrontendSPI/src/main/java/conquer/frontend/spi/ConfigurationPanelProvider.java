package conquer.frontend.spi;

import conquer.data.ConquerInfo;

import java.util.Optional;
import javax.swing.JPanel;

/**
 * A provider for giving a configuration panel for undefined configuration.
 */
public interface ConfigurationPanelProvider {
	/**
	 * Builds an configuration panel for configuring the info by the player.
	 *
	 * @param clazz Class of the instance.
	 * @return {@link Optional#empty()} if this provider can't provide an JPanel for
	 * this class, otherwise an optional containing the JPanel.
	 */
	Optional<JPanel> forClass(Class<? extends ConquerInfo> clazz);

	/**
	 * Get an unique, localized name for this panel.
	 *
	 * @return Name
	 */
	String getName();
}
