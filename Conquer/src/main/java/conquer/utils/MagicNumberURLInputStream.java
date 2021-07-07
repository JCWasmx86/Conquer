package conquer.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import conquer.data.MagicNumberInputStream;

/**
 * An utility class allowing for lazily loading a magic number from a (remote)
 * URL.
 */
public class MagicNumberURLInputStream extends MagicNumberInputStream {

<<<<<<< HEAD
	private URL url;
	private InputStream in;
	private byte[] bytes;
	private int cnter;
=======
    private URL url;
    private InputStream in;
    private byte[] bytes;
    private int cnter = 0;
>>>>>>> parent of f8bbb68 (Formatting)

    /**
     * Construct a new {@code MagicNumberURLInputStream}.
     *
     * @param url The {@link URL} to read from. May not be {@code null}, otherwise
     *            an {@link IllegalArgumentException} will be thrown.
     */
    public MagicNumberURLInputStream(final URL url) {
        if (url == null) {
            throw new IllegalArgumentException("url==null");
        }
        this.url = url;
    }

<<<<<<< HEAD
	/**
	 * Construct a new {@code MagicNumberURLInputStream}.
	 *
	 * @param file The {@link File} to read from. May not be {@code null}, otherwise
	 *             an {@link IllegalArgumentException} will be thrown.
	 *
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
	 * @param uri The {@link URI} to read from. May not be {@code null}, otherwise
	 *            an {@link IllegalArgumentException} will be thrown.
	 *
	 * @throws MalformedURLException If {@link URI#toURL()} fails.
	 */
	public MagicNumberURLInputStream(final URI uri) throws MalformedURLException {
		if (uri == null) {
			throw new IllegalArgumentException("uri==null");
		}
		this.url = uri.toURL();
	}
=======
    /**
     * Construct a new {@code MagicNumberURLInputStream}.
     *
     * @param file The {@link File} to read from. May not be {@code null}, otherwise
     *             an {@link IllegalArgumentException} will be thrown.
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
     * @param uri The {@link URI} to read from. May not be {@code null}, otherwise
     *            an {@link IllegalArgumentException} will be thrown.
     * @throws MalformedURLException If {@link URI#toURL()} fails.
     */
    public MagicNumberURLInputStream(final URI uri) throws MalformedURLException {
        if (uri == null) {
            throw new IllegalArgumentException("uri==null");
        }
        this.url = uri.toURL();
    }
>>>>>>> parent of f8bbb68 (Formatting)

    /**
     * Construct a new {@code MagicNumberURLInputStream}.
     *
     * @param in The {@link InputStream} to read from. May not be {@code null},
     *           otherwise an {@link IllegalArgumentException} will be thrown.
     */
    public MagicNumberURLInputStream(final InputStream in) {
        if (in == null) {
            throw new IllegalArgumentException("in==null");
        }
        this.in = in;
    }

    @Override
    public byte[] getMagicNumber(final int maxLength) {
        try {
            this.tryOpen();
            this.bytes = this.in.readNBytes(maxLength);
            return this.bytes;
        } catch (final IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private void tryOpen() throws IOException {
        if (this.in == null) {
            this.in = this.url.openStream();
        }
    }

<<<<<<< HEAD
	@Override
	public int read() throws IOException {
		if (this.bytes == null) {
			this.tryOpen();
			return this.in.read();
		} else {
			if (this.cnter < this.bytes.length) {
				return this.bytes[this.cnter++];
			} else {
				return this.in.read();
			}
		}
	}
=======
    @Override
    public int read() throws IOException {
        if (this.bytes != null) {
            if (this.cnter < this.bytes.length) {
                return this.bytes[this.cnter++];
            } else {
                return this.in.read();
            }
        } else {
            this.tryOpen();
            return this.in.read();
        }
    }
>>>>>>> parent of f8bbb68 (Formatting)

    @Override
    public void close() throws IOException {
        super.close();
        if (this.in != null) {
            this.in.close();
        }
    }
}
