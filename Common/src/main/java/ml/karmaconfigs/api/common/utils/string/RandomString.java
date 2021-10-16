package ml.karmaconfigs.api.common.utils.string;

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

import java.util.Random;

/**
 * Karma random string generator
 */
public final class RandomString {

    /**
     * The text options
     */
    private final OptionsBuilder options;

    /**
     * Initialize the random string generator
     */
    RandomString() {
        options = new OptionsBuilder();
    }

    /**
     * Initialize the random string generator
     *
     * @param opts the generator options
     */
    RandomString(final OptionsBuilder opts) {
        options = opts;
    }

    /**
     * Create the random text
     *
     * @return the generated random text
     */
    public String create() {
        char[] salt;
        switch (options.getContent()) {
            case ONLY_NUMBERS:
                salt = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
                break;
            case NUMBERS_AND_LETTERS:
                salt = new char[]{
                        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                        'u', 'v', 'x', 'y', 'z', '0', '1', '2', '3', '4',
                        '5', '6', '7', '8', '9'};
                break;
            default:
                salt = new char[]{
                        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                        'u', 'v', 'x', 'y', 'z'};
                break;
        }
        StringBuilder result = new StringBuilder();
        int last_int = 0;
        for (int i = 0; i < options.getSize(); i++) {
            int random = (new Random()).nextInt(salt.length);
            if (last_int != random) {
                int random_s;
                String lower = String.valueOf(salt[random]);
                String upper = String.valueOf(salt[random]).toUpperCase();
                switch (options.getType()) {
                    case ALL_LOWER:
                        result.append(lower);
                        break;
                    case ALL_UPPER:
                        result.append(upper);
                        break;
                    default:
                        random_s = (new Random()).nextInt(100);
                        if (random_s > 50) {
                            result.append(lower);
                            break;
                        }
                        result.append(upper);
                        break;
                }
                last_int = random;
            } else {
                i--;
            }
        }

        return result.toString();
    }

    /**
     * Create a new options builder
     *
     * @return a new options builder
     */
    public static OptionsBuilder createBuilder() {
        return new OptionsBuilder();
    }
}
