package ml.karmaconfigs.api.velocity;

import com.velocitypowered.api.plugin.Plugin;
import ml.karmaconfigs.api.common.Console;
import ml.karmaconfigs.api.common.JarInjector;
import ml.karmaconfigs.api.common.Level;
import ml.karmaconfigs.api.common.utils.FileUtilities;

import java.io.File;

/**
 * Get velocity utilities
 */
public final class Util {

    private final Plugin plugin;

    /**
     * Initialize the velocity plugin util
     *
     * @param owner the velocity plugin owner
     */
    public Util(final Plugin owner) {
        plugin = owner;
    }

    private static boolean injected;

    /**
     * Initialize the velocity plugin
     */
    public final void initialize() {
        String lombok = "https://repo1.maven.org/maven2/org/projectlombok/lombok/1.18.20/lombok-1.18.20.jar";
        File lo_jar = new File(FileUtilities.getServerFolder() + File.separator + "cache" + File.separator + "KarmaAPI" + File.separator + "dependencies", "Lombok.jar");
        try {
            JarInjector lombok_injector = new JarInjector(lo_jar);
            if (!injected) {
                if (!lombok_injector.isDownloaded())
                    lombok_injector.download(lombok);

                if (lombok_injector.inject(plugin)) {
                    ml.karmaconfigs.api.common.Console.send("&aInjected Lombok dependency ( from KarmaAPI ) into " + plugin.name(), Level.OK);
                    injected = true;
                } else {
                    Console.send("&cCouldn't inject Lombok dependency ( from KarmaAPI ), some features like yaml manager or commented yaml files won't be available", Level.GRAVE);
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get velocity plugin data folder
     *
     * @return the velocity plugin data folder
     */
    public final File getDataFolder() {
        File plugins = FileUtilities.getPluginsFolder();

        return new File(plugins, plugin.name());
    }
}
