package conquer.frontend.spi;

import conquer.data.ConquerInfo;

import java.io.Serial;
import javax.swing.JButton;

/**
 * A button shown while playing, e.g. for opening a chat or something else.
 */
public class InGameButton extends JButton {

	@Serial
	private static final long serialVersionUID = 1L;

	public void initialize(final ConquerInfo info) {

	}
}
