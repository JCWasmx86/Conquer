package conquer.plugins;

/**
 * A callback that is called as soon as the specified key is pressed.
 */
@FunctionalInterface
public interface KeyHandler {
    /**
     * Called as soon as the specified key is pressed
     *
     * @param key The key that was pressed.
     */
    void handleKey(String key);
}
