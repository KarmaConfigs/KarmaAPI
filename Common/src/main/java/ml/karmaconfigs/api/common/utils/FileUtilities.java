package ml.karmaconfigs.api.common.utils;

import ml.karmaconfigs.api.common.Console;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Collections;
import java.util.List;

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
 * Utilities for files
 */
public interface FileUtilities {

    /**
     * Create the new file, with no returns
     * and no debug. Just that, create the file
     *
     * @param file the file to create
     */
    static void create(@NotNull final File file) {
        if (!file.isDirectory()) {
            try {
                if (!file.getParentFile().exists()) {
                    Files.createDirectories(file.getParentFile().toPath());
                    Console.send("&b[ KarmaAPI ] &3Created directory {0}", FileUtilities.getPrettyDirPath(file.getParentFile()));
                }

                if (!file.exists()) {
                    Files.createFile(file.toPath());
                    Console.send("&b[ KarmaAPI ] &3Created file {0}", FileUtilities.getPrettyDirPath(file));
                }
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * Create a new file but return an exception
     *
     * @param file the file to create
     * @throws IOException as part of Files#createDirectories and Files#createFile methods
     */
    static void createWithException(@NotNull final File file) throws IOException {
        if (!file.isDirectory()) {
            if (!file.getParentFile().exists()) {
                Files.createDirectories(file.getParentFile().toPath());
                Console.send("&b[ KarmaAPI ] &3Created directory {0}", FileUtilities.getPrettyDirPath(file.getParentFile()));
            }

            if (!file.exists()) {
                Files.createFile(file.toPath());
                Console.send("&b[ KarmaAPI ] &3Created file {0}", FileUtilities.getPrettyDirPath(file));
            }
        }
    }

    /**
     * Tries to create the file
     *
     * @param file the file to create
     * @return if the file could be created
     */
    static boolean createWithResults(@NotNull final File file) {
        if (!file.isDirectory()) {
            try {
                if (!file.getParentFile().exists()) {
                    Files.createDirectories(file.getParentFile().toPath());
                    Console.send("&b[ KarmaAPI ] &3Created directory {0}", FileUtilities.getPrettyDirPath(file.getParentFile()));
                }
                if (!file.exists()) {
                    Files.createFile(file.toPath());
                    Console.send("&b[ KarmaAPI ] &3Created file {0}", FileUtilities.getPrettyDirPath(file));
                }

                return true;
            } catch (Throwable ex) {
                return false;
            }
        }

        return false;
    }

    /**
     * Check if the file is a KarmaFile
     *
     * @param file the file to check
     * @return if the file is a KarmaFile
     */
    static boolean isKarmaFile(final File file) {
        try {
            UserDefinedFileAttributeView view = Files.getFileAttributeView(file.toPath(), UserDefinedFileAttributeView.class);
            ByteBuffer buf = ByteBuffer.allocate(view.size("filetp"));
            view.read("filetp", buf);
            buf.flip();

            return Charset.defaultCharset().decode(buf).toString().equals("KarmaFile");
        } catch (Throwable ex) {
            return false;
        }
    }

    /**
     * Get if the specified file is valid
     *
     * @param file the file
     * @return if the file is valid
     */
    static boolean isValidFile(final File file) {
        try {
            file.getCanonicalPath();
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    /**
     * Get if the specified path is valid
     *
     * @param path the path
     * @return if the path is valid
     */
    static boolean isValidFile(final Path path) {
        try {
            path.toFile().getCanonicalPath();
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    /**
     * Get if the specified file name/path is
     * valid
     *
     * @param path the file name/path
     * @return if the specified name/path is valid
     */
    static boolean isValidFile(final String path) {
        File file = new File(path);
        try {
            file.getCanonicalPath();
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    /**
     * Get the file complete path
     *
     * @param file the file to get path
     * @return the file path
     */
    static String getPath(@NotNull final File file) {
        return getPath(file, ' ');
    }

    /**
     * Get the directory complete path
     *
     * @param file the directory
     * @return the directory path
     */
    static String getDirPath(final File file) {
        return getDirPath(file, ' ');
    }

    /**
     * Get the file complete path
     *
     * @param file the file to get path
     * @return the file path
     */
    static String getPrettyPath(@NotNull final File file) {
        return getPath(file, '/');
    }

    /**
     * Get the directory complete path
     *
     * @param file the directory
     * @return the directory path
     */
    static String getPrettyDirPath(final File file) {
        return getDirPath(file, '/');
    }

    /**
     * Get the file complete path
     *
     * @param file the file to get path
     * @param barReplace the replacement for file separator ( null or empty to not replace )
     * @return the file path
     */
    static String getPath(@NotNull final File file, final char barReplace) {
        if (Character.isSpaceChar(barReplace)) {
            return file.getParentFile().getAbsolutePath().replaceAll("%20", " ");
        } else {
            return file.getParentFile().getAbsolutePath().replaceAll("%20", " ").replace(File.separatorChar, barReplace);
        }
    }

    /**
     * Get the directory complete path
     *
     * @param file the directory to get path
     * @param barReplace the replacement for file separator ( null or empty to not replace )
     * @return the directory path
     */
    static String getDirPath(@NotNull final File file, final char barReplace) {
        if (Character.isSpaceChar(barReplace)) {
            return file.getAbsolutePath().replaceAll("%20", " ");
        } else {
            return file.getAbsolutePath().replaceAll("%20", " ").replace(File.separatorChar, barReplace);
        }
    }

    /**
     * Get the file extension
     *
     * @param file the file to get extension from
     * @return the file extension
     */
    static String getExtension(@NotNull final File file) {
        if (!file.isDirectory() && file.getName().contains(".")) {
            String name = file.getName();
            String[] nameData = name.split("\\.");

            return nameData[nameData.length - 1];
        } else {
            return "dir";
        }
    }

    /**
     * Get the file extension
     *
     * @param name the file name
     * @return the file extension
     */
    static String getExtension(@NotNull final String name) {
        if (name.contains(".")) {
            String[] nameData = name.split("\\.");

            return nameData[nameData.length - 1];
        } else {
            return "";
        }
    }

    /**
     * Get the file name, with or without extension
     *
     * @param file the to get name from
     * @param extension include extension in the name
     * @return the file name
     */
    static String getName(@NotNull final File file, final boolean extension) {
        if (extension) {
            return file.getName();
        } else {
            return StringUtils.replaceLast(file.getName(), "." + getExtension(file), "");
        }
    }

    /**
     * Get the file type
     *
     * @param file the file
     * @return the file type
     */
    static String getFileType(final File file) {
        try {
            String mimetype = Files.probeContentType(file.toPath());

            if (mimetype != null) {
                if (mimetype.contains("/")) {
                    return mimetype.split("/")[0];
                } else {
                    return mimetype;
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return "";
    }

    /**
     * Get the file type
     *
     * @param file the file
     * @return the file type
     */
    static String getFileCompleteType(final File file) {
        try {
            String mimetype = Files.probeContentType(file.toPath());

            if (mimetype != null) {
                return mimetype;
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return "";
    }

    /**
     * Read all the file lines
     *
     * @param file the file
     * @return all the file lines
     */
    static List<String> readAllLines(final File file) {
        try {
             return Files.readAllLines(getFixedFile(file).toPath());
        } catch (Throwable ex) {
            return Collections.emptyList();
        }
    }

    /**
     * Get the file with absolute path
     *
     * @param file the file to get with absolute
     *             path
     * @return the file with valid absolute path
     */
    static File getFixedFile(@NotNull final File file) {
        return new File(getDirPath(file, ' '));
    }

    /**
     * Get plugins folders, no matters if you are
     * in bungee or bukkit, this will return plugins
     * folder
     *
     * @return the server plugins folder
     */
    static File getPluginsFolder() {
        File jar = new File(FileUtilities.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath());

        File folder = new File(jar.getAbsolutePath().replace(jar.getName(), ""));
        if (!folder.getName().equals("plugins")) {
            String path = folder.getAbsolutePath();
            if (path.contains("plugins")) {
                String[] path_data = path.split(File.separator.replace("\\", "\\\\"));

                int plugins_amount = 0;
                for (String data : path_data)
                    if (data.equals("plugins"))
                        plugins_amount++;

                int plugins_count = 0;
                StringBuilder builder = new StringBuilder();
                for (String data : path_data) {
                    if (data.equals("plugins"))
                        plugins_count++;

                    builder.append(data).append(File.separatorChar);

                    if (plugins_count >= plugins_amount)
                        break;
                }

                return getFixedFile(new File(builder.toString()));
            }
        }

        return getFixedFile(folder);
    }

    /**
     * Get the server main folder
     *
     * @return the server main folder
     */
    static File getServerFolder() {
        return getFixedFile(getPluginsFolder().getParentFile());
    }

    /**
     * Get the plugin main folder
     *
     * @param plugin the plugin object
     * @return the plugin main folder
     */
    static File getPluginFolder(final Object plugin) {
        return getFixedFile(new File(getPluginsFolder(), ReflectionUtil.getName(plugin)));
    }
}
