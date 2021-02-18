package conquer.data;

/**
 * Represents a scenario.<br>
 * {@code name} is the name of the scenario, {@code file} is the corresponding
 * file. {@code thumbnail} is an optional picture. {@code in} or {@code file}
 * can be {@code null}. But only one of these should be {@code null}.
 */
public final record InstalledScenario(String name, String file, String thumbnail,
		MagicNumberInputStream in) {
}
