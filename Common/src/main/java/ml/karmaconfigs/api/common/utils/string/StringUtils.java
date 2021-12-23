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

import ml.karmaconfigs.api.common.karma.KarmaAPI;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.PrefixConsoleData;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.utils.placeholder.GlobalPlaceholderEngine;
import ml.karmaconfigs.api.common.utils.placeholder.SimplePlaceholder;
import ml.karmaconfigs.api.common.utils.placeholder.util.Placeholder;
import ml.karmaconfigs.api.common.utils.string.util.time.CleanTimeBuilder;
import ml.karmaconfigs.api.common.utils.string.util.time.TimeName;
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
     * Insert a text each amount of characters
     *
     * @param text the original text
     * @param insert the text to insert
     * @param period the amount of characters
     * @return the formatted text
     */
    public static String insetInEach(final String text, final String insert, final int period) {
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
     * @return the splitted text
     */
    public static String[] splitInEach(final String text, final int period) {
        String result = insetInEach(text, "\n", period);
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
     * @return the splitted text
     */
    public static String[] splitInEachSpace(final String text, final int period, final boolean replaceSpace) {
        String result = insertInEachSpace(text, "\n", period, replaceSpace);
        if (result.contains("\n"))
            return result.split("\n");

        return new String[]{result};
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
        if (check != null) {
            return String.valueOf(check).replaceAll("\\s", "").isEmpty() || check.toString().replaceAll("\\s", "").isEmpty();
        } else {
            return true;
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
                if (!String.valueOf(check).replaceAll("\\s", "").isEmpty()) {
                    if (check.toString().replaceAll("\\s", "").isEmpty()) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }

        return false;
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
        /*
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long weeks = days / 7;
        long months = weeks / 4;
        long years = months / 12;

        StringBuilder builder = new StringBuilder();
        if (years >= 1 && Math.round(years) == years) {
            builder.append(Math.round(years)).append(" year(s)").append(", ")
                    .append(Math.abs(years * 12 - months)).append(" month(s)").append(", ")
                    .append(Math.abs(months * 4 - weeks)).append(" week(s)").append(", ")
                    .append(Math.abs(weeks * 7 - days)).append(" day(s)").append(", ")
                    .append(Math.abs(days * 24 - hours)).append(" hour(s)").append(", ")
                    .append(Math.abs(hours * 60 - minutes)).append(" minute(s)").append(" and ")
                    .append(Math.abs(minutes * 60 - seconds)).append(" second(s)");
        } else {
            if (months >= 1 && Math.round(months) == months) {
                builder.append(Math.round(months)).append(" month(s)").append(", ")
                        .append(Math.abs(months * 4 - weeks)).append(" week(s)").append(", ")
                        .append(Math.abs(weeks * 7 - days)).append(" day(s)").append(", ")
                        .append(Math.abs(days * 24 - hours)).append(" hour(s)").append(", ")
                        .append(Math.abs(hours * 60 - minutes)).append(" minute(s)").append(" and ")
                        .append(Math.abs(minutes * 60 - seconds)).append(" second(s)");
            } else {
                if (weeks >= 1 && Math.round(weeks) == weeks) {
                    builder.append(Math.round(weeks)).append(" week(s)").append(", ")
                            .append(Math.abs(weeks * 7 - days)).append(" day(s)").append(", ")
                            .append(Math.abs(days * 24 - hours)).append(" hour(s)").append(", ")
                            .append(Math.abs(hours * 60 - minutes)).append(" minute(s)").append(" and ")
                            .append(Math.abs(minutes * 60 - seconds)).append(" second(s)");
                } else {
                    if (days >= 1) {
                        builder.append(Math.round(days)).append(" day(s)").append(", ")
                                .append(Math.abs((days * 24 - hours))).append(" hour(s)").append(", ")
                                .append(Math.abs((hours * 60 - minutes))).append(" minute(s)").append(" and ")
                                .append(Math.abs((minutes * 60 - seconds))).append(" second(s)");
                    } else {
                        if (hours >= 1) {
                            builder.append(Math.round(hours)).append(" hour(s)").append(", ")
                                    .append(Math.abs((hours * 60 - minutes))).append(" minute(s)").append(" and ")
                                    .append(Math.abs((minutes * 60 - seconds))).append(" second(s)");
                        } else {
                            if (minutes >= 1) {
                                builder.append(Math.round(minutes)).append(" minute(s)").append(" and ")
                                        .append(Math.abs((minutes * 60 - seconds))).append(" second(s)");
                            } else {
                                builder.append(Math.round(seconds)).append(" second(s)");
                            }
                        }
                    }
                }
            }
        }

        return builder.toString();*/

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
        /*
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long weeks = days / 7;
        long months = weeks / 4;
        long years = months / 12;

        int month = (int) Math.abs(years * 12 - months);
        int week = (int) Math.abs(months * 4 - weeks);
        int day = (int) Math.abs(weeks * 7 - days);
        int hour = (int) Math.abs(days * 24 - hours);
        int minute = (int) Math.abs(hours * 60 - minutes);
        int second = (int) Math.abs(minutes * 60 - seconds);

        StringBuilder builder = new StringBuilder();
        if (years >= 1 && Math.round(years) == years) {
            builder.append(Math.round(years)).append(" ").append((Math.round(years) > 1 ? name.get(YEARS) : name.get(YEAR))).append(", ")
                    .append(month).append(" ").append((month > 1 ? name.get(MONTHS) : name.get(MONTH))).append(", ")
                    .append(week).append(" ").append((week > 1 ? name.get(WEEKS) : name.get(WEEK))).append(", ")
                    .append(day).append(" ").append((day > 1 ? name.get(DAYS) : name.get(DAY))).append(", ")
                    .append(hour).append(" ").append((hour > 1 ? name.get(HOURS) : name.get(HOUR))).append(", ")
                    .append(minute).append(" ").append((minute > 1 ? name.get(MINUTES) : name.get(MINUTE))).append(" and ")
                    .append(second).append(" ").append((second > 1 ? name.get(SECONDS) : name.get(SECOND)));
        } else {
            if (months >= 1 && Math.round(months) == months) {
                builder.append(Math.round(month)).append(" ").append((Math.round(month) > 1 ? name.get(MONTHS) : name.get(MONTH))).append(", ")
                        .append(week).append(" ").append((week > 1 ? name.get(WEEKS) : name.get(WEEK))).append(", ")
                        .append(day).append(" ").append((day > 1 ? name.get(DAYS) : name.get(DAY))).append(", ")
                        .append(hour).append(" ").append((hour > 1 ? name.get(HOURS) : name.get(HOUR))).append(", ")
                        .append(minute).append(" ").append((minute > 1 ? name.get(MINUTES) : name.get(MINUTE))).append(" and ")
                        .append(second).append(" ").append((second > 1 ? name.get(SECONDS) : name.get(SECOND)));
            } else {
                if (weeks >= 1 && Math.round(weeks) == weeks) {
                    builder.append(Math.round(weeks)).append(" ").append((Math.round(weeks) > 1 ? name.get(WEEKS) : name.get(WEEK))).append(", ")
                            .append(day).append(" ").append((day > 1 ? name.get(DAYS) : name.get(DAY))).append(", ")
                            .append(hour).append(" ").append((hour > 1 ? name.get(HOURS) : name.get(HOUR))).append(", ")
                            .append(minute).append(" ").append((minute > 1 ? name.get(MINUTES) : name.get(MINUTE))).append(" and ")
                            .append(second).append(" ").append((second > 1 ? name.get(SECONDS) : name.get(SECOND)));
                } else {
                    if (days >= 1) {
                        builder.append(Math.round(days)).append(" ").append((Math.round(days) > 1 ? name.get(DAYS) : name.get(DAY))).append(", ")
                                .append(hour).append(" ").append((hour > 1 ? name.get(HOURS) : name.get(HOUR))).append(", ")
                                .append(minute).append(" ").append((minute > 1 ? name.get(MINUTES) : name.get(MINUTE))).append(" and ")
                                .append(second).append(" ").append((second > 1 ? name.get(SECONDS) : name.get(SECOND)));
                    } else {
                        if (hours >= 1) {
                            builder.append(Math.round(hours)).append(" ").append((Math.round(hours) > 1 ? name.get(HOURS) : name.get(HOUR))).append(", ")
                                    .append(minute).append(" ").append((minute > 1 ? name.get(MINUTES) : name.get(MINUTE))).append(" and ")
                                    .append(second).append(" ").append((second > 1 ? name.get(SECONDS) : name.get(SECOND)));
                        } else {
                            if (minutes >= 1) {
                                builder.append(Math.round(minutes)).append(" ").append((Math.round(minutes) > 1 ? name.get(MINUTES) : name.get(MINUTE))).append(" and ")
                                        .append(second).append(" ").append((second > 1 ? name.get(SECONDS) : name.get(SECOND)));
                            } else {
                                builder.append(Math.round(seconds)).append(" ").append((Math.round(seconds) > 1 ? name.get(SECONDS) : name.get(SECOND)));
                            }
                        }
                    }
                }
            }
        }

        return builder.toString();*/

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
