package org.jel.game.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

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
		try {
			this.bw.write("[EXCEPTION date= " + new Date() + "]\n");
			this.bw.write(throwable.getClass().getName() + ": " + throwable.getMessage() + "\n");
			for (final StackTraceElement ste : throwable.getStackTrace()) {
				this.bw.write("\t" + ste.getClassName() + "@" + ste.getModuleName() + "::" + ste.getMethodName()
						+ "(Line: " + ste.getLineNumber() + ")\n");
			}
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
