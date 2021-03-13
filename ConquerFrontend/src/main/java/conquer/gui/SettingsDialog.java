package conquer.gui;

import conquer.data.Shared;
import conquer.frontend.spi.SettingMenuPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

final class SettingsDialog extends JFrame {
	private static final long serialVersionUID = -2372114950563857591L;
	private static final SettingsDialog INSTANCE = new SettingsDialog();
	private final DefaultSettingsDialogPanel panel = new DefaultSettingsDialogPanel();
	private final List<SettingMenuPlugin> plugins = new ArrayList<>();
	private java.util.Properties properties;

	public static void showWindow() {
		SettingsDialog.INSTANCE.setVisible(true);
		SettingsDialog.INSTANCE.update();
	}

	private void update() {
		this.properties = this.getProperties();
		//Reset
		this.plugins.forEach(SettingMenuPlugin::reset);
		this.panel.reset();
	}

	private Properties getProperties() {
		try (final var in = Files.newInputStream(Paths.get(new File(Shared.PROPERTIES_FILE).toURI()), StandardOpenOption.CREATE)) {
			final var p = new Properties();
			p.load(in);
			return p;
		} catch (final IOException e) {
			Shared.LOGGER.exception(e);
		}
		return new Properties();
	}

	private SettingsDialog() {
		this.setTitle("Settings");
		this.properties = this.getProperties();
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		final var pane = new JTabbedPane();
		pane.addTab("Default", this.panel);
		this.panel.restore(this.properties);
		ServiceLoader.load(SettingMenuPlugin.class).forEach(a -> {
			this.plugins.add(a);
			a.restore(this.properties);
			final var icon = a.getIcon();
			final var title = a.getTitle();
			if (icon.isPresent()) {
				pane.addTab(title, icon.get(), a.getComponent());
			} else {
				pane.addTab(title, a.getComponent());
			}
		});
		this.add(pane);
		final var buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		final var save = new JButton(Messages.getString("Settings.save"));
		final var reset = new JButton(Messages.getString("Settings.reset"));
		buttonPanel.add(save);
		buttonPanel.add(reset);
		reset.addActionListener(a -> {
			this.plugins.forEach(SettingMenuPlugin::reset);
			this.panel.reset();
		});
		save.addActionListener(a -> this.dump());
		this.add(buttonPanel);
		this.setVisible(true);
		this.pack();
	}

	private void dump() {
		this.panel.dump(this.properties);
		this.plugins.forEach(a -> a.save(this.properties));
		System.getProperties().putAll(this.properties);
		try (final var out = Files.newOutputStream(Paths.get(new File(Shared.PROPERTIES_FILE).toURI()), StandardOpenOption.WRITE)) {
			this.properties.store(out, "Properties for conquer");
		} catch (final IOException e) {
			Shared.LOGGER.exception(e);
		}
		this.properties = this.getProperties();
	}
}
