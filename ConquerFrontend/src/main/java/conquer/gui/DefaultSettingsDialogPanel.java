package conquer.gui;

import java.awt.GraphicsEnvironment;
import java.util.Properties;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import conquer.data.Shared;

public class DefaultSettingsDialogPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final JCheckBox useSPI = new JCheckBox(Messages.getString("Settings.useSPI"), Shared.useSPI());
	private final JTextField jtextfield = new JTextField(null, Shared.getNetworktimeout() + "", 10);
	private final JCheckBox level1Logging = new JCheckBox(Messages.getString("Settings.level1"),
		Shared.level1Logging());
	private final JCheckBox level2Logging = new JCheckBox(Messages.getString("Settings.level2"),
		Shared.level2Logging());
	private final JSlider maximumFPS = new JSlider(5, getMaxRefreshRate(), timeToFPS(Utils.getRefreshRate()));

	private int timeToFPS(double refreshRate) {
		final var max = 1000.0;//One second
		return (int) Math.floor(max / refreshRate);
	}

	private int getMaxRefreshRate() {
		final var mode = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDisplayMode();
		final var refreshRate = mode.getRefreshRate();
		if (refreshRate == java.awt.DisplayMode.REFRESH_RATE_UNKNOWN) {
			return 144;//Maximum FPS
		} else {
			return refreshRate;
		}
	}

	void reset() {
		this.useSPI.setSelected(Shared.useSPI());
		this.level1Logging.setSelected(Shared.level1Logging());
		this.level2Logging.setSelected(Shared.level2Logging());
		this.jtextfield.setText(Shared.getNetworktimeout() + "");
		this.maximumFPS.setValue(timeToFPS(Utils.getRefreshRate()));
	}

	DefaultSettingsDialogPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(this.useSPI);
		this.add(this.level1Logging);
		this.add(this.level2Logging);
		final var networkTimeOutPanel = new JPanel();
		networkTimeOutPanel.setLayout(new BoxLayout(networkTimeOutPanel, BoxLayout.X_AXIS));
		networkTimeOutPanel.add(new JLabel(Messages.getString("Settings.timeout")));
		networkTimeOutPanel.add(this.jtextfield);
		networkTimeOutPanel.add(new JLabel("ms"));
		this.add(networkTimeOutPanel);
		final var fpsSelectionPanel = new JPanel();
		fpsSelectionPanel.setLayout(new javax.swing.BoxLayout(fpsSelectionPanel, javax.swing.BoxLayout.X_AXIS));
		final var fpsTextLabel = new JLabel(Messages.getMessage("Settings.fps", timeToFPS(Utils.getRefreshRate())));
		fpsSelectionPanel.add(fpsTextLabel);
		maximumFPS.addChangeListener(a ->
			fpsTextLabel.setText(Messages.getMessage("Settings.fps", timeToFPS(normalize(maximumFPS.getValue()))))
		);
		fpsSelectionPanel.add(this.maximumFPS);
		this.add(fpsSelectionPanel);
	}

	private int normalize(int value) {
		double refreshRate = 1000.0 / value;
		return (int) Math.floor(refreshRate);
	}

	void dump(Properties properties) {
		try {
			final var i = Integer.parseInt(this.jtextfield.getText());
			if (i < 0) {
				JOptionPane.showMessageDialog(null, Messages.getString("Settings.negativeInteger"));
			} else {
				properties.put("conquer.network.timeout", this.jtextfield.getText());
			}
		} catch (final NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, Messages.getString("Settings.badInteger"));
		}

		properties.put("conquer.usespi", this.useSPI.isSelected() + "");
		properties.put("conquer.logging.level1", this.level1Logging.isSelected() + "");
		properties.put("conquer.logging.level2", this.level2Logging.isSelected() + "");
		properties.put("conquer.frontend.rate", this.normalize(this.maximumFPS.getValue()) + "");
	}

	public void restore(Properties properties) {
		System.getProperties().putAll(properties);
		this.reset();
	}
}
