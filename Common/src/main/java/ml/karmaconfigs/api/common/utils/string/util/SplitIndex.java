package ml.karmaconfigs.api.common.utils.string.util;

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
 * Split indexes
 */
public final class SplitIndex {

    /**
     * Start index
     */
    private final int start;

    /**
     * End index
     */
    private final int end;

    /**
     * Initialize the split index
     *
     * @param s the start index
     * @param e the end index
     */
    SplitIndex(final int s, final int e) {
        start = s;
        end = e;
    }

    /**
     * Get the start index
     *
     * @return the start index
     */
    public int getStart() {
        return start;
    }

    /**
     * Get the end index
     *
     * @return the end index
     */
    public int getEnd() {
        return end;
    }

    /**
     * Create a new index
     *
     * @param start the start index
     * @param end the end index
     * @return the new index
     */
    public static SplitIndex newIndex(final int start, final int end) {
        return new SplitIndex(start, end);
    }
}
