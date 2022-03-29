package ml.karmaconfigs.api.common.utils.placeholder;

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

import ml.karmaconfigs.api.common.annotations.Unstable;
import ml.karmaconfigs.api.common.karma.KarmaAPI;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.placeholder.util.Placeholder;
import ml.karmaconfigs.api.common.utils.placeholder.util.PlaceholderEngine;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Global KarmaAPI placeholder engine
 */
public final class GlobalPlaceholderEngine extends PlaceholderEngine {

    private final static Map<KarmaSource, Set<Placeholder<?>>> sourcePlaceholders = new ConcurrentHashMap<>();
    private final static Map<KarmaSource, Character> open = new ConcurrentHashMap<>();
    private final static Map<KarmaSource, Character> close = new ConcurrentHashMap<>();

    private final static Set<KarmaSource> protect = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final KarmaSource source;

    /**
     * Initialize the global placeholder engine
     *
     * @param owner the placeholder owner
     */
    public GlobalPlaceholderEngine(final KarmaSource owner) {
        source = owner;

        Set<Placeholder<?>> registered = sourcePlaceholders.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        SimplePlaceholder<String> java = new SimplePlaceholder<>("karma java", KarmaAPI.getCompilerVersion());
        SimplePlaceholder<String> version = new SimplePlaceholder<>("karma version", KarmaAPI.getVersion());
        SimplePlaceholder<String> build = new SimplePlaceholder<>("karma build", KarmaAPI.getBuildDate());

        registered.add(java);
        registered.add(version);
        registered.add(build);

        sourcePlaceholders.put(source, registered);
    }

    /**
     * Set the placeholder open identifier
     *
     * @param identifier the placeholder identifier character
     */
    @Override
    public void setOpenIdentifier(final char identifier) {
        if (!Character.isLetterOrDigit(identifier) && !Character.isSpaceChar(identifier)) {
            open.put(source, identifier);
        }
    }

    /**
     * Set the placeholder close identifier
     *
     * @param identifier the placeholder identifier character
     */
    @Override
    public void setCloseIdentifier(final char identifier) {
        if (!Character.isLetterOrDigit(identifier) && !Character.isSpaceChar(identifier)) {
            close.put(source, identifier);
        }
    }

    /**
     * Protect the placeholder engine against
     * already added placeholder modifications
     */
    @Override
    public void protect() {
        KarmaSource original = KarmaAPI.source(true);
        //KarmaAPI source should never be blocked...
        if (!source.equals(original)) {
            protect.add(source);
        }
    }

    /**
     * Register more placeholders
     *
     * @param placeholders the placeholders to register
     */
    @Override
    public void register(Placeholder<?>... placeholders) {
        Set<Placeholder<?>> registered = sourcePlaceholders.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        Set<String> keys = new HashSet<>();
        registered.forEach(placeholder -> keys.add(placeholder.getKey()));

        for (Placeholder<?> placeholder : placeholders) {
            if (placeholder != null) {
                if (!keys.contains(placeholder.getKey())) {
                    registered.add(placeholder);
                }
            }
        }

        sourcePlaceholders.put(source, registered);
    }

    /**
     * Force placeholder registrations
     *
     * @param placeholders the placeholders to register
     */
    @Override
    @Unstable(reason = "Using this method may register null placeholders which can cause more issues in the future")
    public void forceRegister(final Placeholder<?>... placeholders) {
        if (!protect.contains(source)) {
            Set<Placeholder<?>> registered = sourcePlaceholders.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));

            registered.addAll(Arrays.asList(placeholders));

