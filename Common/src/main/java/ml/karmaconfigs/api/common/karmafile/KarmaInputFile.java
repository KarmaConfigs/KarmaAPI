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

import ml.karmaconfigs.api.common.utils.string.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Karma file only for input stream and used only
 * to read
 */
public final class KarmaInputFile implements Serializable {

    /**
     * The karma file
     */
    private final List<String> lines = new ArrayList<>();

    /**
     * Initialize the karma file
     *
     * @param stream the file to read
     */
    public KarmaInputFile(final InputStream stream) {
        InputStreamReader ir = new InputStreamReader(stream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(ir);

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
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

        for (String line : lines) {
            if (line.split(":")[0] != null) {
                String actualPath = line.split(":")[0];
                if (actualPath.equals(path))
                    val = line.replace(actualPath + ": ", "");
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

        for (String line : lines) {
            if (line.split(":")[0] != null) {
                String actualPath = line.split(":")[0];
                if (actualPath.equals(path))
                    val = line.replace(actualPath + ": ", "");
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
        List<Object> values = new ArrayList();
        if (isSet(path)) {
            boolean adding = false;

            for (String line : lines) {
                if (isOpenList(line, path))
                    adding = true;
                if (isCloseList(line, path))
                    adding = false;
                if (adding &&
                        !isOpenList(line, path))
                    if (!line.startsWith("/// ") && !line.endsWith(" -->"))
                        values.add(line);
            }

            return values;
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
        return lines;
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

        for (String line : lines) {
            if (line.split(":")[0] != null) {
                String actualPath = line.split(":")[0];
                if (actualPath.equals(path))
                    val = Boolean.parseBoolean(line.replace(actualPath + ": ", ""));
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
        if (isSet(path)) {
            for (String line : lines) {
                if (isOpenList(line, path))
                    exist = true;
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

        for (String line : lines) {
            if (line.split(":")[0] != null) {
                String currentPath = line.split(":")[0];
                if (currentPath.equals(path) || isOpenList(line, path) || isCloseList(line, path)) {
                    set = true;
                    break;
                }
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

        for (String line : lines) {
            if (line.split(":")[0] != null) {
                String actualPath = line.split(":")[0];
                if (actualPath.equals(path))
                    val = Integer.parseInt(line.replace(actualPath + ": ", ""));
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

        for (String line : lines) {
            if (line.split(":")[0] != null) {
                String actualPath = line.split(":")[0];
                if (actualPath.equals(path))
                    val = Double.parseDouble(line.replace(actualPath + ": ", ""));
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

        for (String line : lines) {
            if (line.split(":")[0] != null) {
                String actualPath = line.split(":")[0];
                if (actualPath.equals(path))
                    val = Long.parseLong(line.replace(actualPath + ": ", ""));
            }
        }

        return val;
    }

    /**
     * Get the karma file key set
     *
     * @param deep include non-valued keys
     * @return the karma file keys
     */
    public Set<Key> getKeys(final boolean deep) {
        Set<Key> keys = new LinkedHashSet<>();

        for (String line : lines) {
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

        return keys;
    }

    /**
     * Get the karma file as string
     *
     * @return the karma file as string
     */
    public String toString() {
        String val;

        StringBuilder val_builder = new StringBuilder();
        for (String line : lines) {
            val_builder.append(line);
        }

        val = val_builder.toString();

        return val;
    }
}
