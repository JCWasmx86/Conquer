package org.jel.game.data;

import org.junit.Test;

public class VersionTest {
	@Test
	public void testComparisonEq() {
		final Version v = new Version(1, 0, 0);
		assert v.compareTo(new Version(1, 0, 0)) == 0;
	}

	@Test
	public void testComparisonGt() {
		final Version v = new Version(2, 0, 0);
		assert v.compareTo(new Version(1, 2, 0)) > 0;
	}

	@Test
	public void testComparisonLt() {
		final Version v = new Version(1, 0, 0);
		assert v.compareTo(new Version(1, 2, 0)) < 0;
	}
}