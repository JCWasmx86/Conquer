package conquer.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.Serial;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import conquer.data.Shared;
import conquer.gui.utils.Sound;
import conquer.init.Initializer;

/**
 * This class provides the introduction for the game.
 */
final class Intro extends JFrame implements EmptyWindowListenerImpl, KeyListener, ActionListener {
	@Serial
	private static final long serialVersionUID = 4354833119880282433L;
	private final Sound sound;
	private final Timer timer = new Timer(Utils.getRefreshRate(), this);
	private final Random r = new Random(System.nanoTime());
	private boolean needed = true;

	private Intro() {
		this.setSize(600, 600);
		this.setResizable(false);
		this.sound = new Sound("Intro");
		this.addWindowListener(this);
		this.addKeyListener(this);
		this.timer.start();
		this.setTitle(Messages.getString("Intro.title"));
		this.setLocationByPlatform(true);
	}

	/**
	 * The entry point, the arguments are ignored
	 *
	 * @param args Ignored commandline arguments
	 */
	public static void main(final String[] args) {
		try {
			if (Boolean.getBoolean("conquer.useNativeLAF")) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
		} catch (final ClassNotFoundException | InstantiationException | IllegalAccessException
			| UnsupportedLookAndFeelException e) { // Just use the Default LaF
			// Just print the stack trace. It is no critical thing, so you shouldn't have to
			// fear anything, if an exception is thrown.
			e.printStackTrace();// The logger is not setup currently.
		}
		new Thread(() -> Initializer.INSTANCE().initialize(a -> {
			JOptionPane.showMessageDialog(null, Messages.getString("Intro.initFailed"),
				Messages.getString("Intro.error"), JOptionPane.ERROR_MESSAGE);
			System.exit(-127);
		})).start();
		final var main = new Intro();
		main.setVisible(true);
		main.createBufferStrategy(4);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource() == this.timer) {
			this.repaint();
		}
	}

	@Override
	public void keyPressed(final KeyEvent e) {
	}

	@Override
	public void keyReleased(final KeyEvent e) {

	}

	@Override
	public void keyTyped(final KeyEvent e) {
		if ((e == null) || (e.getKeyChar() == KeyEvent.VK_SPACE)) {
			this.needed = false;
			if (this.sound.isPlaying()) {
				this.sound.stop();
			}
			MainScreen.forward(this.getLocation(), true);
			this.setVisible(false);
		} else if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {// 27==ESC
			System.exit(0);
		}
	}

	@Override
	public void paint(final Graphics g) {
		for (var i = 0; i < 5; i++) {
			g.setColor(new Color(this.r.nextFloat(), this.r.nextFloat(), this.r.nextFloat()));
			final var n = 10;
			final var x = this.randomArray(n, 0, this.getWidth());
			final var y = this.randomArray(n, 0, this.getHeight());
			final var p = new Polygon(x, y, n);
			((Graphics2D) g).draw(p);
		}
	}

	private int[] randomArray(final int n, final int min, final int width) {
		final var ret = new int[n];
		for (var i = 0; i < n; i++) {
			ret[i] = this.r.nextInt((width - min) + 1) + min;
		}
		return ret;
	}

	@Override
	public void setVisible(final boolean b) {
		super.setVisible(b);
		if (!b) {
			this.sound.stop();
			this.needed = false;
			this.timer.stop();
		}
	}

	@Override
	public void windowClosed(final WindowEvent e) {
		if (this.sound.isPlaying()) {
			this.sound.stop();
		}
		if (this.needed) {
			System.exit(0);
		} else {
			MainScreen.forward(this.getLocation(), true);
		}
	}

	@Override
	public void windowClosing(final WindowEvent e) {
		if (this.needed) {
			System.exit(0);
		}
	}

	@Override
	public void windowOpened(final WindowEvent e) {
		new Thread(() -> {
			try {
				this.sound.play();
			} catch (final Exception exc) {
				exc.printStackTrace();
				Shared.LOGGER.exception(exc);
				return;
			}

			while (this.sound.isPlaying()) {
				try {
					Thread.sleep(1000);
				} catch (final InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			this.setVisible(false);
			this.needed = false;
			MainScreen.forward(this.getLocation(), true);
			this.setVisible(false);
		}).start();
	}
}
