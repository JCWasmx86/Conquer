package org.jel.game.data;

import java.util.List;

/**
 * An interface for SPI, that just searches for scenarios.
 */
@FunctionalInterface
public interface InstalledScenarioProvider {
	/**
	 * Find installed scenarios.
	 *
	 * @return All found scenarios. May not be {@code null}.
	 */
	List<InstalledScenario> getScenarios();
}
