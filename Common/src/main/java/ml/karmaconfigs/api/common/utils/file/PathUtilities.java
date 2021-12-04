package ml.karmaconfigs.api.common.utils.file;

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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Karma file utilities
 */
public final class PathUtilities {

    /**
     * Create a file
     *
     * @param file the file to create
     */
    public static void create(final @NotNull Path file) {
        FileUtilities.create(file.toFile());
    }

    /**
     * Create a file and catch any exception
     *
     * @param file the file to create
     * @throws IOException any exception
     */
    public static void createWithException(final @NotNull Path file) throws IOException {
        FileUtilities.createWithException(file.toFile());
    }

    /**
     * Create a file and return if the file
     * could be created
     *
     * @param file the file to create
     * @return if the file could be created
     */
    public static boolean createWithResults(final @NotNull Path file) {
        return FileUtilities.createWithResults(file.toFile());
    }

    /**
     * Get if the file is a karma file
     *
     * @param file the file
     * @return if the file is a karma file
     */
    public static boolean isKarmaPath(final Path file) {
        return FileUtilities.isKarmaFile(file.toFile());
    }

    /**
     * Get if the file is valid
     *
     * @param path the file
     * @return if the file is a valid file
     */
    public static boolean isValidPath(final Path path) {
        return FileUtilities.isValidFile(path.toFile());
    }

    /**
     * Get if the file is a valid file
     *
     * @param file the file
     * @return if the file is a valid file
     */
    public static String getParentPath(final @NotNull Path file) {
        return FileUtilities.getParentFile(file.toFile());
    }

    /**
     * Get the file path
     *
     * @param file the file path
     * @return the file path
     */
    public static String getPath(final Path file) {
        return FileUtilities.getFile(file.toFile());
    }

    /**
     * Get the pretty file
     *
     * @param file the file
     * @return the pretty file
     */
    public static String getPrettyParentPath(final @NotNull Path file) {
        return FileUtilities.getPrettyParentFile(file.toFile());
    }

    /**
     * Get the pretty file path
     *
     * @param file the file
     * @return the pretty file path
     */
    public static String getPrettyFile(final Path file) {
        return FileUtilities.getPrettyFile(file.toFile());
    }

    /**
     * Get the parent file replacing %20 ( space char )
     *
     * @param file the file
     * @param barReplace the %20 replace
     * @return the parent file
     */
    public static String getParentPath(final @NotNull Path file, char barReplace) {
        return FileUtilities.getParentFile(file.toFile(), barReplace);
    }

    /**
     * Get the file replacing the %20 ( space char )
     *
     * @param file the file
     * @param barReplace the %20 replace
     * @return the file
     */
    public static String getPath(final @NotNull Path file, char barReplace) {
        return FileUtilities.getFile(file.toFile(), barReplace);
    }

    /**
     * Get the file extension
     *
     * @param file the file
     * @return the file extension
     */
    public static String getExtension(final @NotNull Path file) {
        return FileUtilities.getExtension(file.toFile());
    }

    /**
     * Get the file name
     *
     * @param file the file name
     * @param extension include extension
     * @return the file name
     */
    public static String getName(final @NotNull Path file, boolean extension) {
        return FileUtilities.getName(file.toFile(), extension);
    }

    /**
     * Get the file type
     *
     * @param file the file tpy
     * @return the file type
     */
    public static String getPathType(final Path file) {
        return FileUtilities.getFileType(file.toFile());
    }

    /**
     * Get the file complete type
     *
     * @param file the file complete type
     * @return the file type
     */
    public static String getPathCompleteType(final Path file) {
        return FileUtilities.getFileCompleteType(file.toFile());
    }

    /**
     * Read all the file lines
     *
     * @param file the file
     * @return all the file lines
     */
    public static List<String> readAllLines(final Path file) {
        return FileUtilities.readAllLines(file.toFile());
    }

    /**
     * Get the karma source jar file
     *
     * @param source the karma source
     * @return the source jar file
     */
    public static Path getSourcePath(final KarmaSource source) {
        File file = new File(source.getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
        return FileUtilities.getFixedFile(file).toPath();
    }

    /**
     * Fix a file
     *
     * @param file the file
     * @return the fixed file
     */
    public static Path getFixedPath(final @NotNull Path file) {
        return FileUtilities.getFixedFile(file.toFile()).toPath();
    }

    /**
     * Get the project folder
     *
     * @param delimiter the project complete path delimiter
     * @return the project folder
     */
    public static Path getProjectPath(final String delimiter) {
        return FileUtilities.getProjectFolder(delimiter).toPath();
    }

    /**
     * Get the project parent folder
     *
     * @return the project parent folder
     */
    public static Path getProjectParent() {
        return FileUtilities.getProjectParent().toPath();
    }

    /**
     * Get the project folder
     *
     * @param source the source
     * @return the project folder
     * @deprecated Use {@link KarmaSource#getDataPath()} instead
     */
    @Deprecated
    public static Path getProjectPath(KarmaSource source) {
        return source.getDataPath();
    }
}
