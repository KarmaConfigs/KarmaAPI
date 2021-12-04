package ml.karmaconfigs.api.common;

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

import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.PrefixConsoleData;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import ml.karmaconfigs.api.common.utils.enums.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Karma console
 */
public final class Console {

    /**
     * The custom message actions
     */
    private final static Map<KarmaSource, Consumer<String>> messageActions = new ConcurrentHashMap<>();
    /**
     * The console source
     */
    private final KarmaSource source;

    /**
     * Initialize a new console
     *
     * @param src the console source
     */
    public Console(final KarmaSource src) {
        source = src;
    }

    /**
     * Initialize a new console
     *
     * @param src the console source
     * @param onMessage the console message action
     */
    public Console(final KarmaSource src, final Consumer<String> onMessage) {
        this.source = src;
        if (onMessage != null && messageActions.getOrDefault(src, null) == null) {
            send("&b[ KarmaAPI &b]&7 Using custom console message sender");
        }

        messageActions.put(src, onMessage);
    }

    /**
     * Get the console prefix data
     *
     * @return this source prefix console data
     */
    public PrefixConsoleData getData() {
        return new PrefixConsoleData(this.source);
    }

    /**
     * Send a message to the console
     *
     * @param message the message to send
     */
    public void send(final CharSequence message) {
        Consumer<String> messageAction = messageActions.getOrDefault(source, null);

        if (messageAction == null) {
            System.out.println("\033[0m" + StringUtils.toConsoleColor(message) + "\033[0m");
        } else {
            messageAction.accept(String.valueOf(message));
        }
    }

    /**
     * Send a message to the console
     *
     * @param message the message to send
     * @param replaces the message replaces
     */
    public void send(final CharSequence message, final Object... replaces) {
        Consumer<String> messageAction = messageActions.getOrDefault(source, null);

        String tmpMessage = String.valueOf(message);
        for (int i = 0; i < replaces.length; i++) {
            String placeholder = "{" + i + "}";
            String value = replaces[i].toString();
            tmpMessage = tmpMessage.replace(placeholder, value);
        }
        if (messageAction == null) {
            System.out.println("\033[0m" + StringUtils.toConsoleColor(tmpMessage) + "\033[0m");
        } else {
            messageAction.accept(tmpMessage);
        }
    }

    /**
     * Send a message to the console
     *
     * @param message the message to send
     * @param level the message level
     */
    public void send(final @NotNull CharSequence message, final @NotNull Level level) {
        Consumer<String> messageAction = messageActions.getOrDefault(source, null);

        String tmpMessage = String.valueOf(message);
        String prefix = "&b[ &fALERT &b] &7NONE: &b";
        PrefixConsoleData data = getData();
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
        if (messageAction == null) {
            if (tmpMessage.contains("\n")) {
                for (String msg : tmpMessage.split("\n"))
                    send(msg);
            } else {
                send(prefix + tmpMessage);
            }
        } else {
            if (tmpMessage.contains("\n"))
                tmpMessage = StringUtils.listToString(Arrays.asList(tmpMessage.split("\n")), false);

            messageAction.accept(prefix + tmpMessage);
        }
    }

    /**
     * Send a message to the console
     *
     * @param message the message to send
     * @param level the message level
     * @param replaces the message replaces
     */
    public void send(final @NotNull CharSequence message, final @NotNull Level level, final @NotNull Object... replaces) {
        Consumer<String> messageAction = messageActions.getOrDefault(source, null);

        String tmpMessage = String.valueOf(message);
        String prefix = "&b[ &fALERT &b] &7NONE: &b";
        PrefixConsoleData data = getData();
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
            String value = replaces[i].toString();
            tmpMessage = tmpMessage.replace(placeholder, value);
        }
        tmpMessage = StringUtils.stripColor(tmpMessage);
        if (messageAction == null) {
            if (tmpMessage.contains("\n")) {
                for (String msg : tmpMessage.split("\n"))
                    send(msg);
            } else {
                send(prefix + tmpMessage);
            }
        } else {
            if (tmpMessage.contains("\n"))
                tmpMessage = StringUtils.listToString(Arrays.asList(tmpMessage.split("\n")), false);

            messageAction.accept(prefix + tmpMessage);
        }
    }
}
