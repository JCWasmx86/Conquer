package org.jel.gui;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.jel.game.data.City;
import org.jel.game.data.Shared;

/**
 * Allows the player to recruit soldiers
 */
final class RecruitButton extends JPanel {
	private static final long serialVersionUID = 4846741301367606008L;
	private JSlider js;
	private JButton jbutton;
	private final transient City city;
	private boolean sharp = false;

	/**
	 * Constructs a new button
	 *
	 * @param city Target city
	 * @param cip  A reference to the CityInfoPanel.
	 */
	RecruitButton(City city, CityInfoPanel cip) {
		this.city = city;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		final var max = city.getInfo().maximumNumberOfSoldiersToRecruit((byte) city.getClanId(),
				city.getNumberOfPeople());
		this.js = new JSlider(0, (int) max);
		this.js.setValue((int) (max / 2));
		this.jbutton = new JButton("Recruit " + (max / 2) + " soldiers!");
		this.jbutton.addActionListener(e -> {
			if (RecruitButton.this.sharp) {
				return;
			}
			final var cnt = RecruitButton.this.js.getValue();
			city.getInfo().recruitSoldiers(Shared.PLAYER_CLAN, (byte) 0, city, true, cnt);
			cip.doUpdate();
		});
		this.js.addChangeListener(
				e -> RecruitButton.this.jbutton.setText("Recruit " + RecruitButton.this.js.getValue() + " soldiers!"));
		this.add(this.js);
		this.add(this.jbutton);
	}

	/**
	 * Updates all components on this panel.
	 */
	void doUpdate() {
		if (this.city.getClanId() != 0) {
			this.jbutton.setEnabled(false);
			this.js.setEnabled(false);
			this.sharp = true;
			this.js.setMinimum(0);
			this.js.setMaximum(0);
			this.sharp = false;
		} else {
			if (!this.city.getInfo().isDead(Shared.PLAYER_CLAN)) {
				this.jbutton.setEnabled(true);
				this.js.setEnabled(true);
				this.sharp = true;
				this.js.setMaximum((int) this.city.getInfo()
						.maximumNumberOfSoldiersToRecruit((byte) this.city.getClanId(), this.city.getNumberOfPeople()));
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

}
