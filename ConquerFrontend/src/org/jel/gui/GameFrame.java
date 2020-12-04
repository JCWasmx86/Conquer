package org.jel.gui;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import org.jel.game.data.City;
import org.jel.game.data.Game;
import org.jel.game.data.Shared;
import org.jel.game.data.StreamUtils;
import org.jel.gui.utils.ImageResource;
import org.jel.gui.utils.LoopPlayer;

/**
 * This class is the class that shows the entire map with all other components.
 * It has 3 big components:
 * <ol>
 * <li>A JLabel that shows the entire map (Upper left)</li>
 * <li>A JPanel with buttons and labels from plugins(Lower left)</li>
 * <li>A JTabbedPane with the CityInfo, ClanInfo and Relationships tabs</li>
 * </ol>
 */
final class GameFrame extends JFrame implements WindowListener, ComponentListener {
	private static final long serialVersionUID = 4456629322882679917L;
	private final transient Game game;
	private final Map<City, CityLabel> labels = new HashMap<>();
	private final LoopPlayer loopPlayer = new LoopPlayer();
	private JLabel gameStage;
	private JScrollPane gameStageScrollPane;
	private JTabbedPane sideBarPane;
	private JPanel buttonPanel;
	private transient List<DashedLine> lines = new ArrayList<>();
	private transient Thread coinsLabelUpdateThread;

	/**
	 * Create a new GameFrame with a specified game as base
	 *
	 * @param game The game to show
	 */
	GameFrame(Game game) {
		this.game = game;
		this.addComponentListener(this);
		this.addWindowListener(this);
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
	}

	private void adjustX() {
		if (!this.buttonPanel.isShowing() || !this.gameStageScrollPane.isShowing()) {
			return;
		}
		final var diff = (this.buttonPanel.getLocationOnScreen().y
				- (this.gameStageScrollPane.getLocationOnScreen().y + this.game.getBackground().getHeight(null))) / 2;
		if (diff <= 0) {
			return;
		}
		this.labels.values().forEach(b -> {
			final var image = b.getCity().getImage();
			b.setBounds(b.getLocation().x, b.getCity().getY() + diff, image.getWidth(null),
					image.getHeight(null) + CityLabel.CLAN_COLOR_HEIGHT);
			b.repaint();
		});
	}

