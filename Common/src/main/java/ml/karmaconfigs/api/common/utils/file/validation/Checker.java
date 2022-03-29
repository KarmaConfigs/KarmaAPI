package ml.karmaconfigs.api.common.utils.file.validation;

import ml.karmaconfigs.api.common.utils.file.FileChecker;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Class that contains what a file checker
 * should have
 */
public abstract class Checker {

    /**
     * Initialize the file checker
     *
     * @param target the target file
     * @return this instance
     */
    public abstract Checker withFile(final Path target);

    /**
     * Initialize the file checker
     *
     * @param target the target file
     * @return this instance
     */
    public abstract Checker withFile(final File target);

    /**
     * Set the file to perform the
     * check with
     *
     * @param target the file to perform the
     *               check with
     * @return this instance
     */
    public abstract Checker withCheck(final InputStream target);

    /**
     * Validate file integrity, by default KarmaAPI
     * uses a CRC32 checksum test
     *
     * @return the file integrity
     */
    public abstract boolean verify();

    /**
     * Generate a hash using the file bytes, this could be
     * a md5 hash or even a UUID generated using the file
     * bytes. By default, KarmaAPI generates a md5 hash
     *
     * @param algorithm the algorithm to use. Nonsense while using {@link ml.karmaconfigs.api.common.utils.file.FileChecker}
     * @return the file hash
     */
    public abstract String generateHash(final String algorithm);

    /**
     * Generate a hash using the file bytes, this could be
     * a md5 hash or even a UUID generated using the file
     * bytes. By default, KarmaAPI generates a md5 hash
     *
     * @param algorithm the algorithm to use. Nonsense while using {@link ml.karmaconfigs.api.common.utils.file.FileChecker}
     * @return the file hash
     */
    public abstract String generateHashCheck(final String algorithm);
}
