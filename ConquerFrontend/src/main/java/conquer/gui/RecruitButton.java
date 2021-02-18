package conquer.gui;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;

import conquer.data.ICity;

/**
 * Allows the player to recruit soldiers
 */
final class RecruitButton extends JPanel {
	private static final long serialVersionUID = 4846741301367606008L;
	private JSlider js;
	private JButton jbutton;
	private final transient ICity city;
	private boolean sharp = false;

	/**
	 * Constructs a new button
	 *
	 * @param city Target city
	 * @param cip  A reference to the CityInfoPanel.
	 */
	RecruitButton(final ICity city, final CityInfoPanel cip) {
		this.city = city;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		final var max = city.getInfo().maximumNumberOfSoldiersToRecruit(city.getClan(), city.getNumberOfPeople());
		this.js = new JSlider(0, (int) max);
		this.js.setValue((int) (max / 2));
		this.jbutton = new JButton(Messages.getMessage("RecruitButton.recruitNSoldiers", max / 2)); //$NON-NLS-1$
		this.jbutton.addActionListener(e -> {
			if (RecruitButton.this.sharp) {
				return;
			}
			final var cnt = RecruitButton.this.js.getValue();
			city.getInfo().recruitSoldiers(0.0, city, true, cnt);
			cip.doUpdate();
		});
		this.js.addChangeListener(e -> RecruitButton.this.jbutton
				.setText(Messages.getMessage("RecruitButton.recruitNSoldiers", RecruitButton.this.js.getValue()))); //$NON-NLS-1$
		this.add(this.js);
		this.add(this.jbutton);
	}

	/**
	 * Updates all components on this panel.
	 */
	void doUpdate() {
		if (!this.city.isPlayerCity()) {
			this.jbutton.setEnabled(false);
			this.js.setEnabled(false);
			this.sharp = true;
			this.js.setMinimum(0);
			this.js.setMaximum(0);
			this.sharp = false;
		} else if (!this.city.getInfo().isDead(this.city.getInfo().getPlayerClan())) {
			this.jbutton.setEnabled(true);
			this.js.setEnabled(true);
			this.sharp = true;
			this.js.setMaximum((int) this.city.getInfo().maximumNumberOfSoldiersToRecruit(this.city.getClan(),
					this.city.getNumberOfPeople()));
			this.sharp = false;
		} else {
			this.jbutton.setEnabled(false);
			this.js.setEnabled(false);
			this.sharp = true;
			this.js.setMaximum(0);
			this.sharp = false;
		}
	}

}
