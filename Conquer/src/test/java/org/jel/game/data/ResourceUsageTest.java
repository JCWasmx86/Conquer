package org.jel.game.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResourceUsageTest {
	@Test
	public void testStatsIsNull() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			new ResourceUsage(null, 0);
		});
	}

	@Test
	public void testStatsIsTooShort() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			new ResourceUsage(new double[0][0], 0);
		});
	}

	@Test
	public void testStatsArrayHasBadValue() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			new ResourceUsage(new double[][] { null }, 0.0);
		});
	}

	@Test
	public void testStatsArrayHasBadValue2() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			new ResourceUsage(new double[][] { {}, {}, {}, {}, {}, {}, {}, {}, {} }, 0.0);
		});
	}

	@Test
	public void testStatsGood() {
		new ResourceUsage(new double[][] { { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 },
				{ 1, 0 }, { 1, 0 } }, 0.0);
	}

	@Test
	public void testStatsSubarrayContainsBadValue() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			new ResourceUsage(new double[][] { { -1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 },
					{ 1, 0 }, { 1, 0 } }, 0.0);
		});
	}

	@Test
	public void testStatsSubarrayContainsBadValue2() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			new ResourceUsage(new double[][] { { Double.NaN, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 },
					{ 1, 0 }, { 1, 0 }, { 1, 0 } }, 0.0);
		});
	}

	@Test
	public void testStatsSubarrayContainsBadValue3() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			new ResourceUsage(new double[][] { { Double.POSITIVE_INFINITY, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 },
					{ 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 } }, 0.0);
		});
	}

	@Test
	public void testCoinsNegative() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			new ResourceUsage(new double[][] { { Double.POSITIVE_INFINITY, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 },
					{ 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 } }, -1);
		});
	}

	@Test
	public void testCoinsNan() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			new ResourceUsage(new double[][] { { Double.POSITIVE_INFINITY, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 },
					{ 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 } }, Double.NaN);
		});
	}

	@Test
	public void testCoinsInfinity() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			new ResourceUsage(new double[][] { { Double.POSITIVE_INFINITY, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 },
					{ 1, 0 }, { 1, 0 }, { 1, 0 }, { 1, 0 } }, Double.POSITIVE_INFINITY);
		});
	}
}
