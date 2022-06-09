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
import ml.karmaconfigs.api.common.timer.scheduler.LateScheduler;
import ml.karmaconfigs.api.common.timer.scheduler.worker.AsyncLateScheduler;
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
import java.util.concurrent.atomic.AtomicReference;

import static ml.karmaconfigs.api.common.karma.KarmaAPI.source;

/**
 * Karma file
 *
 * @deprecated As of 1.3.3-SNAPSHOT. {@link ml.karmaconfigs.api.common.karma.file.KarmaMain} should be used
 */
@SuppressWarnings("unused")
@Deprecated
public final class AsyncKarmaFile implements Serializable {

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
    public AsyncKarmaFile(final KarmaSource source, final String name, final String... dir) throws IllegalStateException {
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
    public AsyncKarmaFile(final File target) {
        this.file = FileUtilities.getFixedFile(target);
    }

    /**
     * Initialize the karma file
     *
     * @param target the target karma file
     */
    public AsyncKarmaFile(final Path target) {
        file = PathUtilities.getFixedPath(target).toFile();
    }

    /**
     * Export the karma file from an internal file
     *
     * @param resource the internal file
     * @return the result
     */
    public LateScheduler<Void> exportFromFile(final InputStream resource) {
        LateScheduler<Void> result = new AsyncLateScheduler<>();

        source(true).async().queue("async_file_export", () -> {
            if (!exists()) {
                create().whenComplete(() -> exportFromFile(resource).whenComplete((rs, err) -> {
                    if (err != null) {
                        result.complete(rs, null);
                    } else {
                        result.complete(rs);
                    }
                }));
            } else {
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

                    result.complete(null);
                } catch (Throwable ex) {
                    result.complete(null, ex);
                }
            }
        });

        return result;
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
     *
     * @return the result
     */
    public LateScheduler<Void> applyKarmaAttribute() {
        LateScheduler<Void> result = new AsyncLateScheduler<>();

        source(true).async().queue("async_file_attribute", () -> {
            if (exists()) {
                try {
                    UserDefinedFileAttributeView view = Files.getFileAttributeView(this.file.toPath(), UserDefinedFileAttributeView.class);
                    view.write("filetp", Charset.defaultCharset().encode("KarmaFile"));

                    result.complete(null);
                } catch (Throwable ex) {
                    result.complete(null, ex);
                }
            } else {
                result.complete(null);
            }
        });

        return result;
    }

    /**
     * Create the file
     *
     * @return the result
     */
    public LateScheduler<Void> create() {
        LateScheduler<Void> result = new AsyncLateScheduler<>();

        source(true).async().queue("async_file_creation", () -> {
            FileUtilities.create(file);
            result.complete(null);
        });

        return result;
    }

    /**
     * Set a new value
     *
     * @param value the value
     * @return the result
     */
    public LateScheduler<Void> set(final Object value) {
        LateScheduler<Void> result = new AsyncLateScheduler<>();

        source(true).async().queue("async_file_set", () -> {
            if (!exists()) {
                create().whenComplete(() -> set(value).whenComplete((rs, err) -> {
                    if (err != null) {
                        result.complete(rs, err);
                    } else {
                        result.complete(rs);
                    }
                }));
            } else {
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

                    result.complete(null);
                } catch (Throwable ex) {
                    result.complete(null, ex);
                } finally {
                    closeStreams(reader);
                }
            }
        });

        return result;
    }

