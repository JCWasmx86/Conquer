package org.jel.gui;

import java.util.ServiceLoader;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import conquer.frontend.spi.SettingMenuPlugin;

final class SettingsDialog extends JFrame {
	private static final long serialVersionUID = -2372114950563857591L;
	private static final SettingsDialog INSTANCE = new SettingsDialog();
	private final DefaultSettingsDialogPanel panel = new DefaultSettingsDialogPanel();

	public static void showWindow() {
		SettingsDialog.INSTANCE.setVisible(true);
		SettingsDialog.INSTANCE.update();
	}

	private void update() {
		panel.update();
	}

	private SettingsDialog() {
		this.setTitle("Settings");
		JTabbedPane pane = new JTabbedPane();
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
