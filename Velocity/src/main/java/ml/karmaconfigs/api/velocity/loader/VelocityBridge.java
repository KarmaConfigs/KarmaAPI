package ml.karmaconfigs.api.velocity.loader;

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

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.karma.KarmaConfig;
import ml.karmaconfigs.api.common.utils.BridgeLoader;
import ml.karmaconfigs.api.common.utils.enums.Level;

/**
 * Bridge between Velocity and KarmaAPI
 */
public class VelocityBridge extends BridgeLoader<KarmaSource> {

    private static KarmaSource instance;
    private static ProxyServer server;
    private static PluginContainer plugin;

    /**
     * Initialize the bridge loader
     *
     * @param source the source class
     * @param sv the server where the bridge will be done
     * @param owner the bridge owner
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
        KarmaConfig config = new KarmaConfig();

        plugin.getInstance().ifPresent((pluginI) -> {
            //In fact that's not needed, but just to be sure everything is in the same loader so
            //everyone can read from everywhere
            if (config.debug(Level.INFO)) {
                instance.console().send("Initializing Velocity <-> KarmaAPI bridge", Level.INFO);
            }
            connect(instance.getSourceFile());
            if (config.debug(Level.INFO)) {
                instance.console().send("Velocity <-> KarmaAPI bridge made successfully", Level.INFO);
            }

            try {
                for (PluginContainer container : server.getPluginManager().getPlugins()) {
                    if (container.getDescription().getDependency("anotherbarelycodedkarmaplugin").isPresent()) {
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
        KarmaConfig config = new KarmaConfig();

        if (config.debug(Level.INFO)) {
            instance.console().send("Closing Velocity <-> KarmaAPI bridge, please wait...", Level.INFO);
        }
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
