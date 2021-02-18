package conquer.gui.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * A class that plays a list of sounds in a loop
 */
public final class LoopPlayer extends Thread implements Serializable {
	private static final long serialVersionUID = 2553365886138549665L;
	private boolean aborted;
	private List<Sound> sounds;

	/**
	 * Construct new LoopPlayer
	 */
	public LoopPlayer() {
		this.sounds = new ArrayList<>();
	}

	/**
	 * Stop the player now.
	 */
	public void abort() {
		this.aborted = true;
	}

	/**
	 * Add a new song
	 *
	 * @param name The sound to add
	 * @return {@code this}
	 */
	public LoopPlayer addSong(final Sound sound) {
		this.sounds.add(sound);
		return this;
	}

	/**
	 * Add a new song
	 *
	 * @param name The name of the sound to add
	 * @return {@code this}
	 */
	public LoopPlayer addSong(final String name) {
		return this.addSong(new Sound(name));
	}

	/**
	 * Enable it after calling {@code aborted}
	 */
	public void enable() {
		this.aborted = false;
	}

	/**
	 * Returns whether this player is aborted
	 *
	 * @return True if the player is aborted.
	 */
	public boolean isAborted() {
		return this.aborted;
	}

	/**
	 * Plays the sound.
	 */
	@Override
	public void run() {
		if (this.sounds.isEmpty() || Boolean.getBoolean("conquer.nosound")) {
			return;
		}
		this.sounds = this.sounds.stream().distinct().collect(Collectors.toList());
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
