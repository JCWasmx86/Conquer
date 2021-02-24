package conquer.gui;

import conquer.frontend.spi.SettingMenuPlugin;

import javax.swing.*;
import java.util.ServiceLoader;

final class SettingsDialog extends JFrame {
	private static final long serialVersionUID = -2372114950563857591L;
	private static final SettingsDialog INSTANCE = new SettingsDialog();
	private final DefaultSettingsDialogPanel panel = new DefaultSettingsDialogPanel();

	public static void showWindow() {
		SettingsDialog.INSTANCE.setVisible(true);
		SettingsDialog.INSTANCE.update();
	}

	private void update() {
		this.panel.update();
	}

	private SettingsDialog() {
		this.setTitle("Settings");
		final var pane = new JTabbedPane();
		pane.addTab("Default", this.panel);
		ServiceLoader.load(SettingMenuPlugin.class).forEach(a -> {
			final var icon = a.getIcon();
			final var title = a.getTitle();
			if (icon.isPresent()) {
				pane.addTab(title, icon.get(), a.getComponent());
			} else {
				pane.addTab(title, a.getComponent());
			}
		});
		this.add(pane);
		this.setVisible(true);
		this.pack();
	}
}
