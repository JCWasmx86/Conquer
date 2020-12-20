package org.jel.game.data;

public interface ConquerSaver {
	ConquerInfo restore() throws Exception;

	void save(ConquerInfo info) throws Exception;
}
