package ml.karmaconfigs.api.bukkit.region.event.block;

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

import ml.karmaconfigs.api.bukkit.region.Cuboid;
import ml.karmaconfigs.api.bukkit.util.BlockUtil;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Generic block listener
 */
public class BlockModifiedAtRegionEvent extends BlockEvent implements Cancellable {

    private final static HandlerList HANDLER_LIST = new HandlerList();

    private final BlockAction action;
    private final Cuboid region;

    private Object source;
    private boolean cancelled = false;

    /**
     * Initialize the generic block event
     *
     * @param bl the block
     * @param issuer the entity that modified a block
     * @param act the block action
     * @param rg the region
     */
    public BlockModifiedAtRegionEvent(final Block bl, final Object issuer, final BlockAction act, final Cuboid rg) {
        super(bl);
        source = issuer;
        action = act;
        region = rg;
    }

    /**
     * Initialize the generic block event
     *
     * @param src the block
     * @param affected the affected blocks
     * @param act the block action
     * @param rg the region
     */
    public BlockModifiedAtRegionEvent(final Block src, final Collection<Block> affected, final BlockAction act, final Cuboid rg) {
        super(src);
        source = affected;
        action = act;
        region = rg;
    }

    /**
     * Remove the specified blocks from the affected blocks
     * if available
     *
     * @param remove the blocks to remove
     */
    @SuppressWarnings("unchecked")
    public void removeAffected(final Block... remove) {
        if (source instanceof Collection) {
            Collection<Block> tmpCollection = (Collection<Block>) source;
            Collection<Block> target = new ArrayList<>();

            for (Block block : tmpCollection) {
                for (Block filter : remove) {
                    if (BlockUtil.equals(block, filter)) {
                        target.add(block);
                        break;
                    }
                }
            }

            tmpCollection.removeAll(target);

            source = tmpCollection;
        }
    }

    /**
     * Get the entity that modified the block
     *
     * @return the event entity
     */
    @Nullable
    public Entity getEntity() {
        return (source instanceof Entity ? (Entity) source : null);
    }

    /**
     * Get the block source
     *
     * @return the event source block
     */
    @Nullable
    public BlockState getSource() {
        if (source instanceof Block) {
            return ((Block) source).getState();
        } else {
            if (source instanceof BlockState) {
                return (BlockState) source;
            }
        }

        return null;
    }

    /**
     * Get the affected blocks
     *
     * @return the event affected blocks
     */
    @Nullable
    public @SuppressWarnings("unchecked") Block[] getAffectedBlocks() {
        if (source instanceof Collection) {
            Collection<Block> tmpCollection = (Collection<Block>) source;
            return tmpCollection.toArray(new Block[0]);
        }

        return null;
    }

    /**
     * Get the block
     *
     * @return the event block
     */
    public BlockAction getAction() {
        return action;
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
