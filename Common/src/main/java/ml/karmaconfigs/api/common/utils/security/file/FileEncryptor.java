package ml.karmaconfigs.api.common.utils.security.file;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.Key;

public final class FileEncryptor {

    private final Path file;
    private final String token;

    public FileEncryptor(final File tar, final String key) {
        file = tar.toPath();
        token = key;
    }

    public FileEncryptor(final Path tar, final String key) {
        file = tar;
        token = key;
    }

    public boolean encrypt() {
        try {
            Path tmp = file.getParent().resolve(file.getFileName().toString() + ".tmp");

            Key secret = new SecretKeySpec(token.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secret);

            FileInputStream inputStream = new FileInputStream(file.toFile());
            byte[] inputBytes = new byte[(int) file.toFile().length()];

            byte[] outputBytes = cipher.doFinal(inputBytes);


            FileOutputStream outputStream = new FileOutputStream(tmp.toFile());
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();

            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Throwable ignored) {}

        return false;
    }

    public boolean decrypt() {
        try {
            Path tmp = file.getParent().resolve(file.getFileName().toString() + ".tmp");

            Key secret = new SecretKeySpec(token.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secret);

            FileInputStream inputStream = new FileInputStream(file.toFile());
            byte[] inputBytes = new byte[(int) file.toFile().length()];

            byte[] outputBytes = cipher.doFinal(inputBytes);


            FileOutputStream outputStream = new FileOutputStream(tmp.toFile());
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();

            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Throwable ignored) {}

        return false;
    }
}
