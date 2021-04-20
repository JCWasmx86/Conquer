module conquer.frontend {
	exports conquer.gui.debug;

	requires java.desktop;
	requires conquer;
	requires conquer.frontend.spi;

	uses conquer.frontend.spi.InGameButton;
	uses conquer.frontend.spi.GUIMenuPlugin;
	uses conquer.frontend.spi.SettingMenuPlugin;
	uses conquer.frontend.spi.ConfigurationPanelProvider;
	uses conquer.frontend.spi.MusicProvider;
}
