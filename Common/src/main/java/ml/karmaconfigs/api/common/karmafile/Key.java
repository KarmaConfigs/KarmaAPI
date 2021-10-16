package ml.karmaconfigs.api.common.karmafile;

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

import java.io.Serializable;

/**
 * Karma files key
 */
public final class Key implements Serializable {

    /**
     * The key path
     */
    private final String path;

    /**
     * The key value
     */
    private final Object value;

    /**
     * Initialize the karma key
     *
     * @param keyPath the path
     * @param keyValue the value
     */
    public Key(String keyPath, Object keyValue) {
        this.path = keyPath;
        this.value = keyValue;
    }

    /**
     * Get the key path
     *
     * @return the key path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Get the key value
     *
     * @return the key value
     */
    public Object getValue() {
        return this.value;
    }
}
