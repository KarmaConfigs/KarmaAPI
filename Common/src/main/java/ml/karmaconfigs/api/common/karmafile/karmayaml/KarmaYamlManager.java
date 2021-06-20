package ml.karmaconfigs.api.common.karmafile.karmayaml;

import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.FileUtilities;
import ml.karmaconfigs.api.common.utils.StringUtils;
import ml.karmaconfigs.api.common.utils.reader.BoundedBufferedReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

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
 * Karma yaml file manager
 */
public final class KarmaYamlManager {

    private char spacer = '.';

    private final Map<String, Object> map = new LinkedHashMap<>();
    private final Set<KarmaYamlManager> children = new HashSet<>();
    private final KYMSource sourceRoot;
    private KarmaYamlManager parent = null;
    private String root = "";

    /**
     * Initialize the KarmaConfiguration
     *
     * @param source the karma source
     * @param name the file name
     * @param sub the file sub directories
     */
    public KarmaYamlManager(final KarmaSource source, String name, final String... sub) {
        if (!name.endsWith(".no_extension")) {
            String extension = FileUtilities.getExtension(name);
            if (StringUtils.isNullOrEmpty(extension))
                name = name + ".yml";
        }

        try {
            File file;
            String currPath = "";

            if (sub.length > 0) {
                StringBuilder pathBuilder = new StringBuilder();
                for (String path : sub)
                    pathBuilder.append(File.separator).append(path);

                currPath = pathBuilder.toString();

                file = new File(source.getDataPath().toFile() + currPath, name);
            } else {
                file = new File(source.getDataPath().toFile(), name);
            }

            if (FileUtilities.isValidFile(file)) {
                try {
                    Reader reader = new BoundedBufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                    Yaml yaml = new Yaml();
                    Map<String, Object> values = yaml.load(reader);
                    if (values != null)
                        map.putAll(values);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }

                sourceRoot = new KYMSource(file);
            } else {
                throw new RuntimeException("Tried to setup KarmaYamlManager for invalid file path/name ( Path: " + currPath + ", File name: " + name + " ) ");
            }
        } catch (Throwable ex) {
            throw new RuntimeException("Tried to setup KarmaYamlManager but something went wrong ( " + ex.fillInStackTrace() + " )");
        }
    }

    /**
     * Initialize the KarmaConfiguration
     *
     * @param configuration the configuration
     */
    public KarmaYamlManager(final Reader configuration) {
        Yaml yaml = new Yaml();
        Map<String, Object> values = yaml.load(configuration);
        if (values != null)
            map.putAll(values);

        sourceRoot = new KYMSource(configuration);
    }

    /**
     * Initialize the KarmaConfiguration
     *
     * @param configuration the configuration
     */
    public KarmaYamlManager(final InputStream configuration) {
        Yaml yaml = new Yaml();
        Map<String, Object> values = yaml.load(configuration);
        if (values != null)
            map.putAll(values);

        sourceRoot = new KYMSource(configuration);
    }

    /**
     * Initialize the KarmaConfiguration
     *
     * @param configuration the configuration
     * @param isPath if the
     */
    public KarmaYamlManager(final String configuration, final boolean isPath) {
        if (isPath) {
            File file = new File(configuration);
            try {
                Reader reader = new BoundedBufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                Yaml yaml = new Yaml();
                Map<String, Object> values = yaml.load(reader);
                if (values != null)
                    map.putAll(values);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }

            sourceRoot = new KYMSource(configuration, true);
        } else {
            Yaml yaml = new Yaml();
            Map<String, Object> values = yaml.load(configuration);
            if (values != null)
                map.putAll(values);

            sourceRoot = new KYMSource(configuration, false);
        }
    }

