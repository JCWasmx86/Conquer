package conquer.gui;

import conquer.data.Shared;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;

public final class ErrorReporterUtils {
	private ErrorReporterUtils() {
		//Hide public default constructor
	}

	private static String getSystemProperties(final Throwable t) {
		try (final var sw = new StringWriter(); final var pw = new PrintWriter(sw)) {
			t.printStackTrace(pw);
			System.getProperties().forEach((key, value) -> {
				sw.write(key + ": " + value + "\n");
			});
			return sw.toString();
		} catch (final IOException e) {
			Shared.LOGGER.exception(e);
			e.printStackTrace();
		}
		return "";
	}

	private static String getEnvironmentVariables() {
		try (final var sw = new StringWriter()) {
			System.getProperties().forEach((key, value) -> {
				sw.write(key + "=" + value + "\n");
			});
			return sw.toString();
		} catch (final IOException e) {
			Shared.LOGGER.exception(e);
			e.printStackTrace();
		}
		return "";
	}

	static String getString(final Throwable t) {
		StringBuilder sb = new StringBuilder();
		sb.append("--Crashreport--\n");
		sb.append(ErrorReporterUtils.getSystemProperties(t));
		sb.append(ErrorReporterUtils.getEnvironmentVariables());
		Thread.getAllStackTraces().forEach((thread, stackTraceElements) -> {
			sb.append(thread.getName() + ": \n");
			for (var ste : stackTraceElements) {
				sb.append("\t" + ste.toString() + "\n");
			}
		});
		return sb.toString();
	}

	// Returns the errorlog filename.
	public static String writeErrorLog(final Throwable t) {
		final var reportsDir = new File(Shared.BASE_DIRECTORY, "reports");
		reportsDir.mkdirs();
		final var logfileName = "log_" + Long.toHexString(System.nanoTime()) + ".report";
		final var logfile = new File(reportsDir, logfileName);
		try (final var bw = new BufferedWriter(new FileWriter(logfile));
			 final var sw = new StringWriter();
			 final var pw = new PrintWriter(sw)) {
			bw.write("Crash log\n");
			t.printStackTrace(pw);
			bw.write("Stacktrace\n");
			bw.write(sw.toString());
			bw.write("\n");
			bw.write("System properties\n");
			System.getProperties().forEach((key, value) -> {
				try {
					bw.write(key + "=" + value + "\n");
				} catch (final IOException e) {
					Shared.LOGGER.exception(e);
				}
			});
			bw.write("Environment variables\n");
			System.getenv().forEach((key, value) -> {
				try {
					bw.write(key + "=" + value + "\n");
				} catch (final IOException e) {
					Shared.LOGGER.exception(e);
				}
			});
			bw.write("Services\n\n");
			ErrorReporterUtils.class.getModule().getDescriptor().uses().forEach(a -> {
				try {
					final var clazz = Class.forName(a);
					ServiceLoader.load(clazz).stream().map(Provider::get).map(Object::getClass).forEach(b -> {
						try {
							bw.write(clazz + " is provided by " + b.toString());
						} catch (final IOException e) {
							Shared.LOGGER.exception(e);
						}
					});
				} catch (final ClassNotFoundException cnfe) {
					Shared.LOGGER.exception(cnfe);
				}
			});
		} catch (final IOException e) {
			Shared.LOGGER.exception(e);
		}
		return logfile.getAbsolutePath();
	}
}
