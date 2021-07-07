package conquer;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

<<<<<<< HEAD
public final class Messages {
	private static final String BUNDLE_NAME = "conquer.messages";
=======
public class Messages {
    private static final String BUNDLE_NAME = "conquer.messages"; //$NON-NLS-1$
>>>>>>> parent of f8bbb68 (Formatting)

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(Messages.BUNDLE_NAME);

<<<<<<< HEAD
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
=======
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

    private Messages() {
    }
>>>>>>> parent of f8bbb68 (Formatting)
}
