package conquer.gui;

import conquer.data.Shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class ErrorReporterUtils {
	private ErrorReporterUtils() {
		//Hide public default constructor
	}

	private static String getSystemProperties(final Throwable t) {
		try (final var sw = new StringWriter(); final var pw = new PrintWriter(sw)) {
			t.printStackTrace(pw);
			System.getProperties().forEach((key, value) -> sw.write(key + ": " + value + "\n"));
			return sw.toString();
		} catch (final IOException e) {
			Shared.LOGGER.exception(e);
			e.printStackTrace();
		}
		return "";
	}

	private static String getEnvironmentVariables() {
		try (final var sw = new StringWriter()) {
			System.getProperties().forEach((key, value) -> sw.write(key + "=" + value + "\n"));
			return sw.toString();
		} catch (final IOException e) {
			Shared.LOGGER.exception(e);
			e.printStackTrace();
		}
		return "";
	}

	static String getString(final Throwable t) {
		final StringBuilder sb = new StringBuilder();
		sb.append("--Crashreport--\n");
		sb.append(ErrorReporterUtils.getSystemProperties(t));
		sb.append(ErrorReporterUtils.getEnvironmentVariables());
		Thread.getAllStackTraces().forEach((thread, stackTraceElements) -> {
			sb.append(thread.getName()).append(": \n");
			for (final var ste : stackTraceElements) {
				sb.append("\t").append(ste.toString()).append("\n");
			}
		});
		return sb.toString();
	}
}
