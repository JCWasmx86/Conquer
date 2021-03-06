package conquer.gui;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import conquer.data.ConquerInfo;
import conquer.data.ICity;
import conquer.data.Shared;
import conquer.data.StreamUtils;
import conquer.frontend.spi.InGameButton;
import conquer.frontend.spi.MusicProvider;
import conquer.gui.utils.ImageResource;
import conquer.gui.utils.LoopPlayer;

/**
 * This class is the class that shows the entire map with all other components.
 * It has 3 big components:
 * <ol>
 * <li>A JLabel that shows the entire map (Upper left)</li>
 * <li>A JPanel with buttons and labels from plugins(Lower left)</li>
 * <li>A JTabbedPane with the CityInfo, ClanInfo and Relationships tabs</li>
 * </ol>
 */
final class GameFrame extends JFrame implements EmptyWindowListenerImpl, ComponentListener, KeyListener {
	private static final String TITLE_PART = Messages.getString("GameFrame.conquerTitle") + " ";
	@Serial
	private static final long serialVersionUID = 4456629322882679917L;
	private final transient ConquerInfo game;
	private final Map<ICity, CityLabel> labels = new HashMap<>();
	private final transient List<DashedLine> lines = new ArrayList<>();
	private final JPanel basePanel;
	private LoopPlayer loopPlayer = new LoopPlayer();
	private JLabel gameStage;
	private JScrollPane gameStageScrollPane;
	private JTabbedPane sideBarPane;
	private JPanel buttonPanel;
	private transient Thread coinsLabelUpdateThread;
	private String saveName;
	private transient Thread endlessThread;
	private transient GiftCallback callback;

	/**
	 * Create a new GameFrame with a specified game as base
	 *
	 * @param game The game to show
	 */
	GameFrame(final ConquerInfo game) {
		this.game = game;
		this.addComponentListener(this);
		this.addWindowListener(this);
		this.addKeyListener(this);
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		this.basePanel = new JPanel();
		this.basePanel.setLayout(new BoxLayout(this.basePanel, BoxLayout.LINE_AXIS));
	}

