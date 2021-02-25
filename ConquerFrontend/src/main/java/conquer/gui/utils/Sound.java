package conquer.gui.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.URL;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
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
			}
			if (url == null) {
				url = this.locate(this.filename + ".aiff");
			}
			if (url == null) {
				url = this.locate(this.filename + ".au");
			}
			if (url == null) {
				url = this.locate(this.filename + ".ogg");
			}
			if (url == null) {
				url = this.locate(this.filename + ".mp3");
			}
			if ((url == null) && !new File(this.filename).exists()) {
				throw new RuntimeException(new FileNotFoundException(this.filename));
			}
			final var audioStream = url == null ? AudioSystem.getAudioInputStream(new File(this.filename))
				: AudioSystem.getAudioInputStream(url);
			final var format = audioStream.getFormat();
			final var frameRate = 44100F;
			final var channels = format.getChannels();
			final var targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, frameRate, 16, channels,
				channels * 2, frameRate, false);
			if (AudioSystem.isConversionSupported(targetFormat, format)) {
				final var din = AudioSystem.getAudioInputStream(targetFormat, audioStream);
				this.isPlaying = true;
				this.audioClip = AudioSystem.getClip();
				this.audioClip.addLineListener(this);
				this.audioClip.open(din);
				this.audioClip.start();
			} else {
				throw new IllegalArgumentException("Conversion not supported!");
			}
		} catch (final Exception e) {
			this.isPlaying = false;
			throw new IllegalArgumentException(e);
		}
	}

	private URL locate(final String filename) {
		var url = ClassLoader.getSystemResource(filename);
		if (url == null) {
			url = ClassLoader.getSystemResource("music/" + filename);
		}
		if (url == null) {
			url = ClassLoader.getSystemResource("sounds/" + filename);
		}
		if (url == null) {
			url = ClassLoader.getSystemResource("resources/music/" + filename);
		}
		if (url == null) {
			url = ClassLoader.getSystemResource("resources/sounds/" + filename);
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

	@Override
	public int hashCode() {
		return this.filename.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Sound s) {
			return s.filename.equals(this.filename);
		} else {
			return false;
		}
	}

}
