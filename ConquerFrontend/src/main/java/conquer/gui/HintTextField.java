package conquer.gui;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * A HintTextField based on <a href=
 * "https://stackoverflow.com/a/24571681">https://stackoverflow.com/a/24571681</a>.
 */
final class HintTextField extends JTextField implements FocusListener {
    private static final long serialVersionUID = 7292642801367703358L;
    private final String hint;
    private boolean showingHint;

    HintTextField(final String hint) {
        super(hint);
        this.hint = hint;
        this.showingHint = true;
        super.addFocusListener(this);
    }

    @Override
    public void focusGained(final FocusEvent e) {
        if (this.getText().isEmpty()) {
            super.setText("");
            this.showingHint = false;
        }
    }

    @Override
    public void focusLost(final FocusEvent e) {
        if (this.getText().isEmpty()) {
            super.setText(this.hint);
            this.showingHint = true;
        }
    }

    @Override
    public String getText() {
        return this.showingHint ? "" : super.getText();
    }
}
