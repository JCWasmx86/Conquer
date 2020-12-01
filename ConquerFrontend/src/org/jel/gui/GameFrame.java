package org.jel.gui;

import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
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
import javax.swing.Timer;
import javax.swing.WindowConstants;

import org.jel.game.data.City;
import org.jel.game.data.Game;
import org.jel.game.data.Shared;
import org.jel.game.data.StreamUtils;
import org.jel.gui.utils.ImageResource;
import org.jel.gui.utils.LoopPlayer;

final class GameFrame extends JFrame implements WindowListener {
	private static final long serialVersionUID = 4456629322882679917L;
	private final transient Game game;
	private final Map<City, CityLabel> labels = new HashMap<>();
	private final LoopPlayer loopPlayer = new LoopPlayer();

	GameFrame(Game game) {
		this.game = game;
	}

	void init() {
		final var gameStage = new JLabel(new ImageIcon(this.game.getBackground()));
		final var cityInfoPanel = new JPanel();
		final var ll = new CardLayout();
		cityInfoPanel.setLayout(ll);
		gameStage.setLayout(null);
		EventLog.init(this.game);
		StreamUtils.getCitiesAsStream(this.game.getCities()).forEach(a -> {
			final var cl = new CityLabel(a, this.labels, b -> ll.show(cityInfoPanel, b.getName()));
			final var cip = new CityInfoPanel(a);
			cip.init();
			cityInfoPanel.add(cip, a.getName());
			ll.addLayoutComponent(cip, a.getName());
			gameStage.add(cl);
			this.labels.put(a, cl);
		});
		this.game.setPlayerGiftCallback(new GiftCallback());
		ll.show(cityInfoPanel, StreamUtils.getCitiesAsStream(this.game.getCities()).filter(a -> a.getClan() == 0)
				.findFirst().orElseThrow().getName());
		gameStage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					GameFrame.this.labels.values().forEach(CityLabel::unmark);
				}
			}
		});
		gameStage.setFocusable(true);
		this.addWindowListener(this);
		final var jsp = new JScrollPane();
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		jsp.setViewportView(gameStage);
		final var buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		this.initPanel(buttonPanel);
		final var scrollPane = new JScrollPane(buttonPanel);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		final var panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
		panel1.add(jsp);
		panel1.add(scrollPane);
		this.add(panel1);
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
		final var jtabbedPane = new JTabbedPane();
		jtabbedPane.addTab("City info", cityInfoPanel);
		final var clanInfo = new ClanInfoPanel(this.game.getClan(0), this.game);
		clanInfo.init();
		jtabbedPane.addTab("Clan info", clanInfo);
		final var relationships = new RelationshipPanel(this.game);
		relationships.init();
		final var jspR = new JScrollPane(relationships);
		jspR.getVerticalScrollBar().setUnitIncrement(16);
		jtabbedPane.add("Relationships", jspR);
		this.add(jtabbedPane);
		this.setVisible(true);
		this.setTitle("Conquer: Round " + this.game.currentRound());
		this.pack();
		this.game.getExtraMusic().forEach(this.loopPlayer::addSong);
		this.loopPlayer.start();
		final var timer = new Timer(17, a -> {
			// Adjust in x-direction
			if (!buttonPanel.isShowing() || !jsp.isShowing()) {
				return;
			}
			final var diff = (buttonPanel.getLocationOnScreen().y
					- (jsp.getLocationOnScreen().y + this.game.getBackground().getHeight(null))) / 2;
			if (diff <= 0) {
				return;
			}
			this.labels.values().forEach(b -> {
				final var image = b.getCity().getImage();
				b.setBounds(b.getLocation().x, b.getCity().getY() + diff, image.getWidth(null),
						image.getHeight(null) + 12);
				b.repaint();
			});
			gameStage.repaint();
		});
		timer.addActionListener(a -> {
			// Adjust in y-direction
			if (!jtabbedPane.isShowing() || !jsp.isShowing() || !gameStage.isShowing()) {
				return;
			}
			final var diff = (jtabbedPane.getLocationOnScreen().x
					- (jsp.getLocationOnScreen().x + this.game.getBackground().getWidth(null)));
			final var diff2 = gameStage.getLocationOnScreen().x - jsp.getLocationOnScreen().x;
			if ((diff <= 0) || (diff2 <= 0)) {
				return;
			}
			this.labels.values().forEach(b -> {
				final var image = b.getCity().getImage();
				b.setBounds(b.getCity().getX() + ((diff + diff2) / 2), b.getCity().getY(), image.getWidth(null),
						image.getHeight(null) + 12);
				b.repaint();
			});
			gameStage.repaint();
		});
		gameStage.setFocusable(true);
		timer.start();
	}

	private void initPanel(JPanel buttonPanel) {
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
		buttonPanel.add(coinsLabel);
		buttonPanel.add(nextRound);
		buttonPanel.add(openMessages);
		buttonPanel.add(run);
		plugins.forEach(a -> {
			final var listOfButtons = a.getButtons();
			if (listOfButtons != null) {
				listOfButtons.forEach(buttonPanel::add);
			}
		});
		new Thread(() -> {
			while (true) {
				coinsLabel.setText("Coins: " + String.format("%.2f%n", this.game.getCoins().get(0)));
				try {
					Thread.sleep(20);
				} catch (final InterruptedException e) {
					Shared.LOGGER.exception(e);// Oops
				}
			}
		}).start();
	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (!this.game.onlyOneClanAlive()) {
			this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			final var status = JOptionPane.showConfirmDialog(null, "Do you really want to quit?", "Exit",
					JOptionPane.YES_NO_OPTION);
			if (status == JOptionPane.YES_OPTION) {
				EventLog.clear();
				this.loopPlayer.abort();
				MainScreen.forward(this.getLocation(), 800, 600, false);
				this.dispose();
			} else {
				this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			}
		} else {
			EventLog.clear();
			this.loopPlayer.abort();
			MainScreen.forward(this.getLocation(), 800, 600, false);
			this.dispose();
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

}
