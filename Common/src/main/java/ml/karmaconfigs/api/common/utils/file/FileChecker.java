package ml.karmaconfigs.api.common.utils.file;

import ml.karmaconfigs.api.common.utils.file.validation.Checker;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.zip.CRC32;

/**
 * A simple file checker
 */
public class FileChecker extends Checker {

    private Path file;
    private InputStream check = null;

    /**
     * Initialize the file checker
     *
     * @param target the target file
     */
    @Override
    public FileChecker withFile(final Path target) {
        file = target;

        return this;
    }

    /**
     * Initialize the file checker
     *
     * @param target the target file
     */
    @Override
    public FileChecker withFile(final File target) {
        file = target.toPath();

        return this;
    }

    /**
     * Set the file to perform the
     * check with
     *
     * @param target the file to perform the
     *               check with
     * @return this instance
     */
    @Override
    public FileChecker withCheck(final InputStream target) {
        check = target;

        return this;
    }

    /**
     * Validate file integrity, by default KarmaAPI
     * uses a CRC32 checksum test
     *
     * @return the file integrity
     */
    @Override
    public boolean verify() {
        if (check != null && file != null) {
            PathUtilities.create(file);

            try {
                byte[] host = new byte[(int) check.available()];
                byte[] current = Files.readAllBytes(file);

                DataInputStream dataInputStream = new DataInputStream(check);
                dataInputStream.readFully(host);

                ByteBuffer hostBuffer = ByteBuffer.wrap(host);
                ByteBuffer currentBuffer = ByteBuffer.wrap(current);

                CRC32 check = new CRC32();
                check.update(hostBuffer);

                long hostCheck = check.getValue();
                check.update(currentBuffer);

                long currentCheck = check.getValue();

                return hostCheck != currentCheck;
            } catch (Throwable ex) {
                return false;
            }
        }

        return true;
    }

    /**
     * Generate a hash using the file bytes, this could be
     * a md5 hash or even a UUID generated using the file
     * bytes. By default, KarmaAPI generates a md5 hash
     *
     * @param algorithm the algorithm to use. Nonsense while using {@link FileChecker}
     * @return the file hash
     */
    @Override
    public @Nullable String generateHash(final String algorithm) {
        String result = null;
        if (file != null) {
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                byte[] hash = digest.digest(Files.readAllBytes(file));
                StringBuilder builder = new StringBuilder();
                for (byte aByte : hash) {
                    builder.append(String.format("%02x", aByte));
                }
                result = builder.toString();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        return result;
    }

    /**
     * Generate a hash using the file bytes, this could be
     * a md5 hash or even a UUID generated using the file
     * bytes. By default, KarmaAPI generates a md5 hash
     *
     * @param algorithm the algorithm to use. Nonsense while using {@link FileChecker}
     * @return the file hash
     */
    @Override
    public String generateHashCheck(String algorithm) {
        String result = null;
        if (check != null) {
            try {
                byte[] host = new byte[(int) check.available()];

                DataInputStream dataInputStream = new DataInputStream(check);
                dataInputStream.readFully(host);

                MessageDigest digest = MessageDigest.getInstance("MD5");
                byte[] hash = digest.digest(host);
                StringBuilder builder = new StringBuilder();
                for (byte aByte : hash) {
                    builder.append(String.format("%02x", aByte));
                }
                result = builder.toString();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        return result;
    }
}
