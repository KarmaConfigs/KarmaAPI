package ml.karmaconfigs.api.bukkit.server;

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

import ml.karmaconfigs.api.common.utils.string.ComparatorBuilder;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import ml.karmaconfigs.api.common.utils.string.VersionComparator;
import ml.karmaconfigs.api.common.utils.string.util.VersionDiff;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Minecraft bukkit server
 * version utils. Check versions
 * and more...
 */
public final class BukkitServer {

    private final static Server server = Bukkit.getServer();

    /**
     * Get the full version string of
     * the server
     *
     * @return a String
     */
    public static String getRealVersion() {
        return server.getBukkitVersion();
    }

    /**
     * Get the version full version
     * as string
     *
     * @return a String
     */
    public static String getFullVersion() {
        return server.getBukkitVersion().split("-")[0];
    }

    /**
     * Get the server version package type
     *
     * @return a string
     */
    public static String getPackageType() {
        return server.getBukkitVersion().split("-")[2];
    }

    /**
     * Get the server version package build
     *
     * @return a string
     */
    public static String getPackageBuild() {
        return server.getBukkitVersion().split("-")[1];
    }

    /**
     * Get the server version
     *
     * @return a float
     */
    public static float getVersion() {
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
    public static int getVersionUpdate() {
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
    public static Version version() {
        String full = getFullVersion();
        full = "v" + full.replace(".", "_");

        try {
            return Version.valueOf(full);
        } catch (IllegalArgumentException ex) {
            return Version.UNKNOWN; //Do not panic. We will make this version unknown and use the latest instead
        }
    }

    /**
     * Check if the current server version is over the specified
     * one
     *
     * @param version the check version
     * @return if current version is over the
     * specified one
     */
    public static boolean isOver(final Version version) {
        String current_version = version().name().replace("v", "").replace("_", ".");
        String check_version = version.name().replace("v", "").replace("_", ".");

        ComparatorBuilder builder = VersionComparator.createBuilder()
                .currentVersion(current_version)
                .checkVersion(check_version);
        VersionComparator comparator = StringUtils.compareTo(builder);

        return comparator.getDifference().equals(VersionDiff.OVERDATED);
    }

    /**
     * Check if the current server version is the specified one
     *
     * @param version the check version
     * @return if the current version is the
     * specified one
     */
    public static boolean isUpdated(final Version version) {
        Version current = version();

        if (current == Version.UNKNOWN)
            current = Version.values()[Version.values().length - 1];

        String current_version = current.name().replace("v", "").replace("_", ".");
        String check_version = version.name().replace("v", "").replace("_", ".");

        ComparatorBuilder builder = VersionComparator.createBuilder()
                .currentVersion(current_version)
                .checkVersion(check_version);
        VersionComparator comparator = StringUtils.compareTo(builder);

        return comparator.getDifference().equals(VersionDiff.UPDATED);
    }

    /**
     * Check if the current server version is under the specified
     * one
     *
     * @param v the server version
     * @return if current version is over the
     * specified one
     */
    public static boolean isUnder(final Version v) {
        String current_version = version().name().replace("v", "").replace("_", ".");
        String check_version = v.name().replace("v", "").replace("_", ".");

        ComparatorBuilder builder = VersionComparator.createBuilder()
                .currentVersion(current_version)
                .checkVersion(check_version);
        VersionComparator comparator = StringUtils.compareTo(builder);

        return comparator.getDifference().equals(VersionDiff.OUTDATED);
    }

    /**
     * Get a nms class directly from the server version
     *
     * @param clazz the class name
     * @return a Class
     */
    @Nullable
    public static Class<?> getMinecraftClass(@NotNull final String clazz) {
        try {
            String version = server.getClass().getPackage().getName().replace(".", ",").split(",")[3];
            return Class.forName("net.minecraft.server." + version + "." + clazz);
        } catch (Throwable ex) {
            try {
                return Class.forName("net.minecraft.server." + clazz);
            } catch (Throwable exc) {
                return null;
            }
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
    public static Class<?> getBukkitClass(@NotNull final String clazz) {
        try {
            String version = server.getClass().getPackage().getName().replace(".", ",").split(",")[3];
            return Class.forName("org.bukkit.craftbukkit." + version + "." + clazz);
        } catch (Throwable e) {
            return null;
        }
    }
}
