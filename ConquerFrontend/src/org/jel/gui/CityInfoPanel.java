package org.jel.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.text.DefaultCaret;

import org.jel.game.data.City;
import org.jel.game.data.Resource;

/**
 * This panel shows information about a selected city. At the top it is showing
 * the statistics about this city (Number of people, number of soldiers,
 * growth,...). After that, there are {@code Resource.values().length +1}
 * ResourceButtons. Each of these buttons upgrades one resource. The last button
 * is for upgrading the defense. The last component is a RecruitButton which is
 * responsible for recruiting soldiers in this city.
 */
class CityInfoPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 2409517597504961241L;
	private final transient City city;
	private final Timer timer;
	private JEditorPane statsViewer;
	private final List<ResourceButton> resourceButtons;
	private RecruitButton recruitButton;

	/**
	 * Create a CityInfoPanel with a specified city as base.
	 *
	 * @param city The city to show.
	 */
	CityInfoPanel(City city) {
		this.city = city;
		this.timer = new ExtendedTimer(17, this);
		this.timer.start();
		this.resourceButtons = new ArrayList<>();
	}

	/**
	 * Shouldn't be used.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		this.doUpdate();
	}

	/**
	 * Update all components on this panel.
	 */
	void doUpdate() {
		this.statsViewer.setText(this.generateText());
		this.resourceButtons.forEach(ResourceButton::doUpdate);
		this.recruitButton.doUpdate();
	}

	private String generateText() {
		if (!this.city.isPlayerCity()) {
			return "<html><body>No information available!</body></html>";
		}
		final var sb = new StringBuilder().append("<html><body>Name: ").append(this.city.getName()).append("<br>Clan: ")
				.append(this.city.getGame().getClanNames().get(this.city.getClanId())).append("<br>Soldiers: ")
				.append(this.city.getNumberOfSoldiers()).append("<br>Civilians: ").append(this.city.getNumberOfPeople())
				.append("<br>Defense: ").append(String.format("%.2f", this.city.getDefense()))
				.append("<br>Defense bonus: ").append(String.format("%.2f", this.city.getBonus()))
				.append("<br>Growth: ").append(String.format("%.2f", this.city.getGrowth()))
				.append("<br>Recruitable Civilians: ").append(this.city.getGame()
						.maximumNumberOfSoldiersToRecruit((byte) this.city.getClanId(), this.city.getNumberOfPeople()));
		final var list = this.city.getProductions();
		for (var i = 0; i < list.size(); i++) {
			sb.append("<br>").append(Resource.values()[i].getName()).append(": ");
			final var value = this.city.productionPerRound(Resource.values()[i]);
			// Not enough is produced.
			if ((value / this.city.getNumberOfPeople()) < 1) {
				sb.append("<font color='red'>");
			} else {
				sb.append("<font color='green'>");
			}
			sb.append(String.format("%.2f", value)).append("</font>");
		}
		return sb.append("</body></html>").toString();
	}

	/**
	 * This method has to be called in order to initialize the component.
	 */
	void init() {
		this.setLayout(new FlowLayout());
		this.statsViewer = new JEditorPane("text/html", this.generateText());
		((DefaultCaret) this.statsViewer.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		this.statsViewer.setEditable(false);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		final var scrollingPanel = new JPanel();
		scrollingPanel.add(this.statsViewer);
		scrollingPanel.setLayout(new BoxLayout(scrollingPanel, BoxLayout.Y_AXIS));
		for (final var r : Resource.values()) {
			final var resourceButton = new ResourceButton(r, this.city, this);
			scrollingPanel.add(resourceButton);
			this.resourceButtons.add(resourceButton);
		}
		final var resourceButton = new ResourceButton(null, this.city, this);
		scrollingPanel.add(resourceButton);
		this.resourceButtons.add(resourceButton);
		this.recruitButton = new RecruitButton(this.city, this);
		scrollingPanel.add(this.recruitButton);
		final var scrollPane = new JScrollPane(scrollingPanel);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setIgnoreRepaint(true);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		this.add(scrollPane);
		this.setVisible(true);
	}
}
