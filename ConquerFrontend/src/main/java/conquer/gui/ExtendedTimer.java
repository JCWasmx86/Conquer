package conquer.gui;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

/**
 * This class wraps {@code javax.swing.Timer} and adds it to a list which can be
 * cleared with {@code stopAll}
 */
final class ExtendedTimer extends Timer {
	private static final long serialVersionUID = -6167424556364793575L;
	private static List<Timer> registeredTimers = new ArrayList<>();

	/**
	 * Stop all registered timers and clear the list of timers.
	 */
	static synchronized void stopAll() {
		ExtendedTimer.registeredTimers.forEach(Timer::stop);
		ExtendedTimer.registeredTimers.clear();
	}

	ExtendedTimer(final int delay, final ActionListener listener) {
		super(delay, listener);
		ExtendedTimer.registeredTimers.add(this);
	}
}
