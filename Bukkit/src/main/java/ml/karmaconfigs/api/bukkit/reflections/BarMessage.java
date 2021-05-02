package ml.karmaconfigs.api.bukkit.reflections;

import ml.karmaconfigs.api.common.utils.StringUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
public final class BarMessage {

    private final Player player;

    private String message;

    private boolean sent = false;
    private boolean send = false;

    /**
     * Initialize the ActionBar class
     *
     * @param p the player
     * @param m the message
     */
    public BarMessage(final Player p, @Nullable final String m) {
        player = p;
        message = m;
    }

    /**
     * Send the action bar
     */
    private void send() {
        if (player != null && player.isOnline()) {
            String msg = StringUtils.toColor(message);
            try {
                Constructor<?> constructor = Objects.requireNonNull(TitleMessage.getNMSClass("PacketPlayOutChat")).getConstructor(TitleMessage.getNMSClass("IChatBaseComponent"), byte.class);

                Object icbc = Objects.requireNonNull(TitleMessage.getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + msg + "\"}");
                Object packet = constructor.newInstance(icbc, (byte) 2);
                Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
                Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

                playerConnection.getClass().getMethod("sendPacket", TitleMessage.getNMSClass("Packet")).invoke(playerConnection, packet);
                sent = true;
            } catch (Throwable ex) {
                try {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, UUID.randomUUID(), TextComponent.fromLegacyText(msg));
                    sent = true;
                } catch (Throwable exc) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
                    sent = true;
                }
            }
        }
    }

    /**
     * Send the message until you tell it to stop
     *
     * @param persistent if the message should be persistent
     *                   until you order to stop
     */
    public final void send(final boolean persistent) {
        send = persistent;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!send)
                    cancel();

                send();
            }
        }, 0, TimeUnit.SECONDS.toMillis(1));
    }

    /**
     * Send the message the specified amount of times
     *
     * @param repeats the amount of times to send it
     */
    public final void send(final int repeats) {
        send = true;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int repeated = 0;
            @Override
            public void run() {
                if (!send)
                    stop();

                repeated++;
                send();
                if (repeated == repeats) {
                    cancel();
                }
            }
        }, 0, TimeUnit.SECONDS.toMillis(2));
    }

    /**
     * Update the actionbar message
     *
     * @param _message the new message
     */
    public final void setMessage(final String _message) {
        message = _message;
    }

    /**
     * Stop sending the action bar
     */
    public final void stop() {
        send = false;
    }

    /**
     * Check if the bar has been sent
     *
     * @return if the bar has been sent
     */
    public final boolean isSent() {
        return sent;
    }
}
