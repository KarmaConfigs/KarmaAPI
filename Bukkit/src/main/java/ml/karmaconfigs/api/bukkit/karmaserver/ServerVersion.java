package ml.karmaconfigs.api.bukkit.karmaserver;

import ml.karmaconfigs.api.bukkit.Console;
import ml.karmaconfigs.api.common.utils.FileUtilities;
import ml.karmaconfigs.api.common.JarInjector;
import ml.karmaconfigs.api.common.KarmaPlugin;
import ml.karmaconfigs.api.common.Level;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Constructor;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
public final class ServerVersion {

    static boolean injected = false;

    private final JavaPlugin main;

    /**
     * Get the minecraft server version
     * of the server
     *
     * @param plugin the plugin to get the server
     *               from
     */
    public ServerVersion(@NotNull final JavaPlugin plugin) {
        main = plugin;

        String mavenArtifact = "https://repo1.maven.org/maven2/org/apache/maven/maven-artifact/3.6.3/maven-artifact-3.6.3.jar";
        String lang3 = "https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.11/commons-lang3-3.11.jar";

        File ma_jar = new File(FileUtilities.getServerFolder() + File.separator + "cache" + File.separator + "KarmaAPI" + File.separator + "dependencies", "MavenArtifact.jar");
        File la_jar = new File(FileUtilities.getServerFolder() + File.separator + "cache" + File.separator + "KarmaAPI" + File.separator + "dependencies", "Lang3.jar");
        try {
            JarInjector ma_injector = new JarInjector(ma_jar);
            JarInjector la_injector = new JarInjector(la_jar);
            if (!injected) {
                if (!ma_injector.isDownloaded())
                    ma_injector.download(mavenArtifact);
                if (!la_injector.isDownloaded())
                    la_injector.download(lang3);

                if (ma_injector.inject(plugin) && la_injector.inject(plugin)) {
                    Console.send(plugin, "Injected MavenArtifact and Lang3 dependency ( from KarmaAPI ) into " + KarmaPlugin.getters.getName(plugin), Level.OK);
                    injected = true;
                } else {
                    Console.send(plugin, "Couldn't inject MavenArtifact or Lang3 dependency ( from KarmaAPI ), some features like server version check won't be available", Level.GRAVE);
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get a new version utils instance
     *
     * @return a new version utils instance
     */
    public final VersionUtils utils() {
        try {
            Constructor<?> constructor = VersionUtils.class.getDeclaredConstructor(JavaPlugin.class);
            constructor.setAccessible(true);

            return (VersionUtils) constructor.newInstance(main);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
