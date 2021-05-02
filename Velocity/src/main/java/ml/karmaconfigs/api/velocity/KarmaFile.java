package ml.karmaconfigs.api.velocity;

import com.velocitypowered.api.plugin.Plugin;
import ml.karmaconfigs.api.common.Level;
import ml.karmaconfigs.api.common.karmafile.Key;
import ml.karmaconfigs.api.common.utils.FileUtilities;
import ml.karmaconfigs.api.velocity.karmayaml.FileCopy;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.*;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="http://karmaconfigs.cf/license/"> here </a>
 * or (fallback domain) <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
@SuppressWarnings("unused")
public final class KarmaFile implements Serializable {

    private final File file;
    private final List<File> folders = new ArrayList<>();

    private final Plugin main;

    private boolean broadcast_file_creation = true;
    private boolean broadcast_folder_creation = true;

    /**
     * Initialize the custom
     * file creator
     *
     * @param plugin the plugin that will create the file
     * @param name   the custom file name
     */
    public KarmaFile(@NotNull final Plugin plugin, final String name) {
        Util util = new Util(plugin);
        util.initialize();

        file = new File(util.getDataFolder(), name);
        folders.add(util.getDataFolder());
        main = plugin;
    }

    /**
     * Initialize the custom file creator
     * with an specified directory
     *
     * @param plugin the plugin that will create the file
     * @param name   the file name
     * @param dirs   the file directory
     */
    public KarmaFile(@NotNull final Plugin plugin, final String name, final String... dirs) {
        Util util = new Util(plugin);
        util.initialize();

        List<String> directories = new ArrayList<>(Arrays.asList(dirs));
        String folderPath;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < directories.size(); i++) {
            if (i != directories.size() - 1) {
                builder.append(directories.get(i).replace("/", "")).append("/");
            } else {
                builder.append(directories.get(i).replace("/", ""));
            }
        }

        folderPath = builder.toString();

        File lastFolder;
        file = new File(util.getDataFolder() + "/" + folderPath, name);
        lastFolder = util.getDataFolder();
        for (String str : dirs) {
            File newFolder = new File(lastFolder + "/" + str.replace("/", ""));
            folders.add(newFolder);
            lastFolder = newFolder;
        }

