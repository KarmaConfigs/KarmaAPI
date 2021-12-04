package ml.karmaconfigs.api.bukkit.loader;

import ml.karmaconfigs.api.bukkit.KarmaPlugin;
import ml.karmaconfigs.api.common.karmafile.karmayaml.KarmaYamlManager;
import ml.karmaconfigs.api.common.utils.BridgeLoader;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.utils.file.PathUtilities;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class BukkitBridge extends BridgeLoader<KarmaPlugin> {

    private static KarmaPlugin instance;
    private final static Set<Plugin> loaded = Collections.newSetFromMap(new ConcurrentHashMap<>());

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
        Path plugins = PathUtilities.getProjectParent();

        /*
        Basically we list all the files inside
        the plugins' folder. If apparently, there's
        a jar file that contains 'KARMA.MF' file with
        a valid karma plugin location, this will be
        sent to Bukkit and loaded by Bukkit using
        KarmaAPI. The plugin won't have to include
        KarmaAPI inside him, and he will be able to use
        KarmaAPI anyway
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
                                List<String> softDepend = yaml.getStringList("softdepend");

                                //Plugin name is AnotherBarelyCodedKarmaPlugin, but to make it easier for
                                //developers, they will have to put "KarmaAPI" in softdepend
                                if (softDepend.stream().anyMatch((s -> s.equalsIgnoreCase("KarmaAPI")))) {
                                    instance.console().send("Plugin {0} added to Bukkit <-> KarmaAPI bridge", Level.INFO, name);
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

            instance.console().send("Creating bridge between Bukkit and KarmaAPI for {0}", Level.INFO, name);
            connect(file);

            try {
                Plugin plugin = Bukkit.getPluginManager().loadPlugin(file);

                if (plugin != null) {
                    loaded.add(plugin);
                    instance.console().send("Bridge between Bukkit and KarmaAPI created successfully for {0}", Level.OK, name);
                } else {
                    instance.console().send("There was a problem while trying to create a bridge between Bukkit and KarmaAPI for {0}", Level.GRAVE, name);
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Stop the bridge
     */
    @Override
    public void stop() {
        instance.console().send("Closing Bukkit <-> KarmaAPI bridge, please wait...", Level.INFO);

        for (Plugin plugin : loaded) {
            String name = plugin.getName();

            BukkitManager.unload(plugin);
            instance.console().send("Unloaded bridged plugin {0}", Level.OK, name);
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
