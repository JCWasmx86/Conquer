package org.jel.gui;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

final class Messages {
	private static final String BUNDLE_NAME = "org.jel.gui.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(Messages.BUNDLE_NAME);

	public static String getMessage(String key, Object... arguments) {
		return MessageFormat.format(Messages.getString(key), arguments);
	}

	public static String getString(String key) {
		try {
			return Messages.RESOURCE_BUNDLE.getString(key);
		} catch (final MissingResourceException e) {
			e.printStackTrace();
			return '!' + key + '!';
		}
	}

	private Messages() {
	}
}
