package ml.karmaconfigs.api.bukkit.region.event.block;

import ml.karmaconfigs.api.bukkit.region.Cuboid;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Generic block listener
 */
public class ExplosionBreakAtRegionEvent extends BlockEvent implements Cancellable {

    private final static HandlerList HANDLER_LIST = new HandlerList();

    private final Block source;
    private final Cuboid region;

    private boolean cancelled = false;

    /**
     * Initialize the generic block event
     *
     * @param bl the block
     * @param src the block that started the explosion
     * @param rg the region
     */
    public ExplosionBreakAtRegionEvent(final Block bl, final Block src, final Cuboid rg) {
        super(bl);
        source = src;
        region = rg;
    }

    /**
     * Get the block that started the explosion
     *
     * @return the explosion source
     */
    public Block getSource() {
        return source;
    }

    /**
     * Get the region where the block event has
     * been fired
     *
     * @return the region
     */
    public Cuboid getRegion() {
        return region;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * Get a list of event handlers
     *
     * @return a list of event handlers
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
