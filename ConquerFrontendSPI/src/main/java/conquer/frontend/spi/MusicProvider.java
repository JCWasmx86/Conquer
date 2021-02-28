package conquer.frontend.spi;

import java.util.List;

/**
 * An interface for giving sound names.
 */
@FunctionalInterface
public interface MusicProvider {
    /**
     * Returns a list of sound names/paths.
     *
     * @return List of sound names/paths
     */
    List<String> getMusic();
}
