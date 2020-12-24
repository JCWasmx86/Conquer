package org.jel.game.data;

import java.util.List;

import org.junit.Test;

public class GiftTest {

	@Test(expected = IllegalArgumentException.class)
	public void testNegativeCoins() {
		new Gift(null, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNegativeResources() {
		new Gift(List.of(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, -1.0), 50);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEnoughResources() {
		new Gift(List.of(), 50);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullResources() {
		new Gift(null, 50);
	}
}