            sourcePlaceholders.put(source, registered);
        }
    }

    /**
     * Unregister placeholders
     *
     * @param placeholders the placeholders to unregister
     */
    @Override
    public void unregister(final String... placeholders) {
        if (!protect.contains(source)) {
            Set<Placeholder<?>> registered = sourcePlaceholders.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
            Set<String> unregister = new HashSet<>(Arrays.asList(placeholders));

            registered.forEach(placeholder -> {
                if (placeholder != null) {
                    if (unregister.contains(placeholder.getKey())) {
                        registered.remove(placeholder);
                    }
                }
            });

            sourcePlaceholders.put(source, registered);
        }
    }

    /**
     * Unregister placeholders
     *
     * @param placeholders the placeholders to unregister
     */
    @Override
    public void unregister(final Placeholder<?>... placeholders) {
        if (!protect.contains(source)) {
            Set<Placeholder<?>> registered = sourcePlaceholders.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
            Set<Placeholder<?>> unregister = new HashSet<>(Arrays.asList(placeholders));

            registered.removeAll(unregister);

            sourcePlaceholders.put(source, registered);
        }
    }

    /**
     * Get a placeholder
     *
     * @param key the placeholder identifier
     * @return the placeholder
     */
    @Override
    @Unstable(reason = "Return method may differ from stored method")
    public @Nullable @SuppressWarnings("unchecked") <T> Placeholder<T> getPlaceholder(final String key) {
        Set<Placeholder<?>> registered = sourcePlaceholders.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));

        Placeholder<T> result = null;
        for (Placeholder<?> placeholder : registered) {
            if (placeholder.getKey().equals(key)) {
                try {
                    result = (Placeholder<T>) placeholder;
                } catch (Throwable ignored) {}
            }
        }

        return result;
    }

    /**
     * Parse a message
     *
     * @param message the message
     * @param containers the placeholder containers
     * @return the parsed message
     */
    @Override
    public String parse(final String message, final Object... containers) {
        //Always escape the open character
        String startChar = StringUtils.escapeString(String.valueOf(open.getOrDefault(source, '%')));
        String closeChar = String.valueOf(close.getOrDefault(source, '%'));

        //KarmaAPI will use the custom start and open characters ( {} ) with the custom source characters
        Pattern pattern = Pattern.compile("(" + startChar + ".[^" + closeChar + "]*" + closeChar + ")|(\\{.[^}]*})");
        Map<String, String> replaces = new ConcurrentHashMap<>();

        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            String name = message.substring(start + 1, end - 1);
            String key = message.substring(start, end);

            replaces.put(name, key);
        }
        if (replaces.isEmpty() && matcher.matches()) {
            int start = matcher.start();
            int end = matcher.end();

            String name = message.substring(start + 1, end - 1);
            String key = message.substring(start, end);

            replaces.put(name, key);
        }

        Set<Placeholder<?>> registered = sourcePlaceholders.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));

        String finalMessage = message;
        for (Placeholder<?> placeholder : registered) {
            if (placeholder != null) {
                String placeholderName = placeholder.getKey();
                String placeholderKey = replaces.getOrDefault(placeholderName, null);

                if (placeholderKey != null) {
                    if (containers.length > 0) {
                        for (Object container : containers) {
                            if (container != null) {
                                if (placeholder.getType().isAssignableFrom(container.getClass())) {
                                    finalMessage = finalMessage.replace(placeholderKey, String.valueOf(placeholder.getValue(container)));
                                }
                            }
                        }
                    } else {
                        try {
                            finalMessage = finalMessage.replace(placeholderKey, String.valueOf(placeholder.getValue(null)));
                        } catch (Throwable ignored) {}
                    }
                }
            }
        }

        return finalMessage;
    }

    /**
     * Parse a message
     *
     * @param message    the message
     * @param containers the placeholder containers
     * @return the parsed message
     */
    @Override
    public List<String> parse(final List<String> message, final Object... containers) {
        List<String> parsed = new ArrayList<>();
        for (String str : message) {
            parsed.add(parse(str, containers));
        }

        return parsed;
    }

    /**
     * Parse a message
     *
     * @param message    the message
     * @param containers the placeholder containers
     * @return the parsed message
     */
    @Override
    public String[] parse(final String[] message, final Object... containers) {
        return parse(Arrays.asList(message), containers).toArray(new String[0]);
    }

    /**
     * Get all the placeholders registered to this engine
     *
     * @return the placeholder keys
     */
    @Override
    public Set<Placeholder<?>> getKeys() {
        return sourcePlaceholders.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
    }
}
