package ml.karmaconfigs.api.common.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.HashSet;
import java.util.Set;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
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
                if (!file.getParentFile().exists())
                    Files.createDirectories(file.getParentFile().toPath());
                if (!file.exists())
                    Files.createFile(file.toPath());
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * Create a new file but return an exception
     *
     * @param file the file to create
     * @throws Throwable if the file throws an exception
     */
    static void createWithException(@NotNull final File file) throws Throwable {
        if (!file.isDirectory()) {
            if (!file.getParentFile().exists())
                Files.createDirectories(file.getParentFile().toPath());

            if (!file.exists())
                Files.createFile(file.toPath());
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
                if (!file.getParentFile().exists())
                    Files.createDirectories(file.getParentFile().toPath());
                if (!file.exists())
                    Files.createFile(file.toPath());

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
     * Get the file complete path, replacing
     * ugly "\" with "/"
     *
     * @param file the file to get path
     * @return the file path
     */
    static String getPath(@NotNull final File file) {
        /*
        String path = file.getAbsolutePath().replaceAll("\\\\", "/").replace("%20", " ");
        if (path.startsWith("/"))
            path = path.replaceFirst("/", "{bar}");

        path = path.replaceAll("\\\\", "/");
        String[] data = path.split("/");
        Set<String> invalid_paths = new HashSet<>();

        String last = "";
        int barsAmount = 0;
        for (String str : data) {
            if (StringUtils.isNullOrEmpty(last) && !StringUtils.isNullOrEmpty(str)) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < barsAmount; i++) {
                    builder.append("/");
                }
                invalid_paths.add(builder.toString());
                barsAmount = 0;
            } else {
                barsAmount++;
            }

            last = str;
        }

        for (String str : invalid_paths) {
            path = path.replace(str, "/");
        }

        path = path.replace("/", "{file_separator}").replaceAll("\\\\", "").replace("{bar}", "/");
        return path.replace("{file_separator}", "\\");*/

        return file.getAbsolutePath().replaceAll("\\\\", "/").replace("%20", " ");
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
     * Get the file with absolute path
     *
     * @param file the file to get with absolute
     *             path
     * @return the file with valid absolute path
     */
    static File getFilePath(@NotNull final File file) {
        if (!file.isDirectory()) {
            String name = file.getName();

            return new File(getPath(file).replace("/" + name, "").replaceAll("%20", " "));
        } else {
            return file;
        }
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
            String path = folder.getAbsolutePath().replaceAll("\\\\", "/");
            if (path.contains("plugins")) {
                String[] path_data = path.split("/");

                int plugins_amount = 0;
                for (String data : path_data)
                    if (data.equals("plugins"))
                        plugins_amount++;

                int plugins_count = 0;
                StringBuilder builder = new StringBuilder();
                for (String data : path_data) {
                    if (data.equals("plugins"))
                        plugins_count++;

                    builder.append(data).append("\\\\");

                    if (plugins_count >= plugins_amount)
                        break;
                }

                return new File(builder.toString());
            }
        }

        return folder;
    }

    /**
     * Get the server main folder
     *
     * @return the server main folder
     */
    static File getServerFolder() {
        return getPluginsFolder().getParentFile();
    }
}
