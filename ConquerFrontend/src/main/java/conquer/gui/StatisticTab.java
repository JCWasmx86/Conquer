package conquer.gui;

import javax.swing.JTabbedPane;

import conquer.data.ConquerInfo;
import conquer.data.ICity;
import conquer.data.IClan;
import conquer.data.StreamUtils;

public class StatisticTab extends JTabbedPane {
	StatisticTab() {
		//Empty
	}

	void init(final ConquerInfo info) {
		final var population = new StatsPanel(info, this::getPopulation);
		population.init();
		this.addTab(Messages.getString("GameFrame.stats.population"), population);
		final var soldiers = new StatsPanel(info, this::getSoldiers);
		soldiers.init();
		this.addTab(Messages.getString("GameFrame.stats.soldiers"), soldiers);
		for (var i = 0; i < this.getTabCount(); i++) {
			final var tabComponent = this.getTabComponentAt(i);
			if (tabComponent instanceof StatsPanel sp) {
				sp.init();
			}
		}
	}

	private double getSoldiers(final ConquerInfo info, final IClan clan) {
		return StreamUtils.getCitiesAsStream(info.getCities(), clan).mapToLong(ICity::getNumberOfSoldiers).sum();
	}

	private double getPopulation(final ConquerInfo info, final IClan clan) {
		return StreamUtils.getCitiesAsStream(info.getCities(), clan).mapToLong(ICity::getNumberOfPeople).sum();
	}
}
