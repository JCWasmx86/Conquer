package org.jel.gui;

import javax.swing.JFrame;

final class InGameTutorial extends JFrame {
	private static final long serialVersionUID = -7560355564072744176L;
	private static final InGameTutorial INSTANCE = new InGameTutorial();

	InGameTutorial() {
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		final var panel = new TutorialPanel();
		panel.init(this);
		this.add(panel);
		this.pack();
	}

	static void showWindow() {
		INSTANCE.setVisible(true);
	}

}
