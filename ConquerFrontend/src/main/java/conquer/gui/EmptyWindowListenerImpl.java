package conquer.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

interface EmptyWindowListenerImpl extends WindowListener {
	@Override
	default void windowOpened(final WindowEvent windowEvent) {
		//Empty as default
	}

	@Override
	default void windowClosing(final WindowEvent windowEvent) {
		//Empty as default
	}

	@Override
	default void windowClosed(final WindowEvent windowEvent) {
		//Empty as default
	}

	@Override
	default void windowIconified(final WindowEvent windowEvent) {
		//Empty as default
	}

	@Override
	default void windowDeiconified(final WindowEvent windowEvent) {
		//Empty as default
	}

	@Override
	default void windowActivated(final WindowEvent windowEvent) {
		//Empty as default
	}

	@Override
	default void windowDeactivated(final WindowEvent windowEvent) {
		//Empty as default
	}
}
