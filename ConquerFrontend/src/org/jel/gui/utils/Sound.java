package org.jel.gui.utils;

import java.io.FileNotFoundException;
import java.io.Serializable;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

public class Sound implements LineListener, Serializable {
	private static final long serialVersionUID = -2159162046590240266L;
	private final String filename;
	private boolean isPlaying;
	private transient Clip audioClip;

	public Sound(String name) {
		this.filename = name;
	}

	public boolean isPlaying() {
		return this.isPlaying;
	}

	public void play() {
		try {
			var url = ClassLoader.getSystemResource(this.filename);
			if (url == null) {
				url = ClassLoader.getSystemResource("music/" + this.filename);
				if (url == null) {
					url = ClassLoader.getSystemResource("sounds/" + this.filename);
				}
			}
			if (url == null) {
				throw new IllegalArgumentException(new FileNotFoundException(this.filename + " wasn't found!"));
			}
			final var audioStream = AudioSystem.getAudioInputStream(url);
			final var format = audioStream.getFormat();
			final var info = new DataLine.Info(Clip.class, format);
			this.audioClip = (Clip) AudioSystem.getLine(info);
			this.audioClip.addLineListener(this);
			this.audioClip.open(audioStream);
			this.audioClip.start();
			this.isPlaying = true;
		} catch (final Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void stop() {
		if (this.audioClip != null) {
			this.audioClip.stop();
			this.audioClip.close();
			this.audioClip = null;
		}
		this.isPlaying = false;
	}

	@Override
	public void update(LineEvent event) {
		if ((event.getType() == Type.STOP) || (event.getType() == Type.CLOSE)) {
			this.audioClip.close();
			this.stop();
			this.isPlaying = false;
		}
	}

}
