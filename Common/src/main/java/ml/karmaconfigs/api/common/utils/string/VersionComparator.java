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

import ml.karmaconfigs.api.common.utils.string.util.VersionDiff;

/**
 * Karma version comparator
 */
public class VersionComparator {

    /**
     * The comparator options
     */
    private ComparatorBuilder options;

    /**
     * Initialize the version comparator
     */
    private VersionComparator() {}

    /**
     * Initialize the version comparator
     *
     * @param opts the comparator options
     */
    VersionComparator(final ComparatorBuilder opts) {
        options = opts;
    }

    /**
     * Get the version difference
     *
     * @return the version difference
     */
    public VersionDiff getDifference() {
        String[] currentParts = options.getCurrentVersion().split("\\.");
        String[] checkParts = options.getCheckVersion().split("\\.");
        int length = Math.max(currentParts.length, checkParts.length);
        for (int i = 0; i < length; i++) {
            int currentPart = (i < currentParts.length) ? Integer.parseInt(currentParts[i]) : 0;
            int checkPart = (i < checkParts.length) ? Integer.parseInt(checkParts[i]) : 0;
            if (currentPart < checkPart)
                return VersionDiff.OUTDATED;
            if (currentPart > checkPart)
                return VersionDiff.OVERDATED;
        }
        return VersionDiff.UPDATED;
    }

    /**
     * Get if the current version is up-to-date
     *
     * @return if the version is up-to-date
     */
    public boolean isUpToDate() {
        return !getDifference().equals(VersionDiff.OUTDATED);
    }

    /**
     * Create a new version comparator options
     * builder
     *
     * @return a new version comparator options
     * builder
     */
    public static ComparatorBuilder createBuilder() {
        return new ComparatorBuilder();
    }
}
