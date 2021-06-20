package ml.karmaconfigs.api.common.utils;

import ml.karmaconfigs.api.common.Console;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

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

/**
 * Utilities for strings
 */
public interface StringUtils {

    /**
     * Replace the last string of the specified text
     *
     * @param text the text to look for regex
     * @param regex the string to replace
     * @param replace the string to replace with
     * @return the replaced text
     */
    static String replaceLast(final String text, final String regex, final String replace) {
        try {
            return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replace);
        } catch (Throwable ex) {
            try {
                return escapeString(text).replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replace);
            } catch (Throwable exc) {
                try {
                    return escapeString(text).replaceFirst("(?s)" + escapeString(regex) + "(?!.*?" + escapeString(regex) + ")", replace);
                } catch (Throwable exce) {
                    return escapeString(text).replaceFirst("(?s)" + escapeString(regex) + "(?!.*?" + escapeString(regex) + ")", escapeString(replace));
                }
            }
        }
    }

    /**
     * Transform bukkit/bungee '&amp;' character to UTF \u00a7
     * code, to colorize the text
     *
     * @param text the text to colorize
     * @return the colorized text
     */
    static String toColor(final String text) {
        String str = text;
        HashSet<String> color_codes = new HashSet<>();

        for (int i = 0; i < text.length(); i++) {
            char curr = text.charAt(i);
            char next = '\0';
            if (i + 1 != text.length())
                next = text.charAt(i + 1);

            if (next != '\0' && !Character.isSpaceChar(next)) {
                if (curr == '&') {
                    color_codes.add(String.valueOf(curr) + next);
                }
            }
        }

        for (String color_code : color_codes) {
            str = str.replace(color_code, color_code.replace('&', '\u00a7'));
        }

        return str;
    }

    /**
     * Transform bukkit/bungee '&amp;' character to UTF \u00a7
     * code, to colorize the text
     *
     * @param text the text to colorize
     * @return the colorized text
     */
    static String toConsoleColor(final String text) {
        String str = text;
        HashSet<String> color_codes = new HashSet<>();

        for (int i = 0; i < text.length(); i++) {
            char curr = text.charAt(i);
            char next = '\0';
            if (i + 1 != text.length())
                next = text.charAt(i + 1);

            if (next != '\0' && !Character.isSpaceChar(next)) {
                if (curr == '&' || curr == '\u00a7') {
                    color_codes.add(String.valueOf(curr).replace("\u00a7", "&") + next);
                }
            }
        }

        for (String color_code : color_codes) {
            String tmp_color = Console.Colors.RESET;
            
            switch (color_code.toLowerCase()) {
                case "&0":
                    tmp_color = Console.Colors.BLACK;
                    break;
                case "&1":
                    tmp_color = Console.Colors.BLUE;
                    break;
                case "&2":
                    tmp_color = Console.Colors.GREEN;
                    break;
                case "&3":
                    tmp_color = Console.Colors.CYAN;
                    break;
                case "&4":
                    tmp_color = Console.Colors.RED;
                    break;
                case "&5":
                    tmp_color = Console.Colors.PURPLE;
                    break;
                case "&6":
                    tmp_color = Console.Colors.YELLOW;
                    break;
                case "&7":
                    tmp_color = Console.Colors.WHITE;
                    break;
                case "&8":
                    tmp_color = Console.Colors.BLACK_BRIGHT;
                    break;
                case "&9":
                    tmp_color = Console.Colors.BLUE_BRIGHT;
                    break;
                case "&a":
                    tmp_color = Console.Colors.GREEN_BRIGHT;
                    break;
                case "&b":
                    tmp_color = Console.Colors.CYAN_BRIGHT;
                    break;
                case "&c":
                    tmp_color = Console.Colors.RED_BRIGHT;
                    break;
                case "&d":
                    tmp_color = Console.Colors.PURPLE_BRIGHT;
                    break;
                case "&e":
                    tmp_color = Console.Colors.YELLOW_BRIGHT;
                    break;
                case "&f":
                    tmp_color = Console.Colors.WHITE_BRIGHT;
                    break;
                case "&r":
                    tmp_color = Console.Colors.RESET;
                    break;
                default:
                    break;
            }
            
            str = str.replace(color_code, tmp_color);
        }

        return str;
    }
    
    /**
     * Transform bukkit/bungee '&amp;' character to UTF \u00a7
     * code, to colorize the text
     *
     * @param texts the texts to colorize
     * @return the colorized text
     */
    static List<String> toColor(final List<String> texts) {
        List<String> newTexts = new ArrayList<>();

        HashSet<String> color_codes = new HashSet<>();

        for (String text : texts) {
            for (int x = 0; x < text.length(); x++) {
                char curr = text.charAt(x);
                char next = '\0';
                if (x + 1 != text.length())
                    next = text.charAt(x + 1);

                if (next != '\0' && !Character.isSpaceChar(next)) {
                    if (curr == '&') {
                        color_codes.add(String.valueOf(curr) + next);
                    }
                }
            }

            for (String color_code : color_codes) {
                text = text.replace(color_code, color_code.replace('&', '\u00a7'));
            }

            newTexts.add(text);
        }

        return newTexts;
    }

    /**
     * Get the text colors limited to bukkit/bungee color
     * codes
     *
     * @param text the texts to get colors from
     * @return the colors
     */
    static Set<String> getColors(final String text) {
        Set<String> color_codes = new HashSet<>();

        for (int i = 0; i < text.length(); i++) {
            char curr = text.charAt(i);
            char next = '\0';
            if (i + 1 != text.length())
                next = text.charAt(i + 1);

            if (next != '\0' && !Character.isSpaceChar(next)) {
                if (curr == '&') {
                    color_codes.add(String.valueOf(curr) + next);
                }
            }
        }

        return color_codes;
    }

    /**
     * Get last color code of the text
     *
     * @param text the text to read from
     * @return the last text color code
     */
    static String getLastColor(final String text) {
        List<String> color_codes = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            char curr = text.charAt(i);
            char next = '\0';
            if (i + 1 != text.length())
                next = text.charAt(i + 1);

            if (next != '\0' && !Character.isSpaceChar(next)) {
                if (curr == '&' || curr == '\u00a7') {
                    color_codes.add(String.valueOf(curr) + next);
                }
            }
        }

        try {
            return color_codes.get(color_codes.size() - 1);
        } catch (Throwable ex) {
            try {
                return color_codes.get(0);
            } catch (Throwable exc) {
                return "";
            }
        }
    }

    /**
     * Get last color code of the text
     *
     * @param texts the text to read from
     * @param index the line index to get last
     *              color from
     * @return the last text color code
     */
    static String getLastColor(final List<String> texts, int index) {
        List<String> color_codes = new ArrayList<>();
        if (index == texts.size())
            index--;

        if (texts.size() > index) {
            try {
                String text = texts.get(index);

                for (int i = 0; i < text.length(); i++) {
                    char curr = text.charAt(i);
                    char next = '\0';
                    if (i + 1 != text.length())
                        next = text.charAt(i + 1);

                    if (next != '\0' && !Character.isSpaceChar(next)) {
                        if (curr == '&' || curr == '\u00a7') {
                            color_codes.add(String.valueOf(curr) + next);
                        }
                    }
                }

                try {
                    return color_codes.get(color_codes.size() - 1);
                } catch (Throwable ex) {
                    try {
                        return color_codes.get(0);
                    } catch (Throwable exc) {
                        return "";
                    }
                }
            } catch (Throwable ignored) {}
        }

        return "";
    }

    /**
     * Remove all color codes from string
     *
     * @param text the text to remove colors from
     * @return a clean text with no color codes
     */
    static String stripColor(final String text) {
        String str = text;

        HashSet<String> color_codes = new HashSet<>();
        for (int i = 0; i < str.length(); i++) {
            char curr = str.charAt(i);
            char next = '\0';
            if (i + 1 != str.length())
                next = str.charAt(i + 1);

            if (next != '\0' && !Character.isSpaceChar(next)) {
                if (curr == '&' || curr == '\u00a7') {
                    color_codes.add(String.valueOf(curr) + next);
                }
            }
        }

        for (String color_code : color_codes) {
            str = str.replace(color_code, "");
        }

        return str;
    }

    /**
     * Remove all color codes from string
     *
     * @param texts the text to remove colors from
     * @return a clean text with no color codes
     */
    static List<String> stripColor(final List<String> texts) {
        List<String> newTexts = new ArrayList<>();

        for (String text : texts) {
            HashSet<String> color_codes = new HashSet<>();

            for (int x = 0; x < text.length(); x++) {
                char curr = text.charAt(x);
                char next = '\0';
                if (x + 1 != text.length())
                    next = text.charAt(x + 1);

                if (next != '\0' && !Character.isSpaceChar(next)) {
                    if (curr == '&' || curr == '\u00a7') {
                        color_codes.add(String.valueOf(curr) + next);
                    }
                }
            }

            for (String color_code : color_codes) {
                text = text.replace(color_code, "");
            }

            newTexts.add(text);
        }

        return newTexts;
    }

    /**
     * Generate a new random string
     *
     * @param size the string size
     * @param generator the generator content
     * @param type the generator type
     * @return a new random string
     */
    static String randomString(final int size, final StringGen generator, final StringType type) {
        char[] salt;
        switch (generator) {
            case ONLY_NUMBERS:
                salt = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
                break;
            case NUMBERS_AND_LETTERS:
                 salt = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'x', 'y', 'z',
                    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
                 break;
            case ONLY_LETTERS:
            default:
                salt = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'x', 'y', 'z'};
                break;
        }

        StringBuilder result = new StringBuilder();
        int last_int = 0;
        for (int i = 0; i < size; i++) {
            int random = new Random().nextInt(salt.length);
            if (last_int != random) {
                String lower = String.valueOf(salt[random]);
                String upper = String.valueOf(salt[random]).toUpperCase();

                switch (type) {
                    case ALL_LOWER:
                        result.append(lower);
                        break;
                    case ALL_UPPER:
                        result.append(upper);
                        break;
                    default:
                    case RANDOM_SIZE:
                        int random_s = new Random().nextInt(100);
                        if (random_s > 50)
                            result.append(salt[random]);
                        else
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
     * Get a random color
     *
     * @return a random color
     */
    static String randomColor() {
        char[] valid_colors = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        int random = new Random().nextInt(valid_colors.length);

        return "\u00a7" + valid_colors[random];
    }

    /**
     * Format the specified string, replacing the text
     *
     * @param text the text to replace
     * @param replaces the text replaces
     * @return a formatted string
     */
    static String formatString(final String text, final Object... replaces) {
        String str = text;
        for (int i = 0; i < replaces.length; i++) {
            String placeholder = "{" + i + "}";
            String val = replaces[i].toString();

            str = str.replace(placeholder, val);
        }

        return str;
    }

    /**
     * Format the specified string, replacing the text
     *
     * @param text the text to replace
     * @param replaces the text replaces
     * @return a formatted string
     */
    static String formatString(final String text, final Map<String, Object> replaces) {
        String str = text;
        for (String key : replaces.keySet()) {
            String placeholder = "{" + key + "}";
            String val = replaces.get(key).toString();

            str = str.replace(placeholder, val);
        }

        return str;
    }

    /**
     * Read from a file
     *
     * @param file the file to read from
     * @return the file content in string
     */
    static String readFrom(final File file) {
        try {
            byte[] encoded = Files.readAllBytes(file.toPath());
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (Throwable ex) {
            return "";
        }
    }

    /**
     * Serialize a list into a single string
     *
     * @param lines the lines to convert to string
     * @param spaces replace new lines with space
     * @return the list as string
     */
    static String listToString(final List<String> lines, final boolean spaces) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (spaces)
                builder.append(line).append((i < lines.size() - 1 ? " " : ""));
            else
                builder.append(line).append((i < lines.size() - 1) ? "\n" : "");
        }

        return builder.toString();
    }

    /**
     * Un escape the string
     *
     * @param text the escaped text
     * @return the un-escaped text
     */
    static String unEscapeString(final String text) {
        return text.replaceAll("\\\\", "");
    }

    /**
     * Scape the string
     *
     * @param text the original text
     * @return the escaped text
     */
    static String escapeString(final String text) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            Character character = text.charAt(i);

            switch (character) {
                case '.':
                case '[':
                case ']':
                case '{':
                case '}':
                case '(':
                case ')':
                case '*':
                case '+':
                case '-':
                case '?':
                case '^':
                case '$':
                case '|':
                    builder.append("\\").append(character);
                    break;
                default:
                    builder.append(character);
                    break;
            }
        }

        return builder.toString();
    }

    /**
     * Serialize a object into a string
     *
     * @param instance the object instance
     * @return the serialized instance
     */
    static String serialize(final Object instance) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(instance);
            so.flush();
            return Base64.getEncoder().encodeToString(bo.toByteArray());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return "";
    }

    /**
     * Load the serialized object
     *
     * @param instance the object string instance
     * @return the un-serialized object
     */
    @Nullable
    static Object load(final String instance) {
        try {
            byte[] bytes = Base64.getDecoder().decode(instance);
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream si = new ObjectInputStream(bi);
            return si.readObject();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Load and cast the object to the
     * required type
     *
     * @param instance the object string instance
     * @param <T> the object type
     * @return the object instance as the required type
     */
    @Nullable
    static @SuppressWarnings("unchecked") <T> T loadUnsafe(final String instance) {
        return (T) load(instance);
    }

    /**
     * Check if the string contains letters
     *
     * @param sequence the text sequence
     * @return if the string contains letters
     */
    static boolean containsLetters(final CharSequence sequence) {
        for (int i = 0; i < sequence.length(); i++)
            if (Character.isLetter(sequence.charAt(i)))
                return true;

        return false;
    }

    /**
     * Check if the string contains numbers
     *
     * @param sequence the text sequence
     * @return if the string contains numbers
     */
    static boolean containsNumbers(final CharSequence sequence) {
        for (int i = 0; i < sequence.length(); i++)
            if (Character.isDigit(sequence.charAt(i)))
                return true;

        return false;
    }

    /**
     * Check if the object is null or empty in case of string
     *
     * @param check the object to check
     * @return if the object is null or empty
     */
    static boolean isNullOrEmpty(final Object check) {
        return check == null || check.toString().replaceAll("\\s", "").isEmpty();
    }

    /**
     * Get the version level of the two versions
     *
     * @param v1 the version 1
     * @param v2 the version 2
     * @return 1 if v1 is over v2, -1 if v1 is under v2, 0 if v1 equals v2
     */
    static int compareTo(final String v1, final String v2) {
        String[] v1parts = v1.split("\\.");
        String[] v2parts = v2.split("\\.");
        int length = Math.max(v1parts.length, v2parts.length);
        for(int i = 0; i < length; i++) {
            int thisPart = i < v1parts.length ?
                    Integer.parseInt(v1parts[i]) : 0;
            int thatPart = i < v2parts.length ?
                    Integer.parseInt(v2parts[i]) : 0;
            if(thisPart < thatPart)
                return -1;
            if(thisPart > thatPart)
                return 1;
        }

        return 0;
    }

    /**
     * Remove the numbers from the specified string
     *
     * @param original the original string
     * @return the string without numbers
     */
    static String removeNumbers(final String original) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < original.length(); i++) {
            char character = original.charAt(i);
            if (!Character.isDigit(character))
                builder.append(character);
        }

        return builder.toString();
    }

    /**
     * Remove the letters from the specified string
     *
     * @param original the original string
     * @return the string without letters
     */
    static String removeLetters(final String original) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < original.length(); i++) {
            char character = original.charAt(i);
            if (!Character.isLetter(character)) {
                builder.append(character);
            }
        }

        return builder.toString();
    }

    /**
     * Get only the numbers of the string
     *
     * @param original the original string
     * @param keep the characters to keep
     * @return the string with only numbers
     */
    static String parseNumbers(final String original, final Character... keep) {
        StringBuilder builder = new StringBuilder();
        Set<Character> chars = arrayToSet(keep);
        for (int i = 0; i < original.length(); i++) {
            char character = original.charAt(i);
            if (Character.isDigit(character) || chars.contains(character)) {
                builder.append(character);
            }
        }

        return builder.toString();
    }

    /**
     * Get only the text of the string
     *
     * @param original the original string
     * @param keep the characters to keep
     * @return the string with only letters
     */
    static String parseLetters(final String original, final Character... keep) {
        StringBuilder builder = new StringBuilder();
        Set<Character> chars = arrayToSet(keep);
        for (int i = 0; i < original.length(); i++) {
            char character = original.charAt(i);
            if (Character.isLetter(character) || chars.contains(character)) {
                builder.append(character);
            }
        }

        return builder.toString();
    }

    /**
     * The random string generation string contents
     */
    enum StringGen {
        /** Generate string with only numbers */ ONLY_NUMBERS,
        /** Generate string with only letters */ ONLY_LETTERS,
        /** Generate string with numbers and letters */ NUMBERS_AND_LETTERS
    }

    /**
     * Random string generation string type
     */
    enum StringType {
        /** Generate string with all upper */ ALL_UPPER,
        /** Generate string with all lower */ ALL_LOWER,
        /** Generate string randomly upper and lower */ RANDOM_SIZE
    }

    /**
     * Converse the array into a set
     *
     * @param array the array
     * @param <T> the array type
     * @return the array as set
     */
    static <T> Set<T> arrayToSet(final T[] array) {
        return new HashSet<>(Arrays.asList(array));
    }
}
