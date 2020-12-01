package org.jel.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.jel.game.data.City;

final class CityLabel extends JLabel implements ActionListener, MouseListener {
	private static final int CLAN_COLOR_HEIGHT = 12;
	private static final long serialVersionUID = -5091974547825438103L;
	private final transient City city;
	private final transient Timer timer;
	private final transient Map<City, CityLabel> labels;
	private boolean marked = false;
	private int counter = 0;
	private transient City origin;
	private final transient Consumer<City> consumer;

	CityLabel(City city, Map<City, CityLabel> labels, Consumer<City> consumer) {
		this.city = city;
		final var image = city.getImage();
		this.setBounds(city.getX(), city.getY(), image.getWidth(null),
				image.getHeight(null) + CityLabel.CLAN_COLOR_HEIGHT);
		this.timer = new Timer(17, this);
		this.timer.start();
		this.labels = labels;
		this.addMouseListener(this);
		this.consumer = consumer;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.paint(this.getGraphics());
		var s = "<html>";
		s += this.city.getName();
		s += "<br>Clan: " + this.city.getGame().getClanNames().get(this.city.getClan()) + "<br>";
		if (this.city.getClan() == 0) {
			s += String.format("People: %d<br>Soldiers: %d</html>", this.city.getNumberOfPeople(),
					this.city.getNumberOfSoldiers());
		} else {
			s += "People: ???<br>Soldiers: ???</html>";
		}
		this.setToolTipText(s);
	}

	City getCity() {
		return this.city;
	}

	int getPreferredX() {
		return this.getX() + (this.city.getImage().getWidth(null) / 2);
	}

	int getPreferredY() {
		return this.getY() + (this.city.getImage().getHeight(null) / 2) + (CityLabel.CLAN_COLOR_HEIGHT / 2);
	}

	private void mark(City origin) {
		this.marked = true;
		this.origin = origin;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 1) {
			this.consumer.accept(this.city);
			if (!this.marked && (this.city.getClan() == 0)) {
				this.labels.values().forEach(CityLabel::unmark);
				this.city.getGame().getCities().getConnected(this.city)
						.forEach(a -> this.labels.get(a).mark(this.city));
			} else if (this.marked) {
				this.proceed();
				this.labels.values().forEach(CityLabel::unmark);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void paint(Graphics g) {
		try {
			super.paint(g);
		} catch (final NullPointerException npe) {
			this.timer.stop();
			return;
		}
		if (this.marked && (this.origin != null) && (this.origin.getClan() != 0)) {
			this.unmark();
		}
		g.drawImage(this.city.getImage(), 0, 0, null);
		final var image = this.city.getImage();
		final var baseYValue = image.getHeight(null);
		g.setColor(this.city.getGame().getClan(this.city.getClan()).getColor());
		g.fillRect(0, baseYValue, image.getWidth(null), CityLabel.CLAN_COLOR_HEIGHT);
		if (this.marked) {
			if (this.counter <= 45) {
				g.setColor(Color.RED);
			} else {
				g.setColor(Color.GREEN);
			}
			this.counter++;
			if (this.counter == 91) {
				this.counter = 0;
			}
		} else {
			g.setColor(Color.WHITE);
		}
		g.fillRect(0, baseYValue, image.getWidth(null), 2);
		g.fillRect(0, baseYValue + 10, image.getWidth(null), 2);
		g.fillRect(0, baseYValue, 2, 20);
		g.fillRect(image.getWidth(null) - 2, baseYValue, 2, CityLabel.CLAN_COLOR_HEIGHT);
	}

	private void proceed() {
		final var game = this.city.getGame();
		if (this.city.getClan() == 0) {
			final var count = this.city.getGame().maximumNumberToMove((byte) 0,
					game.getCities().getWeight(this.city, this.origin), this.origin.getNumberOfSoldiers());
			long l = -1;
			while (l == -1) {
				final var s = JOptionPane.showInputDialog(null, "How many soldiers do you want to move from "
						+ this.origin.getName() + " to " + this.city.getName() + "? (0 ->" + count + ")");
				if (s == null) {// Exit was pressed
					return;
				}
				try {
					l = Long.parseLong(s);
				} catch (final NumberFormatException nfe) {
					continue;// Invalid input? - Try again
				}
				// Too many/few soldiers? - Try again
				if ((l <= -1) || (l > (this.city.getGame().maximumNumberToMove((byte) 0,
						game.getCities().getWeight(this.origin, this.city), this.origin.getNumberOfSoldiers())))) {
					l = -1;
				}
			}
			this.city.getGame().moveSoldiers(this.origin, (Stream<City>) null, (byte) 0, true, this.city, l);
		} else {
			final var count = game.maximumNumberToMove((byte) 0, game.getCities().getWeight(this.city, this.origin),
					this.origin.getNumberOfSoldiers());
			long l = -1;
			while (l == -1) {
				final var s = JOptionPane.showInputDialog(null, "How many soldiers should be used for the attack from  "
						+ this.origin.getName() + " to " + this.city.getName() + "? (0 ->" + count + ")");
				if (s == null) {// Exit was pressed
					return;
				}
				try {
					l = Long.parseLong(s);
				} catch (final NumberFormatException nfe) {
					continue;// Invalid input? - Try again
				}
				// Too many/few soldiers? - Try again
				if ((l <= -1) || (l > (this.city.getGame().maximumNumberToMove((byte) 0,
						game.getCities().getWeight(this.origin, this.city), this.origin.getNumberOfSoldiers())))) {
					l = -1;
				}
			}
			this.city.getGame().attack(this.origin, this.city, (byte) 0, true, l);
		}
	}

	void unmark() {
		this.marked = false;
		this.counter = 0;
		this.origin = null;
	}
}
