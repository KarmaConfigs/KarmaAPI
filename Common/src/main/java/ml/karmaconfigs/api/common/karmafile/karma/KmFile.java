package ml.karmaconfigs.api.common.karmafile.karma;

import ml.karmaconfigs.api.common.karmafile.karma.error.NotKmFileException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Karma file
 */
public abstract class KmFile {

    /**
     * The current file
     */
    private final Path path;

    /**
     * The section divider character
     */
    public static char SECTION_DIVIDER_CHARACTER = '.';

    /**
     * Initialize the karma file
     *
     * @param basePath the file path
     */
    public KmFile(final String basePath) {
        path = Paths.get(basePath);
    }

    /**
     * Initialize the karma file
     *
     * @param file the file
     * @throws NotKmFileException if the file is not a
     * karma file
     */
    public KmFile(final File file) throws NotKmFileException {
        path = file.toPath();
    }

    /**
     * Initialize the karma file
     *
     * @param file the file
     * @throws NotKmFileException if the file is not a karma file
     */
    public KmFile(final Path file) throws NotKmFileException {
        path = file;
    }

    /**
     * Set a new value
     *
     * @param path the value path
     * @param value the value
     * @param comments the comments of the path
     */
    public abstract void set(final String path, final Object value, final String... comments);

    /**
     * Unset a path
     *
     * @param path the path
     */
    public abstract void unset(final String path);

    /**
     * Get if the specified path is set
     *
     * @param path the path
     * @return if the path is set
     */
    public abstract boolean isSet(final String path);

    /**
     * Get if the specified path and value are set
     *
     * @param path the path
     * @param expected the expected value
     * @return if the path is set and its value is the expected value
     */
    public abstract boolean isSet(final String path, final Object expected);

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    @Nullable
    public abstract Object get(final String path);

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    @Nullable
    public abstract KmFile getSection(final String path);

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    @Nullable
    public abstract String getString(final String path);

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    public abstract boolean getBoolean(final String path);

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    public abstract int getInteger(final String path);

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    public abstract double getDouble(final String path);

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    public abstract long getLong(final String path);

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    public abstract float getFloat(final String path);

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    public abstract byte getByte(final String path);

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    public abstract byte[] getBytes(final String path);

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value if
     *            value not present
     * @return the value
     */
    @NotNull
    public abstract Object get(final String path, final Object def);

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value if
     *            value not present
     * @return the value
     */
    @NotNull
    public abstract KmFile getSection(final String path, final KmFile def);

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value if
     *            value not present
     * @return the value
     */
    @NotNull
    public abstract String getString(final String path, final String def);

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value if
     *            value not present
     * @return the value
     */
    public abstract boolean getBoolean(final String path, final boolean def);

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value if
     *            value not present
     * @return the value
     */
    public abstract int getInteger(final String path, final int def);

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value if
     *            value not present
     * @return the value
     */
    public abstract double getDouble(final String path, final double def);

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value if
     *            value not present
     * @return the value
     */
    public abstract long getLong(final String path, final long def);

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value if
     *            value not present
     * @return the value
     */
    public abstract float getFloat(final String path, final float def);

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value if
     *            value not present
     * @return the value
     */
    public abstract byte getByte(final String path, final byte def);

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value if
     *            value not present
     * @return the value
     */
    public abstract byte[] getBytes(final String path, final byte[] def);

    /**
     * Get the file keys
     *
     * @return the file keys
     */
    public abstract Map<String, Object> getKeys();

    /**
     * Creates the file
     *
     * @return if the file could be created
     */
    public abstract boolean create();

    /**
     * Removes the file
     *
     * @return if the file could be removed
     */
    public abstract boolean remove();

    /**
     * Get if the file exists
     *
     * @return if the file exists
     */
    public abstract boolean exists();

    /**
     * Get the if both files contains the same data
     *
     * @param check the file to check with
     * @return if both files contains the same data
     */
    public boolean check(final KmFile check) {
        Map<String, Object> values = getKeys();

        boolean status = true;
        for (String key : values.keySet()) {
            if (status) {
                Object value = values.get(key);

                if (check.isSet(key, value)) {
                    if (value instanceof KmFile) {
                        KmFile section = (KmFile) value;
                        KmFile checkSection = check.getSection(key);

                        if (checkSection != null) {
                            status = section.check(checkSection);
                        } else {
                            status = false;
                        }
                    }
                } else {
                    status = false;
                }
            } else {
                break;
            }
        }

        return status;
    }

    /**
     * Get the file that is being managed
     *
     * @return the managed file
     */
    public final Path getFile() {
        return path;
    }
}
