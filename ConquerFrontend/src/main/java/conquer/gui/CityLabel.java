package conquer.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import conquer.data.ICity;

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
	private final transient ICity city;
	private final transient Timer timer;
	private final transient Map<ICity, CityLabel> labels;
	private boolean marked = false;
	private int counter = 0;
	private transient ICity origin;
	private final transient Consumer<ICity> consumer;

	/**
	 * Constructs a new CityLabel
	 *
	 * @param city     Which city to represent
	 * @param labels   A map of labels and the corresponding CityLabels.
	 * @param consumer A consumer, that is executed as soon as the mouse was clicked
	 *                 on it.
	 */
	CityLabel(final ICity city, final Map<ICity, CityLabel> labels, final Consumer<ICity> consumer) {
		this.city = city;
		final var image = city.getImage();
		this.setBounds(city.getX(), city.getY(), image.getWidth(null),
			image.getHeight(null) + CityLabel.CLAN_COLOR_HEIGHT);
		this.timer = new ExtendedTimer(Utils.getRefreshRate(), this);
		this.timer.start();
		this.labels = labels;
		this.addMouseListener(this);
		this.consumer = consumer;
	}

	/**
	 * Shouldn't be used.
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		this.paint(this.getGraphics());
		final var s = new StringBuilder("<html>").append(this.city.getName()); //$NON-NLS-1$
		s.append(String.format("<br>%s: %s<br>", Messages.getString("Shared.clan"), this.city.getClan().getName())); //$NON-NLS-1$ //$NON-NLS-2$
		if (this.city.isPlayerCity()) {
			s.append(String.format("%s: %d<br>%s: %d</html>", Messages.getString("Shared.people"), //$NON-NLS-1$ //$NON-NLS-2$
				this.city.getNumberOfPeople(), Messages.getString("Shared.soldiers"), //$NON-NLS-1$
				this.city.getNumberOfSoldiers()));
		} else {
			s.append(String.format("%s: ???<br>%s: ???</html>", Messages.getString("Shared.people"), //$NON-NLS-1$ //$NON-NLS-2$
				Messages.getString("Shared.soldiers"))); //$NON-NLS-1$
		}
		this.setToolTipText(s.toString());
	}

	/**
	 * Returns the city this label is representing
	 */
	ICity getCity() {
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

	private void mark(final ICity origin) {
		if (!this.city.getInfo().canMove(origin, this.city)) {
			this.unmark();
			return;
		}
		this.marked = true;
		this.origin = origin;
	}

	/**
	 * Shouldn't be used.
	 */
	@Override
	public void mouseClicked(final MouseEvent e) {
		if (e.getClickCount() == 1) {
			this.consumer.accept(this.city);
			if (!this.marked && (this.city.isPlayerCity())) {
				this.labels.values().forEach(CityLabel::unmark);
				this.city.getInfo().getCities().getConnected(this.city)
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
	public void mouseEntered(final MouseEvent e) {

	}

	/**
	 * Shouldn't be used.
	 */
	@Override
	public void mouseExited(final MouseEvent e) {

	}

	/**
	 * Shouldn't be used.
	 */
	@Override
	public void mousePressed(final MouseEvent e) {

	}

	/**
	 * Shouldn't be used.
	 */
	@Override
	public void mouseReleased(final MouseEvent e) {

	}

	/**
	 * Repaints the whole CityLabel.
	 */
	@Override
	public void paint(final Graphics g) {
		try {
			super.paint(g);
		} catch (final NullPointerException npe) {
			this.timer.stop();
			return;
		}
		if (this.marked && (this.origin != null) && (!this.origin.isPlayerCity())) {
			this.unmark();
		}
		g.drawImage(this.city.getImage(), 0, 0, null);
		final var image = this.city.getImage();
		final var baseYValue = image.getHeight(null);
		g.setColor(this.city.getClan().getColor());
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
		final var game = this.city.getInfo();
		final var weight = game.getCities().getWeight(this.city, this.origin);
		final var maximumNumberOfSoldiersToMove = game.maximumNumberToMove(game.getPlayerClan(), weight,
			this.origin.getNumberOfSoldiers());
		final String prompt;
		if (this.city.isPlayerCity()) {
			prompt = Messages.getMessage("CityLabel.moveLabel", //$NON-NLS-1$
				this.origin.getName(), this.city.getName(), maximumNumberOfSoldiersToMove);
		} else {
			prompt = Messages.getMessage("CityLabel.attackLabel", //$NON-NLS-1$
				this.city.getName(), this.origin.getName(), maximumNumberOfSoldiersToMove);
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
			if ((numberOfSelectedSoldiers <= -1) || (numberOfSelectedSoldiers > maximumNumberOfSoldiersToMove)) {
				numberOfSelectedSoldiers = -1;
			}
		}
		if (this.city.isPlayerCity()) {
			game.moveSoldiers(this.origin, null, true, this.city, numberOfSelectedSoldiers);
		} else {
			game.attack(this.origin, this.city, true, numberOfSelectedSoldiers);
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
