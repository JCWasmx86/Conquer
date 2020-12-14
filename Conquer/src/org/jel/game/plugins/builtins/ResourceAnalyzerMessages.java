package org.jel.game.plugins.builtins;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceAnalyzerMessages {
	private static final String BUNDLE_NAME = "org.jel.game.plugins.builtins.resourceAnalyzer"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private ResourceAnalyzerMessages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
