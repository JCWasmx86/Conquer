package conquer.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResourceUsageTest {
	@Test
	void testStatsIsNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new conquer.data.ResourceUsage(null, 0));
	}

	@Test
	void testStatsIsTooShort() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new conquer.data.ResourceUsage(new double[0][0], 0));
	}

	@Test
	void testStatsArrayHasBadValue() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new conquer.data.ResourceUsage(new double[][] {null}, 0.0));
	}

	@Test
	void testStatsArrayHasBadValue2() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new conquer.data.ResourceUsage(new double[][] {{}, {}, {}, {}, {}, {}, {}, {}, {}}, 0.0));
	}

	@Test
	void testStatsGood() {
		new ResourceUsage(new double[][] {{1, 0}, {1, 0}, {1, 0}, {1, 0}, {1, 0}, {1, 0}, {1, 0},
			{1, 0}, {1, 0}}, 0.0);
	}

	@Test
	void testStatsSubarrayContainsBadValue() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new conquer.data.ResourceUsage(new double[][] {{-1, 0}, {1, 0}, {1, 0}, {1, 0}, {1, 0}, {1, 0}, {1, 0},
			{1, 0}, {1, 0}}, 0.0));
	}

	@Test
	void testStatsSubarrayContainsBadValue2() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new conquer.data.ResourceUsage(new double[][] {{Double.NaN, 0}, {1, 0}, {1, 0}, {1, 0}, {1, 0}, {1, 0},
			{1, 0}, {1, 0}, {1, 0}}, 0.0));
	}

	@Test
	void testStatsSubarrayContainsBadValue3() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new conquer.data.ResourceUsage(new double[][] {{Double.POSITIVE_INFINITY, 0}, {1, 0}, {1, 0}, {1, 0}, {1, 0},
			{1, 0}, {1, 0}, {1, 0}, {1, 0}}, 0.0));
	}

	@Test
	void testCoinsNegative() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new conquer.data.ResourceUsage(new double[][] {{Double.POSITIVE_INFINITY, 0}, {1, 0}, {1, 0}, {1, 0}, {1, 0},
			{1, 0}, {1, 0}, {1, 0}, {1, 0}}, -1));
	}

	@Test
	void testCoinsNan() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new conquer.data.ResourceUsage(new double[][] {{Double.POSITIVE_INFINITY, 0}, {1, 0}, {1, 0}, {1, 0}, {1, 0},
			{1, 0}, {1, 0}, {1, 0}, {1, 0}}, Double.NaN));
	}

	@Test
	void testCoinsInfinity() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new conquer.data.ResourceUsage(new double[][] {{Double.POSITIVE_INFINITY, 0}, {1, 0}, {1, 0}, {1, 0}, {1, 0},
			{1, 0}, {1, 0}, {1, 0}, {1, 0}}, Double.POSITIVE_INFINITY));
	}
}
