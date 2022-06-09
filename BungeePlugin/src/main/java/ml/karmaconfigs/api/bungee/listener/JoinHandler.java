package ml.karmaconfigs.api.bungee.listener;

import ml.karmaconfigs.api.bungee.KarmaPlugin;
import ml.karmaconfigs.api.common.utils.uuid.UUIDUtil;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class JoinHandler implements Listener {

    private final KarmaPlugin plugin;

    public JoinHandler(final KarmaPlugin owner) {
        plugin = owner;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void profileRequest(PreLoginEvent e) {
        plugin.async().queue("oka_register_client", () -> UUIDUtil.registerMinecraftClient(e.getConnection().getName()));
    }
}
