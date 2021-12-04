package ml.karmaconfigs.api.velocity.loader;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import ml.karmaconfigs.api.common.karma.KarmaSource;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class VelocityBridge extends BridgeLoader<KarmaSource> {

    private static KarmaSource instance;
    private static ProxyServer server;
    private static PluginContainer plugin;

    private final static Set<KarmaSource> loaded = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Initialize the bridge loader
     *
     * @param source the source class
     */
    public VelocityBridge(final KarmaSource source, final ProxyServer sv, final PluginContainer owner) {
        super("Velocity", source);

        instance = source;
        server = sv;
        plugin = owner;
    }

    /**
     * Start the bridge loader
     */
    @Override
    public void start() {
        plugin.getInstance().ifPresent((pluginI) -> {
            instance.console().send("Initializing Velocity <-> KarmaAPI bridge", Level.INFO);

            /*
            This method works also in BungeeCord and Bukkit, but...
            it doesn't gives any warranty that the plugin will be
            loaded after KarmaAPI plugin, so could give errors
             */
            connect(instance.getSourceFile());

            instance.console().send("Velocity <-> KarmaAPI bridge made successfully", Level.INFO);

            try {
                for (PluginContainer container : server.getPluginManager().getPlugins()) {
                    //Plugin name is AnotherBarelyCodedKarmaPlugin, but to make it easier for
                    //developers, they will have to put "KarmaAPI" in softdepend
                    if (container.getDescription().getDependency("KarmaAPI").isPresent()) {
                        //In fact that's not needed, but just to be sure everything is in the same loader so
                        //everyone can read from everywhere
                        container.getDescription().getSource().ifPresent(this::connect);
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * Stop the bridge
     */
    @Override
    public void stop() {
        //Velocity does not provide any API to even
        //disable a plugin
    }

    /**
     * Get the loader instance
     *
     * @return the loader instance
     */
    public static KarmaSource getSource() {
        return instance;
    }

    /**
     * Get the server
     *
     * @return the server
     */
    public static ProxyServer getServer() {
        return server;
    }
}