	GameFrame(final String saveName, final ConquerInfo game) {
		this(game);
		this.saveName = saveName;
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

	@SuppressWarnings("deprecation")
	private void cleanup() {
		EventLog.clear();
		this.loopPlayer.abort();
		MainScreen.forward(this.getLocation(), false);
		this.coinsLabelUpdateThread.stop();
		if ((this.endlessThread != null) && this.endlessThread.isAlive()) {
			this.endlessThread.stop();
		}
		this.callback.stop();
		this.dispose();
	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void componentHidden(final ComponentEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void componentMoved(final ComponentEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void componentResized(final ComponentEvent e) {
		this.adjustY();
		this.adjustX();
	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void componentShown(final ComponentEvent e) {

	}

	@Override
	public void dispose() {
		super.dispose();
		ExtendedTimer.stopAll();
	}

	/**
	 * Initializes this frame.
	 */
	void init() {
		EventLog.init(this.game);
		this.initButtonPanel();
		this.makeLeftPanel();
		this.initGameStage();
		this.initSideBar();
		this.initMenubar();
		this.setVisible(true);
		this.setTitle(this.game.getVersion() + " - " + GameFrame.TITLE_PART + this.game.currentRound());
		this.add(this.basePanel);
		this.pack();
		this.nonGUIInit();
		this.gameStage.addKeyListener(this);
	}

	private List<Component> getAllComponents(final Container c) {
		final var comps = c.getComponents();
		final var compList = new ArrayList<Component>();
		for (final var comp : comps) {
			compList.add(comp);
			if (comp instanceof Container cont)
				compList.addAll(this.getAllComponents(cont));
		}
		return compList;
	}

	private void initButtonPanel() {
		this.buttonPanel = new JPanel();
		this.buttonPanel.setLayout(new FlowLayout());
		final var nextRound = new JButton(new ImageResource("hourglass.png"));
		nextRound.setToolTipText(Messages.getString("GameFrame.nextRound"));
		nextRound.addActionListener(a -> new Thread(() -> {
			nextRound.setEnabled(false);
			if (this.game.isPlayersTurn()) {
				this.game.executeActions();
			}
			this.setTitle(this.game.getVersion() + " - " + GameFrame.TITLE_PART + this.game.currentRound());
			nextRound.setEnabled(true);
		}).start());
		final var openMessages = new JButton(new ImageResource("messagebox.png"));
		openMessages.setToolTipText(Messages.getString("GameFrame.openMessageBox"));
		openMessages.addActionListener(a -> EventLog.showWindow());
		final var coinsLabel = new JLabel(
			Messages.getString("Shared.coins") + ": " + this.game.getPlayerClan().getCoins());
		final var run = new JButton(Messages.getString("GameFrame.runForever"));
		run.addActionListener(a -> {
			run.setEnabled(false);
			nextRound.setEnabled(false);
			this.endlessThread = new Thread(() -> {
				while (!this.game.onlyOneClanAlive()) {
					this.game.executeActions();
					this.setTitle(this.game.getVersion() + " - " + GameFrame.TITLE_PART + this.game.currentRound());
					this.labels.values().forEach(b -> b.actionPerformed(null));
					try {
						Thread.sleep(50);
					} catch (final InterruptedException ie) {
						Shared.LOGGER.exception(ie);
					}
				}
				run.setEnabled(true);
				nextRound.setEnabled(true);
			});
			this.endlessThread.start();
		});
		this.buttonPanel.add(coinsLabel);
		this.buttonPanel.add(nextRound);
		this.buttonPanel.add(openMessages);
		this.buttonPanel.add(run);
		ServiceLoader.load(InGameButton.class).forEach(this.buttonPanel::add);
		this.coinsLabelUpdateThread = new Thread(() -> {
			var flag = false;
			while (true) {
				coinsLabel.setText(Messages.getString("Shared.coins") + ": "
					+ String.format("%.2f%n", this.game.getPlayerClan().getCoins()));
				try {
					Thread.sleep(20);
				} catch (final InterruptedException e) {
					Shared.LOGGER.exception(e);// Oops
				}
				if (!flag) {
					final var playerDead = this.game.isDead(this.game.getPlayerClan());
					if (playerDead) {
						this.loopPlayer.abort();
						this.loopPlayer = new LoopPlayer();
						this.loopPlayer.addSong("Defeated");
						this.loopPlayer.start();
						flag = true;
					} else if (this.game.onlyOneClanAlive()) {
						this.loopPlayer.abort();
						this.loopPlayer = new LoopPlayer();
						this.loopPlayer.addSong("Victory");
						this.loopPlayer.start();
						flag = true;
					}
				}
			}
		});
		this.coinsLabelUpdateThread.start();
	}

	private void initGameStage() {
		this.gameStage = new JLabel(new ImageIcon(this.game.getBackground())) {
			@Serial
			private static final long serialVersionUID = -2190001953669516117L;

			@Override
			public void paint(final Graphics g) {
				super.paint(g);
				final var g2d = (Graphics2D) g.create();
				final var dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
					new float[] {9}, 0);
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
			public void keyPressed(final KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					GameFrame.this.labels.values().forEach(CityLabel::unmark);
				}
			}
		});
		this.gameStage.setLayout(null);
	}

	private void initMenubar() {
		final var jmenubar = new JMenuBar();
		final var jmenu = new JMenu(Messages.getString("GameFrame.settings"));
		jmenubar.add(jmenu);
		final var saveGame = new JMenuItem();
		saveGame.setAction(new AbstractAction() {
			@Serial
			private static final long serialVersionUID = 2560703690131079830L;

			@Override
			public void actionPerformed(final ActionEvent event) {
				if (GameFrame.this.saveName == null) {
					GameFrame.this.setSaveName();
					if (GameFrame.this.saveName == null) {
						return;
					}
					if (Arrays.binarySearch(Shared.savedGames(), GameFrame.this.saveName) >= 0) {
						final var selected = JOptionPane.showConfirmDialog(null,
							Messages.getMessage("GameFrame" +
								".comfirmOverwriting", GameFrame.this.saveName));
						if (selected != JOptionPane.YES_OPTION) {
							return;// Abort saving
						}
					}
				}
				GameFrame.this.save();
			}
		});
		saveGame.setText(Messages.getString("GameFrame.save"));
		saveGame.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		jmenu.add(saveGame);
		final var tutorial = new JMenuItem();
		tutorial.setAction(new AbstractAction() {
			@Serial
			private static final long serialVersionUID = 7044880562698255228L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				InGameTutorial.showWindow();
			}
		});
		tutorial.setText(Messages.getString("GameFrame.tutorial"));
		jmenu.add(tutorial);
		final var close = new JMenuItem();
		close.setAction(new AbstractAction() {
			@Serial
			private static final long serialVersionUID = 2560703690131079830L;

			@Override
			public void actionPerformed(final ActionEvent event) {
				GameFrame.this.saveAndMaybeExit();
			}
		});
		close.setText(Messages.getString("GameFrame.close"));
		close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		jmenu.add(close);
		this.add(jmenubar);
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
		cardLayout.show(cityInfoPanel, StreamUtils.getCitiesAsStream(cities).filter(ICity::isPlayerCity).findFirst()
			.orElse(cities.getValue(0)).getName());
		this.sideBarPane = new JTabbedPane();
		this.sideBarPane.addTab(Messages.getString("GameFrame.cityInfo"), cityInfoPanel);
		final var clanInfo = new ClanInfoPanel(this.game.getPlayerClan(), this.game);
		clanInfo.init();
		this.sideBarPane.addTab(Messages.getString("GameFrame.clanInfo"), clanInfo);
		final var relationships = new RelationshipPanel(this.game);
		relationships.init();
		final var relationShipScrollPane = new JScrollPane(relationships);
		relationShipScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		this.sideBarPane.add(Messages.getString("GameFrame.relationships"), relationShipScrollPane);
		final var statsPanel = new StatisticTab();
		statsPanel.init(this.game);
		this.sideBarPane.add(Messages.getString("GameFrame.stats"), statsPanel);
		this.basePanel.add(this.sideBarPane);
	}

	private void makeLeftPanel() {
		this.gameStageScrollPane = new JScrollPane();
		final var buttonPanelScrollPane = new JScrollPane(this.buttonPanel);
		buttonPanelScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		final var panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));
		panel1.add(this.gameStageScrollPane);
		panel1.add(buttonPanelScrollPane);
		this.basePanel.add(panel1);
	}

