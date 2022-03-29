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

import ml.karmaconfigs.api.common.JavaVM;
import ml.karmaconfigs.api.common.karma.KarmaAPI;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.PrefixConsoleData;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.utils.placeholder.GlobalPlaceholderEngine;
import ml.karmaconfigs.api.common.utils.placeholder.SimplePlaceholder;
import ml.karmaconfigs.api.common.utils.placeholder.util.Placeholder;
import ml.karmaconfigs.api.common.utils.string.color.ConsoleColor;
import ml.karmaconfigs.api.common.utils.string.util.SplitIndex;
import ml.karmaconfigs.api.common.utils.string.util.time.CleanTimeBuilder;
import ml.karmaconfigs.api.common.utils.string.util.time.TimeName;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Karma string utilities
 */
public final class StringUtils {

    /**
     * Single color identifier character
     */
    public static char SINGLE_COLOR_IDENTIFIER = '&';

    /**
     * Color letters
     */
    private final static List<Character> COLOR_LETTERS = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'r', 'k', 'l', 'm', 'n', 'o');

    /**
     * Map containing Pattern => Non windows color replacement
     */
    private final static Map<Pattern, String> NON_WINDOWS_REPLACEMENT = new ConcurrentHashMap<>();

    /**
     * Map containing Pattern => Windows color replacement
     */
    private final static Map<Pattern, String> WINDOWS_REPLACEMENT = new ConcurrentHashMap<>();

    static {
        for (Character character : COLOR_LETTERS) {
            Pattern pattern = Pattern.compile(String.valueOf(SINGLE_COLOR_IDENTIFIER) + character);
            switch (character) {
                case '0':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[0;38;2;0;0;0m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[30m");
                    break;
                case '1':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[0;38;2;0;0;128m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[94m");
                    break;
                case '2':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[0;38;2;0;128;0m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[92m");
                    break;
                case '3':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[0;38;2;0;128;128m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[96m");
                    break;
                case '4':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[0;38;2;128;0;0m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[91m");
                    break;
                case '5':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[0;38;2;128;0;128m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[95m");
                    break;
                case '6':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[0;38;2;128;128;0m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[93m");
                    break;
                case '7':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[0;38;2;192;192;192m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[90m");
                    break;
                case '8':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[0;38;2;128;128;128m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[97m");
                    break;
                case '9':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[0;38;2;0;95;255m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[34m");
                    break;
                case 'a':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[0;38;2;0;255;0m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[32m");
                    break;
                case 'b':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[0;38;2;0;255;255m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[36m");
                    break;
                case 'c':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[0;38;2;255;0;0m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[31m");
                    break;
                case 'd':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[0;38;2;255;0;255m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[35m");
                    break;
                case 'e':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[0;38;2;255;255;0m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[33m");
                    break;
                case 'f':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[0;38;2;255;255;255m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[37m");
                    break;
                case 'r':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[0m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[0m");
                    break;
                case 'l':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[0;1m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[1m");
                    break;
                case 'n':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[4m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[4m");
                    break;
                case 'o':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[3m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[3m");
                    break;
                case 'm':
                    NON_WINDOWS_REPLACEMENT.put(pattern, "\033[9m");
                    WINDOWS_REPLACEMENT.put(pattern, "\u001B[9m");
                    break;
                case 'k':
                default:
                    break;
            }
        }
    }

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
     * Insert a text each amount of characters
     *
     * @param text the original text
     * @param insert the text to insert
     * @param period the amount of characters
     * @return the formatted text
     */
    public static String insertInEach(final String text, final String insert, final int period) {
        StringBuilder builder = new StringBuilder(text.length() + insert.length() * (text.length()/period)+1);

        int index = 0;
        String prefix = "";
        while (index < text.length()) {
            builder.append(prefix);
            prefix = insert;
            builder.append(text, index, Math.min(index + period, text.length()));
            index += period;
        }

        StringBuilder fixed = new StringBuilder();
        String[] data = builder.toString().split(insert);
        for (String str : data) {
            if (str.startsWith(" ")) {
                int character = 0;
                for (int i = 0; i < str.length(); i++) {
                    char charAt = str.charAt(i);
                    if (Character.isSpaceChar(charAt)) {
                        character++;
                    } else {
                        break;
                    }
                }

                fixed.append(str.substring(character)).append(insert);
            } else {
                fixed.append(str).append(insert);
            }
        }

        return StringUtils.replaceLast(fixed.toString(), insert, "");
    }

    /**
     * Replace the max amount of characters starting from
     * the specified index
     *
     * @param text the text to replace
     * @param replacement the character to replace with
     * @param start the beginning index of scan
     * @param max the max index to scan
     * @param ignored the ignored characters
     * @return the replaced text
     */
    public static String smartReplace(final String text, final char replacement, final int start, final int max, char... ignored) {
        StringBuilder builder;
        if (max == 0) {
            builder = new StringBuilder();
        } else {
            builder = new StringBuilder(text.substring(0, start));
        }

        Set<Character> charIgnored = new HashSet<>();
        for (char character : ignored) {
            charIgnored.add(character);
        }

        for (int i = start; i < max; i++) {
            char character = text.charAt(i);
            System.out.println(character);
            if (!charIgnored.contains(character)) {
                builder.append(replacement);
            } else {
                builder.append(character);
            }
        }
        if (max != text.length())
            builder.append(text.substring(max));

        return builder.toString();
    }

    /**
     * Insert a text each amount of characters as
     * soon as the character is a space/empty
     *
     * @param text the original text
     * @param insert the text to insert
     * @param period the amount of characters
     * @param replaceSpace replace the empty
     *                     character with the insert
     * @return the formatted text
     */
    public static String insertInEachSpace(final String text, final String insert, final int period, final boolean replaceSpace) {
        StringBuilder builder = new StringBuilder();

        int index = 0;
        for (int i = 0; i < text.length(); i++) {
            if (index++ >= period) {
                char character = text.charAt(i);
                if (Character.isSpaceChar(character)) {
                    builder.append((replaceSpace ? insert : character + insert));
                } else {
                    builder.append(character);
                }

                index = 0;
            }
        }

        return builder.toString();
    }

    /**
     * Split a text by each characters
     *
     * @param text the original text
     * @param period the amount of characters
     * @return the split text
     */
    public static String[] splitInEach(final String text, final int period) {
        String result = insertInEach(text, "\n", period);
        if (result.contains("\n"))
            return result.split("\n");

        return new String[]{result};
    }

    /**
     * Split a text by each characters
     * if it's a space
     *
     * @param text the original text
     * @param period the amount of characters
     * @param replaceSpace replace the empty
     *                     character with the insert
     * @return the split text
     */
    public static String[] splitInEachSpace(final String text, final int period, final boolean replaceSpace) {
        String result = insertInEachSpace(text, "\n", period, replaceSpace);
        if (result.contains("\n"))
            return result.split("\n");

        return new String[]{result};
    }

    /**
     * Split a text between the specified indexes
     *
     * @param text the text to split
     * @param start the start index
     * @param end the end index
     * @return the split text
     */
    public static String[] splitBetween(final String text, final int start, final int end) {
        String before;
        String then;
        String extra;
        if (start != 0) {
            before = text.substring(0, start);
            then = text.substring(start, end);
            if (end != text.length()) {
                extra = text.substring(end);
            } else {
                extra = null;
            }
        } else {
            before = text.substring(start, end);
            if (end != text.length()) {
                then = text.substring(end);
            } else {
                then = null;
            }

            extra = null;
        }

        return new String[]{before, then, extra};
    }

    /**
     * Split a text between the specified indexes
     *
     * @param text the text to split
     * @param indexes the indexes
     * @return the split text
     */
    public static String[] splitBetween(final String text, SplitIndex... indexes) {
        List<String> sliced = new ArrayList<>();

        if (indexes.length > 0) {
            for (int i = 0; i < indexes.length; i++) {
                SplitIndex index = indexes[i];
                SplitIndex next = null;
                if (i + 1 != indexes.length) {
                    next = indexes[i + 1];
                }

                int start = index.getStart();
                int end = index.getEnd();

                if (start != 0 && i == 0) {
                    String result = text.substring(0, start);

                    sliced.add(result);
                }

                String tmpResult = text.substring(start, end);
                sliced.add(tmpResult);

                if (end != text.length() && i == (indexes.length - 1)) {
                    String result = text.substring(end);
                    sliced.add(result);
                } else {
                    if (i != (indexes.length - 1) && next != null) {
                        int nextStart = next.getStart();

                        if (nextStart != end) {
                            String result = text.substring(end, nextStart);
                            sliced.add(result);
                        }
                    }
                }
            }
        } else {
            sliced.add(text);
        }

        return sliced.toArray(new String[0]);
    }

    /**
     * Glue an array of strings
     *
     * @param array the string array
     * @return the array of strings together
     */
    public static String glue(final String... array) {
        StringBuilder builder = new StringBuilder();
        for (String str : array)
            builder.append(str);

        return builder.toString();
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
     * @deprecated The new function {@link StringUtils#toAnyOsColor(CharSequence)} is more efficient and
     * should work on any OS. This method won't work on Windows terminal and/or other
     */
    @Deprecated
    public static String toConsoleColor(final CharSequence text) {
        String str = String.valueOf(text);
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
     * Parse the string to any type of color at any OS
     *
     * @param text the text to parse colors
     * @return the colored text
     */
    public static String toAnyOsColor(final CharSequence text) {
        String str = text.toString().replace("\u00a7", "&");

        switch (JavaVM.getSystem()) {
            case WINDOWS:
                for (Pattern pattern : WINDOWS_REPLACEMENT.keySet()) {
                    Matcher matcher = pattern.matcher(str);
                    str = matcher.replaceAll(WINDOWS_REPLACEMENT.get(pattern));
                }
                break;
            case MAC:
            case LINUX:
            case OTHER:
            default:
                for (Pattern pattern : NON_WINDOWS_REPLACEMENT.keySet()) {
                    Matcher matcher = pattern.matcher(str);
                    str = matcher.replaceAll(NON_WINDOWS_REPLACEMENT.get(pattern));
                }
                break;
        }

        return str;
    }

    /**
     * Get the text replacing any os color with a single color character
     *
     * @param text the console text
     * @return the text with single color character instead of os color character
     */
    public static String fromAnyOsColor(final CharSequence text) {
        String str = text.toString().replace("\u00a7", "&");

        switch (JavaVM.getSystem()) {
            case WINDOWS:
                for (Pattern pattern : WINDOWS_REPLACEMENT.keySet()) {
                    String replacement = WINDOWS_REPLACEMENT.getOrDefault(pattern, null);
                    if (replacement != null) {
                        str = str.replace(replacement, pattern.pattern());
                    }
                }
                break;
            case MAC:
            case LINUX:
            case OTHER:
            default:
                for (Pattern pattern : NON_WINDOWS_REPLACEMENT.keySet()) {
                    String replacement = NON_WINDOWS_REPLACEMENT.getOrDefault(pattern, null);
                    if (replacement != null) {
                        str = str.replace(replacement, pattern.pattern());
                    }
                }
                break;
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
        String color = "";

        for (int i = 0; i < text.length(); i++) {
            char curr = text.charAt(i);
            char next = Character.MIN_VALUE;
            if (i + 1 != text.length())
                next = text.charAt(i + 1);
            if (next != '\000' && !Character.isSpaceChar(next) && (
                    curr == '&' || curr == '\u00A7'))
                color = String.valueOf(curr) + next;
        }

        return color;
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
        String color = "";

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
                    if (next != '\u0000' && !Character.isSpaceChar(next) && (
                            curr == '&' || curr == '\u00A7'))
                        color = String.valueOf(curr) + next;
                }
            } catch (Throwable ignored) {
            }

        return color;
    }

    /**
     * Get a set of colors present in
     * the text
     *
     * @param text the text to read from
     * @return the text colors
     */
    public static Set<Character> getCharColors(final String text) {
        Set<Character> color_codes = new LinkedHashSet<>();

        for (int i = 0; i < text.length(); i++) {
            char curr = text.charAt(i);
            char next = Character.MIN_VALUE;
            if (i + 1 != text.length())
                next = text.charAt(i + 1);
            if (next != '\u0000' && !Character.isSpaceChar(next) &&
                    curr == '&')
                color_codes.add(next);
        }

        return color_codes;
    }

    /**
     * Get the last color present on a text
     *
     * @param text the text to read from
     * @return the text colors
     */
    public static char getLastCharColor(final String text) {
        char color = '\u0000';

        for (int i = 0; i < text.length(); i++) {
            char curr = text.charAt(i);
            char next = Character.MIN_VALUE;
            if (i + 1 != text.length())
                next = text.charAt(i + 1);
            if (next != '\u0000' && !Character.isSpaceChar(next) && (
                    curr == '&' || curr == '\u00A7'))
                color = next;
        }

        return color;
    }

    /**
     * Get the last color from a list of
     * texts
     *
     * @param texts the list of texts
     * @param index the text index
     * @return the texts last color
     */
    public static char getLastCharColor(final List<String> texts, final int index) {
        char color = '\u0000';

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
                    if (next != '\u0000' && !Character.isSpaceChar(next) && (
                            curr == '&' || curr == '\u00A7'))
                        color = next;
                }
            } catch (Throwable ignored) {
            }

        return color;
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
            if (next != '\u0000' && !Character.isSpaceChar(next) && (
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
                if (next != '\u0000' && !Character.isSpaceChar(next) && (
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
        ConsoleColor[] colors = ConsoleColor.values();
        int random = new Random().nextInt(colors.length);
        if (random == colors.length)
            random--;

        ConsoleColor color = colors[random];
        if (color.isCustom())
            color = ConsoleColor.customColor("k");

        return color.getCode();
    }

    /**
     * Format the specified text
     *
     * @param text the text to format
     * @param replaces the text replaces
     * @return the formatted text
     */
    public static String formatString(final CharSequence text, final Object... replaces) {
        String str = String.valueOf(text);
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
     *
     * @deprecated It's better to use {@link ml.karmaconfigs.api.common.utils.placeholder.util.PlaceholderEngine now}. By
     * default, KarmaAPI uses {@link ml.karmaconfigs.api.common.utils.placeholder.GlobalPlaceholderEngine} with a implementation
     * of {@link ml.karmaconfigs.api.common.utils.placeholder.util.Placeholder} as {@link ml.karmaconfigs.api.common.utils.placeholder.SimplePlaceholder}. For
     * now, this method registers the map key to a placeholder which contains the map value and uses global placeholder engine to return the formatted string
     */
    @Deprecated
    public static String formatString(final CharSequence text, final Map<String, Object> replaces) {
        Set<Placeholder<Object>> placeholders = new HashSet<>();

        for (String key : replaces.keySet()) {
            Object value = replaces.getOrDefault(key, null);

            if (value != null) {
                placeholders.add(new SimplePlaceholder<>(key, value));
            }
        }

        GlobalPlaceholderEngine engine = new GlobalPlaceholderEngine(KarmaAPI.source(true));
        placeholders.forEach(engine::forceRegister);

        return engine.parse(String.valueOf(text));
    }

    /**
     * Format the specified text
     *
     * @param owner the text owner
     * @param text the text to format
     * @param level the text level
     * @return the formatted text
     */
    public static String formatString(final KarmaSource owner, final CharSequence text, final Level level) {
        String tmpMessage = String.valueOf(text);
        String prefix = "&b[ &fALERT &b] &7NONE: &b";
        PrefixConsoleData data = new PrefixConsoleData(owner);
        switch (level) {
            case OK:
                prefix = data.getOkPrefix();
                break;
            case INFO:
                prefix = data.getInfoPrefix();
                break;
            case WARNING:
                prefix = data.getWarningPrefix();
                break;
            case GRAVE:
                prefix = data.getGravePrefix();
                break;
        }

        tmpMessage = StringUtils.stripColor(tmpMessage);
        return prefix + tmpMessage;
    }

    /**
     * Format the specified text
     *
     * @param owner the text owner
     * @param text the text to format
     * @param level the text level
     * @param replaces the text replaces
     * @return the formatted text
     */
    public static String formatString(final KarmaSource owner, final CharSequence text, final Level level, final Object... replaces) {
        String tmpMessage = String.valueOf(text);
        String prefix = "&b[ &fALERT &b] &7NONE: &b";
        PrefixConsoleData data = new PrefixConsoleData(owner);
        switch (level) {
            case OK:
                prefix = data.getOkPrefix();
                break;
            case INFO:
                prefix = data.getInfoPrefix();
                break;
            case WARNING:
                prefix = data.getWarningPrefix();
                break;
            case GRAVE:
                prefix = data.getGravePrefix();
                break;
        }

        for (int i = 0; i < replaces.length; i++) {
            String placeholder = "{" + i + "}";
            String value = String.valueOf(replaces[i]);
            tmpMessage = tmpMessage.replace(placeholder, value);
        }

        tmpMessage = StringUtils.stripColor(tmpMessage);
        return prefix + tmpMessage;
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
     * @param <T> the objet type
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
        if (check instanceof Object[]) {
            Object[] array = (Object[]) check;
            return array.length <= 0;
        } else {
            if (check instanceof Iterable<?>) {
                Iterable<?> collection = (Iterable<?>) check;
                boolean parsed = false;
                for (Object obj : collection) {
                    if (String.valueOf(obj).replaceAll("\\s", "").isEmpty() || obj.toString().replaceAll("\\s", "").isEmpty()) {
                        return true;
                    }

                    parsed = true;
                }

                return !parsed;
            } else {
                if (check != null) {
                    return String.valueOf(check).replaceAll("\\s", "").isEmpty() || check.toString().replaceAll("\\s", "").isEmpty();
                } else {
                    return true;
                }
            }
        }
    }

    /**
     * Get if the objects are null or empty
     *
     * @param checks the objects to check
     * @return if the objects are null or empty
     */
    public static boolean areNullOrEmpty(final Object... checks) {
        for (Object check : checks) {
            if (check != null) {
                if (check instanceof Object[]) {
                    Object[] array = (Object[]) check;
                    if (array.length <= 0)
                        return true;
                } else {
                    if (check instanceof Iterable<?>) {
                        Iterable<?> collection = (Iterable<?>) check;
                        for (Object obj : collection) {
                            if (StringUtils.isNullOrEmpty(obj)) {
                                return true;
                            }
                        }
                    } else {
                        if (String.valueOf(check).replaceAll("\\s", "").isEmpty() || check.toString().replaceAll("\\s", "").isEmpty()) {
                            return true;
                        }
                    }
                }
            } else {
                return true;
            }
        }

        return false;
    }

    /**
     * Get if the objects are null or empty
     *
     * @param checkAll if false, it will return true as soon
     *                 as any of the objects, are null or empty
     * @param checks the objects to check
     * @return if the objects are null or empty
     */
    public static boolean areNullOrEmpty(final boolean checkAll, final Object... checks) {
        int nullOrEmpty = 0;

        for (Object check : checks) {
            if (check != null) {
                if (check instanceof Object[]) {
                    Object[] array = (Object[]) check;
                    if (array.length <= 0) {
                        if (!checkAll) {
                            return true;
                        } else {
                            nullOrEmpty++;
                        }
                    }
                } else {
                    if (check instanceof Iterable<?>) {
                        Iterable<?> collection = (Iterable<?>) check;
                        for (Object obj : collection) {
                            if (StringUtils.isNullOrEmpty(obj)) {
                                if (checkAll) {
                                    return true;
                                } else {
                                    nullOrEmpty++;
                                }
                            }
                        }
                    } else {
                        if (String.valueOf(check).replaceAll("\\s", "").isEmpty() || check.toString().replaceAll("\\s", "").isEmpty()) {
                            if (!checkAll) {
                                return true;
                            } else {
                                nullOrEmpty++;
                            }
                        }
                    }
                }
            } else {
                if (!checkAll) {
                    return true;
                } else {
                    nullOrEmpty++;
                }
            }
        }

        return nullOrEmpty == checks.length;
    }

    /**
     * Get if the string is equals the other
     *
     * @param str1 the string
     * @param str2 the other string
     * @return if both strings are the same
     */
    public static boolean equals(final String str1, final String str2) {
        byte[] sum1 = str1.getBytes(StandardCharsets.UTF_8);
        byte[] sum2 = str2.getBytes(StandardCharsets.UTF_8);

        int high = sum1.length;
        if (sum1.length < sum2.length) {
            high = sum2.length;

            int diff = high - sum1.length;
            List<Byte> bytes = new ArrayList<>();
            for (int i = 0; i < diff; i++) {
                bytes.add((byte) 0);
            }
            for (byte b : sum1)
                bytes.add(b);

            byte[] tmp = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i++)
                tmp[i] = bytes.get(i);

            sum1 = tmp;
        } else {
            int diff = high - sum2.length;
            List<Byte> bytes = new ArrayList<>();
            for (int i = 0; i < diff; i++) {
                bytes.add((byte) 0);
            }
            for (byte b : sum2)
                bytes.add(b);

            byte[] tmp = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i++)
                tmp[i] = bytes.get(i);

            sum2 = tmp;
        }

        for (int i = 0; i < high; i++) {
            byte b1 = sum1[i];
            byte b2 = sum2[i];

            String bin1 = Integer.toBinaryString(b1);
            String bin2 = Integer.toBinaryString(b2);

            int max = bin1.length();
            if (bin1.length() < bin2.length()) {
                max = bin2.length();
                bin1 = String.format("%0" + max + "d", Integer.parseInt(bin1));
            } else {
                bin2 = String.format("%0" + max + "d", Integer.parseInt(bin2));
            }

            for (int x = 0; x < max; x++) {
                char b1char = bin1.charAt(x);
                char b2char = bin2.charAt(x);

                String b1str = String.valueOf(b1char);
                String b2str = String.valueOf(b2char);

                int s1 = Integer.parseInt(b1str);
                int s2 = Integer.parseInt(b2str);

                if (s1 != s2) {
                    return false;
                }
            }
        }

        return true;
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
     * Convert the time in milliseconds
     * into a readable time string format
     *
     * @param milliseconds the milliseconds
     * @return the time in seconds
     */
    public static String timeToString(final long milliseconds) {
        CleanTimeBuilder builder = new CleanTimeBuilder(TimeName.create(), milliseconds);
        return builder.create();
    }

    /**
     * Convert the time in milliseconds
     * into a readable time string format
     *
     * @param milliseconds the milliseconds
     * @param name the unit names
     * @return the time in seconds
     */
    public static String timeToString(final long milliseconds, final TimeName name) {
        CleanTimeBuilder builder = new CleanTimeBuilder(name, milliseconds);
        return builder.create();
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
