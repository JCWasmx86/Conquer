package conquer.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.text.DefaultCaret;

import conquer.data.ICity;
import conquer.data.Resource;

/**
 * This panel shows information about a selected city. At the top it is showing
 * the statistics about this city (Number of people, number of soldiers,
 * growth,...). After that, there are {@code Resource.values().length +1}
 * ResourceButtons. Each of these buttons upgrades one resource. The last button
 * is for upgrading the defense. The last component is a RecruitButton which is
 * responsible for recruiting soldiers in this city.
 */
class CityInfoPanel extends JPanel implements ActionListener {
	@Serial
	private static final long serialVersionUID = 2409517597504961241L;
	private final transient ICity city;
	private final List<ResourceButton> resourceButtons;
	private JEditorPane statsViewer;
	private RecruitButton recruitButton;

	/**
	 * Create a CityInfoPanel with a specified city as base.
	 *
	 * @param city The city to show.
	 */
	CityInfoPanel(final ICity city) {
		this.city = city;
		final Timer timer = new ExtendedTimer(Utils.getRefreshRate(), this);
		timer.start();
		this.resourceButtons = new ArrayList<>();
	}

	/**
	 * Shouldn't be used.
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
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
		if (this.city.getInfo().isDead(this.city.getInfo().getPlayerClan())) {
			return "<html><body><font color='red'>" + Messages.getString("CityInfoPanel.youAreDead")

				+ "</font></body></html>";
		} else if (!this.city.isPlayerCity()) {
			return "<html><body><font color='red'>" + Messages.getString("CityInfoPanel.youDontOwnThisCity")
				+ "</font></body></html>";
		}
		final var sb = new StringBuilder().append("<html><body>").append(Messages.getString("Shared.name"))
			.append(": ")
			.append(this.city.getName()).append("<br>").append(Messages.getString(
				"Shared.clan"))
			.append(": ")
			.append(this.city.getClan().getName()).append("<br>")
			.append(Messages.getString("Shared.soldiers"))
			.append(": ").append(this.city.getNumberOfSoldiers()).append("<br>")
			.append(Messages.getString("Shared.people")).append(": ")
			.append(this.city.getNumberOfPeople()).append("<br>")
			.append(Messages.getString("Shared.defense")).append(": ")
			.append(String.format("%.2f", this.city.getDefense())).append("<br>")
			.append(Messages.getString("Shared.defenseBonus"))
			.append(": ").append(String.format("%.2f", this.city.getBonus())).append("<br>")
			.append(Messages.getString("Shared.growth"))
			.append(": ").append(String.format("%.2f", this.city.getGrowth())).append("<br>")
			.append(Messages.getString("CityInfoPanel.recruitablePeople")).append(": ").append(this.city.getInfo()
				.maximumNumberOfSoldiersToRecruit(this.city.getClan(), this.city.getNumberOfPeople()));
		final var list = this.city.getProductions();
		for (var i = 0; i < list.size(); i++) {
			sb.append("<br>").append(Resource.values()[i].getName()).append(": ");
			// 
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
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		final var scrollingPanel = new JPanel();
		scrollingPanel.add(this.statsViewer);
		scrollingPanel.setLayout(new BoxLayout(scrollingPanel, BoxLayout.PAGE_AXIS));
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
