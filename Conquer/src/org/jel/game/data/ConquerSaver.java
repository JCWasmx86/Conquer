package org.jel.game.data;

public interface ConquerSaver {
	void save(ConquerInfo info) throws Exception;
	ConquerInfo restore() throws Exception;
}
