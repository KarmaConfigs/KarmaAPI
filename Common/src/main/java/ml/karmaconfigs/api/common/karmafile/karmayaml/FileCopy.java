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

import ml.karmaconfigs.api.common.karma.APISource;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.file.FileUtilities;
import ml.karmaconfigs.api.common.utils.reader.BoundedBufferedReader;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * Initialize the file copier
 *
 */
public final class FileCopy {

    /**
     * File key sets
     */
    private final HashMap<String, Object> keySet = new HashMap<>();
    /**
     * File key set sections
     */
    private final HashMap<String, Integer> keySection = new HashMap<>();
    /**
     * Repeated key amounts
     */
    private final HashMap<String, Integer> repeatedCount = new HashMap<>();
    /**
     * Repeated key section amount
     */
    private final HashMap<String, Integer> repeatedCountSection = new HashMap<>();

    /**
     * Internal file name
     */
    private final String inFile;

    /**
     * Main class
     */
    private final Class<?> main;

    /**
     * Enable debug
     */
    private boolean debug;

    /**
     * Initialize the file copy
     *
     * @param source the source containing the file to export
     * @param name the source file name
     */
    public FileCopy(final KarmaSource source, final String name) {
        this.inFile = name;
        this.main = source.getClass();
    }

    /**
     * Initialize the file copy
     *
     * @param main the main class
     * @param name the source file name
     */
    public FileCopy(final Class<?> main, final String name) {
        this.inFile = name;
        this.main = main;
    }

    /**
     * Set debug status
     *
     * @param status the debug status
     * @return this instance
     */
    public FileCopy withDebug(boolean status) {
        this.debug = status;
        return this;
    }

