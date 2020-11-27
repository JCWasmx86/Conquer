package org.jel.gui.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class LoopPlayer extends Thread implements Serializable {
	private static final long serialVersionUID = 2553365886138549665L;
	private boolean aborted;
	private final List<Sound> sounds;

	public LoopPlayer() {
		this.sounds = new ArrayList<>();
	}

	public void abort() {
		this.aborted = true;
	}

	public LoopPlayer addSong(final Sound name) {
		this.sounds.add(name);
		return this;
	}

	public LoopPlayer addSong(final String name) {
		return this.addSong(new Sound(name));
	}

	public void enable() {
		this.aborted = false;
	}

	public boolean isAborted() {
		return this.aborted;
	}

	@Override
	public void run() {
		if (this.sounds.isEmpty() || Boolean.getBoolean("conquer.nosound")) {
			return;
		}
		final var r = new Random(System.nanoTime());
		try {
			Thread.sleep(r.nextInt(500));
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
		while (true) {
			final var index = r.nextInt(this.sounds.size());
			final var gs = this.sounds.get(index);
			gs.play();
			while (gs.isPlaying()) {
				if (this.aborted) {
					gs.stop();
					break;
				}
				try {
					Thread.sleep(500);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (this.aborted) {
				if (gs.isPlaying()) {
					gs.stop();
				}
				break;
			}
			try {
				Thread.sleep(r.nextInt(50_000));
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
