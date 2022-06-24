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
import ml.karmaconfigs.api.common.karma.KarmaConfig;
import ml.karmaconfigs.api.common.utils.PrefixConsoleData;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.utils.placeholder.GlobalPlaceholderEngine;
import ml.karmaconfigs.api.common.utils.placeholder.util.PlaceholderEngine;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import ml.karmaconfigs.api.common.utils.string.color.ConsoleColor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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
    private final static Map<KarmaSource, Set<PlaceholderEngine>> engines = new ConcurrentHashMap<>();

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

        PlaceholderEngine global = new GlobalPlaceholderEngine(src);
        Set<PlaceholderEngine> stored = engines.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        stored.add(global);

        engines.put(source, stored);
    }

    /**
     * Initialize a new console
     *
     * @param src the console source
     * @param onMessage the console message action
     */
    public Console(final KarmaSource src, final Consumer<String> onMessage) {
        this.source = src;

        if (onMessage != null) {
            boolean isNew = messageActions.getOrDefault(src, null) == null;

            messageActions.put(src, onMessage);
            if (isNew) {
                KarmaConfig config = new KarmaConfig();

                if (config.debug(Level.INFO)) {
                    send(StringUtils.formatString(src, "Using custom console message sender", Level.INFO));
                }
            }
        } else {
            messageActions.remove(src);
        }

        PlaceholderEngine global = new GlobalPlaceholderEngine(src);
        Set<PlaceholderEngine> stored = engines.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        stored.add(global);

        engines.put(source, stored);
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
     * Add placeholder engine to parse automatically
     * placeholders on the messages
     *
     * @param engine the engine to add
     */
    public void addEngine(final PlaceholderEngine engine) {
        Set<PlaceholderEngine> stored = engines.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        stored.add(engine);

        engines.put(source, stored);
    }

    /**
     * Remove a placeholder engine
     *
     * @param engine the placeholder engine to remove
     */
    public void removeEngine(final PlaceholderEngine engine) {
        Set<PlaceholderEngine> added = engines.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        added.remove(engine);

        engines.put(source, added);
    }

    /**
     * Get the placeholder engines of the console
     *
     * @return the placeholder engines of the console
     */
    public Set<PlaceholderEngine> getEngines() {
        return engines.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
    }

    /**
     * Send a message to the console
     *
     * @param message the message to send
     */
    public void send(final CharSequence message) {
        Consumer<String> messageAction = messageActions.getOrDefault(source, null);

        String msg = String.valueOf(message);
        Set<PlaceholderEngine> added = engines.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        for (PlaceholderEngine engine : added)
            msg = engine.parse(msg);

        if (messageAction == null) {
            System.out.println("\033[0m" + StringUtils.toAnyOsColor(ConsoleColor.RESET.getCode() + msg + ConsoleColor.RESET.getCode()));
        } else {
            messageAction.accept(msg);
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
            String value = String.valueOf(replaces[i]);
            tmpMessage = tmpMessage.replace(placeholder, value);
        }

        Set<PlaceholderEngine> added = engines.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        for (PlaceholderEngine engine : added)
            tmpMessage = engine.parse(tmpMessage);

        if (messageAction == null) {
            System.out.println("\033[0m" +  StringUtils.toAnyOsColor(ConsoleColor.RESET.getCode() + tmpMessage + ConsoleColor.RESET.getCode()));
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
        Set<PlaceholderEngine> added = engines.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        for (PlaceholderEngine engine : added)
            tmpMessage = engine.parse(tmpMessage);

        if (messageAction == null) {
            if (tmpMessage.contains("\n")) {
                for (String msg : tmpMessage.split("\n"))
                    send( msg);
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
     */
    public void debug(final @NotNull CharSequence message, final @NotNull Level level) {
        KarmaConfig config = new KarmaConfig();

        if (config.debug(level)) {
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
            Set<PlaceholderEngine> added = engines.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
            for (PlaceholderEngine engine : added)
                tmpMessage = engine.parse(tmpMessage);

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

    /**
     * Send a message to the console
     *
     * @param message the message to send
     * @param level the message level
     */
    public void debugFile(final @NotNull CharSequence message, final @NotNull Level level) {
        KarmaConfig config = new KarmaConfig();

        if (config.fileDebug(level)) {
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
            Set<PlaceholderEngine> added = engines.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
            for (PlaceholderEngine engine : added)
                tmpMessage = engine.parse(tmpMessage);

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

    /**
     * Send a message to the console
     *
     * @param message the message to send
     * @param level the message level
     */
    public void debugUtil(final @NotNull CharSequence message, final @NotNull Level level) {
        KarmaConfig config = new KarmaConfig();

        if (config.utilDebug(level)) {
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
            Set<PlaceholderEngine> added = engines.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
            for (PlaceholderEngine engine : added)
                tmpMessage = engine.parse(tmpMessage);

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
            String value = String.valueOf(replaces[i]);
            tmpMessage = tmpMessage.replace(placeholder, value);
        }
        tmpMessage = StringUtils.stripColor(tmpMessage);
        Set<PlaceholderEngine> added = engines.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        for (PlaceholderEngine engine : added)
            tmpMessage = engine.parse(tmpMessage);

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
    public void debug(final @NotNull CharSequence message, final @NotNull Level level, final @NotNull Object... replaces) {
        KarmaConfig config = new KarmaConfig();

        if (config.debug(level)) {
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
                String value = String.valueOf(replaces[i]);
                tmpMessage = tmpMessage.replace(placeholder, value);
            }
            tmpMessage = StringUtils.stripColor(tmpMessage);
            Set<PlaceholderEngine> added = engines.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
            for (PlaceholderEngine engine : added)
                tmpMessage = engine.parse(tmpMessage);

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

    /**
     * Send a message to the console
     *
     * @param message the message to send
     * @param level the message level
     * @param replaces the message replaces
     */
    public void debugFile(final @NotNull CharSequence message, final @NotNull Level level, final @NotNull Object... replaces) {
        KarmaConfig config = new KarmaConfig();

        if (config.fileDebug(level)) {
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
                String value = String.valueOf(replaces[i]);
                tmpMessage = tmpMessage.replace(placeholder, value);
            }
            tmpMessage = StringUtils.stripColor(tmpMessage);
            Set<PlaceholderEngine> added = engines.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
            for (PlaceholderEngine engine : added)
                tmpMessage = engine.parse(tmpMessage);

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

    /**
     * Send a message to the console
     *
     * @param message the message to send
     * @param level the message level
     * @param replaces the message replaces
     */
    public void debugUtil(final @NotNull CharSequence message, final @NotNull Level level, final @NotNull Object... replaces) {
        KarmaConfig config = new KarmaConfig();

        if (config.utilDebug(level)) {
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
                String value = String.valueOf(replaces[i]);
                tmpMessage = tmpMessage.replace(placeholder, value);
            }
            tmpMessage = StringUtils.stripColor(tmpMessage);
            Set<PlaceholderEngine> added = engines.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
            for (PlaceholderEngine engine : added)
                tmpMessage = engine.parse(tmpMessage);

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
}
