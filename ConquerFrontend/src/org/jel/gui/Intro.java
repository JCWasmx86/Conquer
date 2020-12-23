package org.jel.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jel.game.data.Shared;
import org.jel.game.init.Initializer;
import org.jel.game.init.Installer;
import org.jel.gui.utils.Sound;

/**
 * This class provides the introduction for the game. It is currently just a
 * black screen, anything else will be done soon.
 */
public final class Intro extends JFrame implements WindowListener, KeyListener, ActionListener {
	private static final long serialVersionUID = 4354833119880282433L;

	/**
	 * The entry point, the arguments are ignored
	 *
	 * @param args Ignored commandline arguments
	 */
	public static void main(final String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {// Just use the Default LaF
			// Just print the stack trace. It is no critical thing, so you shouldn't have to
			// fear anything, if an exception is thrown.
			e.printStackTrace();
		}
		new Thread(() -> {
			final var installerWindow = new InstallerWindow();
			new Installer(
					options -> JOptionPane.showOptionDialog(null, Messages.getString("Intro.selectTypeOfInstallation"), //$NON-NLS-1$
							Messages.getString("Intro.installation"), JOptionPane.YES_NO_CANCEL_OPTION, //$NON-NLS-1$
							JOptionPane.QUESTION_MESSAGE, null, options, options[1]),
					installerWindow, exception -> {
					}).run();
			Initializer.INSTANCE().initialize(a -> {
				JOptionPane.showMessageDialog(null, Messages.getString("Intro.initFailed"), //$NON-NLS-1$
						Messages.getString("Intro.error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
				System.exit(-127);
			});
			installerWindow.dispose();
		}).start();
		final var main = new Intro();
		main.setLocationByPlatform(true);
		main.setVisible(true);
		main.createBufferStrategy(4);
	}

	private final Sound sound;
	private boolean needed = true;
	private final Timer timer = new Timer(17, this);

	private final Random r = new Random(System.nanoTime());

	private Intro() {
		this.setSize(600, 600);
		this.setResizable(false);
		this.sound = new Sound("Intro.wav"); //$NON-NLS-1$
		this.addWindowListener(this);
		this.addKeyListener(this);
		this.timer.start();
		this.setTitle(Messages.getString("Intro.title"));
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
	public void windowActivated(final WindowEvent e) {

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
	public void windowDeactivated(final WindowEvent e) {

	}

	@Override
	public void windowDeiconified(final WindowEvent e) {

	}

	@Override
	public void windowIconified(final WindowEvent e) {

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
