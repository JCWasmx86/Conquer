package org.jel.gui;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

final class SelectPanel extends JPanel {
	private static final long serialVersionUID = 5883369029393808982L;

	SelectPanel(String buttonValue, String jtextfield, Consumer<String> consumer) {
		final var hi = new HintTextField(jtextfield);
		final var jbutton = new JButton();
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		jbutton.setAction(new AbstractAction() {
			private static final long serialVersionUID = 1969775571560431511L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!hi.getText().equals("")) {
					consumer.accept(hi.getText());
					hi.setText("");
					hi.focusLost(null);
				}
			}
		});
		jbutton.setText(buttonValue);
		this.add(hi);
		this.add(jbutton);
	}

}
