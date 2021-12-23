package ml.karmaconfigs.api.common.utils.placeholder.util;

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

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * Karma placeholder engine
 */
public abstract class PlaceholderEngine {

    /**
     * Set the placeholder open identifier
     *
     * @param identifier the placeholder identifier character
     */
    public abstract void setOpenIdentifier(final char identifier);

    /**
     * Set the placeholder close identifier
     *
     * @param identifier the placeholder identifier character
     */
    public abstract void setCloseIdentifier(final char identifier);

    /**
     * Protect the placeholder engine against
     * already added placeholder modifications
     */
    public abstract void protect();

    /**
     * Register more placeholders
     *
     * @param <T> the placeholder type
     * @param placeholders the placeholders to register
     */
    public abstract <T> void register(final Placeholder<T>... placeholders);

    /**
     * Register placeholders indiscriminately
     *
     * @param placeholders the placeholders to register
     */
    public abstract void registerUnsafe(final Placeholder<?>... placeholders);

    /**
     * Force placeholder registrations
     *
     * @param <T> the placeholder type
     * @param placeholders the placeholders to register
     */
    public abstract <T> void forceRegister(final Placeholder<T>... placeholders);

    /**
     * Unregister placeholders
     *
     * @param placeholders the placeholders to unregister
     */
    public abstract void unregister(final String... placeholders);

    /**
     * Unregister placeholders
     *
     * @param <T> the placeholder type
     * @param placeholders the placeholders to unregister
     */
    public abstract <T> void unregister(final Placeholder<T>... placeholders);

    /**
     * Get a placeholder
     *
     * @param <T> the placeholder type
     * @param key the placeholder identifier
     * @return the placeholder
     */
    @Nullable
    public abstract <T> Placeholder<T> getPlaceholder(final String key);

    /**
     * Parse a message
     *
     * @param message the message
     * @param containers the placeholder containers
     * @return the parsed message
     */
    public abstract String parse(final String message, final Object... containers);

    /**
     * Parse a message
     *
     * @param message the message
     * @param containers the placeholder containers
     * @return the parsed message
     */
    public abstract List<String> parse(final List<String> message, final Object... containers);

    /**
     * Parse a message
     *
     * @param message the message
     * @param containers the placeholder containers
     * @return the parsed message
     */
    public abstract String[] parse(final String[] message, final Object... containers);

    /**
     * Get all the placeholders registered to this engine
     *
     * @return the placeholder keys
     */
    public abstract Set<Placeholder<?>> getKeys();

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public final String toString() {
        return super.toString() + "[registered=" + getKeys().size() + ";values=" + getKeys() + "]";
    }
}
