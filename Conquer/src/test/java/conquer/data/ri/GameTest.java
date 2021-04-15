package conquer.data.ri;

import java.io.IOException;

import conquer.data.SPIContextBuilder;
import conquer.init.ExtendedOutputStream;
import conquer.init.Initializer;
import conquer.init.Installer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GameTest {
	@Test
	void testForException() {
		new Installer(a -> a.length - 2, new ExtendedOutputStream() {
			@Override
			public void write(final int b) throws IOException {
				System.out.write(b);
			}

			@Override
			public void write(final String s) throws IOException {
				System.out.println(s);
			}
		}, Assertions::fail).run();
		Initializer.INSTANCE().initialize(a -> {
			a.printStackTrace();
			throw new RuntimeException(a);
		});
		final var info = new SPIContextBuilder().buildContext();
		var roundCounter = 0;
		while (roundCounter < 500_000) {
			for (final var scenario : info.getInstalledMaps()) {
				final var conquerInfo = info.loadInfo(scenario);
				conquerInfo.addContext(info);
				conquerInfo.setErrorHandler(Assertions::fail);
				conquerInfo.setPlayerGiftCallback((a, b, c, d, e, f) -> false);
				conquerInfo.init();
				while (!conquerInfo.onlyOneClanAlive() && (roundCounter < 500_000)) {
					conquerInfo.executeActions();
					roundCounter++;
				}
			}
		}
	}
}
