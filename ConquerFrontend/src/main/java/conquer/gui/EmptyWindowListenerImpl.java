package conquer.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

interface EmptyWindowListenerImpl extends WindowListener {
	@Override
	default void windowOpened(WindowEvent windowEvent) {
		//Empty as default
	}

	@Override
	default void windowClosing(WindowEvent windowEvent) {
		//Empty as default
	}

	@Override
	default void windowClosed(WindowEvent windowEvent) {
		//Empty as default
	}

	@Override
	default void windowIconified(WindowEvent windowEvent) {
		//Empty as default
	}

	@Override
	default void windowDeiconified(WindowEvent windowEvent) {
		//Empty as default
	}

	@Override
	default void windowActivated(WindowEvent windowEvent) {
		//Empty as default
	}

	@Override
	default void windowDeactivated(WindowEvent windowEvent) {
		//Empty as default
	}
}
