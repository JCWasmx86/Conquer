package conquer.gui;

import conquer.data.ConquerInfo;
import conquer.data.ICity;
import conquer.data.IClan;
import conquer.data.StreamUtils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class StatsPanel extends JPanel implements ActionListener {
	private final ConquerInfo info;
	private Statistic statistic;

	StatsPanel(final ConquerInfo info) {
		this.info = info;
	}

	void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		final var timer = new ExtendedTimer(Utils.getRefreshRate(), this);
		timer.start();
	}

	@Override
	public void paint(final Graphics g) {
		super.paint(g);
		if (this.statistic == null) {
			return;
		}
		g.setColor(Color.WHITE);
		g.drawRect(0, 0, this.getWidth(), this.getHeight());
		final var copy = this.statistic;
		copy.draw(g, this.getWidth(), this.getHeight());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.statistic = Statistic.build(info);
		this.repaint();
	}

	record Statistic(Map<IClan, Double> map) {
		static Statistic build(ConquerInfo info) {
			final var list =
					info.getClans().stream().sorted(Comparator.comparingDouble(a -> Statistic.getStrength(info, a))).collect(Collectors.toList());
			final var totalStrength = list.stream().mapToDouble(a -> Statistic.getStrength(info, a)).sum();
			final var map = new HashMap<IClan, Double>();
			list.forEach(a -> map.put(a, Statistic.getStrength(info, a) / totalStrength));
			return new Statistic(map);
		}

		private static double getStrength(final ConquerInfo info, final IClan clan) {
			final var rawStrength =
					StreamUtils.getCitiesAsStream(info.getCities(), clan).mapToLong(ICity::getNumberOfSoldiers).sum() * clan.getSoldiersStrength();
			final var strength =
					rawStrength * clan.getSoldiersDefenseStrength() + rawStrength + clan.getSoldiersOffenseStrength() + StreamUtils.getCitiesAsStream(info.getCities(), clan).mapToDouble(ICity::getDefense).sum();
			final var allCivilians =
					StreamUtils.getCitiesAsStream(info.getCities(), clan).mapToLong(ICity::getNumberOfPeople).sum();
			return strength + allCivilians;
		}

		void draw(final Graphics g, final int width, final int height) {
			final var startX = (int) Math.rint(width * 0.2);
			final var startY = (int) Math.rint(height * 0.2);
			final var endX = (int) Math.rint(width * 0.8);
			final var endY = (int) Math.rint(height * 0.8);
			var currY = startY;
			final var diffY = endY - startY;
			for (final var entry :
					map.entrySet().stream().sorted((a, b) -> Double.compare(b.getValue(), a.getValue())).collect(Collectors.toList())) {
				g.setColor(entry.getKey().getColor());
				final var heightOfRectangle = (int) Math.rint(diffY * entry.getValue());
				g.fillRect(startX, currY, endX - startX, heightOfRectangle);
				currY += heightOfRectangle;
			}
		}
	}
}
