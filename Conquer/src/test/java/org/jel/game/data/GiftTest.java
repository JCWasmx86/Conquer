package org.jel.game.data;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GiftTest {

	@Test
	public void testNegativeCoins() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			new Gift(null, -1);
		});
	}

	@Test
	public void testNegativeResources() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			new Gift(List.of(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, -1.0), 50);
		});
	}

	@Test
	public void testNotEnoughResources() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			new Gift(List.of(), 50);
		});
	}

	@Test
	public void testNullResources() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			new Gift(null, 50);
		});
	}

	@Test
	public void testNan() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			new Gift(List.of(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.NaN), 50);
		});
	}

	@Test
	public void testNegative() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			new Gift(List.of(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, -1.0), 50);
		});
	}

	@Test
	public void testNegativeInfinity() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			new Gift(List.of(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.NEGATIVE_INFINITY), 50);
			new Gift(List.of(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.POSITIVE_INFINITY), 50);
		});
	}

	@Test
	public void testPositiveInfinity() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			new Gift(List.of(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.POSITIVE_INFINITY), 50);
		});
	}
}
