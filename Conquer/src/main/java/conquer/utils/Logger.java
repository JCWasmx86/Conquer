package conquer.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

//This is there because of legacy reasons.
public final class Logger {
	private final String file;
	private BufferedWriter bw;

	public Logger(final String string) {
		this.file = string;
		try {
			this.bw = new BufferedWriter(new FileWriter(this.file, true));
		} catch (final IOException e) {
			throw new InternalError(e);
		}
	}

	public void close() throws IOException {
		this.bw.close();
	}

	public void error(final String message) {
		try {
			this.bw.write("[ERROR date= " + new Date() + "]: " + message + "\n");
			this.bw.flush();
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public void reopen() {
		try {
			this.close();
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
		try {
			this.bw = new BufferedWriter(new FileWriter(this.file, true));
		} catch (final IOException e) {
			throw new InternalError(e);
		}
	}

	public void exception(final Throwable throwable) {
		try (final var sw = new StringWriter(); final var pw = new PrintWriter(sw)) {
			this.bw.write("[EXCEPTION date= " + new Date() + "]\n");
			throwable.printStackTrace(pw);
			this.bw.write(sw.toString());
			this.bw.flush();
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public void message(final String message) {
		try {
			this.bw.write("[MESSAGE date= " + new Date() + "]: " + message + "\n");
			this.bw.flush();
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public void warning(final String message) {
		try {
			this.bw.write("[WARNING date= " + new Date() + "]: " + message + "\n");
			this.bw.flush();
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
