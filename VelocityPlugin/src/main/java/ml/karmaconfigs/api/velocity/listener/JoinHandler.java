package ml.karmaconfigs.api.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.util.GameProfile;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import ml.karmaconfigs.api.common.utils.uuid.UUIDUtil;

public class JoinHandler {

    private final KarmaSource plugin;

    public JoinHandler(final KarmaSource owner) {
        plugin = owner;
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onJoin(GameProfileRequestEvent e) {
        GameProfile original = e.getOriginalProfile();
        GameProfile current = e.getGameProfile();
        String userName = e.getUsername();

        plugin.async().queue("oka_register_client", () -> {
            if (!StringUtils.isNullOrEmpty(original)) {
                UUIDUtil.registerMinecraftClient(original.getName());
            }
            if (!StringUtils.isNullOrEmpty(current)) {
                UUIDUtil.registerMinecraftClient(current.getName());
            }
            if (!StringUtils.isNullOrEmpty(userName)) {
                UUIDUtil.registerMinecraftClient(userName);
            }
        });
    }
}
