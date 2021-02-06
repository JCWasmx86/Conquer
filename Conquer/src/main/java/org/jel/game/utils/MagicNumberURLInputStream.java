package org.jel.game.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.jel.game.data.MagicNumberInputStream;

/**
 * An utility class allowing for lazily loading a magic number from a (remote)
 * URL.
 */
public class MagicNumberURLInputStream extends MagicNumberInputStream {

	private URL url;
	private InputStream in;
	private byte[] bytes;
	private int cnter = 0;

	/**
	 * Construct a new {@code MagicNumberURLInputStream}.
	 * 
	 * @param url The URL to read from. May not be {@code null}, otherwise an
	 *            {@link IllegalArgumentException} will be thrown.
	 */
	public MagicNumberURLInputStream(final URL url) {
		if (url == null) {
			throw new IllegalArgumentException("url==null");
		}
		this.url = url;
	}

	/**
	 * Construct a new {@code MagicNumberURLInputStream}.
	 * 
	 * @param file The file to read from. May not be {@code null}, otherwise an
	 *             {@link IllegalArgumentException} will be thrown.
	 * @throws MalformedURLException If {@link URI#toURL()} fails.
	 */
	public MagicNumberURLInputStream(final File file) throws MalformedURLException {
		if (file == null) {
			throw new IllegalArgumentException("file==null");
		}
		this.url = file.toURI().toURL();
	}

	/**
	 * Construct a new {@code MagicNumberURLInputStream}.
	 * 
	 * @param uri The URI to read from. May not be {@code null}, otherwise an
	 *            {@link IllegalArgumentException} will be thrown.
	 * @throws MalformedURLException If {@link URI#toURL()} fails.
	 */
	public MagicNumberURLInputStream(final URI uri) throws MalformedURLException {
		if (uri == null) {
			throw new IllegalArgumentException("uri==null");
		}
		this.url = uri.toURL();
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
