package ml.karmaconfigs.api.bukkit.karmaserver;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
public final class ServerVersion extends VersionUtils {

    static boolean injected = false;

    /**
     * Get the minecraft server version
     * of the server
     *
     * @param plugin the plugin to get the server
     *               from
     */
    public ServerVersion(@NotNull final JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Get a new version utils instance
     *
     * @return a new version utils instance
     */
    @Deprecated
    public final VersionUtils utils() {
        return this;
    }
}
