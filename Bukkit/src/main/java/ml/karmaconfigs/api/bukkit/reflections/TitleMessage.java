package ml.karmaconfigs.api.bukkit.reflections;

import ml.karmaconfigs.api.common.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
public final class TitleMessage {

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
    public final void send() {
        try {
            Object chatTitle = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + title + "\"}");
            Constructor<?> titleConstructor = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getConstructor(
                    Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object packet = titleConstructor.newInstance(
                    Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle,
                    20 * 2, 20 * 5, 20 * 2);

            Object chatsTitle = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + subtitle + "\"}");
            Constructor<?> timingTitleConstructor = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getConstructor(
                    Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object timingPacket = timingTitleConstructor.newInstance(
                    Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("SUBTITLE").get(null), chatsTitle,
                    20 * 2, 20 * 5, 20 * 2);

            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, timingPacket);
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
    public final void send(final int showIn, final int keepIn, final int hideIn) {
        try {
            Object chatTitle = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + title + "\"}");
            Constructor<?> titleConstructor = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getConstructor(
                    Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object packet = titleConstructor.newInstance(
                    Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle,
                    20 * showIn, 20 * keepIn, 20 * hideIn);

            Object chatsTitle = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + subtitle + "\"}");
            Constructor<?> timingTitleConstructor = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getConstructor(
                    Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object timingPacket = timingTitleConstructor.newInstance(
                    Objects.requireNonNull(getNMSClass("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("SUBTITLE").get(null), chatsTitle,
                    20 * showIn, 20 * keepIn, 20 * hideIn);

            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, timingPacket);
        } catch (Throwable ex) {
            player.sendTitle(title, subtitle, 20 * showIn, 20 * keepIn, 20 * hideIn);
        }
    }

    /**
     * Get a nms class directly from the server version
     *
     * @param clazz the class name
     * @return a Class
     */
    @Nullable
    static Class<?> getNMSClass(@NotNull final String clazz) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            return Class.forName("net.minecraft.server." + version + "." + clazz);
        } catch (Throwable e) {
            return null;
        }
    }
}
