package ml.karmaconfigs.api.bukkit.karmaserver;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Minecraft bukkit server
 * version utils. Check versions
 * and more...
 */
public class VersionUtils {

    private final Server server;

    /**
     * Get the minecraft server version
     * of the server
     *
     * @param plugin the plugin to get the server
     *               from
     */
    private VersionUtils(final JavaPlugin plugin) {
        server = plugin.getServer();
    }

    /**
     * Get the full version string of
     * the server
     *
     * @return a String
     */
    public final String getRealVersion() {
        return server.getBukkitVersion();
    }

    /**
     * Get the version full version
     * as string
     *
     * @return a String
     */
    public final String getFullVersion() {
        return server.getBukkitVersion().split("-")[0];
    }

    /**
     * Get the server version package type
     *
     * @return a string
     */
    public final String getPackageType() {
        return server.getBukkitVersion().split("-")[2];
    }

    /**
     * Get the server version package build
     *
     * @return a string
     */
    public final String getPackageBuild() {
        return server.getBukkitVersion().split("-")[1];
    }

    /**
     * Get the server version
     *
     * @return a float
     */
    public final float getVersion() {
        String[] versionData = server.getBukkitVersion().split("-");
        String version_head = versionData[0].split("\\.")[0];
        String version_sub = versionData[0].split("\\.")[1];

        return Float.parseFloat(version_head + "." + version_sub);
    }

    /**
     * Get the server version update
     * (Example: 1.16.2 will return "2")
     *
     * @return an integer
     */
    public final int getVersionUpdate() {
        String[] versionData = server.getBukkitVersion().split("-");
        String version = versionData[0];
        versionData = version.split("\\.");
        if (versionData.length >= 3) {
            return Integer.parseInt(versionData[2]);
        }
        return -1;
    }

    /**
     * Get the version in enumeration type
     *
     * @return a Version instance
     */
    public final Version getV() {
        String full = getFullVersion();
        full = "v" + full.replace(".", "_");

        return Version.valueOf(full);
    }

    /**
     * Check if the current server version is over the specified
     * one
     *
     * @param v the server version
     * @return if current version is over the
     * specified one
     */
    public final boolean isOver(final Version v) {
        if (ServerVersion.injected) {
            String current_version = getV().name().replace("v", "").replace("_", ".");
            String check_version = v.name().replace("v", "").replace("_", ".");

            DefaultArtifactVersion version = new DefaultArtifactVersion(current_version);
            DefaultArtifactVersion from_version = new DefaultArtifactVersion(check_version);

            return version.compareTo(from_version) > 0;
        }

        return false;
    }

    /**
     * Check if the current server version is under the specified
     * one
     *
     * @param v the server version
     * @return if current version is over the
     * specified one
     */
    public final boolean isUnder(final Version v) {
        if (ServerVersion.injected) {
            String current_version = getV().name().replace("v", "").replace("_", ".");
            String check_version = v.name().replace("v", "").replace("_", ".");

            DefaultArtifactVersion version = new DefaultArtifactVersion(current_version);
            DefaultArtifactVersion from_version = new DefaultArtifactVersion(check_version);

            return version.compareTo(from_version) < 0;
        }

        return false;
    }

    /**
     * Get a nms class directly from the server version
     *
     * @param clazz the class name
     * @return a Class
     */
    @Nullable
    public final Class<?> getMinecraftClass(@NotNull final String clazz) {
        try {
            String version = server.getClass().getPackage().getName().replace(".", ",").split(",")[3];
            return Class.forName("net.minecraft.server." + version + "." + clazz);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * Get an obc class directly from server
     * package
     *
     * @param clazz the class name
     * @return a Class
     */
    @Nullable
    public final Class<?> getBukkitClass(@NotNull final String clazz) {
        try {
            String version = server.getClass().getPackage().getName().replace(".", ",").split(",")[3];
            return Class.forName("org.bukkit.craftbukkit." + version + "." + clazz);
        } catch (Throwable e) {
            return null;
        }
    }
}
