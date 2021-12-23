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

import ml.karmaconfigs.api.common.utils.placeholder.util.Placeholder;
import org.jetbrains.annotations.Nullable;

/**
 * Karma simple placeholder
 *
 * @param <T> the placeholder type
 */
public class SimplePlaceholder<T> extends Placeholder<T> {

    private final String key;
    private final T value;

    /**
     * Simple karma placeholder
     *
     * @param k the placeholder key
     * @param val the placeholder value
     */
    public SimplePlaceholder(final String k, final T val) {
        key = k;
        value = val;
    }

    /**
     * Get the placeholder key
     *
     * @return the placeholder key
     */
    @Override
    public String getKey() {
        return key;
    }

    /**
     * Get the placeholder value
     *
     * @param container the placeholder container
     * @return the placeholder value
     */
    @Override
    public T getValue(@Nullable Object container) {
        return value;
    }

    /**
     * Get the placeholder type
     *
     * @return the placeholder type
     */
    @Override
    public Class<?> getType() {
        return value.getClass();
    }
}
