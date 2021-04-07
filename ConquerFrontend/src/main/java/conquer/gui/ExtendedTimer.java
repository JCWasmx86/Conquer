package conquer.gui;

import java.awt.event.ActionListener;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

/**
 * This class wraps {@code javax.swing.Timer} and adds it to a list which can be
 * cleared with {@code stopAll}
 */
final class ExtendedTimer extends Timer {
	@Serial
	private static final long serialVersionUID = -6167424556364793575L;
	private static final List<Timer> registeredTimers = new ArrayList<>();

	ExtendedTimer(final int delay, final ActionListener listener) {
		super(delay, listener);
		ExtendedTimer.registeredTimers.add(this);
	}

	/**
	 * Stop all registered timers and clear the list of timers.
	 */
	static void stopAll() {
		ExtendedTimer.registeredTimers.forEach(Timer::stop);
		ExtendedTimer.registeredTimers.clear();
	}
}
