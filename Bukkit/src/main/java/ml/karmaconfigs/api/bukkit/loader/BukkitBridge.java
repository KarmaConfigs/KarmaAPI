package ml.karmaconfigs.api.bukkit.loader;

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

import ml.karmaconfigs.api.bukkit.KarmaPlugin;
import ml.karmaconfigs.api.common.karma.KarmaAPI;
import ml.karmaconfigs.api.common.karma.KarmaConfig;
import ml.karmaconfigs.api.common.karmafile.karmayaml.KarmaYamlManager;
import ml.karmaconfigs.api.common.utils.BridgeLoader;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.utils.file.PathUtilities;
import ml.karmaconfigs.api.common.utils.string.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Bridge between Bukkit and KarmaAPI
 */
public class BukkitBridge extends BridgeLoader<KarmaPlugin> {

    private static KarmaPlugin instance;

    /**
     * Initialize the bridge loader
     *
     * @param source the source class
     */
    public BukkitBridge(final KarmaPlugin source) {
        super("Bukkit", source);

        instance = source;
    }

    /**
     * Start the bridge loader
     */
    @Override
    public void start() throws IOException {
        KarmaConfig config = new KarmaConfig();

        KarmaAPI.install();
        Path plugins = PathUtilities.getProjectParent();

        //In fact that's not needed, but just to be sure everything is in the same loader so
        //everyone can read from everywhere
        if (config.debug(Level.INFO)) {
            instance.console().send("Initializing Bukkit <-> KarmaAPI bridge", Level.INFO);
        }
        connect(instance.getSourceFile());
        if (config.debug(Level.INFO)) {
            instance.console().send("Bukkit <-> KarmaAPI bridge made successfully", Level.INFO);
        }

        /*
        Basically we list all the files inside
        the plugins' folder. If that plugin file
        has as dependency the KarmaAPI platform plugin
         */
        Map<String, File> load_target = new LinkedHashMap<>();
        Set<String> generated = new HashSet<>();
        Files.list(plugins).forEachOrdered((sub) -> {
            if (!Files.isDirectory(sub)) {
                //We only like jar files :)
                if (PathUtilities.getPathCompleteType(sub).equalsIgnoreCase("jar")) {
                    //This can still happen :c
                    try {
                        JarFile jar = new JarFile(sub.toFile());
                        ZipEntry plugin = jar.getEntry("plugin.yml");
                        if (plugin != null) {
                            InputStream stream = jar.getInputStream(plugin);

                            if (stream != null) {
                                KarmaYamlManager yaml = new KarmaYamlManager(stream);
                                String name = yaml.getString("name", null);
                                List<String> dependencies = yaml.getStringList("softdepend");
                                dependencies.addAll(yaml.getStringList("depend"));

                                if (dependencies.stream().anyMatch((s -> s.equalsIgnoreCase("AnotherBarelyCodedKarmaPlugin")))) {
                                    if (config.debug(Level.INFO)) {
                                        instance.console().send("Plugin {0} added to Bukkit <-> KarmaAPI bridge", Level.INFO, name);
                                    }
                                    if (!load_target.containsKey(name)) {
                                        load_target.put(name, sub.toFile());
                                    } else {
                                        String gen = StringUtils.generateString().create();
                                        generated.add(gen);
                                        load_target.put(name + "_" + gen, sub.toFile());
                                    }
                                }

                                stream.close();
                            }
                        }

                        //Make sure to close jar file so Bukkit can load it
                        jar.close();
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        for (String name : load_target.keySet()) {
            File file = load_target.get(name);
            if (name.contains("_")) {
                try {
                    String[] data = name.split("_");
                    String veryImportantData = data[data.length - 1];

                    if (generated.contains(veryImportantData)) {
                        name = StringUtils.replaceLast(name, "_" + veryImportantData, "");
                    }
                } catch (Throwable ignored) {}
            }

            if (config.debug(Level.INFO)) {
                instance.console().send("Creating bridge between Bukkit and KarmaAPI for {0}", Level.INFO, name);
            }
            connect(file);
            if (config.debug(Level.OK)) {
                instance.console().send("Bridge between Bukkit and KarmaAPI created successfully for {0}", Level.OK, name);
            }
        }
    }

    /**
     * Stop the bridge
     */
    @Override
    public void stop() {
        KarmaConfig config = new KarmaConfig();
        if (config.debug(Level.INFO)) {
            instance.console().send("Closing Bukkit <-> KarmaAPI bridge, please wait...", Level.INFO);
        }
    }

    /**
     * Get the loader instance
     *
     * @return the loader instance
     */
    public static KarmaPlugin getInstance() {
        return instance;
    }
}
