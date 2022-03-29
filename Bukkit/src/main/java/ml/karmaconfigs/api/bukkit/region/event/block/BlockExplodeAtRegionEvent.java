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
public class BlockExplodeAtRegionEvent extends BlockEvent implements Cancellable {

    private final static HandlerList HANDLER_LIST = new HandlerList();

    private final Cuboid region;

    private boolean cancelled = false;
    private float yield;

    /**
     * Initialize the generic block event
     *
     * @param bl the block
     * @param y the explosion yield
     * @param rg the region
     */
    public BlockExplodeAtRegionEvent(final Block bl, final float y, final Cuboid rg) {
        super(bl);
        region = rg;
        yield = y;
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
     * Get the explosion yield
     *
     * @return the explosion yield
     */
    public float getYield() {
        return yield;
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

    /**
     * Set the explosion yield
     *
     * @param y the new explosion yield
     */
    public void setYield(final float y) {
        yield = y;
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
