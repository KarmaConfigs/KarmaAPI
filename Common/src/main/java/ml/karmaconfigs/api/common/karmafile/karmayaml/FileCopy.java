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
import ml.karmaconfigs.api.common.karma.KarmaConfig;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.utils.file.FileUtilities;
import ml.karmaconfigs.api.common.utils.reader.BoundedBufferedReader;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import static ml.karmaconfigs.api.common.karma.KarmaAPI.source;

/**
 * Initialize the file copier
 *
 */
public final class FileCopy {

    /**
     * File key sets
     */
    private final Map<String, Object> keySet = new HashMap<>();
    /**
     * File key set sections
     */
    private final Map<String, Integer> keySection = new HashMap<>();
    /**
     * Repeated key amounts
     */
    private final Map<String, Integer> repeatedCount = new HashMap<>();
    /**
     * Repeated key section amount
     */
    private final Map<String, Integer> repeatedCountSection = new HashMap<>();

    /**
     * Internal file name
     */
    private final String fileName;

    /**
     * Main class
     */
    private final Class<?> main;

    /**
     * Initialize the file copy
     *
     * @param source the source containing the file to export
     * @param name the source file name
     */
    public FileCopy(final KarmaSource source, final String name) {
        fileName = name;
        this.main = source.getClass();
    }

    /**
     * Initialize the file copy
     *
     * @param main the main class
     * @param name the source file name
     */
    public FileCopy(final Class<?> main, final String name) {
        fileName = name;
        this.main = main;
    }

