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

import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * Karma string utilities
 */
public final class StringUtils {

    /**
     * Replace the last regex in the text
     *
     * @param text the text to search in
     * @param regex the text to find
     * @param replace the text to replace with
     * @return the replaced text
     */
    public static String replaceLast(final String text, final String regex, final String replace) {
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
     * Transform the text colors to
     * a valid colors
     *
     * @param text the text to translate
     * @return the translated text
     */
    public static String toColor(final String text) {
        String str = text;
        HashSet<String> color_codes = new HashSet<>();
        for (int i = 0; i < text.length(); i++) {
            char curr = text.charAt(i);
            char next = Character.MIN_VALUE;
            if (i + 1 != text.length())
                next = text.charAt(i + 1);
            if (next != '\000' && !Character.isSpaceChar(next) &&
                    curr == '&')
                color_codes.add(String.valueOf(curr) + next);
        }
        for (String color_code : color_codes)
            str = str.replace(color_code, color_code.replace('&', '\u00A7'));
        return str;
    }

    /**
     * Transform the text colors to
     * a valid colors
     *
     * @param text the text to translate
     * @return the translated text
     */
    public static String toConsoleColor(final String text) {
        String str = text;
        HashSet<String> color_codes = new HashSet<>();
        for (int i = 0; i < text.length(); i++) {
            char curr = text.charAt(i);
            char next = Character.MIN_VALUE;
            if (i + 1 != text.length())
                next = text.charAt(i + 1);
            if (next != '\000' && !Character.isSpaceChar(next) && (
                    curr == '&' || curr == '\u00A7'))
                color_codes.add(String.valueOf(curr).replace("\u00A7", "&") + next);
        }
        for (String color_code : color_codes) {
            String tmp_color = "\033[0m";
            switch (color_code.toLowerCase()) {
                case "&0":
                    tmp_color = "\033[0;30m";
                    break;
                case "&1":
                    tmp_color = "\033[0;34m";
                    break;
                case "&2":
                    tmp_color = "\033[0;32m";
                    break;
                case "&3":
                    tmp_color = "\033[0;36m";
                    break;
                case "&4":
                    tmp_color = "\033[0;31m";
                    break;
                case "&5":
                    tmp_color = "\033[0;35m";
                    break;
                case "&6":
                    tmp_color = "\033[0;33m";
                    break;
                case "&7":
                    tmp_color = "\033[0;37m";
                    break;
                case "&8":
                    tmp_color = "\033[0;90m";
                    break;
                case "&9":
                    tmp_color = "\033[0;94m";
                    break;
                case "&a":
                    tmp_color = "\033[0;92m";
                    break;
                case "&b":
                    tmp_color = "\033[0;96m";
                    break;
                case "&c":
                    tmp_color = "\033[0;91m";
                    break;
                case "&d":
                    tmp_color = "\033[0;95m";
                    break;
                case "&e":
                    tmp_color = "\033[0;93m";
                    break;
                case "&f":
                    tmp_color = "\033[0;97m";
                    break;
                case "&r":
                    tmp_color = "\033[0m";
                    break;
            }
            str = str.replace(color_code, tmp_color);
        }
        return str;
    }

    /**
     * Transform the list of text to a colored
     * list of text
     *
     * @param texts the texts to translate
     * @return the colored texts
     */
    public static List<String> toColor(final List<String> texts) {
        List<String> newTexts = new ArrayList<>();
        HashSet<String> color_codes = new HashSet<>();
        for (String text : texts) {
            for (int x = 0; x < text.length(); x++) {
                char curr = text.charAt(x);
                char next = Character.MIN_VALUE;
                if (x + 1 != text.length())
                    next = text.charAt(x + 1);
                if (next != '\000' && !Character.isSpaceChar(next) &&
                        curr == '&')
                    color_codes.add(String.valueOf(curr) + next);
            }
            for (String color_code : color_codes)
                text = text.replace(color_code, color_code.replace('&', '\u00A7'));
            newTexts.add(text);
        }
        return newTexts;
    }

    /**
     * Get a set of colors present in
     * the text
     *
     * @param text the text to read from
     * @return the text colors
     */
    public static Set<String> getColors(final String text) {
        Set<String> color_codes = new HashSet<>();
        for (int i = 0; i < text.length(); i++) {
            char curr = text.charAt(i);
            char next = Character.MIN_VALUE;
            if (i + 1 != text.length())
                next = text.charAt(i + 1);
            if (next != '\000' && !Character.isSpaceChar(next) &&
                    curr == '&')
                color_codes.add(String.valueOf(curr) + next);
        }
        return color_codes;
    }

    /**
     * Get the last color present on a text
     *
     * @param text the text to read from
     * @return the text colors
     */
    public static String getLastColor(final String text) {
        List<String> color_codes = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            char curr = text.charAt(i);
            char next = Character.MIN_VALUE;
            if (i + 1 != text.length())
                next = text.charAt(i + 1);
            if (next != '\000' && !Character.isSpaceChar(next) && (
                    curr == '&' || curr == '\u00A7'))
                color_codes.add(String.valueOf(curr) + next);
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
     * Get the last color from a list of
     * texts
     *
     * @param texts the list of texts
     * @param index the text index
     * @return the texts last color
     */
    public static String getLastColor(final List<String> texts, final int index) {
        List<String> color_codes = new ArrayList<>();
        int tmpIndex = index;
        if (index == texts.size())
            tmpIndex--;
        if (texts.size() > tmpIndex)
            try {
                String text = texts.get(tmpIndex);
                for (int i = 0; i < text.length(); i++) {
                    char curr = text.charAt(i);
                    char next = Character.MIN_VALUE;
                    if (i + 1 != text.length())
                        next = text.charAt(i + 1);
                    if (next != '\000' && !Character.isSpaceChar(next) && (
                            curr == '&' || curr == '\u00A7'))
                        color_codes.add(String.valueOf(curr) + next);
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
            } catch (Throwable ignored) {
            }
        return "";
    }

    /**
     * Remove the color on the text
     *
     * @param text the text to read
     * @return the text without colors
     */
    public static String stripColor(final String text) {
        String str = text;
        HashSet<String> color_codes = new HashSet<>();
        for (int i = 0; i < str.length(); i++) {
            char curr = str.charAt(i);
            char next = Character.MIN_VALUE;
            if (i + 1 != str.length())
                next = str.charAt(i + 1);
            if (next != '\000' && !Character.isSpaceChar(next) && (
                    curr == '&' || curr == '\u00A7'))
                color_codes.add(String.valueOf(curr) + next);
        }
        for (String color_code : color_codes)
            str = str.replace(color_code, "");
        return str;
    }

    /**
     * Remove the colors from a texts
     *
     * @param texts the texts
     * @return the uncolored texts
     */
    public static List<String> stripColor(final List<String> texts) {
        List<String> newTexts = new ArrayList<>();
        for (String text : texts) {
            HashSet<String> color_codes = new HashSet<>();
            for (int x = 0; x < text.length(); x++) {
                char curr = text.charAt(x);
                char next = Character.MIN_VALUE;
                if (x + 1 != text.length())
                    next = text.charAt(x + 1);
                if (next != '\000' && !Character.isSpaceChar(next) && (
                        curr == '&' || curr == '\u00A7'))
                    color_codes.add(String.valueOf(curr) + next);
            }
            for (String color_code : color_codes)
                text = text.replace(color_code, "");
            newTexts.add(text);
        }
        return newTexts;
    }

    /**
     * Split the text from index 0 to the specified
     * index
     *
     * @param text the text
     * @param amount the index
     * @return the split text
     */
    public static String[] splitByAmount(final String text, final int amount) {
        List<String> list = new ArrayList<>();
        int index = 0;
        while (index < text.length()) {
            list.add(text.substring(index, Math.min(index + amount, text.length())));
            index += amount;
        }
        return list.toArray(new String[0]);
    }

    /**
     * Generate a new random text
     *
     * @return a random text creator
     */
    public static RandomString generateString() {
        return new RandomString();
    }

    /**
     * Generate a new random text
     *
     * @param options the random text options
     * @return a random text creator
     */
    public static RandomString generateString(final OptionsBuilder options) {
        return new RandomString(options);
    }

    /**
     * Generate a random color
     *
     * @return a random color
     */
    public static String randomColor() {
        char[] valid_colors = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        int random = (new Random()).nextInt(valid_colors.length);
        return "\u00A7" + valid_colors[random];
    }

    /**
     * Format the specified text
     *
     * @param text the text to format
     * @param replaces the text replaces
     * @return the formatted text
     */
    public static String formatString(final String text, final Object... replaces) {
        String str = text;
        for (int i = 0; i < replaces.length; i++) {
            String placeholder = "{" + i + "}";
            Object valObj = replaces[i];
            String val = "[unknown]";
            if (valObj != null) {
                try {
                    val = valObj.toString();
                } catch (Throwable ex) {
                    val = String.valueOf(replaces[i]);
                }
            }

            str = str.replace(placeholder, val);
        }
        return str;
    }

    /**
     * Format the specified text
     *
     * @param text the text to format
     * @param replaces the text replaces
     * @return the formatted text
     */
    public static String formatString(final String text, final Map<String, Object> replaces) {
        String str = text;
        for (String key : replaces.keySet()) {
            String placeholder = "{" + key + "}";
            String val = replaces.get(key).toString();
            str = str.replace(placeholder, val);
        }
        return str;
    }

    /**
     * Read the file completely
     *
     * @param file the file to read
     * @return the file content as text
     */
    public static String readFrom(final File file) {
        try {
            byte[] encoded = Files.readAllBytes(file.toPath());
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (Throwable ex) {
            return "";
        }
    }

    /**
     * Transform a list of texts into a single text line
     *
     * @param lines the lines
     * @param spaces replace new lines with spaces, otherwise,
     *               new lines will be replaced with nothing and '\n'
     *               will be added at the end of the line
     * @return the list of texts as single line text
     */
    public static String listToString(final List<String> lines, final boolean spaces) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (spaces) {
                builder.append(line).append((i < lines.size() - 1) ? " " : "");
            } else {
                builder.append(line).append((i < lines.size() - 1) ? "\n" : "");
            }
        }
        return builder.toString();
    }

    /**
     * Un scape text
     *
     * @param text the text to un scape
     * @return the unescaped text
     */
    public static String unEscapeString(final String text) {
        return text.replaceAll("\\\\", "");
    }

    /**
     * Escape text
     *
     * @param text the text to scape
     * @return the escaped text
     */
    public static String escapeString(final String text) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            Character character = text.charAt(i);
            switch (character) {
                case '$':
                case '(':
                case ')':
                case '*':
                case '+':
                case '-':
                case '.':
                case '?':
                case '[':
                case ']':
                case '^':
                case '{':
                case '|':
                case '}':
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
     * Serialize an object into a text
     *
     * @param instance the object instance
     * @return the serialized object
     */
    public static <T> String serialize(final T instance) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(instance);
            so.flush();
            return Base64.getEncoder().encodeToString(bo.toByteArray());
        } catch (Throwable ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * Load the instance as an unknown object
     *
     * @param instance the serialized instance
     * @return the instance object
     */
    @Nullable
    public static Object load(final String instance) {
        try {
            byte[] bytes = Base64.getDecoder().decode(instance);
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream si = new ObjectInputStream(bi);
            return si.readObject();
        } catch (Throwable ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Load unsafely the instance as a known
     * object
     *
     * @param instance the instance
     * @param <T> the type
     * @return the instance type
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T loadUnsafe(final String instance) {
        return (T) load(instance);
    }

    /**
     * Get if the text contains letter
     *
     * @param sequence the text
     * @return if the text contains letter
     */
    public static boolean containsLetters(final CharSequence sequence) {
        for (int i = 0; i < sequence.length(); i++) {
            if (Character.isLetter(sequence.charAt(i)))
                return true;
        }
        return false;
    }

    /**
     * Get if the text contains numbers
     *
     * @param sequence the text
     * @return if the text contains letter
     */
    public static boolean containsNumbers(final CharSequence sequence) {
        for (int i = 0; i < sequence.length(); i++) {
            if (Character.isDigit(sequence.charAt(i)))
                return true;
        }
        return false;
    }

    /**
     * Get if the object is null or empty
     *
     * @param check the object to check
     * @return if the object is null or empty
     */
    public static boolean isNullOrEmpty(final Object check) {
        if (check != null) {
            return String.valueOf(check).replaceAll("\\s", "").isEmpty() || check.toString().replaceAll("\\s", "").isEmpty();
        } else {
            return true;
        }
    }

    /**
     * Get the version difference between two versions
     *
     * @param builder the version difference builder
     * @return a new version comparator
     */
    public static VersionComparator compareTo(final ComparatorBuilder builder) {
        return new VersionComparator(builder);
    }

    /**
     * Remove the numbers from text
     *
     * @param original the original text
     * @return the text without numbers
     */
    public static String removeNumbers(final CharSequence original) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < original.length(); i++) {
            char character = original.charAt(i);
            if (!Character.isDigit(character))
                builder.append(character);
        }
        return builder.toString();
    }

    /**
     * Remove the letters from text
     *
     * @param original the original text
     * @return the text without letters
     */
    public static String removeLetters(final CharSequence original) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < original.length(); i++) {
            char character = original.charAt(i);
            if (!Character.isLetter(character))
                builder.append(character);
        }
        return builder.toString();
    }

    /**
     * Parse only the numbers from the text
     *
     * @param original the original text
     * @param keep the characters to allow
     * @return the parsed text
     */
    public static String parseNumbers(final CharSequence original, final Character... keep) {
        StringBuilder builder = new StringBuilder();
        Set<Character> chars = arrayToSet(keep);
        for (int i = 0; i < original.length(); i++) {
            char character = original.charAt(i);
            if (Character.isDigit(character) || chars.contains(character))
                builder.append(character);
        }
        return builder.toString();
    }

    /**
     * Parse only the letters from the text
     *
     * @param original the original text
     * @param keep the characters to allow
     * @return the parsed text
     */
    public static String parseLetters(final CharSequence original, final Character... keep) {
        StringBuilder builder = new StringBuilder();
        Set<Character> chars = arrayToSet(keep);
        for (int i = 0; i < original.length(); i++) {
            char character = original.charAt(i);
            if (Character.isLetter(character) || chars.contains(character))
                builder.append(character);
        }
        return builder.toString();
    }

    /**
     * Parse an array to a set
     *
     * @param array the array
     * @param <T> the array type
     * @return the {@link T[] array} as {@link Set<T> set}
     */
    public static <T> Set<T> arrayToSet(final T[] array) {
        return new HashSet<>(Arrays.asList(array));
    }
}
