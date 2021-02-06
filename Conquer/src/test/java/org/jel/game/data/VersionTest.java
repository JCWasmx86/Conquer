package org.jel.game.data;

import org.junit.jupiter.api.Test;

class VersionTest {
	@Test
	void testComparisonEq() {
		final var v = new Version(1, 0, 0);
		assert v.compareTo(new Version(1, 0, 0)) == 0;
	}

	@Test
	void testComparisonGt() {
		final var v = new Version(2, 0, 0);
		assert v.compareTo(new Version(1, 2, 0)) > 0;
	}

	@Test
	void testComparisonLt() {
		final var v = new Version(1, 0, 0);
		assert v.compareTo(new Version(1, 2, 0)) < 0;
	}
}
