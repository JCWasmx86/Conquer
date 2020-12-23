package org.jel.game.data;

public interface ConquerInfoReaderFactory {

	ConquerInfoReader getForFile(String file);

	byte[] getMagicNumber();
}
