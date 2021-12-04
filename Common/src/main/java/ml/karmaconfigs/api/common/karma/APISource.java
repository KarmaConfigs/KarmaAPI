package ml.karmaconfigs.api.common.karma;

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

import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Karma API source
 */
public final class APISource implements KarmaSource {

    private final static Map<String, KarmaSource> sources = new ConcurrentHashMap<>();

    @ApiStatus.ScheduledForRemoval
    @Deprecated
    public static String DEFAULT_SOURCE = "karmaapi";

    private static String DEFAULT = "karmaapi";

    /**
     * Initialize the original karma source
     */
    APISource() {
        try {
            addProvider(this);
        } catch (IllegalStateException ignored) {}
    }

    /**
     * Add a new karma source provider
     *
     * @param src the karma source
     * @throws IllegalStateException if the source is already added
     */
    public static void addProvider(final KarmaSource src) throws IllegalStateException {
        String identifier = src.name().toLowerCase();
        KarmaSource stored = sources.getOrDefault(identifier, null);

        if (stored == null) {
            sources.put(identifier, src);
        } else {
            throw new IllegalStateException("A source from " +
                    src.getClass().getName() +
                    " tried to add an already added source " +
                    stored.name() +
                    " by author(s): " +
                    stored.authors(false, ", "));
        }
    }

    /**
     * Update a source provider
     *
     * @param name the original source name
     * @param target the new source
     *
     * @throws IllegalStateException if the new source replacement
     * is not the same as the stored one
     */
    public static void updateProvider(final String name, final KarmaSource target) throws IllegalStateException {
        String identifier = name.toLowerCase();
        KarmaSource stored = sources.getOrDefault(identifier, null);

        if (stored.srcEquals(target)) {
            sources.put(identifier, target);
        } else {
            throw new IllegalStateException("A source from " +
                    target.getClass().getName() +
                    " tried to overwrite source " +
                    stored.name() +
                    " by author(s): " +
                    stored.authors(false, ", "));
        }
    }

    /**
     * Get if the API source has stored
     * a source with the specified name
     *
     * @param name the source name
     * @return if the API source has that source
     */
    public static boolean hasProvider(final String name) {
        String identifier = name.toLowerCase();
        KarmaSource stored = sources.getOrDefault(identifier, null);

        if (!identifier.equalsIgnoreCase("KarmaAPI")) {
            return stored != null;
        } else {
            return true;
        }
    }

    /**
     * Load a provider
     *
     * @param name the provider name
     * @return the karma source attached to that name
     * @throws IllegalArgumentException if the source is not registered
     */
    public static KarmaSource loadProvider(final String name) throws IllegalArgumentException {
        String identifier = name.toLowerCase();
        KarmaSource stored = sources.getOrDefault(identifier, null);

        if (!identifier.equalsIgnoreCase("KarmaAPI")) {
            if (stored != null) {
                return stored;
            } else {
                throw new IllegalArgumentException("KarmaSource with name " + name + " as not been registered as provider");
            }
        } else {
            if (stored != null) {
                return stored;
            } else {
                return new APISource();
            }
        }
    }

    /**
     * Define the default source that KarmaAPI will use.
     *
     * Differently from {@link APISource#DEFAULT_SOURCE static} is that
     * method throws {@link SecurityException error} if there's already
     * a default source set.
     *
     * @param def the default source
     */
    public static void defineDefault(final KarmaSource def) throws SecurityException {
        String identifier = def.name().toLowerCase();

        if (DEFAULT.equalsIgnoreCase("KarmaAPI")) {
            DEFAULT = identifier;
        } else {
            if (!identifier.equalsIgnoreCase(DEFAULT)) {
                throw new SecurityException("Module " + def + " tried to change default source");
            }
        }
    }

    /**
     * Get the original karma source
     *
     * @return the original karma source
     */
    public KarmaSource getOriginal() {
        return loadProvider(DEFAULT);
    }

    /**
     * Karma source name
     *
     * @return the source name
     */
    @Override
    public String name() {
        return "KarmaAPI";
    }

    /**
     * Karma source version
     *
     * @return the source version
     */
    @Override
    public String version() {
        return KarmaAPI.getVersion();
    }

    /**
     * Karma source description
     *
     * @return the source description
     */
    @Override
    public String description() {
        return "KarmaAPI source";
    }

    /**
     * Karma source authors
     *
     * @return the source authors
     */
    @Override
    public String[] authors() {
        return new String[]{"KarmaDev"};
    }

    /**
     * Karma source update URL
     *
     * @return the source update URL
     */
    @Override
    public String updateURL() {
        return "";
    }
}
