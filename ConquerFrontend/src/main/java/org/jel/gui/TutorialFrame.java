package org.jel.gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jel.gui.utils.ImageResource;

/**
 * Shows credits and licensing information.
 */
final class TutorialFrame extends JFrame implements WindowListener, ActionListener {
	private static final long serialVersionUID = -4549305902050012246L;
	private volatile boolean switchToMainScreen = false;

	@Override
	public void actionPerformed(final ActionEvent e) {
		this.switchToMainScreen = true;
		MainScreen.forward(this.getLocation(), false);
		this.dispose();
	}

	/**
	 * Initialise and show this frame at the specified location
	 *
	 * @param location The location, where the frame will appear
	 */
	void init(final Point location) {
		this.setLocation(location);
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.addWindowListener(this);
		final JButton button = new RoundButton(new ImageResource("back.png"));
		this.add(button);
		button.addActionListener(this);
		final var tutorialPanel = new TutorialPanel();
		tutorialPanel.init(this);
		final var scrollPane = new JScrollPane(tutorialPanel);
		this.add(scrollPane);
		this.pack();
		this.setVisible(true);
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
		if (this.switchToMainScreen) {
			MainScreen.forward(this.getLocation(), false);
		} else {
			System.exit(0);
		}
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
