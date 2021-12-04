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

/**
 * Karma comparator builder
 */
public final class ComparatorBuilder {

    /**
     * The current version
     */
    private String current = "1.0";
    /**
     * The check version
     */
    private String check = "1.0";

    /**
     * Initialize the comparator builder
     */
    ComparatorBuilder() {}

    /**
     * Set the current version
     *
     * @param c the current version
     * @return this instance
     */
    public ComparatorBuilder currentVersion(final String c) {
        current = c;

        return this;
    }

    /**
     * Set the check version
     *
     * @param c the check version
     * @return this instance
     */
    public ComparatorBuilder checkVersion(final String c) {
        check = c;

        return this;
    }

    /**
     * Get the current version
     *
     * @return the current version
     */
    public String getCurrentVersion() {
        return current;
    }

    /**
     * Get the check version
     *
     * @return the check version
     */
    public String getCheckVersion() {
        return check;
    }
}
