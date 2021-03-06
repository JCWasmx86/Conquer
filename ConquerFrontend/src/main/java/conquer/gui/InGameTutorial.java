package conquer.gui;

import java.io.Serial;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

final class InGameTutorial extends JFrame {
	@Serial
	private static final long serialVersionUID = -7560355564072744176L;
	private static final InGameTutorial INSTANCE = new InGameTutorial();

	InGameTutorial() {
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		final var panel = new TutorialPanel();
		panel.init(this);
		this.add(panel);
		this.pack();
	}

	static void showWindow() {
		InGameTutorial.INSTANCE.setVisible(true);
	}

}
