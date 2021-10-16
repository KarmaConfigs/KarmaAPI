package ml.karmaconfigs.api.bukkit.reflection;

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

import ml.karmaconfigs.api.bukkit.server.VersionUtils;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * Title message
 */
public final class TitleMessage {

    private final static VersionUtils utils = VersionUtils.newInstance();
    private final Player player;
    private final String title, subtitle;

    /**
     * Initialize the title class
     *
     * @param p the player
     * @param t the title text
     * @param s the subtitle text
     */
    public TitleMessage(final Player p, @Nullable String t, @Nullable String s) {
        player = p;
        if (t == null)
            t = "";
        title = StringUtils.toColor(t);
        if (s == null)
            s = "";
        subtitle = StringUtils.toColor(s);
    }

    /**
     * Initialize the title class
     *
     * @param plugin the caller
     * @param p the player
     * @param t the title text
     */
    public TitleMessage(final JavaPlugin plugin, final Player p, @Nullable String t) {
        player = p;
        if (t == null)
            t = "";
        title = StringUtils.toColor(t);
        subtitle = "";
    }

    /**
     * Send the title
     */
    public void send() {
        try {
            Object chatTitle = Objects.requireNonNull(utils.getMinecraftClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + title + "\"}");
            Constructor<?> titleConstructor = Objects.requireNonNull(utils.getMinecraftClass("PacketPlayOutTitle")).getConstructor(
                    Objects.requireNonNull(utils.getMinecraftClass("PacketPlayOutTitle")).getDeclaredClasses()[0], utils.getMinecraftClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object packet = titleConstructor.newInstance(
                    Objects.requireNonNull(utils.getMinecraftClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle,
                    20 * 2, 20 * 5, 20 * 2);

            Object chatsTitle = Objects.requireNonNull(utils.getMinecraftClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + subtitle + "\"}");
            Constructor<?> timingTitleConstructor = Objects.requireNonNull(utils.getMinecraftClass("PacketPlayOutTitle")).getConstructor(
                    Objects.requireNonNull(utils.getMinecraftClass("PacketPlayOutTitle")).getDeclaredClasses()[0], utils.getMinecraftClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object timingPacket = timingTitleConstructor.newInstance(
                    Objects.requireNonNull(utils.getMinecraftClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("SUBTITLE").get(null), chatsTitle,
                    20 * 2, 20 * 5, 20 * 2);

            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

            playerConnection.getClass().getMethod("sendPacket", utils.getMinecraftClass("Packet")).invoke(playerConnection, packet);
            playerConnection.getClass().getMethod("sendPacket", utils.getMinecraftClass("Packet")).invoke(playerConnection, timingPacket);
        } catch (Throwable ex) {
            player.sendTitle(title, subtitle, 20 * 2, 20 * 5, 20 * 2);
        }
    }

    /**
     * Send the title
     *
     * @param showIn the time that will take to
     *               completely show the title
     * @param keepIn the time to keep in
     * @param hideIn the time that will take to
     *               completely hide the title
     */
    public void send(final int showIn, final int keepIn, final int hideIn) {
        try {
            Object chatTitle = Objects.requireNonNull(utils.getMinecraftClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + title + "\"}");
            Constructor<?> titleConstructor = Objects.requireNonNull(utils.getMinecraftClass("PacketPlayOutTitle")).getConstructor(
                    Objects.requireNonNull(utils.getMinecraftClass("PacketPlayOutTitle")).getDeclaredClasses()[0], utils.getMinecraftClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object packet = titleConstructor.newInstance(
                    Objects.requireNonNull(utils.getMinecraftClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle,
                    20 * showIn, 20 * keepIn, 20 * hideIn);

            Object chatsTitle = Objects.requireNonNull(utils.getMinecraftClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + subtitle + "\"}");
            Constructor<?> timingTitleConstructor = Objects.requireNonNull(utils.getMinecraftClass("PacketPlayOutTitle")).getConstructor(
                    Objects.requireNonNull(utils.getMinecraftClass("PacketPlayOutTitle")).getDeclaredClasses()[0], utils.getMinecraftClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object timingPacket = timingTitleConstructor.newInstance(
                    Objects.requireNonNull(utils.getMinecraftClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("SUBTITLE").get(null), chatsTitle,
                    20 * showIn, 20 * keepIn, 20 * hideIn);

            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

            playerConnection.getClass().getMethod("sendPacket", utils.getMinecraftClass("Packet")).invoke(playerConnection, packet);
            playerConnection.getClass().getMethod("sendPacket", utils.getMinecraftClass("Packet")).invoke(playerConnection, timingPacket);
        } catch (Throwable ex) {
            player.sendTitle(title, subtitle, 20 * showIn, 20 * keepIn, 20 * hideIn);
        }
    }
}
