package conquer.data.ri;

import conquer.data.ConquerInfoReader;
import conquer.data.ConquerInfoReaderFactory;
import conquer.data.InstalledScenario;

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
