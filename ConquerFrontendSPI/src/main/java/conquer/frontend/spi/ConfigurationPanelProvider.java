package conquer.frontend.spi;

import java.util.Optional;
import javax.swing.JPanel;
import org.jel.game.data.ConquerInfo;

/**
 * A provider for giving a configuration panel for undefined configuration.
 */
public interface ConfigurationPanelProvider {
	/**
	 * Builds an configuration panel for configuring the info by the player.
	 * 
	 * @param clazz Class of the instance.
	 * @return {@link Optional#empty()} if this provider can't provide an JPanel for
	 *         this class, otherwise an optional containing the JPanel.
	 */
	public Optional<JPanel> forClass(Class<? extends ConquerInfo> clazz);
	
	/**
	 * Get an unique, localized name for this panel.
	 * @return Name
	 */
	public String getName();
}
