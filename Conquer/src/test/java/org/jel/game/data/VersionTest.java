package org.jel.game.data;

import org.junit.Test;

public class VersionTest {
	@Test
	public void testComparisonEq() {
		final var v = new Version(1, 0, 0);
		assert v.compareTo(new Version(1, 0, 0)) == 0;
	}

	@Test
	public void testComparisonGt() {
		final var v = new Version(2, 0, 0);
		assert v.compareTo(new Version(1, 2, 0)) > 0;
	}

	@Test
	public void testComparisonLt() {
		final var v = new Version(1, 0, 0);
		assert v.compareTo(new Version(1, 2, 0)) < 0;
	}
}