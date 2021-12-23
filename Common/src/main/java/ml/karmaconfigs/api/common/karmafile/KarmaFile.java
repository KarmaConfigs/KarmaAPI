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

import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.file.FileUtilities;
import ml.karmaconfigs.api.common.utils.file.PathUtilities;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.*;

/**
 * Karma file
 */
public final class KarmaFile implements Serializable {

    /**
     * The karma file
     */
    private final File file;

    /**
     * Initialize the karma file
     *
     * @param source the file source
     * @param name the file name
     * @param dir the file path
     *
     * @throws IllegalStateException if the target path is not a valid path
     */
    public KarmaFile(final KarmaSource source, final String name, final String... dir) throws IllegalStateException {
        Path dataFolder = source.getDataPath();
        if (dir.length > 0) {
            for (String str : dir) {
                dataFolder = dataFolder.resolve(str);
            }
        }

        file = PathUtilities.getFixedPath(dataFolder.resolve(name)).toFile();

        if (!FileUtilities.isValidFile(file)) {
            throw new IllegalStateException("Tried to start a karma file on invalid file path ( " + dataFolder.resolve(name) + " )");
        }
    }

    /**
     * Initialize the karma file
     *
     * @param target the target karma file
     */
    public KarmaFile(final File target) {
        this.file = FileUtilities.getFixedFile(target);
    }

    /**
     * Initialize the karma file
     *
     * @param target the target karma file
     */
    public KarmaFile(final Path target) {
        file = PathUtilities.getFixedPath(target).toFile();
    }

