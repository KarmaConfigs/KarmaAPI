package ml.karmaconfigs.api.bungee.makeiteasy;

import ml.karmaconfigs.api.common.utils.StringUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private final ProxiedPlayer player;
    private final String title, subtitle;

    /**
     * Initialize the title class
     *
     * @param p the player
     * @param t the title text
     * @param s the subtitle text
     */
    public TitleMessage(@NotNull final ProxiedPlayer p, @Nullable String t, @Nullable String s) {
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
     * @param p the player
     * @param t the title text
     */
    public TitleMessage(@NotNull final ProxiedPlayer p, @Nullable String t) {
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
        net.md_5.bungee.api.Title server_title = ProxyServer.getInstance().createTitle();
        server_title.title(TextComponent.fromLegacyText(title));
        server_title.subTitle(TextComponent.fromLegacyText(subtitle));
        server_title.fadeIn(20 * 2);
        server_title.stay(20 * 2);
        server_title.fadeOut(20 * 2);

        server_title.send(player);
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
        net.md_5.bungee.api.Title server_title = ProxyServer.getInstance().createTitle();
        server_title.title(TextComponent.fromLegacyText(title));
        server_title.subTitle(TextComponent.fromLegacyText(subtitle));
        server_title.fadeIn(20 * showIn);
        server_title.stay(20 * keepIn);
        server_title.fadeOut(20 * hideIn);

        server_title.send(player);
    }
}
