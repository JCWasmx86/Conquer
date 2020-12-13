package org.jel.gui;

import java.awt.event.ActionEvent;
import java.util.function.DoubleConsumer;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.jel.game.data.Clan;
import org.jel.game.data.Gift;
import org.jel.game.data.PlayerGiftCallback;
import org.jel.game.data.strategy.StrategyObject;
import org.jel.gui.utils.ImageResource;

/**
 * A hook to allow the player to receive gifts.
 */
final class GiftCallback implements PlayerGiftCallback {

	private boolean accepted;

	private void accept() {
		this.accepted = true;
	}

	/**
	 * This method blocks until the player accepted or rejected.
	 */
	@Override
	public boolean acceptGift(Clan source, Clan destination, Gift gift, double oldValue, DoubleConsumer newValue,
			StrategyObject strategyObject) {
		final var jframe = new JFrame();
		jframe.setLayout(new BoxLayout(jframe.getContentPane(), BoxLayout.Y_AXIS));
		jframe.setTitle(String.format(Messages.getString("GiftCallback.offersAGift"), source.getName())); //$NON-NLS-1$
		jframe.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		if (gift.getNumberOfCoins() != 0) {
			final var jlabel = new JLabel(gift.getNumberOfCoins() + " " + Messages.getString("Shared.coins")); //$NON-NLS-1$
			jframe.add(jlabel);
		}
		for (final var v : gift.getMap().entrySet()) {
			final double value = v.getValue();
			final var resource = v.getKey();
			if (value == 0) {
				continue;
			}
			final var jlabel = new JLabel(value + resource.getName(), new ImageResource(resource.getImage()),
					SwingConstants.LEFT);
			jframe.add(jlabel);
		}
		final var buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		// Accept, decline
		final var accept = new JButton(new AbstractAction() {
			private static final long serialVersionUID = -1684485024318434611L;

			@Override
			public void actionPerformed(ActionEvent e) {
				newValue.accept(oldValue + (Math.random() * 15));
				jframe.setVisible(false);
				GiftCallback.this.accept();
			}
		});
		accept.setText(Messages.getString("GiftCallback.accept")); //$NON-NLS-1$
		final var decline = new JButton(new AbstractAction() {
			private static final long serialVersionUID = 5614286737761253933L;

			@Override
			public void actionPerformed(ActionEvent e) {
				jframe.setVisible(false);
			}
		});
		decline.setText(Messages.getString("GiftCallback.decline")); //$NON-NLS-1$
		buttonPanel.add(accept);
		buttonPanel.add(decline);
		jframe.setAlwaysOnTop(true);
		jframe.pack();
		jframe.setVisible(true);

		while (jframe.isVisible()) {
			// Wait for input
		}
		final var tmp = this.accepted;
		this.accepted = false;
		jframe.dispose();
		return tmp;
	}

}
