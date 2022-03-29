package ml.karmaconfigs.api.common.karmafile.karma;

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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

/**
 * Advanced karma file that follows
 * {@link KmFile standard}
 */
final class AdvancedKarmaFile extends KmFile {

    private char SPACE_CHARACTER = KmFile.SECTION_DIVIDER_CHARACTER;

    private final Path file;

    /**
     * Initialize the karma file
     *
     * @param basePath the file path
     */
    public AdvancedKarmaFile(final String basePath) {
        super(basePath);
        file = getFile();
    }

    /**
     * Initialize the karma file
     *
     * @param f the file
     */
    public AdvancedKarmaFile(final File f) {
        super(f);

        //Instead of calling getFile everytime, this is the
        //best solution
        file = getFile();
    }

    /**
     * Initialize the karma file
     *
     * @param f the file
     */
    public AdvancedKarmaFile(final Path f) {
        super(f);

        //Instead of calling getFile everytime, this is the
        //best solution
        file = getFile();
    }

    /**
     * Set a new value
     *
     * @param path     the value path
     * @param value    the value
     * @param comments the comments of the path
     */
    @Override
    public void set(final String path, final Object value, final String... comments) {

    }

    /**
     * Unset a path
     *
     * @param path the path
     */
    @Override
    public void unset(final String path) {

    }

    /**
     * Get if the specified path is set
     *
     * @param path the path
     * @return if the path is set
     */
    @Override
    public boolean isSet(String path) {
        return false;
    }

    /**
     * Get if the specified path and value are set
     *
     * @param path     the path
     * @param expected the expected value
     * @return if the path is set and its value is the expected value
     */
    @Override
    public boolean isSet(String path, Object expected) {
        return false;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    @Override
    public @Nullable Object get(final String path) {
        return null;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    @Override
    public @Nullable KmFile getSection(final String path) {
        return null;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    @Override
    public @Nullable String getString(final String path) {
        return null;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    @Override
    public boolean getBoolean(final String path) {
        return false;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    @Override
    public int getInteger(final String path) {
        return 0;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    @Override
    public double getDouble(final String path) {
        return 0;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    @Override
    public long getLong(final String path) {
        return 0;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    @Override
    public float getFloat(final String path) {
        return 0;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    @Override
    public byte getByte(final String path) {
        return 0;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @return the value
     */
    @Override
    public byte[] getBytes(final String path) {
        return new byte[0];
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def  the default value if
     *             value not present
     * @return the value
     */
    @Override
    public @NotNull Object get(final String path, final Object def) {
        return null;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def  the default value if
     *             value not present
     * @return the value
     */
    @Override
    public @NotNull KmFile getSection(final String path, final KmFile def) {
        return null;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def  the default value if
     *             value not present
     * @return the value
     */
    @Override
    public @NotNull String getString(final String path, final String def) {
        return null;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def  the default value if
     *             value not present
     * @return the value
     */
    @Override
    public boolean getBoolean(final String path, final boolean def) {
        return false;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def  the default value if
     *             value not present
     * @return the value
     */
    @Override
    public int getInteger(final String path, final int def) {
        return 0;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def  the default value if
     *             value not present
     * @return the value
     */
    @Override
    public double getDouble(final String path, final double def) {
        return 0;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def  the default value if
     *             value not present
     * @return the value
     */
    @Override
    public long getLong(final String path, final long def) {
        return 0;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def  the default value if
     *             value not present
     * @return the value
     */
    @Override
    public float getFloat(final String path, final float def) {
        return 0;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def  the default value if
     *             value not present
     * @return the value
     */
    @Override
    public byte getByte(final String path, final byte def) {
        return 0;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def  the default value if
     *             value not present
     * @return the value
     */
    @Override
    public byte[] getBytes(final String path, final byte[] def) {
        return new byte[0];
    }

    /**
     * Get the file keys
     *
     * @return the file keys
     */
    @Override
    public Map<String, Object> getKeys() {
        return null;
    }

    /**
     * Creates the file
     *
     * @return if the file could be created
     */
    @Override
    public boolean create() {
        return false;
    }

    /**
     * Removes the file
     *
     * @return if the file could be removed
     */
    @Override
    public boolean remove() {
        return false;
    }

    /**
     * Get if the file exists
     *
     * @return if the file exists
     */
    @Override
    public boolean exists() {
        return false;
    }
}
