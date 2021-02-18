package conquer.data;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GiftTest {

	@Test
	void testNegativeCoins() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Gift(null, -1);
		});
	}

	@Test
	void testNegativeResources() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Gift(List.of(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, -1.0), 50);
		});
	}

	@Test
	void testNotEnoughResources() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Gift(List.of(), 50);
		});
	}

	@Test
	void testNullResources() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Gift(null, 50);
		});
	}

	@Test
	void testNan() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Gift(List.of(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.NaN), 50);
		});
	}

	@Test
	void testNegative() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Gift(List.of(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, -1.0), 50);
		});
	}

	@Test
	void testNegativeInfinity() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Gift(List.of(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.NEGATIVE_INFINITY), 50);
			new Gift(List.of(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.POSITIVE_INFINITY), 50);
		});
	}

	@Test
	void testPositiveInfinity() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Gift(List.of(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.POSITIVE_INFINITY), 50);
		});
	}
}
