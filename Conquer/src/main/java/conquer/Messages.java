package conquer;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Messages {
	private static final String BUNDLE_NAME = "conquer.messages";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(Messages.BUNDLE_NAME);

	private Messages() {
	}

	public static String getMessage(final String key, final Object... arguments) {
		return MessageFormat.format(Messages.getString(key), arguments);
	}

	public static String getString(final String key) {
		try {
			return Messages.RESOURCE_BUNDLE.getString(key);
		} catch (final MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
