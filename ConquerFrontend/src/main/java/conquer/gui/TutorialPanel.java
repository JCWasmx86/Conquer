package conquer.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.Serial;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

final class TutorialPanel extends JPanel {
	@Serial
	private static final long serialVersionUID = 177194549342492092L;

	void init(final JFrame parent) {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		final var buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		this.add(buttonPanel);
		final var textArea = new JTextArea();
		this.add(textArea);
		textArea.setEditable(false);
		this.initButtons(buttonPanel, textArea, parent);
	}

	private void initButtons(final JPanel buttonPanel, final JTextArea textArea, final JFrame parent) {
		buttonPanel.add(new ButtonClass(Messages.getString("Tutorial.introductionTitle"),
			Messages.getString("Tutorial.introduction"), textArea, parent));
		buttonPanel.add(new ButtonClass(Messages.getString("Tutorial.movingSoldiersTitle"),
			Messages.getString("Tutorial.movingSoldiers"), textArea, parent));
		buttonPanel.add(new ButtonClass(Messages.getString("Tutorial.coinsTitle"), Messages.getString("Tutorial" +
			".coins"),
			textArea, parent));
		buttonPanel.add(new ButtonClass(Messages.getString("Tutorial.defenseTitle"),
			Messages.getString("Tutorial.defense"), textArea, parent));
		buttonPanel.add(new ButtonClass(Messages.getString("Tutorial.resourcesTitle"),
			Messages.getString("Tutorial.resources"), textArea, parent));
		buttonPanel.add(new ButtonClass(Messages.getString("Tutorial.relationshipsTitle"),
			Messages.getString("Tutorial.relationships"), textArea, parent));
		buttonPanel.add(new ButtonClass(Messages.getString("Tutorial.soldierUpgradesTitle"),
			Messages.getString("Tutorial.soldierUpgrades"), textArea, parent));
		buttonPanel.add(new ButtonClass(Messages.getString("Tutorial.giftsTitle"), Messages.getString("Tutorial" +
			".gifts"),
			textArea, parent));
		buttonPanel.add(new ButtonClass(Messages.getString("Tutorial.attacksTitle"),
			Messages.getString("Tutorial.attacks"), textArea, parent));
	}

	private static final class ButtonClass extends JButton {
		@Serial
		private static final long serialVersionUID = -623497546758855024L;

		private ButtonClass(final String title, final String body, final JTextArea area, final JFrame parent) {
			super(title);
			this.setAction(new AbstractAction() {
				@Serial
				private static final long serialVersionUID = 7008742488084938249L;

				@Override
				public void actionPerformed(final ActionEvent e) {
					area.setText(body);
					parent.pack();
				}
			});
			this.setText(title);
		}
	}
}