	private void nonGUIInit() {
		this.callback = new GiftCallback();
		this.game.setPlayerGiftCallback(this.callback);
		final var cities = this.game.getCities();
		this.game.getExtraMusic().forEach(this.loopPlayer::addSong);
		ServiceLoader.load(MusicProvider.class).stream().map(Supplier::get).map(MusicProvider::getMusic)
			.flatMap(List::stream).forEach(this.loopPlayer::addSong);
		try {
			this.loopPlayer.start();
		} catch (final IllegalThreadStateException itse) {
			// Nothing serious
			Shared.LOGGER.message("Nothing serious");
			Shared.LOGGER.exception(itse);
		}
		final var connections = cities.getConnections();
		final Map<Integer, List<Integer>> drawnLines = new HashMap<>();
		connections.forEach(triple -> {
			final var first = triple.first();
			final var second = triple.second();
			if ((drawnLines.containsKey(first) && (drawnLines.get(first).contains(second)))
				|| (drawnLines.containsKey(second) && (drawnLines.get(second).contains(first)))) {
				return;
			}
			this.lines.add(
				new DashedLine(this.labels.get(cities.getValue(first)), this.labels.get(cities.getValue(second))));
		});
	}

	private boolean save() {
		try {
			Shared.save(this.saveName, this.game);
		} catch (final Exception e) {
			Shared.LOGGER.exception(e);
			JOptionPane.showMessageDialog(null, Messages.getMessage("GameFrame.savingFailed", this.saveName),
				Messages.getString("GameFrame.error"), JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	private void saveAndMaybeExit() {
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		if (this.game.onlyOneClanAlive()) {
			this.cleanup();
			this.game.exit();
			return;
		}
		if (this.saveName == null) {
			if (this.saveDialog()) {
				return;
			}
		} else {
			if (!this.save()) {
				return;
			}
		}
		this.cleanup();

	}

	private boolean saveDialog() {
		final var options = new String[] {Messages.getString("GameFrame.save"),
			Messages.getString("GameFrame.dontSave"), Messages.getString("GameFrame.cancel")};
		final var selected = JOptionPane.showOptionDialog(null, Messages.getString("GameFrame.doYouWantToSave"),
			Messages.getString("GameFrame.close"),
			JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
			null,
			options, options[2]);
		switch (selected) {
			case 0:
				this.setSaveName();
				if (this.saveName == null) {
					this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
					return true;
				}
				if (Arrays.binarySearch(Shared.savedGames(), this.saveName) >= 0) {
					final var selectedValue = JOptionPane.showConfirmDialog(null,
						Messages.getMessage("GameFrame" +
							".comfirmOverwriting", this.saveName));
					if (selectedValue != JOptionPane.YES_OPTION) {
						return true;
					}
				}
				this.save();
				break;
			case 1:// Do nothing
				break;
			case JOptionPane.CLOSED_OPTION:
			case 2:
			default:
				this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				return true;
		}
		return false;
	}

	private void setSaveName() {
		do {
			this.saveName = JOptionPane.showInputDialog(null, Messages.getString("GameFrame.pleaseGiveNameToSave"));
			if (this.saveName == null) {
				break;
			}
		} while (this.saveName.isEmpty());
	}

	@Override
	public void windowClosing(final WindowEvent e) {
		this.saveAndMaybeExit();
	}

	@Override
	public void keyTyped(final KeyEvent keyEvent) {
		if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
			this.labels.values().forEach(CityLabel::unmark);
			return;
		}
		final var ch = Character.toLowerCase(keyEvent.getKeyChar());
		var hadMatch = false;
		for (final var entry : this.labels.entrySet()) {
			if (entry.getValue().hasMouse()) {
				final var list =
					this.game.getCityKeyHandlers().getOrDefault(ch + "",
						List.of());
				list.forEach(a -> a.handle(ch + "", entry.getKey()));
				hadMatch = true;
			}
		}
		if (!hadMatch) {
			final var list =
				this.game.getKeyHandlers().getOrDefault(ch + "",
					List.of());
			list.forEach(a -> a.handleKey(ch + ""));
		}
	}

	@Override
	public void keyPressed(final KeyEvent keyEvent) {

	}

	@Override
	public void keyReleased(final KeyEvent keyEvent) {

	}
}
