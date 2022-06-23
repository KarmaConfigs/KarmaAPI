package ml.karmaconfigs.api.bukkit.region.flag;

public enum Flag {
    GLOBAL_EXPLOSION(new SimpleFlag("global_explosion", true)),
    BLOCK_EXPLOSION(new SimpleFlag("block_explosion", true)),
    BLOCK_BURN(new SimpleFlag("block_burn", true)),
    BLOCK_MODIFY(new SimpleFlag("block_modify", true)),
    BLOCK_PROPAGATE(new SimpleFlag("block_propagate", true)),
    BLOCK_DRAGON_EGG(new SimpleFlag("block_dragon_egg", true)),
    BLOCK_FLOW(new SimpleFlag("block_flow", true)),
    EXPLOSION_BREAK(new SimpleFlag("explosion_break", true)),
    ENTITY_DEATH(new SimpleFlag("entity_death", true)),
    ENTITY_INTERACT(new SimpleFlag("entity_interact", true)),
    ENTITY_JOIN(new SimpleFlag("entity_join", true)),
    ENTITY_LEAVE(new SimpleFlag("entity_leave", true)),
    ENTITY_PICKUP(new SimpleFlag("entity_pickup", true)),
    ENTITY_SPAWN(new SimpleFlag("entity_spawn", true)),
    ENTITY_ITEM_SPAWN(new SimpleFlag("entity_item_spawn", true)),
    PLAYER_ACTION(new SimpleFlag("player_action", true)),
    PLAYER_INTERACT_UNKNOWN(new SimpleFlag("player_interact_unknown", true)),
    PLAYER_INTERACT_LEFT_CLICK_BLOCK(new SimpleFlag("player_interact_left_block", true)),
    PLAYER_INTERACT_RIGHT_CLICK_BLOCK(new SimpleFlag("player_interact_right_block", true)),
    PLAYER_INTERACT_LEFT_CLICK_AIR(new SimpleFlag("player_interact_left_none", true)),
    PLAYER_INTERACT_RIGHT_CLICK_AIR(new SimpleFlag("player_interact_right_none", true)),
    PLAYER_DROP_ITEM(new SimpleFlag("player_drop_item", true)),
    PLAYER_PICKUP_ITEM(new SimpleFlag("player_pickup_item", true)),
    PLAYER_JUMP_SOIL(new SimpleFlag("player_jump_soil", true)),
    PLAYER_PRESS_PLATE(new SimpleFlag("player_press_plate", true)),
    PLAYER_PRESS_BUTTON(new SimpleFlag("player_press_button", true)),
    PLAYER_PRESS_LEVER(new SimpleFlag("player_press_lever", true)),
    PLAYER_REDSTONE(new SimpleFlag("player_redstone", true)),
    PLAYER_TRIPWIRE(new SimpleFlag("player_tripwire", true));

    private final RegionFlag flag;

    /**
     * Initialize the flag
     *
     * @param fl the flag
     */
    Flag(final RegionFlag fl) {
        flag = fl;
    }

    /**
     * Get the region flag
     *
     * @return the region flag
     */
    public RegionFlag get() {
        return flag;
    }
}
