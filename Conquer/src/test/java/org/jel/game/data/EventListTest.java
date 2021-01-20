package org.jel.game.data;

import org.junit.Test;

public class EventListTest {

	@Test(expected = IllegalArgumentException.class)
	public void addNull() {
		final var el = new EventList();
		el.add(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addNull2() {
		final var el = new EventList();
		el.add(0, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void removeNull() {
		final var el = new EventList();
		el.remove(null);
	}
}
