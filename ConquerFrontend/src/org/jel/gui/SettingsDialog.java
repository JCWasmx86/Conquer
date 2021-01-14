package org.jel.gui;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jel.game.data.Shared;

final class SettingsDialog extends JFrame {
	private static final long serialVersionUID = -2372114950563857591L;
	private static final SettingsDialog INSTANCE = new SettingsDialog();
	private JCheckBox useSPI = new JCheckBox(Messages.getString("Settings.useSPI"), Shared.useSPI());
	private JTextField jtextfield = new JTextField(null, Shared.getNetworktimeout() + "", 10);
	private JCheckBox level1Logging = new JCheckBox(Messages.getString("Settings.level1"), Shared.level1Logging());
	private JCheckBox level2Logging = new JCheckBox(Messages.getString("Settings.level2"), Shared.level2Logging());

	public static void showWindow() {
		SettingsDialog.INSTANCE.setVisible(true);
		INSTANCE.update();
	}

	private void update() {
		this.useSPI.setSelected(Shared.useSPI());
		this.level1Logging.setSelected(Shared.level1Logging());
		this.level2Logging.setSelected(Shared.level2Logging());
		jtextfield.setText(Shared.getNetworktimeout() + "");
	}

	private SettingsDialog() {
		this.setTitle("Settings");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.add(useSPI);
		this.add(level1Logging);
		this.add(level2Logging);
		final var networkTimeOutPanel = new JPanel();
		networkTimeOutPanel.setLayout(new BoxLayout(networkTimeOutPanel, BoxLayout.X_AXIS));
		networkTimeOutPanel.add(new JLabel(Messages.getString("Settings.timeout")));
		networkTimeOutPanel.add(jtextfield);
		networkTimeOutPanel.add(new JLabel("ms"));
		this.add(networkTimeOutPanel);
		final var buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		final var save = new JButton(Messages.getString("Settings.save"));
		final var reset = new JButton(Messages.getString("Settings.reset"));
		buttonPanel.add(save);
		buttonPanel.add(reset);
		reset.addActionListener(a -> update());
		save.addActionListener(a -> dump());
		this.add(buttonPanel);
		this.pack();
	}

	private void dump() {
		final var p = new Properties();
		try (final var in = Files.newInputStream(Paths.get(Shared.PROPERTIES_FILE))) {
			p.load(in);
		} catch (IOException e) {
			Shared.LOGGER.exception(e);
		}
		p.put("conquer.network.timeout", jtextfield.getText());
		p.put("conquer.usespi", useSPI.isSelected() + "");
		p.put("conquer.logging.level1", level1Logging.isSelected() + "");
		p.put("conquer.logging.level2", level2Logging.isSelected() + "");
		System.getProperties().putAll(p);
		try (final var fw = new FileWriter(Shared.PROPERTIES_FILE)) {
			p.store(fw, null);
		} catch (IOException e) {
			Shared.LOGGER.exception(e);
		}
	}
}
