module org.jel.frontend {
	requires java.desktop;
	requires org.jel.game;
	requires org.jel.game.frontend.spi;

	uses conquer.frontend.spi.GUIMenuPlugin;
}