package ml.karmaconfigs.api.common.karmafile.karmayaml;

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
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import ml.karmaconfigs.api.common.utils.file.FileUtilities;
import ml.karmaconfigs.api.common.utils.reader.BoundedBufferedReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.*;

/**
 * Karma yaml manager
 */
public final class KarmaYamlManager {

    /**
     * Defaults
     */
    @Nullable
    private final KarmaYamlManager def;

    /**
     * Yaml keys and values
     */
    private final Map<String, Object> map = new LinkedHashMap<>();

    /**
     * Yaml children ( sections )
     */
    private final Set<KarmaYamlManager> children = new HashSet<>();

    /**
     * The yaml source
     */
    private final KYMSource sourceRoot;

    /**
     * The section spacer (key 'spacer == .' section name => Hello.World )
     */
    private char spacer = '.';

    /**
     * The parent yaml ( only valid if a section )
     */
    private KarmaYamlManager parent = null;

    /**
     * Root key
     */
    private String root = "";

    /**
     * Initialize the karma yaml manager
     *
     * @param source the yaml source
     * @param name the yaml file name
     * @param sub the yaml file folder
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
                    BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                    Yaml yaml = new Yaml();
                    Map<String, Object> values = yaml.load(boundedBufferedReader);
                    if (values != null)
                        this.map.putAll(values);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
                this.sourceRoot = new KYMSource(file);
            } else {
                throw new RuntimeException("Tried to setup KarmaYamlManager for invalid file path/name ( Path: " + currPath + ", File name: " + name + " ) ");
            }
        } catch (Throwable ex) {
            throw new RuntimeException("Tried to setup KarmaYamlManager but something went wrong ( " + ex.fillInStackTrace() + " )");
        }
        this.def = null;
    }

    /**
     * Initialize the karma yaml manager
     *
     * @param configuration the yaml
     */
    public KarmaYamlManager(final Reader configuration) {
        Yaml yaml = new Yaml();
        Map<String, Object> values = yaml.load(configuration);
        if (values != null)
            this.map.putAll(values);
        this.sourceRoot = new KYMSource(configuration);
        this.def = null;
    }

    /**
     * Initialize the karma yaml manager
     *
     * @param configuration the yaml
     */
    public KarmaYamlManager(final InputStream configuration) {
        Yaml yaml = new Yaml();
        Map<String, Object> values = yaml.load(configuration);
        if (values != null)
            this.map.putAll(values);
        this.sourceRoot = new KYMSource(configuration);
        this.def = null;
    }

