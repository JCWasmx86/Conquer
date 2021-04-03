package conquer.plugins.builtins;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

final class MoneyAnalyzerMessages {
	private static final String BUNDLE_NAME = "conquer.plugins.builtins.moneyAnalyzer"; 

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(MoneyAnalyzerMessages.BUNDLE_NAME);

	private MoneyAnalyzerMessages() {
	}

	public static String getString(final String key) {
		try {
			return MoneyAnalyzerMessages.RESOURCE_BUNDLE.getString(key);
		} catch (final MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
