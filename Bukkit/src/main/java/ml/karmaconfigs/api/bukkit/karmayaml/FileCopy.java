package ml.karmaconfigs.api.bukkit.karmayaml;

import ml.karmaconfigs.api.common.*;
import ml.karmaconfigs.api.common.Console;
import ml.karmaconfigs.api.common.utils.FileUtilities;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
@SuppressWarnings("unused")
public final class FileCopy {

    private final HashMap<String, Object> keySet = new HashMap<>();

    private final HashMap<String, Integer> keySection = new HashMap<>();
    private final HashMap<String, Integer> repeatedCount = new HashMap<>();
    private final HashMap<String, Integer> repeatedCountSection = new HashMap<>();

    private boolean debug = false;

    private final String inFile;
    private final Class<?> main;

    /**
     * Initialize the file copy class
     *
     * @param main a java plugin instance
     * @param name the file name
     */
    public FileCopy(final JavaPlugin main, final String name) {
        this.inFile = name;
        this.main = main.getClass();
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
            FileUtilities.create(destFile);

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
                                    if (line.startsWith("#") || keySet.getOrDefault(key, null) == null || keySet.get(key) instanceof ConfigurationSection) {
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
    private void fillKeySet(File destFile) {
        if (main != null) {
            InputStream inFile = main.getResourceAsStream("/" + this.inFile);

            if (inFile != null) {
                YamlConfiguration out = YamlConfiguration.loadConfiguration(destFile);
                YamlConfiguration original = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(inFile)));

                for (String key : original.getKeys(false)) {
                    if (original.get(key) instanceof ConfigurationSection && original.getConfigurationSection(key) != null) {
                        fillKeySet(1, key, out.getConfigurationSection(key), out, original);
                        if (keySection.containsKey(key)) {
                            keySection.put(key + "_" + repeatedSection(key), 0);
                        } else {
                            keySection.put(key, 0);
                        }
                    } else {
                        if (out.isSet(key)) {
                            if (Objects.requireNonNull(original.get(key)).getClass().equals(Objects.requireNonNull(out.get(key)).getClass())) {
                                if (!keySet.containsKey(key)) {
                                    keySet.put(key, out.get(key));
                                } else {
                                    keySet.put(key + "_" + repeatedAmount(key), out.get(key));
                                }
                            } else {
                                if (!keySet.containsKey(key)) {
                                    keySet.put(key, original.get(key));
                                } else {
                                    keySet.put(key + "_" + repeatedAmount(key), original.get(key));
                                }
                            }
                        } else {
                            if (!keySet.containsKey(key)) {
                                keySet.put(key, original.get(key));
                            } else {
                                keySet.put(key + "_" + repeatedAmount(key), original.get(key));
                            }
                        }
                    }
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
     * @param section  the configuration section
     * @param out      the yaml file
     * @param original the original yaml file
     */
    private void fillKeySet(final int tree, String main, ConfigurationSection section, YamlConfiguration out, YamlConfiguration original) {
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String path = main + "." + key;
                if (original.isSet(path)) {
                    if (original.get(path) instanceof ConfigurationSection && original.get(path) != null) {
                        fillKeySet(tree + 1, path, out.getConfigurationSection(path), out, original);
                        if (keySection.containsKey(key)) {
                            keySection.put(key + "_" + repeatedSection(key), tree);
                        } else {
                            keySection.put(key, tree);
                        }
                    } else {
                        if (out.isSet(path)) {
                            if (Objects.requireNonNull(original.get(path)).getClass().equals(Objects.requireNonNull(out.get(path)).getClass())) {
                                if (!keySet.containsKey(key)) {
                                    keySet.put(key, out.get(path));
                                } else {
                                    keySet.put(key + "_" + repeatedAmount(key), out.get(path));
                                }
                            } else {
                                if (!keySet.containsKey(key)) {
                                    keySet.put(key, original.get(key));
                                } else {
                                    keySet.put(key + "_" + repeatedAmount(key), original.get(path));
                                }
                            }
                        } else {
                            if (!keySet.containsKey(key)) {
                                keySet.put(key, original.get(path));
                            } else {
                                keySet.put(key + "_" + repeatedAmount(key), original.get(path));
                            }
                        }
                    }
                }
            }
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

    private String getSpace(String key) {
        int amount = repeatedSection(key);
        StringBuilder builder = new StringBuilder();
        if (amount == 0) {
            builder.append("  ");
        } else {
            for (int i = 0; i < amount; i++)
                builder.append("  ");
        }

        return builder.toString();
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