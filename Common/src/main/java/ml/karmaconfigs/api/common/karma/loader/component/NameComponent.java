package ml.karmaconfigs.api.common.karma.loader.component;

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

import ml.karmaconfigs.api.common.utils.file.FileUtilities;
import ml.karmaconfigs.api.common.utils.file.PathUtilities;
import ml.karmaconfigs.api.common.utils.string.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Karma name component for file/directory
 * purposes
 */
public final class NameComponent {

    private String name;
    private final boolean dir;

    /**
     * Initialize the name component
     *
     * @param n the component name
     * @param directory if the component is a directory
     */
    NameComponent(final String n, final boolean directory) {
        name = n;
        dir = directory;
    }

    /**
     * Add a parent directory at the start of parent
     * directory tree
     *
     * @param parent the parent directory
     */
    public void addParentStart(final String parent) {
        name = parent + File.separator + name;
    }

    /**
     * Add a parent directory at the end of parent
     * directory tree
     *
     * @param parent the parent directory
     */
    public void addParentEnd(final String parent) {
        String[] data = FileUtilities.findParents(name);
        StringBuilder builder = new StringBuilder();
        for (String str : data) {
            builder.append(str).append(File.separator);
        }

        builder.append(parent).append(File.separator);

        name = builder + name;
    }

    /**
     * Get the component name
     *
     * @return the component name
     */
    public String getName() {
        if (!dir) {
            String extension = FileUtilities.getExtension(name);

            if (name.contains(File.separator)) {
                return FileUtilities.clearParents(name.replace("." + extension, ""));
            } else {
                return name.replace("." + extension, "");
            }
        } else {
            if (name.contains(File.separator)) {
                return FileUtilities.clearParents(name);
            } else {
                return name;
            }
        }
    }

    /**
     * Find any extension in the component
     *
     * @return the component extension ( if present )
     */
    public String findExtension() {
        if (!dir) {
            return FileUtilities.getExtension(name);
        }

        return "";
    }

    /**
     * Get the parent directories
     *
     * @param start the start directory
     * @return the parent directory tree
     */
    public Path getParent(final Path start) {
        String[] data;
        if (dir) {
            data = FileUtilities.findParents(name);
        } else {
            String extension = FileUtilities.getExtension(name);
            data = FileUtilities.findParents(name.replace("." + extension, ""));
        }

        Path result = start;
        if (data.length > 0) {
            for (String str : data)
                result = result.resolve(str);
        }

        return result;
    }

    /**
     * Get the parent directories
     *
     * @return the parent directories tree
     */
    public String[] getParents() {
        if (dir) {
            return FileUtilities.findParents(name);
        } else {
            String extension = FileUtilities.getExtension(name);
            return FileUtilities.findParents(name.replace("." + extension, ""));
        }
    }

    /**
     * Get if the component has parent directories
     *
     * @return if the component has parent directories
     */
    public boolean hasParents() {
        return FileUtilities.findParents(name).length > 0;
    }

    /**
     * Get if the component has extension
     *
     * @return if the component has a extension
     */
    public boolean hasExtension() {
        if (!dir) {
            String extension = FileUtilities.getExtension(name);
            return !StringUtils.isNullOrEmpty(extension);
        }

        return false;
    }

    /**
     * Get if the component is a directory
     *
     * @return if the component is a directory
     */
    public boolean isDirectory() {
        return dir;
    }

    /**
     * Create a name component from file
     *
     * @param file the file
     * @return the file name component
     */
    public static NameComponent fromFile(final File file) {
        if (file.isDirectory()) {
            return forDirectory(file.getName());
        } else {
            String extension = FileUtilities.getExtension(file);
            return forFile(file.getName().replace("." + extension, ""), extension);
        }
    }

    /**
     * Create a name component from path file
     *
     * @param file the file
     * @return the file name component
     */
    public static NameComponent fromPath(final Path file) {
        if (Files.isDirectory(file)) {
            return forDirectory(file.getFileName().toString());
        } else {
            String extension = PathUtilities.getExtension(file);
            return forFile(file.getFileName().toString().replace("." + extension, ""), extension);
        }
    }

    /**
     * Create a new name component for file
     * purposes
     *
     * @param name the file name
     * @param extension the file extension
     * @param subDirectory the file parent directory tree
     * @return the file name component
     */
    public static NameComponent forFile(final CharSequence name, final String extension, final String... subDirectory) {
        StringBuilder nameBuilder = new StringBuilder();
        for (String sub : subDirectory)
            nameBuilder.append(sub).append(File.separator);

        nameBuilder.append(name);

        return new NameComponent(nameBuilder + "." + extension, false);
    }

    /**
     * Create a new name component for directory
     * purposes
     *
     * @param name the directory name
     * @return the directory name component
     */
    public static NameComponent forDirectory(final CharSequence name) {
        return new NameComponent(String.valueOf(name), true);
    }
}
