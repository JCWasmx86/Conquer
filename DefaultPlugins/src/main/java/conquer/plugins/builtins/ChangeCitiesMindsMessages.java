package conquer.plugins.builtins;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

final class ChangeCitiesMindsMessages {
	private static final String BUNDLE_NAME = "conquer.plugins.builtins.changeCitiesMinds";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(ChangeCitiesMindsMessages.BUNDLE_NAME);

	private ChangeCitiesMindsMessages() {
	}

	public static String getString(final String key) {
		try {
			return ChangeCitiesMindsMessages.RESOURCE_BUNDLE.getString(key);
		} catch (final MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
