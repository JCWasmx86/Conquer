package org.jel.gui;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

final class ExtendedTimer extends Timer {
	private static final long serialVersionUID = -6167424556364793575L;
	private static List<Timer> registeredTimers = new ArrayList<>();

	ExtendedTimer(int delay, ActionListener listener) {
		super(delay, listener);
		registeredTimers.add(this);
	}

	static synchronized void stopAll() {
		registeredTimers.forEach(Timer::stop);
		registeredTimers.clear();
	}
}
