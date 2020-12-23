package org.jel.game.data.ri;

import org.jel.game.data.ConquerInfoReader;
import org.jel.game.data.ConquerInfoReaderFactory;

public final class ScenarioFileReaderFactory implements ConquerInfoReaderFactory {

	@Override
	public ConquerInfoReader getForFile(final String file) {
		return new ScenarioFileReader(file);
	}

	@Override
	public byte[] getMagicNumber() {
		return new byte[] { (byte) 0xaa, 0x55 };
	}

}
