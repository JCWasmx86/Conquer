package conquer.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;

import conquer.data.Shared;

public class ErrorReporter {
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
				} catch (final java.io.IOException e) {
					conquer.data.Shared.LOGGER.exception(e);
				}
			});
			bw.write("Environment variables\n");
			System.getenv().forEach((key, value) -> {
				try {
					bw.write(key + "=" + value + "\n");
				} catch (final java.io.IOException e) {
					conquer.data.Shared.LOGGER.exception(e);
				}
			});
			bw.write("Services\n\n");
			ErrorReporter.class.getModule().getDescriptor().uses().forEach(a -> {
				try {
					final var clazz = Class.forName(a);
					ServiceLoader.load(clazz).stream().map(Provider::get).map(Object::getClass).forEach(b -> {
						try {
							bw.write(clazz.toString() + " is provided by " + b.toString());
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
