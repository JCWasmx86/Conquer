module org.jel.frontend {
	requires java.desktop;
	requires org.jel.game;
	requires org.jel.game.frontend.spi;

	uses conquer.frontend.spi.InGameButton;
	uses conquer.frontend.spi.GUIMenuPlugin;
	uses conquer.frontend.spi.SettingMenuPlugin;
	uses conquer.frontend.spi.ConfigurationPanelProvider;
	uses conquer.frontend.spi.MusicProvider;
}