    /**
     * Copy the file
     *
     * @param destFile the file destination
     * @throws IOException if something goes wrong
     */
    public void copy(File destFile) throws IOException {
        destFile = FileUtilities.getFixedFile(destFile);
        if (this.main != null)
            if (destFile.exists()) {
                InputStream in = this.main.getResourceAsStream("/" + this.inFile);
                if (in != null) {
                    InputStreamReader inReader = new InputStreamReader(in, StandardCharsets.UTF_8);
                    BoundedBufferedReader reader = new BoundedBufferedReader(inReader, 2147483647, 10240);
                    String ext = FileUtilities.getExtension(destFile);
                    boolean yaml = (ext.equals("yml") || ext.equalsIgnoreCase("yaml"));
                    if (!yaml)
                        try {
                            Yaml yamlParser = new Yaml();
                            Map<String, Object> tmpYaml = yamlParser.load(reader);
                            yaml = (tmpYaml != null && !tmpYaml.isEmpty());
                        } catch (Throwable ignored) {
                        }
                    if (yaml) {
                        fillKeySet(destFile);
                        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile), StandardCharsets.UTF_8));
                        String last_section = "";
                        if (this.debug)
                            APISource.getConsole().send("&7Preparing writer for file generation ( {0} )", FileUtilities.getParentFile(FileUtilities.getFixedFile(destFile), '/'));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (!line.replaceAll("\\s", "").isEmpty()) {
                                if (!line.replaceAll("\\s", "").startsWith("-")) {
                                    String key = getKey(line);
                                    if (line.startsWith("#") || this.keySet.getOrDefault(key, null) == null || this.keySet.get(key) instanceof KarmaYamlManager) {
                                        if (this.debug)
                                            APISource.getConsole().send("&7Writing comment / section &e{0}", key);
                                        writer.write(line + "\n");
                                        continue;
                                    }
                                    if (isRepeated(key)) {
                                        int repeatedAmount = this.repeatedCount.getOrDefault(key, -1);
                                        if (repeatedAmount != -1)
                                            key = key + "_" + repeatedAmount;
                                        repeatedAmount++;
                                        this.repeatedCount.put(getKey(line), repeatedAmount);
                                    }
                                    if (isSectionRepeated(key)) {
                                        last_section = key;
                                        int repeatedAmount = this.repeatedCountSection.getOrDefault(key, -1);
                                        if (repeatedAmount != -1)
                                            key = key + "_" + repeatedAmount;
                                        repeatedAmount++;
                                        this.repeatedCountSection.put(getKey(line), repeatedAmount);
                                    }
                                    String path = line.split(":")[0];
                                    if (this.keySet.get(key) instanceof List) {
                                        List<?> list = (List) this.keySet.get(key);
                                        if (!list.isEmpty()) {
                                            writer.write(path + ":\n");
                                            for (Object object : list) {
                                                String space = getSpace(last_section);
                                                writer.write(space + "- '" + object.toString().replace("'", "''") + "'\n");
                                                if (this.debug)
                                                    APISource.getConsole().send("&7Writing list value &6{0}&7 of &e{1}", object, key);
                                            }
                                            continue;
                                        }
                                        writer.write(path + ": []\n");
                                        if (this.debug)
                                            APISource.getConsole().send("&7Written empty list &e{0}", key);
                                        continue;
                                    }
                                    String val = line.replace(path + ": ", "");
                                    if (this.keySet.get(key) instanceof String) {
                                        writer.write(line.replace(": " + val, "") + ": '" + this.keySet.get(key).toString().replace("'", "''").replace("\"", "") + "'\n");
                                    } else {
                                        writer.write(line.replace(": " + val, "") + ": " + this.keySet.get(key).toString().replace("'", "").replace("\"", "") + "\n");
                                    }
                                    if (this.debug)
                                        APISource.getConsole().send("&7Writing single value &6{0}&7 of &e{1}", val, key);
                                }
                                continue;
                            }
                            writer.write("\n");
                        }
                        writer.flush();
                        writer.close();
                    } else {
                        List<String> lines = Files.readAllLines(destFile.toPath());
                        StringBuilder builder = new StringBuilder();
                        for (String line : lines) {
                            if (!line.replaceAll("\\s", "").isEmpty())
                                builder.append(line);
                        }
                        if (builder.toString().replaceAll("\\s", "").isEmpty()) {
                            APISource.getConsole().send("&7Writing to {0} using in-jar file", FileUtilities.getParentFile(destFile, '/'));
                            in = this.main.getResourceAsStream("/" + this.inFile);
                            if (in != null) {
                                inReader = new InputStreamReader(in, StandardCharsets.UTF_8);
                                reader = new BoundedBufferedReader(inReader, 2147483647, 10240);
                                Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile), StandardCharsets.UTF_8));
                                String line;
                                while ((line = reader.readLine()) != null)
                                    writer.write(line + "\n");
                                writer.flush();
                                writer.close();
                            }
                        }
                    }
                    if (in != null)
                        in.close();
                    inReader.close();
                    reader.close();
                }
            } else {
                if (!destFile.getParentFile().exists() && destFile.getParentFile().mkdirs())
                    APISource.getConsole().send("&7Created directory {0}", FileUtilities.getParentFile(FileUtilities.getFixedFile(destFile.getParentFile()), '/'));
                if (destFile.createNewFile()) {
                    APISource.getConsole().send("&7Writing to {0} using in-jar file", FileUtilities.getParentFile(destFile, '/'));
                    InputStream in = this.main.getResourceAsStream("/" + this.inFile);
                    if (in != null) {
                        InputStreamReader inReader = new InputStreamReader(in, StandardCharsets.UTF_8);
                        BoundedBufferedReader reader = new BoundedBufferedReader(inReader, 2147483647, 10240);
                        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile), StandardCharsets.UTF_8));
                        String line;
                        while ((line = reader.readLine()) != null)
                            writer.write(line + "\n");
                        writer.flush();
                        writer.close();
                    }
                }
            }
    }

    /**
     * Fill key set for yaml copy
     *
     * @param destFile the dest file
     */
    private void fillKeySet(final File destFile) {
        InputStream stream = this.main.getResourceAsStream("/" + this.inFile);
        KarmaYamlManager out = new KarmaYamlManager(FileUtilities.getFixedFile(destFile));
        KarmaYamlManager in = new KarmaYamlManager(stream);
        for (String key : in.getKeySet()) {
            if (in.isSection(key)) {
                KarmaYamlManager inSection = in.getSection(key);
                KarmaYamlManager outSection = out.getSection(key, inSection);
                fillKeySet(0, inSection, outSection);
                putSection(key, 0);
                continue;
            }
            if (out.isSet(key)) {
                Object outValue = out.get(key);
                if (outValue != null) {
                    if (in.matchesWith(key, outValue.getClass())) {
                        putKey(key, outValue);
                        continue;
                    }
                }
                putKey(key, in.get(key));
                continue;
            }
            putKey(key, in.get(key));
        }
    }

    /**
     * Fill key set for yaml file
     *
     * @param tree the current tree
     * @param inSection the internal file section
     * @param outSection the external file section
     */
    private void fillKeySet(int tree, final KarmaYamlManager inSection, final KarmaYamlManager outSection) {
        for (String key : inSection.getKeySet()) {
            if (inSection.isSection(key)) {
                fillKeySet(++tree, inSection.getSection(key), outSection.getSection(key, inSection.getSection(key)));
                putSection(key, ++tree);
                continue;
            }
            if (outSection.isSet(key)) {
                Object outValue = outSection.get(key);
                if (outValue != null) {
                    if (inSection.matchesWith(key, outValue.getClass())) {
                        putKey(key, outValue);
                        continue;
                    }
                }
                putKey(key, inSection.get(key));
                continue;
            }
            putKey(key, inSection.get(key));
        }
    }

    /**
     * Put a key into the key set
     *
     * @param key the key
     * @param value the key value
     */
    private void putKey(final String key, final Object value) {
        if (!this.keySet.containsKey(key)) {
            this.keySet.put(key, value);
        } else {
            this.keySet.put(key + "_" + repeatedAmount(key), value);
        }
    }

    /**
     * Put a section into the key sections
     *
     * @param key the key
     * @param tree the key tree
     */
    private void putSection(final String key, final int tree) {
        if (this.keySection.containsKey(key)) {
            this.keySection.put(key + "_" + repeatedSection(key), tree);
        } else {
            this.keySection.put(key, tree);
        }
    }

    /**
     * Get a valid key from the text line
     *
     * @param line the line
     * @return the line key
     */
    private String getKey(String line) {
        line = line.split(":")[0];
        line = line.replaceAll("\\s", "");
        return line;
    }

    /**
     * Get the line correspondent spaces amount
     *
     * @param line the text line
     * @return the line indent
     */
    private String getSpace(String line) {
        line = line.replaceAll("[^0-9]", "").replaceAll("[0-9]", "");
        return line + "  ";
    }

    /**
     * Get the times a key has been repeated
     *
     * @param key the key
     * @return the times the key has been repeated
     */
    private int repeatedAmount(final String key) {
        int repeated = 0;
        Iterator<String> set = this.keySet.keySet().iterator();
        if (set.hasNext())
            do {
                String next = set.next();
                if (!next.contains("_"))
                    continue;
                String[] keyData = next.split("_");
                if (!keyData[0].equals(key))
                    continue;
                repeated++;
            } while (set.hasNext());
        return repeated;
    }

    /**
     * Get the times a section has been repeated
     *
     * @param key the section key
     * @return the times the section has been repeated
     */
    private int repeatedSection(final String key) {
        int repeated = 0;
        Iterator<String> set = this.keySection.keySet().iterator();
        if (set.hasNext())
            do {
                String next = set.next();
                if (!next.contains("_"))
                    continue;
                String[] keyData = next.split("_");
                if (!keyData[0].equals(key))
                    continue;
                repeated++;
            } while (set.hasNext());
        return repeated;
    }

    /**
     * Get if the key is repeated
     *
     * @param key the key
     * @return if the key is repeated
     */
    private boolean isRepeated(final String key) {
        return (repeatedAmount(key) > 0);
    }

    /**
     * Get if the section is repeated
     *
     * @param key the section key
     * @return if the section is repeated
     */
    private boolean isSectionRepeated(final String key) {
        return (repeatedSection(key) > 0);
    }
}
