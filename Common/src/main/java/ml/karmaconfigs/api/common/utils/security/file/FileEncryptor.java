package ml.karmaconfigs.api.common.utils.security.file;

/*
 * This file is part of KarmaAPI, licensed under the MIT License.
 *
 *  Copyright (c) karma (KarmaDev) <karmaconfigs@gmail.com>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

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

/**
 * File encryption utilities
 */
public final class FileEncryptor {

    private final Path file;
    private final String token;

    /**
     * Initialize the file encryptor
     *
     * @param tar the target file
     * @param key the file key
     */
    public FileEncryptor(final File tar, final String key) {
        file = tar.toPath();
        token = key;
    }

    /**
     * Initialize the file encryptor
     *
     * @param tar the target file
     * @param key the file key
     */
    public FileEncryptor(final Path tar, final String key) {
        file = tar;
        token = key;
    }

    /**
     * Encrypt the file
     *
     * @return if the file could be encrypted
     */
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

    /**
     * Decrypt the file
     *
     * @return if the file could be decrypted
     */
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
