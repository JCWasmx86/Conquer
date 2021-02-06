package org.jel.game.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class EventListTest {

	@Test
	void addNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			final var el = new EventList();
			el.add(null);
		});
	}

	@Test
	void addNull2() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			final var el = new EventList();
			el.add(0, null);
		});
	}

	@Test
	void removeNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			final var el = new EventList();
			el.remove(null);
		});
	}
}
