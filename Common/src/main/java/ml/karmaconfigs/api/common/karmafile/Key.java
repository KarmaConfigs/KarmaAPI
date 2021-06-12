package ml.karmaconfigs.api.common.karmafile;

import java.io.Serializable;

/**
 * KarmaFile key object
 *
 * This object contains the value, in case
 * of no value, the key will be used as value
 */
public final class Key implements Serializable {

    private final String path;
    private final Object value;

    /**
     * Initialize the file value
     *
     * @param keyPath   the key path
     * @param keyValue  the key value
     */
    public Key(final String keyPath, final Object keyValue) {
        path = keyPath;
        value = keyValue;
    }

    /**
     * Get the key
     *
     * @return the key
     */
    public final String getPath() {
        return path;
    }

    /**
     * Get the value
     *
     * @return the value
     */
    public final Object getValue() {
        return value;
    }
}
