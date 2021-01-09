package org.jel.gui.utils;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.URL;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

/**
 * A class wrapping the Sound API.
 */
public class Sound implements LineListener, Serializable {
	private static final long serialVersionUID = -2159162046590240266L;
	private final String filename;
	private boolean isPlaying;
	private transient Clip audioClip;

	/**
	 * Create new sound
	 *
	 * @param name Filename
	 */
	public Sound(final String name) {
		this.filename = name;
	}

	/**
	 * Returns whether the sound is playing
	 *
	 * @return True if the sound is playing.
	 */
	public boolean isPlaying() {
		return this.isPlaying;
	}

	/**
	 * Play the sound
	 */
	public void play() {
		try {
			var url = this.locate(this.filename);
			if (url == null) {
				url = this.locate(this.filename + ".wav");
				if (url == null) {
					url = this.locate(this.filename + ".ogg");
					if (url == null) {
						url = this.locate(this.filename + ".mp3");
					}
				}
			}
			if (url == null) {
				throw new IllegalArgumentException(new FileNotFoundException(this.filename + " wasn't found!"));
			}
			final var audioStream = AudioSystem.getAudioInputStream(url);
			final var format = audioStream.getFormat();
			final var info = new DataLine.Info(Clip.class, format);
			this.isPlaying = true;
			this.audioClip = (Clip) AudioSystem.getLine(info);
			this.audioClip.addLineListener(this);
			this.audioClip.open(audioStream);
			this.audioClip.start();
		} catch (final Exception e) {
			this.isPlaying = false;
			throw new IllegalArgumentException(e);
		}
	}

	private URL locate(String filename) {
		var url = ClassLoader.getSystemResource(filename);
		if (url == null) {
			url = ClassLoader.getSystemResource("music/" + filename);
			if (url == null) {
				url = ClassLoader.getSystemResource("sounds/" + filename);
			}
		}
		return url;
	}

	/**
	 * Stop the sound
	 */
	public void stop() {
		if (this.audioClip != null) {
			this.audioClip.stop();
			this.audioClip.close();
			this.audioClip = null;
		}
		this.isPlaying = false;
	}

	@Override
	public void update(final LineEvent event) {
		if ((event.getType() == Type.STOP) || (event.getType() == Type.CLOSE)) {
			this.audioClip.close();
			this.stop();
			this.isPlaying = false;
		}
	}

}