    /**
     * Export the karma file from an internal file
     *
     * @param resource the internal file
     */
    public void exportFromFile(final InputStream resource) {
        if (!exists())
            create();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8));
            BufferedWriter writer = Files.newBufferedWriter(this.file.toPath(), StandardCharsets.UTF_8);
            String line;
            while ((line = reader.readLine()) != null)
                writer.write(line + "\n");
            writer.flush();
            writer.close();
            reader.close();
            resource.close();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get if the line is a comment
     *
     * @param line the text line
     * @return if the line is a comment
     */
    private boolean isComment(final String line) {
        if (line.startsWith("/*"))
            return true;

        return (line.startsWith("/// ") || (line.startsWith("// ") && line.endsWith(" -->")));
    }

    /**
     * Get if the line is an opening list
     *
     * @param line the text line
     * @param path the list name
     * @return if the line is an opening list
     */
    private boolean isOpenList(final String line, final String path) {
        return line.equals("[LIST=" + path.replaceAll("\\s", "_") + "]");
    }

    /**
     * Get if the line is an opening list
     *
     * @param line the text line
     * @return if the line is an opening list
     */
    private boolean isOpenList(final String line) {
        return (line.startsWith("[LIST=") && line.endsWith("]"));
    }

    /**
     * Get if the line is a closing list
     *
     * @param line the text line
     * @param path the list name
     * @return if the line is a closing list
     */
    private boolean isCloseList(final String line, final String path) {
        return line.equals("[/LIST=" + path.replaceAll("\\s", "_") + "]");
    }

    /**
     * Get if the line is a closing list
     *
     * @param line the text line
     * @return if the line is a closing list
     */
    private boolean isCloseList(final String line) {
        return (line.startsWith("[/LIST=") && line.endsWith("]"));
    }

    /**
     * Get a key path
     *
     * @param line the text line
     * @return the line key path
     */
    private String getKeyPath(final String line) {
        if (isOpenList(line) || isCloseList(line)) {
            String pathN1 = line.replaceFirst("\\[LIST=", "").replaceFirst("\\[/LIST=", "");
            return pathN1.substring(0, pathN1.length() - 1);
        }
        if (line.contains(":"))
            return line.split(":")[0];
        return line;
    }

    /**
     * Apply karma file attribute to the file
     */
    public void applyKarmaAttribute() {
        if (exists())
            try {
                UserDefinedFileAttributeView view = Files.<UserDefinedFileAttributeView>getFileAttributeView(this.file.toPath(), UserDefinedFileAttributeView.class);
                view.write("filetp", Charset.defaultCharset().encode("KarmaFile"));
            } catch (Throwable ignored) {
            }
    }

    /**
     * Create the file
     */
    public void create() {
        FileUtilities.create(file);
    }

    /**
     * Set a new value
     *
     * @param value the value
     */
    public void set(final Object value) {
        if (!exists())
            create();
        byte[] toByte = value.toString().getBytes(StandardCharsets.UTF_8);
        String val = new String(toByte, StandardCharsets.UTF_8);
        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
            List<Object> sets = new ArrayList<>();
            boolean alreadySet = false;
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.equals(value.toString())) {
                    sets.add(line);
                    continue;
                }
                alreadySet = true;
                sets.add(val);
            }
            if (!alreadySet)
                sets.add(val);
            BufferedWriter writer = Files.newBufferedWriter(this.file.toPath(), StandardCharsets.UTF_8);
            for (Object str : sets)
                writer.write(str + "\n");
            writer.flush();
            writer.close();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            closeStreams(reader);
        }
    }

    /**
     * Set a new value
     *
     * @param path the key path
     * @param value the value
     */
    public void set(String path, final Object value) {
        if (!exists())
            create();
        path = path.replaceAll("\\s", "_");
        if (isSet(path) && isList(path))
            unset(path);
        String val;
        byte[] toByte;
        if (value instanceof byte[]) {
            toByte = (byte[]) value;
        } else {
            toByte = String.valueOf(value).getBytes(StandardCharsets.UTF_8);
        }

        val = new String(toByte, StandardCharsets.UTF_8);
        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
            List<Object> sets = new ArrayList<>();
            boolean alreadySet = false;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.split(":")[0] != null) {
                    String currentPath = line.split(":")[0];
                    if (!currentPath.equals(path)) {
                        sets.add(line);
                        continue;
                    }
                    alreadySet = true;
                    sets.add(path + ": " + val);
                }
            }
            if (!alreadySet)
                sets.add(path + ": " + val);
            BufferedWriter writer = Files.newBufferedWriter(this.file.toPath(), StandardCharsets.UTF_8);
            for (Object str : sets)
                writer.write(str + "\n");
            writer.flush();
            writer.close();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            closeStreams(reader);
        }
    }

    /**
     * Set a new value
     *
     * @param path the key path
     * @param list the values
     */
    public void set(String path, final List<?> list) {
        if (!exists())
            create();
        path = path.replaceAll("\\s", "_");
        if (isSet(path) && !isList(path))
            unset(path);
        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
            List<String> sets = new ArrayList<>();
            boolean adding = true;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("[LIST=" + path + "]"))
                    adding = false;
                if (!adding &&
                        line.equals("[/LIST=" + path + "]"))
                    adding = true;
                if (adding &&
                        !line.equals("[LIST=" + path + "]") && !line.equals("[/LIST=" + path + "]"))
                    sets.add(line);
            }
            sets.add("[LIST=" + path + "]");
            for (Object val : list)
                sets.add(val.toString());
            sets.add("[/LIST=" + path + "]");
            BufferedWriter writer = Files.newBufferedWriter(this.file.toPath(), StandardCharsets.UTF_8);
            for (String str : sets)
                writer.write(str + "\n");
            writer.flush();
            writer.close();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            closeStreams(reader);
        }
    }

    /**
     * Unset a key and its value
     * if set
     *
     * @param path the key path
     */
    public void unset(String path) {
        if (!exists())
            create();
        path = path.replaceAll("\\s", "_");
        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
            BufferedWriter writer = Files.newBufferedWriter(this.file.toPath(), StandardCharsets.UTF_8);
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.equals(path))
                    writer.write(line + "\n");
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
     * Unset a list
     *
     * @param path the list path
     */
    public void unsetList(String path) {
        if (!exists())
            create();
        path = path.replaceAll("\\s", "_");
        BufferedReader reader = null;
        boolean list = false;
        try {
            reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
            BufferedWriter writer = Files.newBufferedWriter(this.file.toPath(), StandardCharsets.UTF_8);
            String line;
            while ((line = reader.readLine()) != null) {
                if (isOpenList(line) && getKeyPath(line).equals(path)) {
                    list = true;
                } else if (isCloseList(line) && getKeyPath(line).equals(path)) {
                    list = false;
                }
                if (!list)
                    writer.write(line + "\n");
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
     * Delete the file
     */
    public void delete() {
        FileUtilities.destroy(file);
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value
     * @return the value
     */
    @NotNull
    public Object get(String path, final @NotNull Object def) {
        Object val = def;
        path = path.replaceAll("\\s", "_");
        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.split(":")[0] != null) {
                        String actualPath = line.split(":")[0];
                        if (actualPath.equals(path)) {
                            if (line.startsWith(actualPath + ": ")) {
                                val = line.replace(actualPath + ": ", "");
                            } else {
                                val = line.replace(actualPath + ":", "");
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
        return val;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value
     * @return the value
     */
    @NotNull
    public String getString(String path, final @NotNull String def) {
        String val = def;
        path = path.replaceAll("\\s", "_");
        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.split(":")[0] != null) {
                        String actualPath = line.split(":")[0];
                        if (actualPath.equals(path)) {
                            if (line.startsWith(actualPath + ": ")) {
                                val = line.replace(actualPath + ": ", "");
                            } else {
                                val = line.replace(actualPath + ":", "");
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
        return val;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param default_contents the default values
     * @return the value
     */
    @NotNull
    public List<?> getList(String path, final Object... default_contents) {
        path = path.replaceAll("\\s", "_");
        List<Object> values = new ArrayList<>();
        if (isSet(path)) {
            if (exists()) {
                BufferedReader reader = null;
                try {
                    reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                    boolean adding = false;
                    Object line;
                    while ((line = reader.readLine()) != null) {
                        if (isOpenList(line.toString(), path))
                            adding = true;
                        if (isCloseList(line.toString(), path))
                            adding = false;
                        if (adding &&
                                !isOpenList(line.toString(), path))
                            if (!line.toString().startsWith("/// ") && !line.toString().endsWith(" -->"))
                                values.add(line);
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
     * Get a value
     *
     * @param path the value path
     * @param default_contents the default values
     * @return the value
     */
    @NotNull
    public List<String> getStringList(String path, final String... default_contents) {
        List<String> values = new ArrayList<>();
        path = path.replaceAll("\\s", "_");
        Object[] default_objects = Arrays.copyOf((Object[]) default_contents, default_contents.length);
        List<?> originalList = getList(path, default_objects);
        if (!originalList.isEmpty())
            for (Object value : originalList) {
                String str = value.toString();
                if (!str.startsWith("/// ") && !str.endsWith(" -->"))
                    values.add(str);
            }
        return values;
    }

    /**
     * Read the file completely
     *
     * @return the file lines
     */
    @NotNull
    public List<String> readFullFile() {
        try {
            return Files.readAllLines(this.file.toPath());
        } catch (Throwable ex) {
            return Collections.emptyList();
        }
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value
     * @return the value
     */
    public boolean getBoolean(String path, final boolean def) {
        boolean val = def;
        path = path.replaceAll("\\s", "_");
        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.split(":")[0] != null) {
                        String actualPath = line.split(":")[0];
                        if (actualPath.equals(path)) {
                            if (line.startsWith(actualPath + ": ")) {
                                val = Boolean.parseBoolean(line.replace(actualPath + ": ", ""));
                            } else {
                                val = Boolean.parseBoolean(line.replace(actualPath + ":", ""));
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
        return val;
    }

    /**
     * Get if a key is a list
     *
     * @param path the key
     * @return if the path is a list
     */
    public boolean isList(String path) {
        path = path.replaceAll("\\s", "_");
        boolean exist = false;
        if (isSet(path) &&
                exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                boolean adding = false;
                Object line;
                while ((line = reader.readLine()) != null) {
                    if (isOpenList(line.toString(), path))
                        exist = true;
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            } finally {
                closeStreams(reader);
            }
        }
        return exist;
    }

    /**
     * Get if a key is set
     *
     * @param path the key
     * @return if the path is set
     */
    public boolean isSet(String path) {
        path = path.replaceAll("\\s", "_");
        boolean set = false;
        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
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
     * Get a value
     *
     * @param path the value path
     * @param def the default value
     * @return the value
     */
    public int getInt(String path, final int def) {
        int val = def;
        path = path.replaceAll("\\s", "_");
        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.split(":")[0] != null) {
                        String actualPath = line.split(":")[0];
                        if (actualPath.equals(path)) {
                            if (line.startsWith(actualPath + ": ")) {
                                val = Integer.parseInt(line.replace(actualPath + ": ", ""));
                            } else {
                                val = Integer.parseInt(line.replace(actualPath + ":", ""));
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
        return val;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value
     * @return the value
     */
    public double getDouble(String path, final double def) {
        double val = def;
        path = path.replaceAll("\\s", "_");
        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.split(":")[0] != null) {
                        String actualPath = line.split(":")[0];
                        if (actualPath.equals(path)) {
                            if (line.startsWith(actualPath + ": ")) {
                                val = Double.parseDouble(line.replace(actualPath + ": ", ""));
                            } else {
                                val = Double.parseDouble(line.replace(actualPath + ":", ""));
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
        return val;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value
     * @return the value
     */
    public long getLong(String path, final long def) {
        long val = def;
        path = path.replaceAll("\\s", "_");
        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.split(":")[0] != null) {
                        String actualPath = line.split(":")[0];
                        if (actualPath.equals(path)) {
                            if (line.startsWith(actualPath + ": ")) {
                                val = Long.parseLong(line.replace(actualPath + ": ", ""));
                            } else {
                                val = Long.parseLong(line.replace(actualPath + ":", ""));
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
        return val;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value
     * @return the value
     */
    public float getFloat(String path, final float def) {
        float val = def;
        path = path.replaceAll("\\s", "_");
        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.split(":")[0] != null) {
                        String actualPath = line.split(":")[0];
                        if (actualPath.equals(path)) {
                            if (line.startsWith(actualPath + ": ")) {
                                val = Float.parseFloat(line.replace(actualPath + ": ", ""));
                            } else {
                                val = Float.parseFloat(line.replace(actualPath + ":", ""));
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
        return val;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value
     * @return the value
     */
    public short getShort(String path, final short def) {
        short val = def;
        path = path.replaceAll("\\s", "_");
        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.split(":")[0] != null) {
                        String actualPath = line.split(":")[0];
                        if (actualPath.equals(path)) {
                            if (line.startsWith(actualPath + ": ")) {
                                val = Short.parseShort(line.replace(actualPath + ": ", ""));
                            } else {
                                val = Short.parseShort(line.replace(actualPath + ":", ""));
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
        return val;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value
     * @return the value
     */
    public byte getByte(String path, final byte def) {
        byte val = def;
        path = path.replaceAll("\\s", "_");
        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.split(":")[0] != null) {
                        String actualPath = line.split(":")[0];
                        if (actualPath.equals(path)) {
                            if (line.startsWith(actualPath + ": ")) {
                                val = Byte.parseByte(line.replace(actualPath + ": ", ""));
                            } else {
                                val = Byte.parseByte(line.replace(actualPath + ":", ""));
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
        return val;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value
     * @return the value
     */
    public byte[] getBytes(String path, final byte[] def) {
        byte[] val = def;
        path = path.replaceAll("\\s", "_");
        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.split(":")[0] != null) {
                        String actualPath = line.split(":")[0];
                        if (actualPath.equals(path)) {
                            String result;
                            if (line.startsWith(actualPath + ": ")) {
                                result = line.replace(actualPath + ": ", "");
                            } else {
                                result = line.replace(actualPath + ":", "");
                            }

                            val = result.getBytes(StandardCharsets.UTF_8);
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
     * Get the file
     *
     * @return the file
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Get if the file exists
     *
     * @return if the file exists
     */
    public boolean exists() {
        return this.file.exists();
    }

    /**
     * Get the karma file key set
     *
     * @param deep include non-valued keys
     * @return the karma file keys
     */
    public Set<Key> getKeys(final boolean deep) {
        Set<Key> keys = new LinkedHashSet<>();
        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.replaceAll("\\s", "").isEmpty() &&
                            !isComment(line)) {
                        Key key;
                        String pathKey = getKeyPath(line);
                        if (isList(pathKey)) {
                            List<?> list = getList(pathKey);
                            if (list.isEmpty() && !deep)
                                continue;
                            key = new Key(pathKey, list);
                        } else {
                            Object value = get(pathKey, "");
                            if (StringUtils.isNullOrEmpty(value) && !deep)
                                continue;
                            key = new Key(pathKey, value);
                        }
                        keys.add(key);
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
     * Get the karma file as string
     *
     * @return the karma file as string
     */
    public String toString() {
        String val = "";
        if (exists()) {
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                StringBuilder val_builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    val_builder.append(line);
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
     * Close streams
     *
     * @param reader the reader
     */
    private void closeStreams(final BufferedReader reader) {
        try {
            if (reader != null)
                reader.close();
        } catch (Throwable ignored) {
        }
    }
}
