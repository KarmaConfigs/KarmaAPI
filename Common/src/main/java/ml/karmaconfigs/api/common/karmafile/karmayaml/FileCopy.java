package ml.karmaconfigs.api.common.karmafile.karmayaml;

import ml.karmaconfigs.api.common.Console;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.FileUtilities;
import ml.karmaconfigs.api.common.utils.reader.BoundedBufferedReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
 * Copy files from an internal resource into an external file,
 * with this method, when exporting a yaml file, it will check if
 * it already exists, if it does, it will only copy the comments and
 * check values
 */
public final class FileCopy {

    private final HashMap<String, Object> keySet = new HashMap<>();

    private final HashMap<String, Integer> keySection = new HashMap<>();
    private final HashMap<String, Integer> repeatedCount = new HashMap<>();
    private final HashMap<String, Integer> repeatedCountSection = new HashMap<>();

    private boolean debug;

    private final String inFile;
    private final Class<?> main;

    /**
     * Initialize the file copy class
     *
     * @param source the source owner
     * @param name the file name
     * @throws IllegalArgumentException if the specified plugin container is not valid
     */
    public FileCopy(final KarmaSource source, final String name) throws IllegalArgumentException {
        this.inFile = name;
        main = source.getClass();
    }

    /**
     * Initialize the file copy class
     *
     * @param main a java plugin instance
     * @param name the file name
     */
    public FileCopy(final Class<?> main, final String name) {
        this.inFile = name;
        this.main = main;
    }

    /**
     * Set the file copy debug status
     *
     * @param status the debug status
     * @return this file copy instance
     */
    public final FileCopy withDebug(final boolean status) {
        debug = status;
        return this;
    }

