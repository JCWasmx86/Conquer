package org.jel.gui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

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
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {// Just use the Default LaF
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
	}

	private final Sound sound;
	private boolean needed = true;
	private final Timer timer = new Timer(17, this);

	private Intro() {
		this.setSize(600, 600);
		this.setResizable(false);
		this.sound = new Sound("Intro.wav"); //$NON-NLS-1$
		this.addWindowListener(this);
		this.addKeyListener(this);
		this.timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.timer) {
			this.repaint();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {
		if ((e == null) || (e.getKeyChar() == 32)) {
			this.setVisible(false);
			this.needed = false;
			if (this.sound.isPlaying()) {
				this.sound.stop();
			}
			this.setVisible(false);
			MainScreen.forward(this.getLocation(), true);
		} else if (e.getKeyChar() == 27) {// 27==ESC
			System.exit(0);
		}
	}

	@Override
	public void paint(Graphics g) {
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (!b) {
			this.sound.stop();
			this.needed = false;
			this.timer.stop();
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowClosed(WindowEvent e) {
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
	public void windowClosing(WindowEvent e) {
		if (this.needed) {
			System.exit(0);
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
		new Thread(() -> {
			try {
				this.sound.play();
			} catch (final Exception exc) {
				exc.printStackTrace();
				Shared.LOGGER.exception(exc);
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
			this.setVisible(false);
			MainScreen.forward(this.getLocation(), true);
		}).start();
	}
}