    /**
     * Initialize the KarmaConfiguration
     *
     * @param configuration the configuration
     */
    public KarmaYamlManager(final File configuration) {
        try {
            Reader reader = new BoundedBufferedReader(new InputStreamReader(new FileInputStream(configuration), StandardCharsets.UTF_8));
            Yaml yaml = new Yaml();
            Map<String, Object> values = yaml.load(reader);
            if (values != null)
                map.putAll(values);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        sourceRoot = new KYMSource(configuration);
    }

    /**
     * Initialize the KarmaConfiguration
     *
     * @param configuration the configuration
     */
    public KarmaYamlManager(final Path configuration) {
        try {
            Reader reader = new BoundedBufferedReader(new InputStreamReader(new FileInputStream(configuration.toFile()), StandardCharsets.UTF_8));
            Yaml yaml = new Yaml();
            Map<String, Object> values = yaml.load(reader);
            if (values != null)
                map.putAll(values);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        sourceRoot = new KYMSource(configuration);
    }

    /**
     * Initialize the KarmaConfiguration
     *
     * @param values the values
     */
    public KarmaYamlManager(final Map<?, ?> values) {
        for (Object key : values.keySet()) map.put(key.toString(), values.get(key));

        sourceRoot = new KYMSource(values);
    }

    /**
     * Set this manager spacer character
     *
     * @param spacerChar the new spacer character ( . by default )
     * @return this instance
     */
    public KarmaYamlManager spacer(final char spacerChar) {
        spacer = spacerChar;

        return this;
    }

    /**
     * Update the current values
     *
     * @param configuration the other karma configuration
     * @param addNew add non-added paths
     * @param ignore the keys to ignore when updating
     */
    public final void update(final KarmaYamlManager configuration, final boolean addNew, final String... ignore) {
        List<String> ignored = Arrays.asList(ignore);
        if (addNew) {
            for (String key : getKeySet()) {
                if (!ignored.contains(key)) {
                    set(key, configuration.get(key, get(key)));
                }
            }
        } else {
            for (String key : getKeySet()) {
                if (!ignored.contains(key)) {
                    set(key, configuration.get(key, get(key)));
                }
            }
        }
    }

    /**
     * Set a path value
     *
     * @param path the path
     * @param value the value
     *
     * @return the KarmaConfiguration section where the new
     * variable has been set or updated, if no section is present, the
     * current karma configuration will be returned
     */
    @NotNull
    public final KarmaYamlManager set(final String path, final Object value) {
        if (path.contains(String.valueOf(spacer))) {
            String[] data = path.split(StringUtils.escapeString(String.valueOf(spacer)));
            String realPath = data[data.length - 1];
            data = Arrays.copyOf(data, data.length - 1);

            KarmaYamlManager last = this;
            for (String section : data) {
                last = last.getSection(section);
            }

            last.set(realPath, value);
            return last;
        } else {
            map.put(path, value);
            return this;
        }
    }

    /**
     * Get from where this configuration
     * has been obtained
     *
     * @return the KarmaConfiguration source
     */
    @NotNull
    public final KYMSource getSourceRoot() {
        return sourceRoot;
    }

    /**
     * Get a new reloader for this KarmaConfiguration
     *
     * @return a reloader for this KarmaConfiguration
     *
     * RETURNS NULL IF THE SOURCE FOR THIS CONFIGURATION
     * IS NOT A {@link File} OR {@link Path}
     */
    @Nullable
    public final YamlReloader getReloader() {
        if (sourceRoot.getSource() instanceof File || sourceRoot.getSource() instanceof Path)
            return new YamlReloader(this);

        return null;
    }

    /**
     * Get the root path
     *
     * @return the root path
     */
    @NotNull
    public final String getRoot() {
        return root;
    }

    /**
     * Get the KarmaConfiguration parent
     *
     * @return the KarmaConfiguration parent
     */
    @Nullable
    public final KarmaYamlManager getParent() {
        return parent;
    }

    /**
     * Get an array of parents of parents
     *
     * @return the KarmaConfiguration parent tree
     */
    @NotNull
    public final KarmaYamlManager[] getParents() {
        KarmaYamlManager parent = getParent();
        List<KarmaYamlManager> list = new ArrayList<>();
        if (parent != null) {
            do {
                parent = parent.getParent();
                if (parent != null) {
                    list.add(parent);
                }
            } while (parent != null);
        }

        return list.toArray(new KarmaYamlManager[0]);
    }

    /**
     * Get an array of the current children for
     * this configuration
     *
     * @return this KarmaConfiguration children tree
     */
    @NotNull
    public final KarmaYamlManager[] getChildren() {
        List<KarmaYamlManager> childtree = new ArrayList<>();
        for (KarmaYamlManager child : children) {
            childtree.add(child);
            childtree.addAll(Arrays.asList(child.getChildren()));
        }

        return childtree.toArray(new KarmaYamlManager[0]);
    }

    /**
     * Get the KarmaConfiguration tree master
     *
     * @return the KarmaConfiguration main configuration
     */
    @NotNull
    public final KarmaYamlManager getTreeMaster() {
        KarmaYamlManager parent = getParent();
        if (parent != null) {
            do {
                if (parent.getParent() != null) {
                    parent = parent.getParent();
                } else {
                    break;
                }
            } while (true);
        } else {
            parent = this;
        }

        return parent;
    }

    /**
     * Save the made changes to the file
     *
     * @param target the target file
     *
     * @return the result
     */
    @NotNull
    public final KarmaYamlManager save(final File target) {
        if (parent != null) {
            return getTreeMaster().save(target);
        } else {
            for (KarmaYamlManager yaml : getChildren()) {
                KarmaYamlManager parentYaml = yaml.getParent();
                if (parentYaml != null)
                    parentYaml.set(yaml.getRoot(), yaml.map);
            }

            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            try {
                Yaml yaml = new Yaml(options);
                yaml.dump(map, new FileWriter(target));

                return new KarmaYamlManager(target);
            } catch (Throwable ex) {
                ex.printStackTrace();
                return this;
            }
        }
    }

    /**
     * Save the made changes to the file
     *
     * @param target the target file
     * @param source the source that wants to
     *                 save the file
     * @param resource the karma source internal resource
     *                 name
     *
     * @return the result
     */
    @NotNull
    public final KarmaYamlManager save(final File target, final KarmaSource source, final String resource) {
        save(target);

        try {
            FileCopy copy = new FileCopy(source, resource);
            copy.copy(target);

            return new KarmaYamlManager(target);
        } catch (Throwable ex) {
            ex.printStackTrace();
            return this;
        }
    }

    /**
     * Get a set of the configuration keys
     *
     * @return a set of available keys
     */
    @NotNull
    public final Set<String> getKeySet() {
        return map.keySet();
    }

    /**
     * Get an object
     *
     * @param path the object path
     * @param def the object default
     * @return the path result or default
     */
    public final Object get(final String path, final Object def) {
        if (path.contains(String.valueOf(spacer))) {
            String[] data = path.split(StringUtils.escapeString(String.valueOf(spacer)));
            String realPath = data[data.length - 1];
            data = Arrays.copyOf(data, data.length - 1);

            KarmaYamlManager last = this;
            for (String section : data) {
                last = last.getSection(section);
            }

            return last.map.getOrDefault(realPath, def);
        } else {
            return map.getOrDefault(path, def);
        }
    }

    /**
     * Get an object
     *
     * @param path the object path
     * @return the path result or default
     */
    @Nullable
    public final Object get(final String path) {
        if (path.contains(String.valueOf(spacer))) {
            String[] data = path.split(StringUtils.escapeString(String.valueOf(spacer)));
            String realPath = data[data.length - 1];
            data = Arrays.copyOf(data, data.length - 1);

            KarmaYamlManager last = this;
            for (String section : data) {
                last = last.getSection(section);
            }

            return last.map.getOrDefault(realPath, null);
        } else {
            return map.getOrDefault(path, null);
        }
    }

    /**
     * Get a list of objects
     *
     * @param path the objects path
     * @return the objects
     */
    @NotNull
    public final List<Object> getList(final String path) {
        Object value = get(path, null);
        List<Object> values = new ArrayList<>();

        if (value instanceof List) {
            List<?> list = (List<?>) value;
            values.addAll(list);
        }

        return values;
    }

    /**
     * Get a list of objects
     *
     * @param path the objects path
     * @param defaults the default list objects
     * @return the objects
     */
    @NotNull
    public final List<Object> getList(final String path, final Object... defaults) {
        Object value = get(path, Arrays.asList(defaults));
        List<Object> values = new ArrayList<>();

        if (value instanceof List) {
            List<?> list = (List<?>) value;
            values.addAll(list);
        }

        return values;
    }

    /**
     * Get a string
     *
     * @param path the string path
     * @return the string
     */
    @NotNull
    public final String getString(final String path) {
        Object value = get(path, null);
        if (value instanceof String)
            return (String) value;

        return "";
    }

    /**
     * Get a string
     *
     * @param path the string path
     * @param def the default value
     * @return the string
     */
    public final String getString(final String path, final String def) {
        Object value = get(path, def);
        if (value instanceof String)
            return (String) value;

        return def;
    }

    /**
     * Get an integer
     *
     * @param path the integer path
     * @return the integer
     */
    public final int getInt(final String path) {
        Object value = get(path, null);
        if (value instanceof Integer)
            return (Integer) value;

        return -1;
    }

    /**
     * Get an integer
     *
     * @param path the integer path
     * @param def the default value
     * @return the integer
     */
    public final int getInt(final String path, final int def) {
        Object value = get(path, def);
        if (value instanceof Integer)
            return (Integer) value;

        return def;
    }

    /**
     * Get a double
     *
     * @param path the double path
     * @return the double
     */
    public final double getDouble(final String path) {
        Object value = get(path, null);
        if (value instanceof Double)
            return (Double) value;

        return -1D;
    }

    /**
     * Get a double
     *
     * @param path the double path
     * @param def the default value
     * @return the double
     */
    public final double getDouble(final String path, final double def) {
        Object value = get(path, def);
        if (value instanceof Double)
            return (Double) value;

        return def;
    }

    /**
     * Get a long
     *
     * @param path the long path
     * @return the long
     */
    public final long getLong(final String path) {
        Object value = get(path, null);
        if (value instanceof Long)
            return (Long) value;

        return -1L;
    }

    /**
     * Get a long
     *
     * @param path the long path
     * @param def the default value
     * @return the long
     */
    public final long getLong(final String path, final long def) {
        Object value = get(path, def);
        if (value instanceof Long)
            return (Long) value;

        return def;
    }

    /**
     * Get a boolean
     *
     * @param path the boolean path
     * @return the boolean
     */
    public final boolean getBoolean(final String path) {
        Object value = get(path, null);
        if (value instanceof Boolean)
            return (Boolean) value;

        return false;
    }

    /**
     * Get a boolean
     *
     * @param path the boolean path
     * @param def the default value
     * @return the boolean
     */
    public final boolean getBoolean(final String path, final boolean def) {
        Object value = get(path, def);
        if (value instanceof Boolean)
            return (Boolean) value;

        return def;
    }

    /**
     * Get a list of strings
     *
     * @param path the list path
     * @return the list
     */
    @NotNull
    public final List<String> getStringList(final String path) {
        List<Object> list = getList(path);
        List<String> values = new ArrayList<>();
        for (Object object : list)
            values.add(object.toString());

        return values;
    }

    /**
     * Get a list of strings
     *
     * @param path the list path
     * @param defaults the default list objects
     * @return the list
     */
    @NotNull
    public final List<String> getStringList(final String path, final String... defaults) {
        Object value = get(path, Arrays.asList(defaults));
        List<String> values = new ArrayList<>();
        if (value instanceof List) {
            List<?> list = (List<?>) value;

            for (Object object : list)
                values.add(object.toString());
        }

        return values;
    }

    /**
     * Get the section
     *
     * @param path the section path
     * @return the section karma configuration or a new
     * configuration section if not exists/found
     */
    @NotNull
    public final KarmaYamlManager getSection(final String path) {
        Object value = get(path);
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            Map<String, Object> parsed = new LinkedHashMap<>();
            for (Object key : map.keySet())
                parsed.put(key.toString(), map.get(key));

            KarmaYamlManager sub = new KarmaYamlManager(parsed);
            sub.parent = this;
            sub.root = path;
            children.add(sub);

            return sub;
        } else {
            KarmaYamlManager configuration = new KarmaYamlManager(Collections.emptyMap());
            configuration.parent = this;
            configuration.root = path;
            children.add(configuration);

            return configuration;
        }
    }

    /**
     * Get the section
     *
     * @param path the section path
     * @param defaults the section defaults
     * @return a new configuration sections
     */
    @NotNull
    public final KarmaYamlManager getSection(final String path, final KarmaYamlManager defaults) {
        Object value = get(path);
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            Map<String, Object> parsed = new LinkedHashMap<>();
            for (Object key : map.keySet())
                parsed.put(key.toString(), map.get(key));

            KarmaYamlManager sub = new KarmaYamlManager(parsed);
            sub.parent = this;
            sub.root = path;
            children.add(sub);

            return sub;
        } else {
            KarmaYamlManager configuration = new KarmaYamlManager(defaults.map);
            configuration.parent = this;
            configuration.root = path;
            children.add(configuration);

            return configuration;
        }
    }

    /**
     * Get if the specified path is a section
     *
     * @param path the configuration path
     * @return if the specified path is a section
     */
    public final boolean isSection(final String path) {
        return get(path, "") instanceof LinkedHashMap;
    }

    /**
     * Get if the path is set in the file
     *
     * @param path the value path
     * @return if the path is set
     */
    public final boolean isSet(final String path) {
        return get(path, null) != null;
    }

    /**
     * Get if the specified path is an instance of
     * the expected type
     *
     * @param path the path
     * @param expected the expected type
     * @return if the types match
     */
    public final boolean matchesWith(final String path, final Object expected) {
        Object value = get(path);
        if (value != null)
            return expected.getClass().isAssignableFrom(value.getClass());

        return false;
    }

    /**
     * Get the yaml as string
     *
     * @return the yaml as string
     */
    @Override
    public @NotNull String toString() {
        Yaml yaml = new Yaml();
        return yaml.dump(map);
    }
}
