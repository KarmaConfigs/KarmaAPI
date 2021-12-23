package ml.karmaconfigs.api.bukkit.util;

import ml.karmaconfigs.api.bukkit.KarmaPlugin;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Karma client
 */
public abstract class Client {

    /**
     * Send a message to the client
     *
     * @param message the message
     */
    public abstract void sendMessage(final String message);

    /**
     * Send a message to the client
     *
     * @param message the message
     * @param replaces the message replaces
     */
    public abstract void sendMessage(final String message, final Object... replaces);

    /**
     * Send a title to the client
     *
     * @param title the title
     * @param subtitle the subtitle
     */
    public abstract void sendTitle(final String title, final String subtitle);

    /**
     * Send a title to the client
     *
     * @param title the title
     * @param subtitle the subtitle
     * @param showIn the time to show in
     * @param keepIn the time to keep in
     * @param hideIn the time to hide in
     */
    public abstract void sendTitle(final String title, final String subtitle, final int showIn, final int keepIn, final int hideIn);

    /**
     * Send an action bar to the client
     *
     * @param message the action bar message
     * @param repeats the action bar repeats
     */
    public abstract void sendActionBar(final String message, final int repeats);

    /**
     * Send an action bar to the client
     *
     * @param message the action bar message
     * @param persistent if the action bar should be visible for ever
     */
    public abstract void sendActionBar(final String message, final boolean persistent);

    /**
     * Disconnect the client
     *
     * @param reason the disconnect reason
     */
    public abstract void disconnect(final List<String> reason);

    /**
     * Disconnect the client
     *
     * @param reason the disconnect reason
     */
    public abstract void disconnect(final String... reason);

    /**
     * Disconnect the client
     *
     * @param reason the disconnect reason
     */
    public abstract void disconnect(final String reason);

    /**
     * Create a new client instance
     *
     * @param plugin the client owner
     * @param player the client player
     * @return a new client instance
     */
    public static Client instantiate(final KarmaPlugin plugin, final Player player) {
        return new BukkitClient(plugin, player);
    }
}
