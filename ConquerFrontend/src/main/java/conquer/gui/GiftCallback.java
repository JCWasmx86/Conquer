package conquer.gui;

import java.awt.event.ActionEvent;
import java.io.Serial;
import java.util.function.DoubleConsumer;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import conquer.data.Gift;
import conquer.data.IClan;
import conquer.data.PlayerGiftCallback;
import conquer.data.strategy.StrategyObject;
import conquer.gui.utils.ImageResource;

/**
 * A hook to allow the player to receive gifts.
 */
final class GiftCallback implements PlayerGiftCallback {

	private boolean accepted;
	private JFrame jframe;

	private void accept() {
		this.accepted = true;
	}

	/**
	 * This method blocks until the player accepted or rejected.
	 */
	@Override
	public boolean acceptGift(final IClan source, final IClan destination, final Gift gift, final double oldValue,
							  final DoubleConsumer newValue, final StrategyObject strategyObject) {
		this.jframe = new JFrame();
		this.jframe.setLayout(new BoxLayout(this.jframe.getContentPane(), BoxLayout.PAGE_AXIS));
		this.jframe.setTitle(Messages.getMessage("GiftCallback.offersAGift", source.getName()));
		this.jframe.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		if (gift.getNumberOfCoins() <= 0.005) {
			final var jlabel = new JLabel(
				String.format("%.2f", gift.getNumberOfCoins()) + " " + Messages.getString("Shared.coins"));
			this.jframe.add(jlabel);
		}
		for (final var v : gift.getMap().entrySet()) {
			final double value = v.getValue();
			final var resource = v.getKey();
			if (value <= 0.005) {
				continue;
			}
			final var jlabel = new JLabel(String.format("%.2f", value) + " " + resource.getName(),
				new ImageResource(resource.getImage()), SwingConstants.LEFT);
			this.jframe.add(jlabel);
		}
		final var buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		// Accept, decline
		final var accept = new JButton(new AbstractAction() {
			@Serial
			private static final long serialVersionUID = -1684485024318434611L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				newValue.accept(oldValue + (Math.random() * 15));
				GiftCallback.this.jframe.setVisible(false);
				GiftCallback.this.jframe.dispose();
				GiftCallback.this.accept();
			}
		});
		accept.setText(Messages.getString("GiftCallback.accept"));
		final var decline = new JButton(new AbstractAction() {
			@Serial
			private static final long serialVersionUID = 5614286737761253933L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				GiftCallback.this.jframe.setVisible(false);
				GiftCallback.this.jframe.dispose();
			}
		});
		decline.setText(Messages.getString("GiftCallback.decline"));
		buttonPanel.add(accept);
		buttonPanel.add(decline);
		this.jframe.add(buttonPanel);
		this.jframe.setAlwaysOnTop(true);
		this.jframe.pack();
		this.jframe.setVisible(true);
		while (this.jframe.isDisplayable()) {
			// Wait for input
		}
		final var tmp = this.accepted;
		this.accepted = false;
		this.jframe.dispose();
		this.jframe = null;
		return tmp;
	}

	void stop() {
		if (this.jframe != null) {
			this.jframe.dispose();
		}
	}

}
