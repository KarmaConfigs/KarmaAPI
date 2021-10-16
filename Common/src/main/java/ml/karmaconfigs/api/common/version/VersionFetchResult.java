package ml.karmaconfigs.api.common.version;

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
import ml.karmaconfigs.api.common.version.util.VersionResolver;
import ml.karmaconfigs.api.common.version.util.VersionType;

/**
 * Karma version updater version fetch result
 */
public final class VersionFetchResult {

    /**
     * If the source is updated
     */
    private final boolean updated;

    /**
     * The fetched last version
     */
    private final String latest;

    /**
     * The source current version
     */
    private final String current;

    /**
     * The source update URL
     */
    private final String update;

    /**
     * The update changelog
     */
    private final String[] changelog;

    /**
     * The source version resolver
     */
    private final VersionResolver resolver;

    /**
     * Initialize a new version fetch result
     *
     * @param source the source
     * @param latest_version the latest version
     * @param downloadURL the download url
     * @param changes the changelog
     * @param solver the version resolver
     */
    public VersionFetchResult(final KarmaSource source, final String latest_version, final String downloadURL, final String[] changes, final VersionResolver solver) {
        this.updated = true;
        this.latest = latest_version;
        this.current = source.version();
        this.update = downloadURL;
        this.changelog = changes;
        this.resolver = solver;
    }

    /**
     * Initialize a new version fetch result
     *
     * @param status the update status
     * @param fetched the latest version
     * @param active the current version
     * @param url the update url
     * @param changes the changelog
     * @param solver the version resolver
     */
    VersionFetchResult(final boolean status, final String fetched, final String active, final String url, final String[] changes, final VersionResolver solver) {
        this.updated = status;
        this.latest = fetched;
        this.current = active;
        this.update = url;
        this.changelog = changes;
        this.resolver = solver;
    }

    /**
     * Get if the source is updated
     *
     * @return if the source is updated
     */
    public boolean isUpdated() {
        return this.updated;
    }

    /**
     * Get the source last version
     *
     * @return the source last version
     */
    public String getLatest() {
        return this.latest;
    }

    /**
     * Get the source current version
     *
     * @return the source current version
     */
    public String getCurrent() {
        return this.current;
    }

    /**
     * Get the source update URL
     *
     * @return the source update URL
     */
    public String getUpdateURL() {
        return this.update;
    }

    /**
     * Resolve the version
     *
     * @param type the version type
     * @return the solved version
     */
    public String resolve(final VersionType type) {
        if (resolver != null) {
            switch (type) {
                case LATEST:
                    return resolver.resolve(latest);
                case CURRENT:
                    return resolver.resolve(current);
            }
        } else {
            switch (type) {
                case CURRENT:
                    return current;
                case LATEST:
                    return latest;
            }
        }

        return latest;
    }

    /**
     * Get the version changelog
     *
     * @return the version changelog
     */
    public String[] getChangelog() {
        return this.changelog;
    }
}
