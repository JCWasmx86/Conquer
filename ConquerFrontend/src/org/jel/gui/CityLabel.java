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

/**
 * Represents a city on the map. It is divided into two parts. The first (upper)
 * part is the image of the city itself. The second part is a rectangle showing
 * the color of the corresponding clan.
 */
final class CityLabel extends JLabel implements ActionListener, MouseListener {
	/**
	 * Specifies the height of the colored rectangle that shows the association with
	 * a clan
	 */
	static final int CLAN_COLOR_HEIGHT = 12;
	private static final long serialVersionUID = -5091974547825438103L;
	private final transient City city;
	private final transient Timer timer;
	private final transient Map<City, CityLabel> labels;
	private boolean marked = false;
	private int counter = 0;
	private transient City origin;
	private final transient Consumer<City> consumer;

	/**
	 * Constructs a new CityLabel
	 *
	 * @param city     Which city to represent
	 * @param labels   A map of labels and the corresponding CityLabels.
	 * @param consumer A consumer, that is executed as soon as the mouse was clicked
	 *                 on it.
	 */
	CityLabel(City city, Map<City, CityLabel> labels, Consumer<City> consumer) {
		this.city = city;
		final var image = city.getImage();
		this.setBounds(city.getX(), city.getY(), image.getWidth(null),
				image.getHeight(null) + CityLabel.CLAN_COLOR_HEIGHT);
		this.timer = new ExtendedTimer(17, this);
		this.timer.start();
		this.labels = labels;
		this.addMouseListener(this);
		this.consumer = consumer;
	}

	/**
	 * Shouldn't be used.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		this.paint(this.getGraphics());
		var s = "<html>";
		s += this.city.getName();
		s += "<br>Clan: " + this.city.getGame().getClanNames().get(this.city.getClanId()) + "<br>";
		if (this.city.isPlayerCity()) {
			s += String.format("People: %d<br>Soldiers: %d</html>", this.city.getNumberOfPeople(),
					this.city.getNumberOfSoldiers());
		} else {
			s += "People: ???<br>Soldiers: ???</html>";
		}
		this.setToolTipText(s);
	}

	/**
	 * Returns the city this label is representing
	 */
	City getCity() {
		return this.city;
	}

	/**
	 * Calculates the middle of the icon on the map. (The x-Value)
	 *
	 * @return The x-Value
	 */
	int getPreferredX() {
		return this.getX() + (this.city.getImage().getWidth(null) / 2);
	}

	/**
	 * Calculates the middle of the icon on the map. (The y-Value)
	 *
	 * @return The y-Value
	 */
	int getPreferredY() {
		return this.getY() + (this.city.getImage().getHeight(null) / 2) + (CityLabel.CLAN_COLOR_HEIGHT / 2);
	}

	private void mark(City origin) {
		this.marked = true;
		this.origin = origin;
	}

	/**
	 * Shouldn't be used.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 1) {
			this.consumer.accept(this.city);
			if (!this.marked && (this.city.isPlayerCity())) {
				this.labels.values().forEach(CityLabel::unmark);
				this.city.getGame().getCities().getConnected(this.city)
						.forEach(a -> this.labels.get(a).mark(this.city));
			} else if (this.marked) {
				this.proceed();
				this.labels.values().forEach(CityLabel::unmark);
			}
		}
	}

	/**
	 * Shouldn't be used.
	 */
	@Override
	public void mouseEntered(MouseEvent e) {

	}

	/**
	 * Shouldn't be used.
	 */
	@Override
	public void mouseExited(MouseEvent e) {

	}

	/**
	 * Shouldn't be used.
	 */
	@Override
	public void mousePressed(MouseEvent e) {

	}

	/**
	 * Shouldn't be used.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {

	}

	/**
	 * Repaints the whole CityLabel.
	 */
	@Override
	public void paint(Graphics g) {
		try {
			super.paint(g);
		} catch (final NullPointerException npe) {
			this.timer.stop();
			return;
		}
		if (this.marked && (this.origin != null) && (this.origin.isPlayerCity())) {
			this.unmark();
		}
		g.drawImage(this.city.getImage(), 0, 0, null);
		final var image = this.city.getImage();
		final var baseYValue = image.getHeight(null);
		g.setColor(this.city.getGame().getClan(this.city.getClanId()).getColor());
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
		final var width = image.getWidth(null);
		g.fillRect(0, baseYValue, width, 2);
		g.fillRect(0, baseYValue + 10, width, 2);
		g.fillRect(0, baseYValue, 2, 20);
		g.fillRect(width - 2, baseYValue, 2, CityLabel.CLAN_COLOR_HEIGHT);
	}

	private void proceed() {
		final var game = this.city.getGame();
		final var weight = game.getCities().getWeight(this.city, this.origin);
		final var count = game.maximumNumberToMove((byte) 0, weight, this.origin.getNumberOfSoldiers());
		String prompt;
		if (this.city.isPlayerCity()) {
			prompt = "How many soldiers do you want to move from " + this.origin.getName() + " to "
					+ this.city.getName() + "? (0 ->" + count + ")";
		} else {
			prompt = "How many soldiers should be used for the attack from  " + this.origin.getName() + " to "
					+ this.city.getName() + "? (0 ->" + count + ")";
		}
		long numberOfSelectedSoldiers = -1;
		while (numberOfSelectedSoldiers == -1) {
			final var input = JOptionPane.showInputDialog(null, prompt);
			if (input == null) {// Exit was pressed
				return;
			}
			try {
				numberOfSelectedSoldiers = Long.parseLong(input);
			} catch (final NumberFormatException nfe) {
				continue;// Invalid input? - Try again
			}
			// Too many/few soldiers? - Try again
			if ((numberOfSelectedSoldiers <= -1) || (numberOfSelectedSoldiers > (game.maximumNumberToMove((byte) 0,
					weight, this.origin.getNumberOfSoldiers())))) {
				numberOfSelectedSoldiers = -1;
			}
		}
		if (this.city.isPlayerCity()) {
			game.moveSoldiers(this.origin, (Stream<City>) null, (byte) 0, true, this.city, numberOfSelectedSoldiers);
		} else {
			game.attack(this.origin, this.city, (byte) 0, true, numberOfSelectedSoldiers);
		}
	}

	/**
	 * Resets the CityLabel. (No blinking anymore, resets the source city for
	 * attacks/troop movements)
	 */
	void unmark() {
		this.marked = false;
		this.counter = 0;
		this.origin = null;
	}
}