	private void adjustY() {
		if (!this.sideBarPane.isShowing() || !this.gameStageScrollPane.isShowing() || !this.gameStage.isShowing()) {
			return;
		}
		final var diff = (this.sideBarPane.getLocationOnScreen().x
				- (this.gameStageScrollPane.getLocationOnScreen().x + this.game.getBackground().getWidth(null)));
		final var diff2 = this.gameStage.getLocationOnScreen().x - this.gameStageScrollPane.getLocationOnScreen().x;
		if ((diff <= 0) || (diff2 <= 0)) {
			return;
		}
		this.labels.values().forEach(b -> {
			final var city = b.getCity();
			final var image = city.getImage();
			b.setBounds(city.getX() + ((diff + diff2) / 2), city.getY(), image.getWidth(null),
					image.getHeight(null) + CityLabel.CLAN_COLOR_HEIGHT);
			b.repaint();
		});
	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void componentHidden(ComponentEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void componentMoved(ComponentEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void componentResized(ComponentEvent e) {
		this.adjustY();
		this.adjustX();
	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void componentShown(ComponentEvent e) {

	}

	@Override
	public void dispose() {
		super.dispose();
		ExtendedTimer.stopAll();
	}

	/**
	 * Initialises this frame.
	 */
	void init() {
		EventLog.init(this.game);
		this.initButtonPanel();
		this.makeLeftPanel();
		this.initGameStage();
		this.initSideBar();
		this.setVisible(true);
		this.setTitle("Conquer: Round " + this.game.currentRound());
		this.pack();
		this.nonGUIInit();
	}

	private void initButtonPanel() {
		this.buttonPanel = new JPanel();
		this.buttonPanel.setLayout(new FlowLayout());
		final var plugins = this.game.getPlugins();
		final var nextRound = new JButton(new ImageResource("hourglass.png"));
		nextRound.setToolTipText("Next round");
		nextRound.addActionListener(a -> {
			if (this.game.isPlayersTurn()) {
				this.game.setPlayersTurn(false);
				this.game.executeActions();
			}
			this.setTitle("Conquer: Round " + this.game.currentRound());
		});
		final var openMessages = new JButton(new ImageResource("messagebox.png"));
		openMessages.setToolTipText("Open messagebox");
		openMessages.addActionListener(a -> EventLog.showWindow());
		final var coinsLabel = new JLabel("Coins: " + this.game.getCoins().get(0));
		final var run = new JButton("Run forever");
		run.addActionListener(a -> {
			while (!this.game.onlyOneClanAlive()) {
				this.game.executeActions();
				this.repaint();
				this.setTitle("Conquer: Round " + this.game.currentRound());
				this.labels.values().forEach(b -> b.actionPerformed(null));
				try {
					Thread.sleep(50);
				} catch (final InterruptedException ie) {
					Shared.LOGGER.exception(ie);
				}
			}
		});
		this.buttonPanel.add(coinsLabel);
		this.buttonPanel.add(nextRound);
		this.buttonPanel.add(openMessages);
		this.buttonPanel.add(run);
		plugins.forEach(a -> {
			final var listOfButtons = a.getButtons();
			if (listOfButtons != null) {
				listOfButtons.forEach(this.buttonPanel::add);
			}
		});
		this.coinsLabelUpdateThread = new Thread(() -> {
			while (true) {
				coinsLabel.setText("Coins: " + String.format("%.2f%n", this.game.getCoins().get(0)));
				try {
					Thread.sleep(20);
				} catch (final InterruptedException e) {
					Shared.LOGGER.exception(e);// Oops
				}
			}
		});
		this.coinsLabelUpdateThread.start();
	}

	private void initGameStage() {
		this.gameStage = new JLabel(new ImageIcon(this.game.getBackground())) {
			private static final long serialVersionUID = -2190001953669516117L;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				final var g2d = (Graphics2D) g.create();
				final var dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
						new float[] { 9 }, 0);
				g2d.setColor(Color.WHITE);
				g2d.setStroke(dashed);
				GameFrame.this.lines.forEach(a -> a.draw(g2d));
				g2d.dispose();
			}
		};
		this.gameStageScrollPane.setViewportView(this.gameStage);
		this.gameStage.setFocusable(true);
		this.gameStage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					GameFrame.this.labels.values().forEach(CityLabel::unmark);
				}
			}
		});
		this.gameStage.setLayout(null);
	}

	private void initSideBar() {
		final var cities = this.game.getCities();
		final var cityInfoPanel = new JPanel();
		final var cardLayout = new CardLayout();
		cityInfoPanel.setLayout(cardLayout);
		StreamUtils.getCitiesAsStream(cities).forEach(city -> {
			final var cityLabel = new CityLabel(city, this.labels, b -> cardLayout.show(cityInfoPanel, b.getName()));
			final var infoPanel = new CityInfoPanel(city);
			infoPanel.init();
			cityInfoPanel.add(infoPanel, city.getName());
			cardLayout.addLayoutComponent(infoPanel, city.getName());
			this.gameStage.add(cityLabel);
			this.labels.put(city, cityLabel);
		});
		cardLayout.show(cityInfoPanel, StreamUtils.getCitiesAsStream(cities).filter(a -> a.getClan() == 0).findFirst()
				.orElseThrow().getName());
		this.sideBarPane = new JTabbedPane();
		this.sideBarPane.addTab("City info", cityInfoPanel);
		final var clanInfo = new ClanInfoPanel(this.game.getClan(Shared.PLAYER_CLAN), this.game);
		clanInfo.init();
		this.sideBarPane.addTab("Clan info", clanInfo);
		final var relationships = new RelationshipPanel(this.game);
		relationships.init();
		final var relationShipScrollPane = new JScrollPane(relationships);
		relationShipScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		this.sideBarPane.add("Relationships", relationShipScrollPane);
		this.add(this.sideBarPane);
	}

	private void makeLeftPanel() {
		this.gameStageScrollPane = new JScrollPane();
		final var buttonPanelScrollPane = new JScrollPane(this.buttonPanel);
		buttonPanelScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		final var panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
		panel1.add(this.gameStageScrollPane);
		panel1.add(buttonPanelScrollPane);
		this.add(panel1);
	}

	private void nonGUIInit() {
		this.game.setPlayerGiftCallback(new GiftCallback());
		final var cities = this.game.getCities();
		this.game.getExtraMusic().forEach(this.loopPlayer::addSong);
		this.loopPlayer.start();
		final var connections = cities.getConnections();
		final Map<Integer, List<Integer>> drawnLines = new HashMap<>();
		connections.forEach(triple -> {
			final var first = triple.first();
			final var second = triple.second();
			if ((drawnLines.containsKey(first) && (drawnLines.get(first).indexOf(second) != -1))
					|| (drawnLines.containsKey(second) && (drawnLines.get(second).indexOf(first) != -1))) {
				return;
			}
			this.lines.add(
					new DashedLine(this.labels.get(cities.getValue(first)), this.labels.get(cities.getValue(second))));
		});
	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowActivated(WindowEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowClosed(WindowEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		if (!this.game.onlyOneClanAlive()) {
			this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			final var status = JOptionPane.showConfirmDialog(null, "Do you really want to quit?", "Exit",
					JOptionPane.YES_NO_OPTION);
			if (status == JOptionPane.YES_OPTION) {
				EventLog.clear();
				this.loopPlayer.abort();
				MainScreen.forward(this.getLocation(), false);
				this.coinsLabelUpdateThread.stop();
				this.dispose();
			} else {
				this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			}
		} else {
			EventLog.clear();
			this.loopPlayer.abort();
			MainScreen.forward(this.getLocation(), false);
			this.coinsLabelUpdateThread.stop();
			this.dispose();
		}
	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowDeactivated(WindowEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowIconified(WindowEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowOpened(WindowEvent e) {

	}
}