    /**
     * Copy the file
     *
     * @param destFile the file destination
     * @throws IOException if something goes wrong
     */
    public void copy(File destFile) throws IOException {
        KarmaConfig config = new KarmaConfig();

        destFile = FileUtilities.getFixedFile(destFile);
        if (this.main != null) {
            if (destFile.exists()) {
                File source = new File(main.getProtectionDomain().getCodeSource().getLocation().getFile().replaceAll("%20", " "));

                JarFile jar = new JarFile(source);
                ZipEntry entry = jar.getEntry(fileName);
                InputStream inFile = jar.getInputStream(entry);

                if (inFile != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inFile.read(buffer)) > -1) {
                        baos.write(buffer, 0, len);
                    }
                    baos.flush();

                    InputStream clone = new ByteArrayInputStream(baos.toByteArray());

                    InputStreamReader inReader = new InputStreamReader(clone, StandardCharsets.UTF_8);
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
                        InputStream clone1 = new ByteArrayInputStream(baos.toByteArray());

                        fillKeySet(destFile, clone1);
                        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile), StandardCharsets.UTF_8));
                        String last_section = "";
                        if (config.fileDebug(Level.INFO))
                            source(true).console().send("Preparing writer for file generation ( {0} )", Level.INFO, FileUtilities.getPrettyFile(destFile));

                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (!line.replaceAll("\\s", "").isEmpty()) {
                                if (!line.replaceAll("\\s", "").startsWith("-")) {
                                    String key = getKey(line);

                                    if (line.startsWith("#") || this.keySet.getOrDefault(key, null) == null || this.keySet.get(key) instanceof KarmaYamlManager) {
                                        if (config.fileDebug(Level.INFO))
                                            source(true).console().send("Writing comment / section &e{0}", Level.INFO, key);

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
                                        List<?> list = (List<?>) this.keySet.get(key);
                                        if (!list.isEmpty()) {
                                            writer.write(path + ":\n");
                                            for (Object object : list) {
                                                String space = getSpace(last_section);
                                                writer.write(space + "- '" + object.toString().replace("'", "''") + "'\n");
                                                if (config.fileDebug(Level.INFO))
                                                    source(true).console().send("Writing list value {0} of {1}", Level.INFO, object, key);
                                            }
                                            continue;
                                        }
                                        writer.write(path + ": []\n");
                                        if (config.fileDebug(Level.INFO))
                                            source(true).console().send("Written empty list {0}", Level.INFO, key);
                                        continue;
                                    }
                                    String val = line.replace(path + ": ", "");
                                    if (this.keySet.get(key) instanceof String) {
                                        writer.write(line.replace(": " + val, "") + ": '" + this.keySet.get(key).toString().replace("'", "''").replace("\"", "") + "'\n");
                                    } else {
                                        writer.write(line.replace(": " + val, "") + ": " + this.keySet.get(key).toString().replace("'", "").replace("\"", "") + "\n");
                                    }
                                    if (config.fileDebug(Level.INFO))
                                        source(true).console().send("Writing single value {0} of {1}", Level.INFO, val, key);
                                }
                                continue;
                            }
                            writer.write("\n");
                        }

                        writer.flush();
                        writer.close();
                        clone1.close();

                        reader.close();
                        inReader.close();
                        clone.close();
                    } else {
                        List<String> lines = Files.readAllLines(destFile.toPath());
                        StringBuilder builder = new StringBuilder();
                        for (String line : lines) {
                            if (!line.replaceAll("\\s", "").isEmpty())
                                builder.append(line);
                        }

                        if (builder.toString().replaceAll("\\s", "").isEmpty()) {
                            source(true).console().send("Writing to {0} using in-jar file", Level.INFO, FileUtilities.getPrettyFile(destFile));
                            InputStream clone2 = new ByteArrayInputStream(baos.toByteArray());

                            inReader = new InputStreamReader(clone2, StandardCharsets.UTF_8);
                            reader = new BoundedBufferedReader(inReader, 2147483647, 10240);
                            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile), StandardCharsets.UTF_8));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (config.fileDebug(Level.INFO)) {
                                    source(true).console().send("Writing raw text {0} to {1}", Level.INFO, line, FileUtilities.getPrettyFile(destFile));
                                }
                                writer.write(line + "\n");
                            }
                            writer.flush();
                            writer.close();

                            clone2.close();
                        }
                    }

                    baos.close();
                    inReader.close();
                    reader.close();
                    jar.close();
                }

                if (inFile != null) {
                    inFile.close();
                }
            } else {
                if (!destFile.getParentFile().exists() && destFile.getParentFile().mkdirs())
                    source(true).console().send("Created directory {0}", Level.INFO, FileUtilities.getPrettyParentFile(destFile));

                if (destFile.createNewFile()) {
                    source(true).console().send("Writing to {0} using in-jar file", Level.INFO, FileUtilities.getPrettyFile(destFile));
                    File source = new File(main.getProtectionDomain().getCodeSource().getLocation().getFile());
                    JarFile jar = new JarFile(source);
                    ZipEntry entry = jar.getEntry(fileName);
                    InputStream inFile = jar.getInputStream(entry);

                    if (inFile != null) {
                        InputStreamReader inReader = new InputStreamReader(inFile, StandardCharsets.UTF_8);
                        BoundedBufferedReader reader = new BoundedBufferedReader(inReader, 2147483647, 10240);
                        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile), StandardCharsets.UTF_8));
                        String line;
                        while ((line = reader.readLine()) != null)
                            writer.write(line + "\n");

                        writer.flush();
                        writer.close();

                        reader.close();
                        inReader.close();
                        inFile.close();
                    }

                    jar.close();
                }
            }
        }
    }

    /**
     * Copy the file
     *
     * @param destFile the file destination
     * @throws IOException if something goes wrong
     */
    public void copy(final Path destFile) throws IOException {
        copy(destFile.toFile());
    }

    /**
     * Fix the sections for files who
     * have section names same as keys
     *
     * @param destFile the file to fix
     */
    @SuppressWarnings("unused")
    public void fix(File destFile) {
        try {
            List<String> writeTarget = new ArrayList<>();
            destFile = FileUtilities.getFixedFile(destFile);

            List<String> lines = FileUtilities.readAllLines(destFile);
            boolean written;
            for (int i = 0; i < lines.size(); i++) {
                written = false;
                String current = lines.get(i);

                if (!current.startsWith("#")) {
                    if (i != lines.size() - 1) {
                        String next = lines.get(i + 1);

                        if (!next.startsWith("#")) {
                            int currentSpaces = countSpaces(current);
                            int nextSpaces = countSpaces(next);

                            if (currentSpaces < nextSpaces) {
                                if (current.contains(":")) {
                                    String[] keyData = current.split(":");
                                    String key = keyData[0];
                                    String value = current.replaceFirst(key + ": ", "").replaceAll("\\s", "");

                                    if (!value.isEmpty()) {
                                        writeTarget.add(key + ": ");
                                        written = true;
                                    }
                                }
                            }
                        }
                    }
                }

                if (!written) {
                    writeTarget.add(current);
                }
            }

            BufferedWriter writer = Files.newBufferedWriter(destFile.toPath(), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            for (String line : writeTarget)
                writer.write(line + "\n");

            writer.flush();
            writer.close();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Fill key set for yaml copy
     *
     * @param destFile the dest file
     */
    private void fillKeySet(final File destFile, final InputStream inFile) {
        KarmaYamlManager out = new KarmaYamlManager(FileUtilities.getFixedFile(destFile));
        KarmaYamlManager in = new KarmaYamlManager(inFile);
        for (String key : in.getKeySet()) {
            if (in.isSection(key)) {
                KarmaYamlManager inSection = in.getSection(key);
                KarmaYamlManager outSection = out.getSection(key, inSection);
                fillKeySet(0, inSection, outSection);
                putSection(key, 0);
                //continue;
            } else {
                putSection(key, 1);
                if (out.isSet(key)) {
                    Object outValue = out.get(key);
                    if (outValue != null) {
                        if (in.matchesWith(key, outValue.getClass())) {
                            putKey(key, outValue);
                            continue;
                        }
                    }
                }

                putKey(key, in.get(key));
            }
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
                //continue;
            } else {
                int indent = 0;
                KarmaYamlManager parent = inSection.getParent();
                while (parent != null) {
                    indent++;
                    parent = parent.getParent();
                }

                putSection(key, indent);
                if (outSection.isSet(key)) {
                    Object outValue = outSection.get(key);
                    if (outValue != null) {
                        if (inSection.matchesWith(key, outValue.getClass())) {
                            putKey(key, outValue);
                            continue;
                        }
                    }
                    //continue;
                }

                putKey(key, inSection.get(key));
            }
            //putKey(key, inSection.get(key));
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
        KarmaConfig config = new KarmaConfig();

        if (config.fileDebug(Level.INFO)) {
            source(true).console().send("Added key {0} to section number {1}", Level.INFO, key, tree);
        }

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
        KarmaConfig config = new KarmaConfig();

        if (config.fileDebug(Level.INFO)) {
            source(true).console().send("Getting spaces amount for {0}", Level.INFO, line);
        }

        int spaces = keySection.getOrDefault(line, 0);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < spaces; i++)
            builder.append("  ");

        if (config.fileDebug(Level.INFO)) {
            source(true).console().send("Spaces for {0}: {1} ({2})", Level.INFO, line, spaces, builder);
        }

        return builder.append("  ").toString();
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
     * Count the amount of spaces
     * before reaching the first
     * letter
     *
     * @param line the line
     * @return the amount of spaces
     */
    private int countSpaces(final String line) {
        int spaces = 0;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);

            if (!Character.isLetterOrDigit(character)) {
                if (Character.isSpaceChar(character))
                    spaces++;
            } else {
                break;
            }
        }

        return spaces;
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
