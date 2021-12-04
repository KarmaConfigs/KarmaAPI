package ml.karmaconfigs.api.common.utils;

import ml.karmaconfigs.api.common.karma.KarmaAPI;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.karma.loader.BruteLoader;
import ml.karmaconfigs.api.common.utils.enums.Level;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

/**
 * KarmaAPI bridge connector.
 *
 * This is used by KarmaAPI to load
 * KarmaSource modules at runtime
 * making a bridge with a main loader
 * provided by Bukkit or Bungee loader
 */
public abstract class BridgeLoader<T extends KarmaSource> {

    private static boolean running = false;
    private static boolean hooked = false;
    private static BruteLoader loader = null;

    /**
     * Initialize the bridge loader
     *
     * @param connection the connection source name
     * @param instance the source class
     * @throws IllegalStateException if another bridge is already running
     */
    public BridgeLoader(final String connection, final T instance) throws IllegalStateException {
        if (!hooked || loader == null) {
            instance.console().send("Initializing {0} <-> KarmaAPI bridge for KarmaAPI modules", Level.INFO, connection);

            loader = new BruteLoader((URLClassLoader) instance.getClass().getClassLoader());

            //It doesn't matter if default source is KarmaAPI, as if the default source is
            //KarmaAPI, this stills being executed from a physical jar file which is the
            //source file.
            loader.add(KarmaAPI.source().getSourceFile());
            hooked = true;

            instance.console().send("Created a bridge for {0} and KarmaAPI", Level.INFO, connection);
        } else {
            throw new IllegalStateException("Tried to setup a KarmaAPI bridge but a bridge is already built");
        }
    }

    /**
     * Start the bridge loader
     *
     * @throws Throwable This is just if the bridge start method
     * throws any error
     */
    public abstract void start() throws Throwable;

    /**
     * Stop the bridge
     *
     * @throws Throwable This is just if the bridge stop method
     * throws any error
     */
    public abstract void stop() throws Throwable;

    /**
     * Connect a new source to the bridge
     *
     * @param target the source target
     */
    protected final void connect(final File target) {
        loader.add(target);
    }

    /**
     * Connect a new source to the bridge
     *
     * @param target the source target
     */
    protected final void connect(final Path target) {
        loader.add(target);
    }

    /**
     * Connect a new source to the bridge
     *
     * @param target the source target
     */
    protected final void connect(final URL target) {
        loader.add(target);
    }

    /**
     * Set the bridge running status
     *
     * @param status the bridge running status
     */
    protected final void setRunning(final boolean status) {
        running = status;
    }

    /**
     * Get if the bridge is running
     *
     * @return the bridge status
     */
    public static boolean isRunning() {
        return running;
    }
}
