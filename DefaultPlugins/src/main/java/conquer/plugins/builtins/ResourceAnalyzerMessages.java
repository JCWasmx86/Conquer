package conquer.plugins.builtins;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

final class ResourceAnalyzerMessages {
	private static final String BUNDLE_NAME = "conquer.plugins.builtins.resourceAnalyzer";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
		.getBundle(ResourceAnalyzerMessages.BUNDLE_NAME);

	private ResourceAnalyzerMessages() {
	}

	public static String getString(final String key) {
		try {
			return ResourceAnalyzerMessages.RESOURCE_BUNDLE.getString(key);
		} catch (final MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
