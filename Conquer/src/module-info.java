/**
 * The module exporting all required packages.
 */
module org.jel.game {
	requires transitive java.desktop;

	exports org.jel.game.init;
	exports org.jel.game.data;
	exports org.jel.game.utils;
	exports org.jel.game.plugins;
	exports org.jel.game.data.strategy;
	exports org.jel.game.messages;
}