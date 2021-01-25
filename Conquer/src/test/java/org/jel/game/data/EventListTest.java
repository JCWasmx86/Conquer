package org.jel.game.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class EventListTest {

	@Test
	public void addNull() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			final var el = new EventList();
			el.add(null);
		});
	}

	@Test
	public void addNull2() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			final var el = new EventList();
			el.add(0, null);
		});
	}

	@Test
	public void removeNull() {
		Assertions.assertThrows(IllegalArgumentException.class, ()-> {
			final var el = new EventList();
			el.remove(null);
		});
	}
}