    /**
     * Set a new value
     *
     * @param path the key path
     * @param value the value
     *
     * @return the result
     */
    public LateScheduler<Void> set(String path, final Object value) {
        LateScheduler<Void> result = new AsyncLateScheduler<>();

        path = path.replaceAll("\\s", "_");

        String finalPath = path;
        source(true).async().queue("async_file_set", () -> {
            if (!exists()) {
                create().whenComplete(() -> set(finalPath, value).whenComplete((rs, err) -> {
                    if (err != null) {
                        result.complete(rs, err);
                    } else {
                        result.complete(rs);
                    }
                }));
            } else {
                isSet(finalPath).whenComplete((rs) -> {
                    if (rs) {
                        unset(finalPath).whenComplete(() -> set(finalPath, value)).whenComplete((rs2, ex) -> {
                            if (ex != null) {
                                result.complete(rs2, ex);
                            } else {
                                result.complete(rs2);
                            }
                        });
                    } else {
                        byte[] toByte = value.toString().getBytes(StandardCharsets.UTF_8);
                        String val = new String(toByte, StandardCharsets.UTF_8);
                        BufferedReader reader = null;
                        try {
                            reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                            List<Object> sets = new ArrayList<>();

                            boolean alreadySet = false;

                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (line.split(":")[0] != null) {
                                    String currentPath = line.split(":")[0];
                                    if (!currentPath.equals(finalPath)) {
                                        sets.add(line);
                                        continue;
                                    }
                                    alreadySet = true;
                                    sets.add(finalPath + ": " + val);
                                }
                            }

                            if (!alreadySet)
                                sets.add(finalPath + ": " + val);

                            BufferedWriter writer = Files.newBufferedWriter(this.file.toPath(), StandardCharsets.UTF_8);
                            for (Object str : sets)
                                writer.write(str + "\n");

                            writer.flush();
                            writer.close();
                            result.complete(null);
                        } catch (Throwable ex) {
                            result.complete(null, ex);
                        } finally {
                            closeStreams(reader);
                        }
                    }
                });
            }
        });

