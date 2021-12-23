package ml.karmaconfigs.api.bukkit.region.event.entity;

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
import ml.karmaconfigs.api.bukkit.region.event.death.Forensics;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Entity die event
 */
public final class EntityDieAtRegionEvent extends EntityEvent {

    private final static HandlerList HANDLER_LIST = new HandlerList();

    private final Cuboid region;
    private final Forensics forensics;

    private boolean cancelled = false;

    /**
     * Initialize the entity die region event
     *
     * @param ent the entity
     * @param f the entity forensics
     * @param rg the region the entity
     *           has left
     */
    public EntityDieAtRegionEvent(final Entity ent, final Forensics f, final Cuboid rg) {
        super(ent);

        forensics = f;
        region = rg;
    }

    /**
     * Get the event forensics
     *
     * @return the event forensics
     */
    public Forensics getForensics() {
        return forensics;
    }

    /**
     * Get the region the entity died in
     *
     * @return the event region
     */
    public Cuboid getRegion() {
        return region;
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
}
