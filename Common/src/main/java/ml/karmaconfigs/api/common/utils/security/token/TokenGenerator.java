package ml.karmaconfigs.api.common.utils.security.token;

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

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Karma token generator
 */
public class TokenGenerator {

    /**
     * Secure random instance
     */
    private static final SecureRandom random = new SecureRandom();

    /**
     * Token symbols
     */
    private static final char[] symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    /**
     * The token buffer
     */
    private static final char[] buf = new char[512];

    /**
     * The token max length
     */
    private static int length = -1;

    /**
     * Set the token length
     *
     * @param size the token length
     */
    public static void length(final int size) {
        length = size;
    }

    /**
     * Generate a new token
     *
     * @return a new token
     */
    public static String generateToken() {
        for (int idx = 0; idx < buf.length; idx++)
            buf[idx] = symbols[random.nextInt(symbols.length)];

        String value = new String(buf);
        if (length != -1) {
            value = value.substring(0, length);
        }

        return Base64.getUrlEncoder().encodeToString(value.getBytes());
    }

    /**
     * Generate a token without base 64 encoding
     *
     * @return the token
     */
    public static String generateLiteral() {
        for (int idx = 0; idx < buf.length; idx++)
            buf[idx] = symbols[random.nextInt(symbols.length)];

        String value = new String(buf);
        if (length != -1) {
            value = value.substring(0, length);
        }

        return value;
    }

    /**
     * Generate a token without base 64 encoding
     *
     * @param size the token size
     * @return the token
     */
    public static String generateLiteral(final int size) {
        for (int idx = 0; idx < buf.length; idx++)
            buf[idx] = symbols[random.nextInt(symbols.length)];

        String value = new String(buf);
        if (size != -1) {
            value = value.substring(0, size);
        }

        return value;
    }
}
