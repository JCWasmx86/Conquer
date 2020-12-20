package org.jel.gui;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.jel.game.data.City;
import org.jel.game.data.Game;
import org.jel.game.data.SavedGame;
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
	private static final String TITLE_PART = Messages.getString("GameFrame.conquerTitle") + " "; //$NON-NLS-1$
	private static final long serialVersionUID = 4456629322882679917L;
	private final transient Game game;
	private final Map<City, CityLabel> labels = new HashMap<>();
	private LoopPlayer loopPlayer = new LoopPlayer();
	private JLabel gameStage;
	private JScrollPane gameStageScrollPane;
	private JTabbedPane sideBarPane;
	private JPanel buttonPanel;
	private transient List<DashedLine> lines = new ArrayList<>();
	private transient Thread coinsLabelUpdateThread;
	private String saveName;
	private final JPanel basePanel;

	/**
	 * Create a new GameFrame with a specified game as base
	 *
	 * @param game The game to show
	 */
	GameFrame(final Game game) {
		this.game = game;
		this.addComponentListener(this);
		this.addWindowListener(this);
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.basePanel = new JPanel();
		this.basePanel.setLayout(new BoxLayout(this.basePanel, BoxLayout.X_AXIS));
	}

	GameFrame(final String saveName, final Game game) {
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

	private void cleanup() {
		EventLog.clear();
		this.loopPlayer.abort();
		MainScreen.forward(this.getLocation(), false);
		this.coinsLabelUpdateThread.stop();
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
	 * Initialises this frame.
	 */
	void init() {
		EventLog.init(this.game);
		this.initButtonPanel();
		this.makeLeftPanel();
		this.initGameStage();
		this.initSideBar();
		this.initMenubar();
		this.setVisible(true);
		this.setTitle(GameFrame.TITLE_PART + this.game.currentRound());
		this.add(this.basePanel);
		this.pack();
		this.nonGUIInit();
	}

	private void initButtonPanel() {
		this.buttonPanel = new JPanel();
		this.buttonPanel.setLayout(new FlowLayout());
		final var nextRound = new JButton(new ImageResource("hourglass.png")); //$NON-NLS-1$
		nextRound.setToolTipText(Messages.getString("GameFrame.nextRound")); //$NON-NLS-1$
		nextRound.addActionListener(a -> {
			if (this.game.isPlayersTurn()) {
				this.game.setPlayersTurn(false);
				this.game.executeActions();
			}
			this.setTitle(GameFrame.TITLE_PART + this.game.currentRound());
		});
		final var openMessages = new JButton(new ImageResource("messagebox.png")); //$NON-NLS-1$
		openMessages.setToolTipText(Messages.getString("GameFrame.openMessageBox")); //$NON-NLS-1$
		openMessages.addActionListener(a -> EventLog.showWindow());
		final var coinsLabel = new JLabel(
				Messages.getString("Shared.coins") + ": " + this.game.getCoins().get(Shared.PLAYER_CLAN)); //$NON-NLS-1$ //$NON-NLS-2$
		final var run = new JButton(Messages.getString("GameFrame.runForever")); //$NON-NLS-1$
		run.addActionListener(a -> {
			run.setEnabled(false);
			nextRound.setEnabled(false);
			new Thread(() -> {
				while (!this.game.onlyOneClanAlive()) {
					this.game.executeActions();
					this.setTitle(GameFrame.TITLE_PART + this.game.currentRound());
					this.labels.values().forEach(b -> b.actionPerformed(null));
					try {
						Thread.sleep(50);
					} catch (final InterruptedException ie) {
						Shared.LOGGER.exception(ie);
					}
				}
				run.setEnabled(true);
				nextRound.setEnabled(true);
			}).start();
		});
		this.buttonPanel.add(coinsLabel);
		this.buttonPanel.add(nextRound);
		this.buttonPanel.add(openMessages);
		this.buttonPanel.add(run);
		final var plugins = this.game.getPlugins();
		plugins.forEach(a -> {
			final var listOfButtons = a.getButtons();
			if (listOfButtons != null) {
				listOfButtons.forEach(this.buttonPanel::add);
			}
		});
		this.coinsLabelUpdateThread = new Thread(() -> {
			var flag = false;
			while (true) {
				coinsLabel.setText(Messages.getString("Shared.coins") + ": " //$NON-NLS-1$ //$NON-NLS-2$
						+ String.format("%.2f%n", this.game.getCoins().get(Shared.PLAYER_CLAN))); //$NON-NLS-1$
				try {
					Thread.sleep(20);
				} catch (final InterruptedException e) {
					Shared.LOGGER.exception(e);// Oops
				}
				if (!flag) {
					final var playerDead = this.game.isDead(Shared.PLAYER_CLAN);
					if (playerDead) {
						this.loopPlayer.abort();
						this.loopPlayer = new LoopPlayer();
						this.loopPlayer.addSong("Defeated.wav");
						this.loopPlayer.start();
						flag = true;
					} else if (this.game.onlyOneClanAlive() && !playerDead) {
						this.loopPlayer.abort();
						this.loopPlayer = new LoopPlayer();
						this.loopPlayer.addSong("Victory.wav");
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
			private static final long serialVersionUID = -2190001953669516117L;

			@Override
			public void paint(final Graphics g) {
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
								Messages.getMessage("GameFrame.comfirmOverwriting", GameFrame.this.saveName));
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
		cardLayout.show(cityInfoPanel, StreamUtils.getCitiesAsStream(cities).filter(City::isPlayerCity).findFirst()
				.orElse(cities.getValue(0)).getName());
		this.sideBarPane = new JTabbedPane();
		this.sideBarPane.addTab(Messages.getString("GameFrame.cityInfo"), cityInfoPanel); //$NON-NLS-1$
		final var clanInfo = new ClanInfoPanel(this.game.getClan(Shared.PLAYER_CLAN), this.game);
		clanInfo.init();
		this.sideBarPane.addTab(Messages.getString("GameFrame.clanInfo"), clanInfo); //$NON-NLS-1$
		final var relationships = new RelationshipPanel(this.game);
		relationships.init();
		final var relationShipScrollPane = new JScrollPane(relationships);
		relationShipScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		this.sideBarPane.add(Messages.getString("GameFrame.relationships"), relationShipScrollPane); //$NON-NLS-1$
		this.basePanel.add(this.sideBarPane);
	}

	private void makeLeftPanel() {
		this.gameStageScrollPane = new JScrollPane();
		final var buttonPanelScrollPane = new JScrollPane(this.buttonPanel);
		buttonPanelScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		final var panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
		panel1.add(this.gameStageScrollPane);
		panel1.add(buttonPanelScrollPane);
		this.basePanel.add(panel1);
	}

	private void nonGUIInit() {
		this.game.setPlayerGiftCallback(new GiftCallback());
		final var cities = this.game.getCities();
		this.game.getExtraMusic().forEach(this.loopPlayer::addSong);
		try {
			this.loopPlayer.start();
		} catch (final IllegalThreadStateException itse) {
			// Nothing serious
			Shared.LOGGER.exception(itse);
		}
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

	private boolean save() {
		try {
			new SavedGame(this.saveName).save(this.game);
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
			this.game.exit(this.game.calculateResult());
			return;
		}
		if (this.saveName != null) {
			if (!this.save()) {
				return;
			}
		} else {
			final var options = new String[] { Messages.getString("GameFrame.save"),
					Messages.getString("GameFrame.dontSave"), Messages.getString("GameFrame.cancel") };
			final var selected = JOptionPane.showOptionDialog(null, Messages.getString("GameFrame.doYouWantToSave"),
					Messages.getString("GameFrame.close"), JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, options, options[2]);
			switch (selected) {
			default:
			case JOptionPane.CLOSED_OPTION:
			case 2:
				this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				return;
			case 0:
				this.setSaveName();
				if (this.saveName == null) {
					this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
					return;
				}
				if (Arrays.binarySearch(Shared.savedGames(), this.saveName) >= 0) {
					final var selectedValue = JOptionPane.showConfirmDialog(null,
							Messages.getMessage("GameFrame.comfirmOverwriting", this.saveName));
					if (selectedValue != JOptionPane.YES_OPTION) {
						return;
					}
				}
				this.save();
				break;
			case 1:// Do nothing
				break;
			}
		}
		this.cleanup();
	}

	private void setSaveName() {
		do {
			this.saveName = JOptionPane.showInputDialog(null, Messages.getString("GameFrame.pleaseGiveNameToSave"));
			if (this.saveName == null) {
				break;
			}
		} while (this.saveName.isEmpty());
	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowActivated(final WindowEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowClosed(final WindowEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowClosing(final WindowEvent e) {
		this.saveAndMaybeExit();
	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowDeactivated(final WindowEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowDeiconified(final WindowEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowIconified(final WindowEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void windowOpened(final WindowEvent e) {

	}
}