        main = plugin;
    }

    /**
     * Initialize the KarmaFile from the
     * specified file
     *
     * @param f the file
     */
    public KarmaFile(final File f) {
        main = null;
        file = f;

        File path = new File(FileUtilities.getPath(f).replace(f.getName(), ""));
        folders.add(path);
    }

    /**
     * Select the karma file broadcast options
     *
     * @param broadcast_folder broadcast a message on folder creation
     * @param broadcast_file broadcast a file on file creation
     */
    public final void setBroadcastOptions(final boolean broadcast_folder, final boolean broadcast_file) {
        broadcast_file_creation = broadcast_file;
        broadcast_folder_creation = broadcast_folder;
    }

    /**
     * Create and write the file from the specified
     * resource
     *
     * @param resource the internal resource
     */
    public final void exportFromFile(final String resource) {
        if (!exists())
            create();

        try {
            FileCopy copy = new FileCopy(main, resource);
            copy.copy(file);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Check if the specified line is a
     * comment
     *
     * @param line the line
     * @return if the line is a comment line
     */
    private boolean isComment(final String line) {
        return line.startsWith("/// ") || line.startsWith("// ") && line.endsWith(" -->");
    }

    /**
     * Check if the line is an open list
     * iterator
     *
     * @param line the line
     * @param path the list path
     * @return a boolean
     */
    private boolean isOpenList(String line, String path) {
        return line.equals("[LIST=" + path.replaceAll("\\s", "_") + "]");
    }

    /**
     * Check if the line is an open list
     * iterator
     *
     * @param line the line
     * @return a boolean
     */
    private boolean isOpenList(String line) {
        return line.startsWith("[LIST=") && line.endsWith("]");
    }

    /**
     * Check if the line is an open list
     * iterator
     *
     * @param line the line
     * @param path the list path
     * @return a boolean
     */
    private boolean isCloseList(String line, String path) {
        return line.equals("[/LIST=" + path.replaceAll("\\s", "_") + "]");
    }

    /**
     * Check if the line is an open list
     * iterator
     *
     * @param line the line
     * @return a boolean
     */
    private boolean isCloseList(String line) {
        return line.startsWith("[/LIST=") && line.endsWith("]");
    }

    /**
     * Check if the specified line
     * has a value
     *
     * @param line the line
     * @return if has a value
     */
    private boolean hasValue(final String line) {
        if (line.contains(":")) {
            String path = line.split(":")[0];

            return !line.replaceFirst(path + ":", "").replaceAll("\\s", "").isEmpty();
        }

        return false;
    }

    /**
     * Get the current key path
     *
     * @param line the line
     * @return the line key path
     */
    private String getKeyPath(final String line) {
        if (isOpenList(line) || isCloseList(line)) {
            String pathN1 = line
                    .replaceFirst("\\[LIST=", "")
                    .replaceFirst("\\[/LIST=", "");

            return pathN1.substring(0, pathN1.length() - 1);
        } else {
            if (line.contains(":")) {
                return line.split(":")[0];
            } else {
                return line;
            }
        }
    }

    /**
     * Apply KarmaFile filetp attribute to the file
     * if exists
     */
    public final void applyKarmaAttribute() {
        if (exists()) {
            try {
                UserDefinedFileAttributeView view = Files.getFileAttributeView(file.toPath(), UserDefinedFileAttributeView.class);
                view.write("filetp", Charset.defaultCharset().encode("KarmaFile"));
            } catch (Throwable ignored) {}
        }
    }

    /**
     * Create the file and
     * directories
     */
    public final void create() {
        for (File folder : folders) {
            if (!folder.exists()) {
                String dir = folder.getPath().replaceAll("\\\\", "/");
                if (folder.mkdirs()) {
                    if (broadcast_folder_creation) {
                        if (main != null) {
                            Console.send(main, "Created directory {0}", Level.INFO, dir);
                        } else {
                            KarmaConsoleSender.send("Created directory {0}", Level.INFO, dir);
                        }
                    }
                } else {
                    if (main != null) {
                        Console.send(main, "An unknown error occurred while creating directory {0}", Level.GRAVE, dir);
                    } else {
                        KarmaConsoleSender.send("An unknown error occurred while creating directory {0}", Level.GRAVE, dir);
                    }
                }
            }
        }

        if (!file.exists()) {
            try {
                String dir = file.getPath().replaceAll("\\\\", "/");

                if (file.createNewFile()) {
                    if (broadcast_file_creation) {
                        if (main != null) {
                            Console.send(main, "Created file {0}", Level.INFO, dir);
                        } else {
                            KarmaConsoleSender.send("Created file {0}", Level.INFO, dir);
                        }
                    }

                    applyKarmaAttribute();
                } else {
                    if (main != null) {
                        Console.send(main, "An unknown error occurred while creating file {0}", Level.GRAVE, dir);
                    } else {
                        KarmaConsoleSender.send("An unknown error occurred while creating file {0}", Level.GRAVE, dir);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Write the specified lines directly into
     * the file <p>THIS WILL REPLACE ALL CURRENT
     * FILE LINES WITH THE SPECIFIED ONES</p>
     *
     * @param lines the lines to write
     */
    public final void write(final List<String> lines) {
        try {
            BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);

            for (String str : lines)
                writer.write(str + "\n");

            writer.flush();
            writer.close();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Write a value, with no path
     *
     * @param value the value
     * @since 2.0 - SNAPSHOT this have been un-deprecated
     * since it has an utility, like creating comments or
     * values that doesn't need a path
     */
    public final void set(Object value) {
        if (!exists()) {
            create();
        }

        byte[] toByte = value.toString().getBytes(StandardCharsets.UTF_8);
        String val = new String(toByte, StandardCharsets.UTF_8);

        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);

            List<Object> sets = new ArrayList<>();

            boolean alreadySet = false;
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.equals(value.toString())) {
                    sets.add(line);
                } else {
                    alreadySet = true;
                    sets.add(val);
                }
            }

            if (!alreadySet) {
                sets.add(val);
            }

            BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);
            for (Object str : sets) {
                writer.write(str + "\n");
            }

            writer.flush();
            writer.close();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            closeStreams(reader);
        }
    }

    /**
     * Write a value into the file
     *
     * @param path  the path
     * @param value the value
     */
    public final void set(String path, Object value) {
        if (!exists()) {
            create();
        }

        path = path.replaceAll("\\s", "_");

        byte[] toByte = value.toString().getBytes(StandardCharsets.UTF_8);
        String val = new String(toByte, StandardCharsets.UTF_8);

        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);

            List<Object> sets = new ArrayList<>();

            boolean alreadySet = false;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.split(":")[0] != null) {
                    String currentPath = line.split(":")[0];

                    if (!currentPath.equals(path)) {
                        sets.add(line);
                    } else {
                        alreadySet = true;
                        sets.add(path + ": " + val);
                    }
                }
            }

            if (!alreadySet) {
                sets.add(path + ": " + val);
            }

            BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);
            for (Object str : sets) {
                writer.write(str + "\n");
            }

            writer.flush();
            writer.close();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            closeStreams(reader);
        }
    }

    /**
     * Write a list into the file
     *
     * @param path the path
     * @param list the values
     */
    public final void set(String path, List<?> list) {
        if (!exists()) {
            create();
        }

        path = path.replaceAll("\\s", "_");

        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);

            List<String> sets = new ArrayList<>();

            boolean adding = true;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("[LIST=" + path + "]")) {
                    adding = false;
                }
                if (!adding) {
                    if (line.equals("[/LIST=" + path + "]")) {
                        adding = true;
                    }
                }
                if (adding) {
                    if (!line.equals("[LIST=" + path + "]") && !line.equals("[/LIST=" + path + "]")) {
                        sets.add(line);
                    }
                }
            }

            sets.add("[LIST=" + path + "]");
            for (Object val : list) {
                sets.add(val.toString());
            }
            sets.add("[/LIST=" + path + "]");

            BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);
            for (Object str : sets) {
                writer.write(str + "\n");
            }

            writer.flush();
            writer.close();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            closeStreams(reader);
        }
    }

    /**
     * Write a list into the file
     *
     * @param path the path
     * @param list the values
     */
    public final void set(String path, AbstractSet<?> list) {
        if (!exists()) {
            create();
        }

        path = path.replaceAll("\\s", "_");

        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);

            List<String> sets = new ArrayList<>();

            boolean adding = true;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("[LIST=" + path + "]")) {
                    adding = false;
                }
                if (!adding) {
                    if (line.equals("[/LIST=" + path + "]")) {
                        adding = true;
                    }
                }
                if (adding) {
                    if (!line.equals("[LIST=" + path + "]") && !line.equals("[/LIST=" + path + "]")) {
                        sets.add(line);
                    }
                }
            }

            sets.add("[LIST=" + path + "]");
            for (Object val : list) {
                sets.add(val.toString());
            }
            sets.add("[/LIST=" + path + "]");

            BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);
            for (Object str : sets) {
                writer.write(str + "\n");
            }

            writer.flush();
            writer.close();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            closeStreams(reader);
        }
    }

    /**
     * Unset the value and the path
     *
     * @param path the path
     */
    public final void unset(String path) {
        if (!exists()) {
            create();
        }

        path = path.replaceAll("\\s", "_");

        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
            BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);

            String line;
            boolean list = false;
            while ((line = reader.readLine()) != null) {
                if (!line.equals(path) || !getKeyPath(line).equals(path)) {
                    if (!list) {
                        if (isOpenList(line, path)) {
                            list = true;
                        } else {
                            writer.write(line + "\n");
                        }
                    } else {
                        if (isCloseList(line, path))
                            list = false;
                    }
                }
            }

            writer.flush();
            writer.close();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            closeStreams(reader);
        }
    }

    /**
     * Get a value from a path
     *
     * @param path the path
     * @param def  the default value
     * @return an object
     */
    @NotNull
    public final Object get(String path, @NotNull Object def) {
        Object val = def;

        path = path.replaceAll("\\s", "_");

        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.split(":")[0] != null) {
                        String currentPath = line.split(":")[0];
                        if (currentPath.equals(path)) {
                            val = line.replace(currentPath + ": ", "");
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                closeStreams(reader);
            }
        }

        return val;
    }

    /**
     * Get a String from the path
     *
     * @param path the path
     * @param def  the default value
     * @return a String
     */
    @NotNull
    public final String getString(String path, @NotNull String def) {
        String val = def;

        path = path.replaceAll("\\s", "_");

        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.split(":")[0] != null) {
                        String currentPath = line.split(":")[0];
                        if (currentPath.equals(path)) {
                            val = line.replace(currentPath + ": ", "");
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                closeStreams(reader);
            }
        }

        return val;
    }

    /**
     * Get a list from the path
     *
     * @param path the path
     * @param default_contents default list contents in case of null contents
     * @return a list
     */
    @NotNull
    public final List<?> getList(String path, final Object... default_contents) {
        path = path.replaceAll("\\s", "_");
        List<Object> values = new ArrayList<>();

        if (isSet(path)) {
            if (exists()) {
                BufferedReader reader = null;
                try {
                    reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);

                    boolean adding = false;
                    Object line;
                    while ((line = reader.readLine()) != null) {
                        if (isOpenList(line.toString(), path)) {
                            adding = true;
                        }
                        if (isCloseList(line.toString(), path)) {
                            adding = false;
                        }
                        if (adding) {
                            if (!isOpenList(line.toString(), path)) {
                                //Ignore KarmaFile comments
                                if (!line.toString().startsWith("/// ") && !line.toString().endsWith(" -->")) {
                                    values.add(line);
                                }
                            }
                        }
                    }

                    return values;
                } catch (Throwable ex) {
                    ex.printStackTrace();
                } finally {
                    closeStreams(reader);
                }
            }
        } else {
            values.addAll(Arrays.asList(default_contents));
        }

        return values;
    }

    /**
     * Get a list of strings
     *
     * @param path the path
     * @param default_contents default contents in case of null contents
     * @return a list of strings
     */
    @NotNull
    public final List<String> getStringList(String path, final String... default_contents) {
        List<String> values = new ArrayList<>();

        path = path.replaceAll("\\s", "_");

        Object[] default_objects = Arrays.copyOf(default_contents, default_contents.length);

        List<?> originalList = getList(path, default_objects);
        if (!originalList.isEmpty()) {
            for (Object value : originalList) {
                String str = value.toString();
                //Ignore KarmaFile comments
                if (!str.startsWith("/// ") && !str.endsWith(" -->")) {
                    values.add(str);
                }
            }
        }

        return values;
    }

    /**
     * Read the complete file
     *
     * @return the complete file
     */
    @NotNull
    public final List<String> readFullFile() {
        try {
            return Files.readAllLines(file.toPath());
        } catch (Throwable ex) {
            return Collections.emptyList();
        }
    }

    /**
     * Get a Boolean from the path
     *
     * @param path the path
     * @param def  the default value
     * @return a boolean
     */
    public final boolean getBoolean(String path, boolean def) {
        boolean val = def;

        path = path.replaceAll("\\s", "_");

        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.split(":")[0] != null) {
                        String currentPath = line.split(":")[0];
                        if (currentPath.equals(path)) {
                            val = Boolean.parseBoolean(line.replace(currentPath + ": ", ""));
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                closeStreams(reader);
            }
        }

        return val;
    }

    /**
     * Check if the specified key is set
     *
     * @param path the key path
     * @return if the key path is set
     */
    public final boolean isSet(String path) {
        path = path.replaceAll("\\s", "_");

        boolean set = false;
        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.split(":")[0] != null) {
                        String currentPath = line.split(":")[0];
                        if (currentPath.equals(path) || isOpenList(line, path) || isCloseList(line, path)) {
                            set = true;
                            break;
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                closeStreams(reader);
            }
        }

        return set;
    }

    /**
     * Get an integer from the path
     *
     * @param path the path
     * @param def  the default value
     * @return an integer
     */
    public final int getInt(String path, int def) {
        int val = def;

        path = path.replaceAll("\\s", "_");

        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.split(":")[0] != null) {
                        String currentPath = line.split(":")[0];
                        if (currentPath.equals(path)) {
                            val = Integer.parseInt(line.replace(currentPath + ": ", ""));
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                closeStreams(reader);
            }
        }

        return val;
    }

    /**
     * Get a String from the path
     *
     * @param path the path
     * @param def  the default value
     * @return a String
     */
    public final double getDouble(String path, double def) {
        double val = def;

        path = path.replaceAll("\\s", "_");

        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.split(":")[0] != null) {
                        String currentPath = line.split(":")[0];
                        if (currentPath.equals(path)) {
                            val = Double.parseDouble(line.replace(currentPath + ": ", ""));
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                closeStreams(reader);
            }
        }

        return val;
    }

    /**
     * Get a String from the path
     *
     * @param path the path
     * @param def  the default value
     * @return a String
     */
    public final long getLong(String path, long def) {
        long val = def;

        path = path.replaceAll("\\s", "_");

        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.split(":")[0] != null) {
                        String currentPath = line.split(":")[0];
                        if (currentPath.equals(path)) {
                            val = Long.parseLong(line.replace(currentPath + ": ", ""));
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                closeStreams(reader);
            }
        }

        return val;
    }

    /**
     * Get the custom file
     *
     * @return a file
     */
    public final File getFile() {
        return file;
    }

    /**
     * Check if the file exists
     *
     * @return a boolean
     */
    public final boolean exists() {
        return file.exists();
    }

    /**
     * Get the key set of the file
     *
     * @param deep include keys with no values
     *
     * @return a map of key and set
     */
    public final Set<Key> getKeys(final boolean deep) {
        Set<Key> keys = new LinkedHashSet<>();

        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);

                String line;

                String pathKey;
                boolean list = false;
                Set<String> values = new LinkedHashSet<>();
                while ((line = reader.readLine()) != null) {
                    if (!line.replaceAll("\\s", "").isEmpty()) {
                        if (!isComment(line)) {
                            pathKey = getKeyPath(line);

                            if (isOpenList(line)) {
                                list = true;
                            } else {
                                if (isCloseList(line)) {
                                    list = false;
                                    Key key = new Key(pathKey, new LinkedHashSet<>(values));
                                    keys.add(key);

                                    values.clear();
                                } else {
                                    if (list) {
                                        if (!line.replaceAll("\\s", "").isEmpty()) {
                                            values.add(line
                                                    .replace("[", "{open}")
                                                    .replace("]", "{close}")
                                                    .replace(",", "{comma}"));
                                        }
                                    } else {
                                        if (hasValue(line)) {
                                            Object value = line.replaceFirst(pathKey + ": ", "");

                                            boolean add = true;
                                            if (value.toString().replaceAll("\\s", "").isEmpty()) {
                                                add = deep;
                                                if (add)
                                                    value = pathKey;
                                            }

                                            if (add) {
                                                Key key = new Key(pathKey, value);
                                                keys.add(key);
                                            }
                                        } else {
                                            if (deep) {
                                                Key key = new Key(pathKey, pathKey);
                                                keys.add(key);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                closeStreams(reader);
            }
        }

        return keys;
    }

    /**
     * Read the file completely
     *
     * @return the complete file as string
     */
    @Override
    public final String toString() {
        String val = "";

        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);

                StringBuilder val_builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    val_builder.append(line);
                }

                val = val_builder.toString();
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                closeStreams(reader);
            }
        }

        return val;
    }

    /**
     * Close all the streams to allow file-managing
     * out of plugin
     *
     * @param reader the inReader file reader
     */
    private void closeStreams(BufferedReader reader) {
        try {
            if (reader != null)
                reader.close();
        } catch (Throwable ignored) {
        }
    }
}
