package org.jel.game.plugins.builtins;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MoneyAnalyzerMessages {
	private static final String BUNDLE_NAME = "org.jel.game.plugins.builtins.moneyAnalyzer"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(MoneyAnalyzerMessages.BUNDLE_NAME);

	public static String getString(final String key) {
		try {
			return MoneyAnalyzerMessages.RESOURCE_BUNDLE.getString(key);
		} catch (final MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	private MoneyAnalyzerMessages() {
	}
}
