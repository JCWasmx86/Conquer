package conquer.gui;

import conquer.data.ICity;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * Allows the player to recruit soldiers
 */
final class RecruitButton extends JPanel {
	private static final long serialVersionUID = 4846741301367606008L;
	private final JSlider js;
	private final JButton jbutton;
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
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		final var max = city.getInfo().maximumNumberOfSoldiersToRecruit(city.getClan(), city.getNumberOfPeople());
		this.js = new JSlider(0, (int) max);
		this.js.setValue((int) (max / 2));
		this.jbutton = new JButton(Messages.getMessage("RecruitButton.recruitNSoldiers", max / 2));
		this.jbutton.addActionListener(e -> {
			if (RecruitButton.this.sharp) {
				return;
			}
			final var cnt = RecruitButton.this.js.getValue();
			city.getInfo().recruitSoldiers(0.0, city, true, cnt);
			cip.doUpdate();
		});
		this.js.addChangeListener(e -> RecruitButton.this.jbutton
				.setText(Messages.getMessage("RecruitButton.recruitNSoldiers", RecruitButton.this.js.getValue())));

		this.add(this.js);
		this.add(this.jbutton);
	}

	/**
	 * Updates all components on this panel.
	 */
	void doUpdate() {
		if (this.city.isPlayerCity()) {
			if (this.city.getInfo().isDead(this.city.getInfo().getPlayerClan())) {
				this.jbutton.setEnabled(false);
				this.js.setEnabled(false);
				this.sharp = true;
				this.js.setMaximum(0);
			} else {
				this.jbutton.setEnabled(true);
				this.js.setEnabled(true);
				this.sharp = true;
				this.js.setMaximum((int) this.city.getInfo().maximumNumberOfSoldiersToRecruit(this.city.getClan(),
						this.city.getNumberOfPeople()));
			}
		} else {
			this.jbutton.setEnabled(false);
			this.js.setEnabled(false);
			this.sharp = true;
			this.js.setMinimum(0);
			this.js.setMaximum(0);
		}
		this.sharp = false;
	}

}
