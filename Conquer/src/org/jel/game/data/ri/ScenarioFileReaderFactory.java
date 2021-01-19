package org.jel.game.data.ri;

import org.jel.game.data.ConquerInfoReader;
import org.jel.game.data.ConquerInfoReaderFactory;
import org.jel.game.data.InstalledScenario;

public final class ScenarioFileReaderFactory implements ConquerInfoReaderFactory {
	@Override
	public ConquerInfoReader getForFile(final InstalledScenario is) {
		return new ScenarioFileReader(is);
	}

	@Override
	public byte[] getMagicNumber() {
		return new byte[] { (byte) 0xaa, 0x55 };
	}

}
