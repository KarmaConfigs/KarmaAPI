package ml.karmaconfigs.api.common.exception;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * This exception is thrown when a file
 * is not a jar file, while trying to inject
 */
public final class NoJarException extends Exception {

    /**
     * Initialize the exception
     *
     * @param file the supposed jar file
     */
    public NoJarException(@NotNull final File file) {
        super("The specified file ( " + file.getAbsolutePath().replaceAll("\\\\", "/") + " ) is not a jar file");
    }
}
