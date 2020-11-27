package org.jel.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.jel.game.data.Shared;
import org.jel.gui.utils.LoopPlayer;

final class MainScreen extends JFrame implements KeyListener, WindowListener {
	private static final long serialVersionUID = 5279928865220785850L;
	private static boolean forwarded = false;
	private static boolean alreadyForwardedOnceFromIntro;

	static void forward(Point location, int width, int height, boolean fromIntro) {
		if (MainScreen.forwarded || (MainScreen.alreadyForwardedOnceFromIntro && fromIntro)) {
			return;
		}
		if (fromIntro) {
			MainScreen.alreadyForwardedOnceFromIntro = true;
		}
		MainScreen.forwarded = true;
		final var mw = new MainScreen();
		mw.setLocation(location);
		mw.setVisible(true);
		mw.init();
		mw.pack();
	}

	private final LoopPlayer player;
	private JButton play, credits, tutorial;

	private JMenuBar menu;

	MainScreen() {
		this.player = new LoopPlayer().addSong("MainScreen.wav");
		this.addKeyListener(this);
		this.addWindowListener(this);
	}

	private void init() {
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.menu = new JMenuBar();
		this.menu.setAlignmentX(Component.RIGHT_ALIGNMENT);
		this.menu.setMaximumSize(new Dimension(100, 25));
		final var settings = new JMenu("Settings");
		final var strategiesAndPlugins = new JMenuItem("Strategies and Plugins");
		strategiesAndPlugins.addActionListener(a -> StrategiesAndPluginsDialog.showWindow());
		settings.add(strategiesAndPlugins);
		final var exit = new JMenuItem("Exit");
		exit.addActionListener(a -> System.exit(0));
		settings.add(exit);
		this.menu.add(settings);
		this.add(this.menu);
		final var panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		this.play = new JButton("Play");
		this.play.addActionListener(a -> {
			MainScreen.forwarded = false;
			final var lsf = new LevelSelectFrame();
			lsf.init(800, 600, this.getLocation());
			this.dispose();
			this.player.abort();
		});
		this.play.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.play.setFont(this.play.getFont().deriveFont(50.0f));
		panel.add(this.play);
		this.credits = new JButton("Credits");
		this.credits.addActionListener(a -> {
			MainScreen.forwarded = false;
			final var cf = new CreditFrame();
			cf.init(this.getLocation());
			this.dispose();
			this.player.abort();
		});
		this.credits.setFont(this.play.getFont().deriveFont(50.0f));
		this.credits.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(this.credits);
		this.tutorial = new JButton("Tutorial");
		this.tutorial.addActionListener(a -> System.out.println("Tutorial"));
		this.tutorial.setFont(this.play.getFont().deriveFont(50.0f));
		this.tutorial.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(this.tutorial);
		this.add(panel);
	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (b) {
			try {
				this.player.start();
			} catch (final Exception e) {
				Shared.LOGGER.exception(e);
			}
		} else {
			this.player.abort();
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		System.exit(0);
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
