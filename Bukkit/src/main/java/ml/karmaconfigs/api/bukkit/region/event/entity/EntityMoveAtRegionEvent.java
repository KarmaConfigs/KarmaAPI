package ml.karmaconfigs.api.bukkit.region.event.entity;

import ml.karmaconfigs.api.bukkit.region.Cuboid;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when an entity moves inside a region
 */
public final class EntityMoveAtRegionEvent extends EntityEvent implements Cancellable {


    private final static HandlerList HANDLER_LIST = new HandlerList();

    private final Cuboid region;
    private final Location from;
    private final Location to;

    private boolean cancelled = false;

    /**
     * Initialize the entity move in region event
     *
     * @param ent the entity
     * @param rg the region the entity
     *           has moved in
     * @param f the from location
     * @param t the to location
     */
    public EntityMoveAtRegionEvent(final Entity ent, final Cuboid rg, final Location f, final Location t) {
        super(ent);

        region = rg;
        from = f;
        to = t;
    }

    /**
     * Get the region the entity is moving in
     *
     * @return the event region
     */
    public Cuboid getRegion() {
        return region;
    }

    /**
     * Get from where the entity is coming
     *
     * @return the entity original location
     */
    public Location getFrom() {
        return from;
    }

    /**
     * Get where the entity is going
     *
     * @return the entity next location
     */
    public Location getTo() {
        return to;
    }

    /**
     * Get event handler list
     *
     * @return event handler list
     */
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
    public void setCancelled(final boolean cancel) {
        cancelled = cancel;
    }
}