    /**
     * Copy the file to the destination file
     *
     * @param destFile the destination file
     * @throws Throwable as value if the file could be
     * copied
     */
    public final void copy(File destFile) throws Throwable {
        destFile = FileUtilities.getFixedFile(destFile);

        if (main != null) {
            if (destFile.exists()) {
                InputStream in;
                InputStreamReader inReader;
                BoundedBufferedReader reader;
                in = this.main.getResourceAsStream("/" + this.inFile);
                if (in != null) {
                    inReader = new InputStreamReader(in, StandardCharsets.UTF_8);
                    reader = new BoundedBufferedReader(inReader, Integer.MAX_VALUE, 10240);

                    String ext = FileUtilities.getExtension(destFile);
                    boolean yaml = ext.equals("yml");

                    if (yaml) {
                        fillKeySet(destFile);
                        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile), StandardCharsets.UTF_8));
                        String line;
                        String last_section = "";

                        if (debug)
                            Console.send("&7Preparing writer for file generation ( {0} )", FileUtilities.getPath(FileUtilities.getFixedFile(destFile), '/'));

                        while ((line = reader.readLine()) != null) {
                            if (!line.replaceAll("\\s", "").isEmpty()) {
                                if (!line.replaceAll("\\s", "").startsWith("-")) {
                                    String key = getKey(line);
                                    if (line.startsWith("#") || keySet.getOrDefault(key, null) == null || keySet.get(key) instanceof KarmaYamlManager) {
                                        if (debug)
                                            Console.send("&7Writing comment / section &e{0}", key);

                                        writer.write(line + "\n");
                                    } else {
                                        if (isRepeated(key)) {
                                            int repeatedAmount = repeatedCount.getOrDefault(key, -1);
                                            if (repeatedAmount != -1) {
                                                key = key + "_" + repeatedAmount;
                                            }
                                            repeatedAmount++;
                                            repeatedCount.put(getKey(line), repeatedAmount);
                                        }
                                        if (isSectionRepeated(key)) {
                                            last_section = key;
                                            int repeatedAmount = repeatedCountSection.getOrDefault(key, -1);
                                            if (repeatedAmount != -1) {
                                                key = key + "_" + repeatedAmount;
                                            }
                                            repeatedAmount++;
                                            repeatedCountSection.put(getKey(line), repeatedAmount);
                                        }

                                        String path = line.split(":")[0];
                                        if (keySet.get(key) instanceof List) {
                                            List<?> list = (List<?>) keySet.get(key);

                                            if (!list.isEmpty()) {
                                                writer.write(path + ":\n");
                                                for (Object object : list) {
                                                    String space = getSpace(last_section);
                                                    writer.write(space + "- '" + object.toString().replace("'", "''") + "'\n");

                                                    if (debug)
                                                        Console.send("&7Writing list value &6{0}&7 of &e{1}", object, key);
                                                }
                                            } else {
                                                writer.write(path + ": []\n");

                                                if (debug)
                                                    Console.send("&7Written empty list &e{0}", key);
                                            }
                                        } else {
                                            String val = line.replace(path + ": ", "");
                                            if (keySet.get(key) instanceof String) {
                                                writer.write(line.replace(": " + val, "") + ": '" + keySet.get(key).toString().replace("'", "''").replace("\"", "") + "'\n");
                                            } else {
                                                writer.write(line.replace(": " + val, "") + ": " + keySet.get(key).toString().replace("'", "").replace("\"", "") + "\n");
                                            }

                                            if (debug)
                                                Console.send("&7Writing single value &6{0}&7 of &e{1}", val, key);
                                        }
                                    }
                                }
                            } else {
                                writer.write("\n");
                            }
                        }

                        writer.flush();
                        writer.close();
                    } else {
                        //List file lines
                        List<String> lines = Files.readAllLines(destFile.toPath());
                        StringBuilder builder = new StringBuilder();
                        for (String line : lines) {
                            if (!line.replaceAll("\\s", "").isEmpty()) {
                                //Append the line only if its not empty
                                builder.append(line);
                            }
                        }

                        //Check if the builder is empty, if it is, the file will be saved from in-jar
                        if (builder.toString().replaceAll("\\s", "").isEmpty()) {
                            Console.send("&7Writing to {0} using in-jar file", FileUtilities.getPath(destFile, '/'));
                            in = this.main.getResourceAsStream("/" + this.inFile);

                            if (in != null) {
                                inReader = new InputStreamReader(in, StandardCharsets.UTF_8);
                                reader = new BoundedBufferedReader(inReader, Integer.MAX_VALUE, 10240);

                                Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile), StandardCharsets.UTF_8));
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    writer.write(line + "\n");
                                }
                                writer.flush();
                                writer.close();
                            }
                        }
                    }

                    //Finally, close the streams
                    if (in != null)
                        in.close();

                    inReader.close();
                    reader.close();
                }
            } else {
                if (!destFile.getParentFile().exists() && destFile.getParentFile().mkdirs())
                    Console.send("&7Created directory {0}", FileUtilities.getPath(FileUtilities.getFixedFile(destFile.getParentFile()), '/'));

                if (destFile.createNewFile()) {
                    Console.send("&7Writing to {0} using in-jar file", FileUtilities.getPath(destFile, '/'));
                    InputStream in = this.main.getResourceAsStream("/" + this.inFile);

                    if (in != null) {
                        InputStreamReader inReader = new InputStreamReader(in, StandardCharsets.UTF_8);
                        BoundedBufferedReader reader = new BoundedBufferedReader(inReader, Integer.MAX_VALUE, 10240);

                        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile), StandardCharsets.UTF_8));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            writer.write(line + "\n");
                        }
                        writer.flush();
                        writer.close();
                    }
                }
            }
        }
    }

    /**
     * Fill keySet
     *
     * @param destFile the out file
     */
    private void fillKeySet(final File destFile) {
        InputStream stream = main.getResourceAsStream("/" + inFile);

        KarmaYamlManager out = new KarmaYamlManager(FileUtilities.getFixedFile(destFile));
        KarmaYamlManager in = new KarmaYamlManager(stream);

        for (String key : in.getKeySet()) {
            if (in.isSection(key)) {
                KarmaYamlManager inSection = in.getSection(key);
                KarmaYamlManager outSection = out.getSection(key, inSection);

                fillKeySet(0, key, inSection, outSection);
                putSection(key, 0);
            } else {
                if (out.isSet(key)) {
                    Object outValue = out.get(key);

                    if (in.matchesWith(key, outValue)) {
                        putKey(key, outValue);
                    } else {
                        putKey(key, in.get(key));
                    }
                } else {
                    putKey(key, in.get(key));
                }
            }
        }
    }

    /**
     * Get the key set from the configuration
     * section
     *
     * @param tree     the section tree int
     * @param main     the key path
     * @param inSection  the configuration section
     * @param outSection  the out configuration section
     */
    private void fillKeySet(int tree, String main, KarmaYamlManager inSection, KarmaYamlManager outSection) {
        for (String key : inSection.getKeySet()) {
            if (inSection.isSection(key)) {
                fillKeySet(++tree, key, inSection.getSection(key), outSection.getSection(key, inSection.getSection(key)));
                putSection(key, ++tree);
            } else {
                if (outSection.isSet(key)) {
                    Object outValue = outSection.get(key);

                    if (inSection.matchesWith(key, outValue)) {
                        putKey(key, outValue);
                    } else {
                        putKey(key, inSection.get(key));
                    }
                } else {
                    putKey(key, inSection.get(key));
                }
            }
        }
    }

    /**
     * Put a key
     *
     * @param key the key
     * @param value the value
     */
    private void putKey(final String key, final Object value) {
        if (!keySet.containsKey(key)) {
            keySet.put(key, value);
        } else {
            keySet.put(key + "_" + repeatedAmount(key), value);
        }
    }

    /**
     * Put a key
     *
     * @param key the key
     * @param tree the section tree
     */
    private void putSection(final String key, final int tree) {
        if (keySection.containsKey(key)) {
            keySection.put(key + "_" + repeatedSection(key), tree);
        } else {
            keySection.put(key, tree);
        }
    }

    /**
     * Get the key from line
     *
     * @param line the line
     * @return a String
     */
    private String getKey(String line) {
        line = line.split(":")[0];
        line = line.replaceAll("\\s", "");

        return line;
    }

    /**
     * Get a space line from
     * the original line ready
     * to be used in arraylists
     *
     * @param line the line
     * @return a String
     */
    private String getSpace(String line) {
        line = line.replaceAll("[^0-9]", "").replaceAll("[0-9]", "");
        return line + "  ";
    }

    /**
     * Remove duped contents from the list
     *
     * @param list the original list
     * @return a new ArrayList
     */
    private List<?> removeDupes(List<?> list) {
        List<Object> fixed = new ArrayList<>();

        for (Object item : list) {
            if (!fixed.contains(item)) {
                fixed.add(item);
            }
        }

        return fixed;
    }

    /**
     * Get the amount a key is repeated in a keyset
     *
     * @param key the key
     * @return an integer
     */
    private int repeatedAmount(String key) {
        int repeated = 0;

        Iterator<String> set = keySet.keySet().iterator();
        if (set.hasNext()) {
            do {
                String next = set.next();
                if (next.contains("_")) {
                    String[] keyData = next.split("_");
                    if (keyData[0].equals(key)) {
                        repeated++;
                    }
                }
            } while (set.hasNext());
        }

        return repeated;
    }

    /**
     * Get the amount a key is repeated in a keyset
     *
     * @param key the key
     * @return an integer
     */
    private int repeatedSection(String key) {
        int repeated = 0;

        Iterator<String> set = keySection.keySet().iterator();
        if (set.hasNext()) {
            do {
                String next = set.next();
                if (next.contains("_")) {
                    String[] keyData = next.split("_");
                    if (keyData[0].equals(key)) {
                        repeated++;
                    }
                }
            } while (set.hasNext());
        }

        return repeated;
    }

    /**
     * Check if the key is repeated
     *
     * @param key the key
     * @return a boolean
     */
    private boolean isRepeated(String key) {
        return repeatedAmount(key) > 0;
    }

    /**
     * Check if the key is repeated
     *
     * @param key the key
     * @return a boolean
     */
    private boolean isSectionRepeated(String key) {
        return repeatedSection(key) > 0;
    }
}