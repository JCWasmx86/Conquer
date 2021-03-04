package conquer.gui;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

final class InGameTutorial extends JFrame {
    private static final long serialVersionUID = -7560355564072744176L;
    private static final InGameTutorial INSTANCE = new InGameTutorial();

    static void showWindow() {
        InGameTutorial.INSTANCE.setVisible(true);
    }

    InGameTutorial() {
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        final var panel = new TutorialPanel();
        panel.init(this);
        this.add(panel);
        this.pack();
    }

}
