package conquer.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.Serial;
import java.util.Objects;
import java.util.ServiceLoader;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import conquer.data.Shared;
import conquer.frontend.spi.GUIMenuPlugin;
import conquer.gui.utils.LoopPlayer;

/**
 * The mainscreen that allows you to play, see the credits and the tutorial.
 * Furthermore it has a JMenu with the option to show a dialogue, that allows a
 * trivial installation/removal of plugins and strategies
 */
final class MainScreen extends JFrame implements KeyListener, EmptyWindowListenerImpl {
	@Serial
	private static final long serialVersionUID = 5279928865220785850L;
	private static boolean forwarded;
	private static boolean alreadyForwardedOnceFromIntro;
	private final LoopPlayer player;

	private MainScreen() {
		this.player = new LoopPlayer().addSong("MainScreen");
		this.addKeyListener(this);
		this.addWindowListener(this);
	}

	/**
	 * A static method to show the MainScreen. It won't show a window if another one
	 * is already open.
	 *
	 * @param location  Where the frame will appear
	 * @param fromIntro Whether the call is coming from the Intro or somewhere else.
	 */
	static void forward(final Point location, final boolean fromIntro) {
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

	private void init() {
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		final var menu = new JMenuBar();
		menu.setAlignmentX(Component.RIGHT_ALIGNMENT);
		menu.setMaximumSize(new Dimension(100, 25));
		final var settings = new JMenu(Messages.getString("MainScreen.settings"));
		final var updates = new JMenuItem(Messages.getString("MainScreen.updates"));
		updates.addActionListener(a -> System.out
			.println("TBD: Updates, current version: " + Shared.getReferenceImplementationVersion()));
		settings.add(updates);
		final var furtherSettings = new JMenuItem(Messages.getString("MainScreen.furtherSettings"));
		furtherSettings.addActionListener(a -> SettingsDialog.showWindow());
		settings.add(furtherSettings);
		final var exit = new JMenuItem(Messages.getString("MainScreen.exit"));
		exit.addActionListener(a -> System.exit(0));
		settings.add(exit);
		menu.add(settings);
		ServiceLoader.load(GUIMenuPlugin.class).stream().map(ServiceLoader.Provider::get).map(GUIMenuPlugin::getMenuItem).filter(Objects::nonNull)
			.forEach(menu::add);
		this.add(menu);
		final var panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		final var play = new JButton(Messages.getString("MainScreen.play"));
		play.addActionListener(a -> {
			MainScreen.forwarded = false;
			this.player.abort();
			final var lsf = new LevelSelectFrame();
			lsf.init(this.getLocation());
			this.dispose();
		});
		play.setAlignmentX(Component.CENTER_ALIGNMENT);
		play.setFont(play.getFont().deriveFont(50.0f));
		panel.add(play);
		ServiceLoader.load(GUIMenuPlugin.class).stream().map(ServiceLoader.Provider::get).map(GUIMenuPlugin::getButton).filter(Objects::nonNull)
			.forEach(btn -> {
				btn.setAlignmentX(Component.CENTER_ALIGNMENT);
				btn.setFont(play.getFont().deriveFont(50.0f));
				panel.add(btn);
			});
		final var credits = new JButton(Messages.getString("MainScreen.credits"));
		credits.addActionListener(a -> {
			MainScreen.forwarded = false;
			this.player.abort();
			final var cf = new CreditFrame();
			cf.init(this.getLocation());
			this.dispose();
		});
		credits.setFont(play.getFont());
		credits.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(credits);
		final var tutorial = new JButton(Messages.getString("MainScreen.tutorial"));
		tutorial.addActionListener(a -> {
			MainScreen.forwarded = false;
			this.player.abort();
			final var tutorialFrame = new TutorialFrame();
			tutorialFrame.init(this.getLocation());
			this.dispose();
		});
		tutorial.setFont(play.getFont());
		tutorial.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(tutorial);
		this.add(panel);
	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void keyPressed(final KeyEvent e) {

	}

	/**
	 * Shouldn't be used
	 */

	@Override
	public void keyReleased(final KeyEvent e) {

	}

	/**
	 * Shouldn't be used
	 */
	@Override
	public void keyTyped(final KeyEvent e) {
	}

	/**
	 * If the window is set visible, the music will start to play, else the music
	 * will stop.
	 */
	@Override
	public void setVisible(final boolean b) {
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
	public void windowClosing(final WindowEvent e) {
		System.exit(0);
	}
}
