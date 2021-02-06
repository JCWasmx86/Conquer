package org.jel.game.utils;

import java.io.IOException;
import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MagicNumberURLInputStreamTest {

	@Test
	void testNullArgument() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new MagicNumberURLInputStream(null));
	}

	@Test
	void testReadAfterMagicNumber() throws IOException {
		final var input = new MagicNumberURLInputStream(new URL("https://www.example.com"));
		final var N = 20;
		final var magic = input.getMagicNumber(N);
		for (var i = 0; i < N; i++) {
			final var cmp = input.read();
			Assertions.assertEquals(magic[i], cmp);
			System.out.println(cmp + "//" + magic[i] + "//" + ((char) cmp) + "//" + ((char) magic[i]));
		}
	}

	@Test
	void testRead() throws IOException {
		final var input = new MagicNumberURLInputStream(new URL("https://www.example.com"));
		System.out.println(new String(input.readAllBytes()));
	}
}
