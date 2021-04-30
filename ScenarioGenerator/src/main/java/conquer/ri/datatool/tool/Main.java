package conquer.ri.datatool.tool;

import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import conquer.ri.datatool.tool.model.Scenario;

public class Main {
	public static void main(final String[] args) {
		final var main = new Main();
		System.exit(main.run(args));
	}

	int run(final String[] args) {
		if (args.length == 0 || args[0].equals("--help")) {
			System.out.println("scenario-builder <jsonFile>");
			return 1;
		}
		final var gson = new Gson();
		final Scenario scenario;
		try (final var reader = new FileReader(args[0])) {
			scenario = gson.fromJson(reader, Scenario.class);
		} catch (final IOException e) {
			System.out.println(e.getLocalizedMessage());
			return 127;
		} catch (final JsonSyntaxException jse) {
			System.out.println("Invalid json: " + jse.getLocalizedMessage());
			return 128;
		}
		final DataFile dataFile;
		try {
			scenario.validate();
			dataFile = scenario.toDataFile();
		} catch (final IllegalArgumentException iae) {
			System.out.println(iae.getLocalizedMessage());
			return 123;
		}
		dataFile.dump(args[0].replace(".json", ".data"));
		return 0;
	}
}
