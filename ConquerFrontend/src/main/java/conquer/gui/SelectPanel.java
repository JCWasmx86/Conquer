package conquer.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

/**
 * A panel with a textfield and a button. A button press clears the textfield
 * and sends its contents to the supplied consumer.
 */
final class SelectPanel extends JPanel {
    private static final long serialVersionUID = 5883369029393808982L;

    /**
     * Construct a new SelectPanel.
     *
     * @param buttonText     The string of the button
     * @param jtextfieldHint The hint
     * @param consumer       The consumer which will be called, if the button was
     *                       pressed.
     */
    SelectPanel(final String buttonText, final String jtextfieldHint, final Consumer<String> consumer) {
        final var hintTextField = new HintTextField(jtextfieldHint);
        final var jbutton = new JButton();
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        jbutton.setAction(new AbstractAction() {
            private static final long serialVersionUID = 1969775571560431511L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!"".equals(hintTextField.getText())) {
                    consumer.accept(hintTextField.getText());
                    hintTextField.setText("");
                    hintTextField.focusLost(null);
                }
            }
        });
        jbutton.setText(buttonText);
        this.add(hintTextField);
        this.add(jbutton);
    }

}