        return result;
    }

    /**
     * Set a new value
     *
     * @param path the key path
     * @param list the values
     *
     * @return the result
     */
    public LateScheduler<Void> set(String path, final List<?> list) {
        LateScheduler<Void> result = new AsyncLateScheduler<>();

        path = path.replaceAll("\\s", "_");

        String finalPath = path;
        source(true).async().queue("async_file_set", () -> {
            if (!exists())
                create();

            isSet(finalPath).whenComplete((rs) -> {
                if (rs) {
                    unset(finalPath).whenComplete(() -> set(finalPath, list)).whenComplete((rs2, ex) -> {
                        if (ex != null) {
                            result.complete(rs2, ex);
                        } else {
                            result.complete(rs2);
                        }
                    });
                } else {
                    BufferedReader reader = null;
                    try {
                        reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                        List<String> sets = new ArrayList<>();

                        boolean adding = true;

                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.equals("[LIST=" + finalPath + "]"))
                                adding = false;
                            if (!adding &&
                                    line.equals("[/LIST=" + finalPath + "]"))
                                adding = true;
                            if (adding &&
                                    !line.equals("[LIST=" + finalPath + "]") && !line.equals("[/LIST=" + finalPath + "]"))
                                sets.add(line);
                        }

                        sets.add("[LIST=" + finalPath + "]");
                        for (Object val : list)
                            sets.add(val.toString());
                        sets.add("[/LIST=" + finalPath + "]");

                        BufferedWriter writer = Files.newBufferedWriter(this.file.toPath(), StandardCharsets.UTF_8);
                        for (String str : sets)
                            writer.write(str + "\n");

                        writer.flush();
                        writer.close();

                        result.complete(null);
                    } catch (Throwable ex) {
                        result.complete(null, ex);
                    } finally {
                        closeStreams(reader);
                    }
                }
            });
        });

        return result;
    }

    /**
     * Unset a key and its value
     * if set
     *
     * @param path the key path
     * @return the result
     */
    public LateScheduler<Void> unset(String path) {
        LateScheduler<Void> result = new AsyncLateScheduler<>();

        path = path.replaceAll("\\s", "_");

        String finalPath = path;
        source(true).async().queue("async_file_unset", () -> {
            if (!exists())
                create();

            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                BufferedWriter writer = Files.newBufferedWriter(this.file.toPath(), StandardCharsets.UTF_8);

                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.equals(finalPath))
                        writer.write(line + "\n");
                }

                writer.flush();
                writer.close();

                result.complete(null);
            } catch (Throwable ex) {
                result.complete(null, ex);
            } finally {
                closeStreams(reader);
            }
        });

        return result;
    }

    /**
     * Unset a list
     *
     * @param path the list path
     * @return the result
     */
    public LateScheduler<Void> unsetList(String path) {
        LateScheduler<Void> result = new AsyncLateScheduler<>();

        path = path.replaceAll("\\s", "_");

        String finalPath = path;
        source(true).async().queue("async_file_unset", () -> {
            if (!exists()) {
                create().whenComplete(() -> unsetList(finalPath).whenComplete((rs, err) -> {
                    if (err != null) {
                        result.complete(null, err);
                    } else {
                        result.complete(null);
                    }
                }));
            } else {
                BufferedReader reader = null;
                boolean list = false;
                try {
                    reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                    BufferedWriter writer = Files.newBufferedWriter(this.file.toPath(), StandardCharsets.UTF_8);

                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (isOpenList(line) && getKeyPath(line).equals(finalPath)) {
                            list = true;
                        } else if (isCloseList(line) && getKeyPath(line).equals(finalPath)) {
                            list = false;
                        }
                        if (!list)
                            writer.write(line + "\n");
                    }

                    writer.flush();
                    writer.close();

                    result.complete(null);
                } catch (Throwable ex) {
                    result.complete(null, ex);
                } finally {
                    closeStreams(reader);
                }
            }
        });

        return result;
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
    public LateScheduler<Object> get(String path, final @NotNull Object def) {
        LateScheduler<Object> result = new AsyncLateScheduler<>();

        path = path.replaceAll("\\s", "_");

        String finalPath = path;
        source(true).async().queue("async_file_get", () -> {
            Object val = def;

            if (exists()) {
                BufferedReader reader = null;
                try {
                    reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.split(":")[0] != null) {
                            String actualPath = line.split(":")[0];
                            if (actualPath.equals(finalPath))
                                val = line.replace(actualPath + ": ", "");
                        }
                    }

                    result.complete(val);
                } catch (Throwable ex) {
                    result.complete(val, ex);
                } finally {
                    closeStreams(reader);
                }
            } else {
                result.complete(val);
            }
        });

        return result;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value
     * @return the value
     */
    @NotNull
    public LateScheduler<String> getString(String path, final @NotNull String def) {
        LateScheduler<String> result = new AsyncLateScheduler<>();

        path = path.replaceAll("\\s", "_");

        String finalPath = path;
        source(true).async().queue("async_file_get", () -> {
            String val = def;
            if (exists()) {
                BufferedReader reader = null;
                try {
                    reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.split(":")[0] != null) {
                            String actualPath = line.split(":")[0];
                            if (actualPath.equals(finalPath))
                                val = line.replace(actualPath + ": ", "");
                        }
                    }

                    result.complete(val);
                } catch (Throwable ex) {
                    result.complete(val, ex);
                } finally {
                    closeStreams(reader);
                }
            } else {
                result.complete(val);
            }
        });

        return result;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param default_contents the default values
     * @return the value
     */
    @NotNull
    public LateScheduler<List<?>> getList(String path, final Object... default_contents) {
        LateScheduler<List<?>> result = new AsyncLateScheduler<>();

        path = path.replaceAll("\\s", "_");

        String finalPath = path;
        source(true).async().queue("async_file_get", () -> {
            List<Object> values = new ArrayList<>();
            isSet(finalPath).whenComplete((rs) -> {
               if (rs && exists()) {
                   BufferedReader reader = null;
                   try {
                       reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                       boolean adding = false;
                       Object line;
                       while ((line = reader.readLine()) != null) {
                           if (isOpenList(line.toString(), finalPath))
                               adding = true;
                           if (isCloseList(line.toString(), finalPath))
                               adding = false;
                           if (adding &&
                                   !isOpenList(line.toString(), finalPath))
                               if (!line.toString().startsWith("/// ") && !line.toString().endsWith(" -->"))
                                   values.add(line);
                       }

                       result.complete(values);
                   } catch (Throwable ex) {
                       result.complete(values, ex);
                   } finally {
                       closeStreams(reader);
                   }
               } else {
                   values.addAll(Arrays.asList(default_contents));
                   result.complete(values);
               }
            });
        });

        return result;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param default_contents the default values
     * @return the value
     */
    @NotNull
    public LateScheduler<List<String>> getStringList(String path, final String... default_contents) {
        LateScheduler<List<String>> result = new AsyncLateScheduler<>();
        Object[] default_objects = Arrays.copyOf((Object[]) default_contents, default_contents.length);

        path = path.replaceAll("\\s", "_");

        getList(path, default_objects).whenComplete((unknownList, error) -> {
            List<String> values = new ArrayList<>();

            if (!unknownList.isEmpty()) {
                for (Object value : unknownList) {
                    String str = value.toString();
                    if (!str.startsWith("/// ") && !str.endsWith(" -->"))
                        values.add(str);
                }
            }

            result.complete(values);
        });

        return result;
    }

    /**
     * Read the file completely
     *
     * @return the file lines
     */
    @NotNull
    public LateScheduler<List<String>> readFullFile() {
        LateScheduler<List<String>> result = new AsyncLateScheduler<>();

        source(true).async().queue("async_file_read", () -> {
            try {
                result.complete(Files.readAllLines(this.file.toPath()));
            } catch (Throwable ex) {
                result.complete(Collections.emptyList(), ex);
            }
        });

        return result;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value
     * @return the value
     */
    public LateScheduler<Boolean> getBoolean(String path, final boolean def) {
        LateScheduler<Boolean> result = new AsyncLateScheduler<>();

        path = path.replaceAll("\\s", "_");

        String finalPath = path;
        source(true).async().queue("async_file_get", () -> {
            boolean val = def;

            if (exists()) {
                BufferedReader reader = null;
                try {
                    reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.split(":")[0] != null) {
                            String actualPath = line.split(":")[0];
                            if (actualPath.equals(finalPath))
                                val = Boolean.parseBoolean(line.replace(actualPath + ": ", ""));
                        }
                    }

                    result.complete(val);
                } catch (Throwable ex) {
                    result.complete(val, ex);
                } finally {
                    closeStreams(reader);
                }
            } else {
                result.complete(val);
            }
        });

        return result;
    }

    /**
     * Get if a key is a list
     *
     * @param path the key
     * @return if the path is a list
     */
    public LateScheduler<Boolean> isList(String path) {
        LateScheduler<Boolean> result = new AsyncLateScheduler<>();

        path = path.replaceAll("\\s", "_");

        String finalPath = path;
        source(true).async().queue("async_file_read", () -> isSet(finalPath).whenComplete((rs) -> {
            boolean exist = false;

            if (rs && exists()) {
                BufferedReader reader = null;
                try {
                    reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                    boolean adding = false;
                    Object line;
                    while ((line = reader.readLine()) != null) {
                        if (isOpenList(line.toString(), finalPath))
                            exist = true;
                    }

                    result.complete(exist);
                } catch (Throwable ex) {
                    result.complete(exist, null);
                } finally {
                    closeStreams(reader);
                }
            } else {
                result.complete(false);
            }
        }));

        return result;
    }

    /**
     * Get if a key is set
     *
     * @param path the key
     * @return if the path is set
     */
    public LateScheduler<Boolean> isSet(String path) {
        LateScheduler<Boolean> result = new AsyncLateScheduler<>();

        path = path.replaceAll("\\s", "_");

        String finalPath = path;
        source(true).async().queue("async_file_read", () -> {
            boolean set = false;
            if (exists()) {
                BufferedReader reader = null;
                try {
                    reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.split(":")[0] != null) {
                            String currentPath = line.split(":")[0];
                            if (currentPath.equals(finalPath) || isOpenList(line, finalPath) || isCloseList(line, finalPath)) {
                                set = true;
                                break;
                            }
                        }
                    }

                    result.complete(set);
                } catch (Throwable ex) {
                    result.complete(set, ex);
                } finally {
                    closeStreams(reader);
                }
            } else {
                result.complete(false);
            }
        });

        return result;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value
     * @return the value
     */
    public LateScheduler<Integer> getInt(String path, final int def) {
        LateScheduler<Integer> result = new AsyncLateScheduler<>();

        path = path.replaceAll("\\s", "_");

        String finalPath = path;
        source(true).async().queue("async_file_get", () -> {
            int val = def;
            if (exists()) {
                BufferedReader reader = null;
                try {
                    reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.split(":")[0] != null) {
                            String actualPath = line.split(":")[0];
                            if (actualPath.equals(finalPath))
                                val = Integer.parseInt(line.replace(actualPath + ": ", ""));
                        }
                    }

                    result.complete(val);
                } catch (Throwable ex) {
                    result.complete(val, ex);
                } finally {
                    closeStreams(reader);
                }
            } else {
                result.complete(val);
            }
        });

        return result;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value
     * @return the value
     */
    public LateScheduler<Double> getDouble(String path, final double def) {
        LateScheduler<Double> result = new AsyncLateScheduler<>();

        path = path.replaceAll("\\s", "_");

        String finalPath = path;
        source(true).async().queue("async_file_get", () -> {
            double val = def;

            if (exists()) {
                BufferedReader reader = null;
                try {
                    reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.split(":")[0] != null) {
                            String actualPath = line.split(":")[0];
                            if (actualPath.equals(finalPath))
                                val = Double.parseDouble(line.replace(actualPath + ": ", ""));
                        }
                    }

                    result.complete(val);
                } catch (Throwable ex) {
                    result.complete(val, ex);
                } finally {
                    closeStreams(reader);
                }
            } else {
                result.complete(val);
            }
        });

        return result;
    }

    /**
     * Get a value
     *
     * @param path the value path
     * @param def the default value
     * @return the value
     */
    public LateScheduler<Long> getLong(String path, final long def) {
        LateScheduler<Long> result = new AsyncLateScheduler<>();

        path = path.replaceAll("\\s", "_");

        String finalPath = path;
        source(true).async().queue("async_file_get", () -> {
            long val = def;
            if (exists()) {
                BufferedReader reader = null;
                try {
                    reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.split(":")[0] != null) {
                            String actualPath = line.split(":")[0];
                            if (actualPath.equals(finalPath))
                                val = Long.parseLong(line.replace(actualPath + ": ", ""));
                        }
                    }

                    result.complete(val);
                } catch (Throwable ex) {
                    result.complete(val, ex);
                } finally {
                    closeStreams(reader);
                }
            } else {
                result.complete(val);
            }
        });

        return result;
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
    public LateScheduler<Set<Key>> getKeys(final boolean deep) {
        LateScheduler<Set<Key>> result = new AsyncLateScheduler<>();

        Set<Key> keys = new LinkedHashSet<>();
        source(true).async().queue("asnyc_file_read", () -> {
            if (exists()) {
                BufferedReader reader = null;
                try {
                    reader = Files.newBufferedReader(this.file.toPath(), StandardCharsets.UTF_8);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.replaceAll("\\s", "").isEmpty() &&
                                !isComment(line)) {
                            AtomicReference<Key> key = new AtomicReference<>();
                            String pathKey = getKeyPath(line);
                            isList(pathKey).whenComplete((rs) -> {
                                if (rs) {
                                    getList(pathKey).whenComplete((unknownList) -> {
                                        if (unknownList.isEmpty()) {
                                            if (deep) {
                                                key.set(new Key(pathKey, unknownList));
                                            }
                                        } else {
                                            key.set(new Key(pathKey, unknownList));
                                        }
                                    });

                                    keys.add(key.get());
                                } else {
                                    Object value = get(pathKey, "");
                                    if (StringUtils.isNullOrEmpty(value)) {
                                        if (deep) {
                                            key.set(new Key(pathKey, value));
                                        }
                                    } else {
                                        key.set(new Key(pathKey, value));
                                    }
                                }

                                keys.add(key.get());
                            });
                        }
                    }

                    result.complete(keys);
                } catch (Throwable ex) {
                    result.complete(keys, ex);
                } finally {
                    closeStreams(reader);
                }
            } else {
                result.complete(keys);
            }
        });

        return result;
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
