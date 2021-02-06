package org.jel.game.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jel.game.data.MagicNumberInputStream;

public class MagicNumberURLInputStream extends MagicNumberInputStream {

	private URL url;
	private InputStream in;
	private byte[] bytes;
	private int cnter = 0;

	public MagicNumberURLInputStream(final URL url) {
		if (url == null) {
			throw new IllegalArgumentException("url==null");
		}
		this.url = url;
	}

	@Override
	public byte[] getMagicNumber(int maxLength) {
		try {
			this.tryOpen();
			this.bytes = this.in.readNBytes(maxLength);
			return this.bytes;
		} catch (IOException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	private void tryOpen() throws IOException {
		if (this.in == null) {
			this.in = this.url.openStream();
		}
	}

	@Override
	public int read() throws IOException {
		if (this.bytes != null) {
			if (cnter < bytes.length) {
				return this.bytes[cnter++];
			} else {
				return in.read();
			}
		} else {
			this.tryOpen();
			return this.in.read();
		}
	}

}
