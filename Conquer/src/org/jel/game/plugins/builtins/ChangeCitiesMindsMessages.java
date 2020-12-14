package org.jel.game.plugins.builtins;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ChangeCitiesMindsMessages {
	private static final String BUNDLE_NAME = "org.jel.game.plugins.builtins.changeCitiesMinds"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private ChangeCitiesMindsMessages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
