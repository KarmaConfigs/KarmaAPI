package ml.karmaconfigs.api.common.karma.file.error;

import ml.karmaconfigs.api.common.utils.file.PathUtilities;

import java.nio.file.Path;

/**
 * An error when a KarmaFile is read, but it
 * has an invalid format or syntax
 *
 * @author KarmaDev
 * @since 1.3.2-SNAPSHOT
 */
public class KarmaFormatException extends RuntimeException {

    /**
     * Initialize the error
     *
     * @param file the file that is being read
     * @param info extra information such as line and where
     *             the file failed to read
     * @param index the line index
     */
    public KarmaFormatException(final Path file, final String info, final int index) {
        super("An error occurred while reading file " + PathUtilities.getPrettyPath(file) + " at index (" + index + "): " + info);
    }
}
