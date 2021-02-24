package conquer.gui;

import conquer.data.Shared;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class DefaultSettingsDialogPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final JCheckBox useSPI = new JCheckBox(Messages.getString("Settings.useSPI"), Shared.useSPI());
	private final JTextField jtextfield = new JTextField(null, Shared.getNetworktimeout() + "", 10);
	private final JCheckBox level1Logging = new JCheckBox(Messages.getString("Settings.level1"),
		Shared.level1Logging());
	private final JCheckBox level2Logging = new JCheckBox(Messages.getString("Settings.level2"),
		Shared.level2Logging());

	void update() {
		this.useSPI.setSelected(Shared.useSPI());
		this.level1Logging.setSelected(Shared.level1Logging());
		this.level2Logging.setSelected(Shared.level2Logging());
		this.jtextfield.setText(Shared.getNetworktimeout() + "");
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
		final var buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		final var save = new JButton(Messages.getString("Settings.save"));
		final var reset = new JButton(Messages.getString("Settings.reset"));
		buttonPanel.add(save);
		buttonPanel.add(reset);
		reset.addActionListener(a -> this.update());
		save.addActionListener(a -> this.dump());
		this.add(buttonPanel);
	}

	private void dump() {
		try {
			final var i = Integer.parseInt(this.jtextfield.getText());
			if (i < 0) {
				JOptionPane.showMessageDialog(null, Messages.getString("Settings.negativeInteger"));
				return;
			}
		} catch (final NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, Messages.getString("Settings.badInteger"));
			return;
		}
		final var p = new Properties();
		try (final var in = Files.newInputStream(Paths.get(Shared.PROPERTIES_FILE))) {
			p.load(in);
		} catch (final IOException e) {
			Shared.LOGGER.exception(e);
		}
		p.put("conquer.network.timeout", this.jtextfield.getText());
		p.put("conquer.usespi", this.useSPI.isSelected() + "");
		p.put("conquer.logging.level1", this.level1Logging.isSelected() + "");
		p.put("conquer.logging.level2", this.level2Logging.isSelected() + "");
		System.getProperties().putAll(p);
		try (final var fw = new FileWriter(Shared.PROPERTIES_FILE)) {
			p.store(fw, null);
		} catch (final IOException e) {
			Shared.LOGGER.exception(e);
		}
	}
}
