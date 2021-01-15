package org.jel.game.data;

import org.junit.Test;

public class ResourceUsageTest {
	@Test(expected = IllegalArgumentException.class)
	public void testStatsIsNull() {
		new ResourceUsage(null, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStatsIsTooShort() {
		new ResourceUsage(new double[0][0], 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStatsArrayHasBadValue() {
		new ResourceUsage(new double[][] { null }, 0.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStatsArrayHasBadValue2() {
		new ResourceUsage(new double[][] { {}, {}, {}, {}, {}, {}, {}, {}, {} }, 0.0);
	}

	@Test
	public void testStatsGood() {
		new ResourceUsage(new double[][] { { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 },
				{ 1, 0 }, { 1, 0 } }, 0.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStatsSubarrayContainsBadValue() {
		new ResourceUsage(new double[][] { { -1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 },
				{ 1, 0 }, { 1, 0 } }, 0.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStatsSubarrayContainsBadValue2() {
		new ResourceUsage(new double[][] { { Double.NaN, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 },
				{ 1, 0 }, { 1, 0 }, { 1, 0 } }, 0.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStatsSubarrayContainsBadValue3() {
		new ResourceUsage(new double[][] { { Double.POSITIVE_INFINITY, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 },
				{ 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 } }, 0.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCoinsNegative() {
		new ResourceUsage(new double[][] { { Double.POSITIVE_INFINITY, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 },
				{ 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 } }, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCoinsNan() {
		new ResourceUsage(new double[][] { { Double.POSITIVE_INFINITY, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 },
				{ 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 } }, Double.NaN);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCoinsInfinity() {
		new ResourceUsage(new double[][] { { Double.POSITIVE_INFINITY, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 },
				{ 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 } }, Double.POSITIVE_INFINITY);
	}
}
