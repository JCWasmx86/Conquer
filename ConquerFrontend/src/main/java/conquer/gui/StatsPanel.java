package conquer.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.BiFunction;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import conquer.data.ConquerInfo;
import conquer.data.IClan;

public class StatsPanel extends JPanel implements ActionListener {
	private final ConquerInfo info;
	private final BiFunction<ConquerInfo, IClan, Double> function;
	private Statistic statistic;

	StatsPanel(final ConquerInfo info, final BiFunction<ConquerInfo, IClan, Double> func) {
		this.function = func;
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
	public void actionPerformed(final ActionEvent e) {
		this.statistic = Statistic.build(this.info, this.function);
		this.repaint();
	}
}