    /**
     * Initialize the karma yaml manager
     *
     * @param configuration the yaml configuration/path
     * @param isPath if the yaml configuration string is a path
     */
    public KarmaYamlManager(final String configuration, final boolean isPath) {
        if (isPath) {
            File file = new File(configuration);
            try {
                BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                Yaml yaml = new Yaml();
                Map<String, Object> values = yaml.load(boundedBufferedReader);
                if (values != null)
                    this.map.putAll(values);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            this.sourceRoot = new KYMSource(configuration, true);
        } else {
            try {
                Yaml yaml = new Yaml();
                Path file = Files.createTempFile("karmayaml", StringUtils.generateString().create());
                Files.write(file, configuration.getBytes(StandardCharsets.UTF_8));
                Map<String, Object> values = yaml.load(new InputStreamReader(new FileInputStream(file.toFile()), StandardCharsets.UTF_8));
                if (values != null)
                    this.map.putAll(values);
                Files.deleteIfExists(file);
            } catch (Throwable ignored) {
            }
            this.sourceRoot = new KYMSource(configuration, false);
        }
        this.def = null;
    }

    /**
     * Initialize the karma yaml manager
     *
     * @param configuration the yaml
     */
    public KarmaYamlManager(final File configuration) {
        try {
            BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(new InputStreamReader(new FileInputStream(configuration), StandardCharsets.UTF_8));
            Yaml yaml = new Yaml();
            Map<String, Object> values = yaml.load(boundedBufferedReader);
            if (values != null)
                this.map.putAll(values);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        this.sourceRoot = new KYMSource(configuration);
        this.def = null;
    }

    /**
     * Initialize the karma yaml manager
     *
     * @param configuration the yaml
     */
    public KarmaYamlManager(final Path configuration) {
        try {
            BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(new InputStreamReader(new FileInputStream(configuration.toFile()), StandardCharsets.UTF_8));
            Yaml yaml = new Yaml();
            Map<String, Object> values = yaml.load(boundedBufferedReader);
            if (values != null)
                this.map.putAll(values);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        this.sourceRoot = new KYMSource(configuration);
        this.def = null;
    }

    /**
     * Initialize the karma yaml manager
     *
     * @param values the yaml key/value
     */
    public KarmaYamlManager(final Map<?, ?> values) {
        for (Object key : values.keySet())
            this.map.put(key.toString(), values.get(key));
        this.sourceRoot = new KYMSource(values);
        this.def = null;
    }

    /**
     * Initialize the karma yaml manager
     *
     * @param defaults the yaml defaults
     * @param source the yaml source
     * @param name the yaml file name
     * @param sub the yaml file folder
     */
    public KarmaYamlManager(final @NotNull KarmaYamlManager defaults, final KarmaSource source, String name, final String... sub) {
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
                    BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                    Yaml yaml = new Yaml();
                    Map<String, Object> values = yaml.load(boundedBufferedReader);
                    if (values != null)
                        this.map.putAll(values);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
                this.sourceRoot = new KYMSource(file);
            } else {
                throw new RuntimeException("Tried to setup KarmaYamlManager for invalid file path/name ( Path: " + currPath + ", File name: " + name + " ) ");
            }
        } catch (Throwable ex) {
            throw new RuntimeException("Tried to setup KarmaYamlManager but something went wrong ( " + ex.fillInStackTrace() + " )");
        }
        this.def = defaults;
    }

    /**
     * Initialize the karma yaml manager
     *
     * @param defaults the yaml defaults
     * @param configuration the yaml
     */
    public KarmaYamlManager(final @NotNull KarmaYamlManager defaults, final Reader configuration) {
        Yaml yaml = new Yaml();
        Map<String, Object> values = yaml.load(configuration);
        if (values != null)
            this.map.putAll(values);
        this.sourceRoot = new KYMSource(configuration);
        this.def = defaults;
    }

    /**
     * Initialize the karma yaml manager
     *
     * @param defaults the yaml defaults
     * @param configuration the yaml
     */
    public KarmaYamlManager(final @NotNull KarmaYamlManager defaults, final InputStream configuration) {
        Yaml yaml = new Yaml();
        Map<String, Object> values = yaml.load(configuration);
        if (values != null)
            this.map.putAll(values);
        this.sourceRoot = new KYMSource(configuration);
        this.def = defaults;
    }

    /**
     * Initialize the karma yaml manager
     *
     * @param defaults the yaml defaults
     * @param configuration the yaml configuration/path
     * @param isPath if the yaml configuration string is a path
     */
    public KarmaYamlManager(final @NotNull KarmaYamlManager defaults, final String configuration, final boolean isPath) {
        if (isPath) {
            File file = new File(configuration);
            try {
                BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                Yaml yaml = new Yaml();
                Map<String, Object> values = yaml.load(boundedBufferedReader);
                if (values != null)
                    this.map.putAll(values);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            this.sourceRoot = new KYMSource(configuration, true);
        } else {
            Yaml yaml = new Yaml();
            Map<String, Object> values = yaml.load(configuration);
            if (values != null)
                this.map.putAll(values);
            this.sourceRoot = new KYMSource(configuration, false);
        }
        this.def = defaults;
    }

    /**
     * Initialize the karma yaml manager
     *
     * @param defaults the yaml defaults
     * @param configuration the yaml
     */
    public KarmaYamlManager(final @NotNull KarmaYamlManager defaults, final File configuration) {
        try {
            BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(new InputStreamReader(new FileInputStream(configuration), StandardCharsets.UTF_8));
            Yaml yaml = new Yaml();
            Map<String, Object> values = yaml.load(boundedBufferedReader);
            if (values != null)
                this.map.putAll(values);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        this.sourceRoot = new KYMSource(configuration);
        this.def = defaults;
    }

    /**
     * Initialize the karma yaml manager
     *
     * @param defaults the yaml defaults
     * @param configuration the yaml
     */
    public KarmaYamlManager(final @NotNull KarmaYamlManager defaults, final Path configuration) {
        try {
            BoundedBufferedReader boundedBufferedReader = new BoundedBufferedReader(new InputStreamReader(new FileInputStream(configuration.toFile()), StandardCharsets.UTF_8));
            Yaml yaml = new Yaml();
            Map<String, Object> values = yaml.load(boundedBufferedReader);
            if (values != null)
                this.map.putAll(values);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        this.sourceRoot = new KYMSource(configuration);
        this.def = defaults;
    }

    /**
     * Initialize the karma yaml manager
     *
     * @param defaults the yaml defaults
     * @param values the yaml key/value
     */
    public KarmaYamlManager(final @NotNull KarmaYamlManager defaults, final Map<?, ?> values) {
        for (Object key : values.keySet())
            this.map.put(key.toString(), values.get(key));
        this.sourceRoot = new KYMSource(values);
        this.def = defaults;
    }

    /**
     * Set the manager spacer
     *
     * @param spacerChar the manager spacer
     * @return this instance
     */
    public KarmaYamlManager spacer(final char spacerChar) {
        this.spacer = spacerChar;
        return this;
    }

    /**
     * Update the current manager
     *
     * @param configuration the configuration to update from
     * @param addNew add non-existent keys
     * @param ignore ignored keys
     */
    public void update(final KarmaYamlManager configuration, final boolean addNew, final String... ignore) {
        List<String> ignored = Arrays.asList(ignore);
        if (addNew) {
            for (String key : getKeySet()) {
                if (!ignored.contains(key))
                    set(key, configuration.get(key, get(key)));
            }
        } else {
            for (String key : getKeySet()) {
                if (!ignored.contains(key))
                    set(key, configuration.get(key, get(key)));
            }
        }
    }

    /**
     * Set the yaml value
     *
     * @param path the key path
     * @param value the key value
     * @return this instance
     */
    @NotNull
    public KarmaYamlManager set(final String path, final Object value) {
        if (path.contains(String.valueOf(this.spacer))) {
            String[] data = path.split(StringUtils.escapeString(String.valueOf(this.spacer)));
            String realPath = data[data.length - 1];
            data = Arrays.<String>copyOf(data, data.length - 1);
            KarmaYamlManager last = this;
            for (String section : data)
                last = last.getSection(section);
            last.set(realPath, value);
            return last;
        }
        this.map.put(path, value);
        return this;
    }

    /**
     * Store an object instance
     *
     * @param path the instance object path
     * @param object the object to store
     * @return this instance
     */
    @NotNull
    public <T> KarmaYamlManager store(final String path, final T object) {
        set(path, StringUtils.serialize(object));

        return this;
    }

    /**
     * Get the yaml source root
     *
     * @return the yaml source root
     */
    @NotNull
    public KYMSource getSourceRoot() {
        return this.sourceRoot;
    }

    /**
     * Get the yaml reloader
     *
     * @return the yaml reloader ( only valid for file/path/configuration path yaml
     * generated sources )
     */
    @Nullable
    public YamlReloader getReloader() {
        if (this.sourceRoot.getSource() instanceof File || this.sourceRoot.getSource() instanceof Path)
            return new YamlReloader(this);

        return null;
    }

    /**
     * Get the yaml root key
     *
     * @return the yaml root key
     */
    @NotNull
    public String getRoot() {
        return this.root;
    }

    /**
     * Get the yaml parent
     *
     * @return the yaml parent
     */
    @Nullable
    public KarmaYamlManager getParent() {
        return this.parent;
    }

    /**
     * Get the yaml parents
     *
     * @return all the yaml parents
     */
    @NotNull
    public KarmaYamlManager[] getParents() {
        KarmaYamlManager parent = getParent();
        List<KarmaYamlManager> list = new ArrayList<>();
        if (parent != null)
            do {
                parent = parent.getParent();
                if (parent == null)
                    continue;
                list.add(parent);
            } while (parent != null);
        return list.<KarmaYamlManager>toArray(new KarmaYamlManager[0]);
    }

    /**
     * Get the yaml children
     *
     * @return all the yaml children
     */
    @NotNull
    public KarmaYamlManager[] getChildren() {
        List<KarmaYamlManager> childtree = new ArrayList<>();
        for (KarmaYamlManager child : this.children) {
            childtree.add(child);
            childtree.addAll(Arrays.asList(child.getChildren()));
        }
        return childtree.<KarmaYamlManager>toArray(new KarmaYamlManager[0]);
    }

    /**
     * Get the master tree of the yaml parent
     *
     * @return the master tree of the yaml parent
     */
    @NotNull
    public KarmaYamlManager getTreeMaster() {
        KarmaYamlManager parent = getParent();
        if (parent != null) {
            while (parent.getParent() != null)
                parent = parent.getParent();
        } else {
            parent = this;
        }
        return parent;
    }

    /**
     * Save the current yaml
     *
     * @param target the file to save in
     * @return this instance
     */
    @NotNull
    public KarmaYamlManager save(final File target) {
        if (this.parent != null)
            return getTreeMaster().save(target);
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
            yaml.dump(this.map, new FileWriter(target));
            return new KarmaYamlManager(target);
        } catch (Throwable ex) {
            ex.printStackTrace();
            return this;
        }
    }

    /**
     * Save the current yaml
     *
     * @param target the file to save in
     * @param source the source to read defaults from
     * @param resource the internal resource to read defaults from
     * @return this instance
     */
    @NotNull
    public KarmaYamlManager save(final File target, final KarmaSource source, final String resource) {
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
     * Get the yaml keys
     *
     * @return the yaml key set
     */
    @NotNull
    public Set<String> getKeySet() {
        return this.map.keySet();
    }

    /**
     * Get a value
     *
     * @param path the key path
     * @param def the key default value
     * @return the yaml value
     */
    public Object get(final String path, final Object def) {
        if (path.contains(String.valueOf(this.spacer))) {
            String[] data = path.split(StringUtils.escapeString(String.valueOf(this.spacer)));
            String realPath = data[data.length - 1];
            data = Arrays.<String>copyOf(data, data.length - 1);
            KarmaYamlManager last = this;
            for (String section : data)
                last = last.getSection(section);
            return last.map.getOrDefault(realPath, def);
        }
        return this.map.getOrDefault(path, def);
    }

    /**
     * Get a value
     *
     * @param path the key path
     * @return the yaml value
     */
    @Nullable
    public Object get(final String path) {
        if (path.contains(String.valueOf(this.spacer))) {
            String[] data = path.split(StringUtils.escapeString(String.valueOf(this.spacer)));
            String realPath = data[data.length - 1];
            data = Arrays.<String>copyOf(data, data.length - 1);
            KarmaYamlManager last = this;
            for (String section : data)
                last = last.getSection(section);
            if (this.def != null)
                return last.map.getOrDefault(realPath, this.def.get(path));
            return last.map.getOrDefault(realPath, null);
        }
        if (this.def != null)
            return this.map.getOrDefault(path, this.def.get(path));
        return this.map.getOrDefault(path, null);
    }

    /**
     * Get a value
     *
     * @param path the key path
     * @return the yaml value
     */
    @NotNull
    public List<Object> getList(final String path) {
        Object value = get(path);
        List<Object> values = new ArrayList<>();
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            values.addAll(list);
        }
        return values;
    }

    /**
     * Get a value
     *
     * @param path the key path
     * @param defaults the key default values
     * @return the yaml value
     */
    @NotNull
    public List<Object> getList(final String path, final Object... defaults) {
        Object value = get(path, Arrays.asList(defaults));
        List<Object> values = new ArrayList<>();
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            values.addAll(list);
        }
        return values;
    }

    /**
     * Get a value
     *
     * @param path the key path
     * @return the yaml value
     */
    @NotNull
    public String getString(final String path) {
        Object value = get(path);
        if (value instanceof String)
            return (String) value;
        return "";
    }

    /**
     * Get a value
     *
     * @param path the key path
     * @param def the key default value
     * @return the yaml value
     */
    public String getString(final String path, final String def) {
        Object value = get(path, def);
        if (value instanceof String)
            return (String) value;
        return def;
    }

    /**
     * Get a value
     *
     * @param path the key path
     * @return the yaml value
     */
    public int getInt(final String path) {
        Object value = get(path);
        if (value instanceof Integer)
            return (Integer) value;
        return -1;
    }

    /**
     * Get a value
     *
     * @param path the key path
     * @param def the key default value
     * @return the yaml value
     */
    public int getInt(final String path, final int def) {
        Object value = get(path, def);
        if (value instanceof Integer)
            return (Integer) value;
        return def;
    }

    /**
     * Get a value
     *
     * @param path the key path
     * @return the yaml value
     */
    public double getDouble(final String path) {
        Object value = get(path);
        if (value instanceof Double)
            return (Double) value;
        return -1.0D;
    }

    /**
     * Get a value
     *
     * @param path the key path
     * @param def the key default value
     * @return the yaml value
     */
    public double getDouble(final String path, final double def) {
        Object value = get(path, def);
        if (value instanceof Double)
            return (Double) value;
        return def;
    }

    /**
     * Get a value
     *
     * @param path the key path
     * @return the yaml value
     */
    public long getLong(final String path) {
        Object value = get(path);
        if (value instanceof Long)
            return (Long) value;
        return -1L;
    }

    /**
     * Get a value
     *
     * @param path the key path
     * @param def the key default value
     * @return the yaml value
     */
    public long getLong(final String path, final long def) {
        Object value = get(path, def);
        if (value instanceof Long)
            return (Long) value;
        return def;
    }

    /**
     * Get a value
     *
     * @param path the key path
     * @return the yaml value
     */
    public boolean getBoolean(final String path) {
        Object value = get(path);
        if (value instanceof Boolean)
            return (Boolean) value;
        return false;
    }

    /**
     * Get a value
     *
     * @param path the key path
     * @param def the key default value
     * @return the yaml value
     */
    public boolean getBoolean(final String path, final boolean def) {
        Object value = get(path, def);
        if (value instanceof Boolean)
            return (Boolean) value;
        return def;
    }

    /**
     * Get a value
     *
     * @param path the key path
     * @return the yaml value
     */
    @NotNull
    public List<String> getStringList(final String path) {
        List<Object> list = getList(path);
        List<String> values = new ArrayList<>();
        for (Object object : list)
            values.add(object.toString());
        return values;
    }

    /**
     * Get a value
     *
     * @param path the key path
     * @param defaults the key default values
     * @return the yaml value
     */
    @NotNull
    public List<String> getStringList(final String path, final String... defaults) {
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
     * Get an object instance
     *
     * @param path the instance string key
     * @param <T> the instance type
     * @return the instance object
     */
    @Nullable
    public <T> T getInstance(final String path) {
        return StringUtils.loadUnsafe(getString(path));
    }

    /**
     * Get a section of the yaml
     *
     * @param path the section path
     * @return the section
     */
    @NotNull
    public KarmaYamlManager getSection(final String path) {
        Object value = get(path);
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            Map<String, Object> parsed = new LinkedHashMap<>();
            for (Object key : map.keySet())
                parsed.put(key.toString(), map.get(key));
            KarmaYamlManager sub = new KarmaYamlManager(parsed);
            sub.parent = this;
            sub.root = path;
            this.children.add(sub);
            return sub;
        }
        KarmaYamlManager configuration = new KarmaYamlManager(Collections.emptyMap());
        configuration.parent = this;
        configuration.root = path;
        this.children.add(configuration);
        return configuration;
    }

    /**
     * Get a section of the yaml
     *
     * @param path the section path
     * @param defaults the section defaults
     * @return the section
     */
    @NotNull
    public KarmaYamlManager getSection(final String path, final KarmaYamlManager defaults) {
        Object value = get(path);
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            Map<String, Object> parsed = new LinkedHashMap<>();
            for (Object key : map.keySet())
                parsed.put(key.toString(), map.get(key));
            KarmaYamlManager sub = new KarmaYamlManager(parsed);
            sub.parent = this;
            sub.root = path;
            this.children.add(sub);
            return sub;
        }
        KarmaYamlManager configuration = new KarmaYamlManager(defaults.map);
        configuration.parent = this;
        configuration.root = path;
        this.children.add(configuration);
        return configuration;
    }

    /**
     * Get if the specified key is
     * a section
     *
     * @param path the key
     * @return if the path is a section
     */
    public boolean isSection(final String path) {
        return get(path, "") instanceof LinkedHashMap;
    }

    /**
     * Get if the specified key is
     * set in the yaml file
     *
     * @param path the key
     * @return if the path is set
     */
    public boolean isSet(final String path) {
        return (get(path, null) != null);
    }

    /**
     * Get if the specified value matches with
     * the expected value
     *
     * @param path the key
     * @param expected the expected value type
     * @return if the value matches with the expected
     * value type
     */
    public boolean matchesWith(final String path, final Class<?> expected) {
        Object value = get(path);
        if (value != null)
            return expected.isAssignableFrom(value.getClass());
        return false;
    }

    /**
     * Yaml to string
     *
     * @return the yaml as string
     */
    @NotNull
    public String toString() {
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        return yaml.dump(this.map);
    }

    /**
     * Get the yaml map of key/values
     *
     * @return the yaml key/value
     */
    @NotNull
    Map<String, Object> getMap() {
        return new LinkedHashMap<>(this.map);
    }
}
