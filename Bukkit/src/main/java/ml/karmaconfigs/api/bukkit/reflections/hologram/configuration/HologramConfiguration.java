package ml.karmaconfigs.api.bukkit.reflections.hologram.configuration;

import org.bukkit.Axis;
import org.bukkit.util.Vector;

import java.io.Serializable;

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

/**
 * KarmaHologram configuration
 */
public final class HologramConfiguration implements Serializable {

    double offsetX = 0D;
    double offsetY = 2D;
    double offsetZ = 0D;

    double lineSeparation = 0.25d;
    double itemSeparation = 0.75d;

    boolean autoCenter = true;
    boolean changeVelocity = true;

    double itemVelocityX = 0D;
    double itemVelocityY = 0D;
    double itemVelocityZ = 0D;

    /**
     * Get a new hologram configuration instance
     * with the default values
     */
    public HologramConfiguration() {}

    /**
     * Initialize the hologram configuration
     *
     * @param offX the hologram offset x
     * @param offY the hologram offset y
     * @param offZ the hologram offset z
     * @param lSep the hologram text separation size
     * @param iSep the hologram item separation size
     * @param autoCent the hologram auto center status
     * @param cVelocity the hologram item velocity changer status
     * @param itemVel the hologram item velocity
     */
    public HologramConfiguration(final double offX, final double offY, final double offZ, final double lSep, final double iSep, final boolean autoCent, final boolean cVelocity, final Vector itemVel) {
        offsetX = offX;
        offsetY = offY;
        offsetZ = offZ;
        lineSeparation = lSep;
        itemSeparation = iSep;
        autoCenter = autoCent;
        changeVelocity = cVelocity;
        itemVelocityX = itemVel.getX();
        itemVelocityY = itemVel.getY();
        itemVelocityZ = itemVel.getZ();
    }

    /**
     * Set the new offset configuration
     * for the specified axis
     *
     * @param axis the axis
     * @param newOffset the new offset for the
     *                  specified axis
     */
    public final void setOffset(final Axis axis, final double newOffset) {
        switch (axis) {
            case X:
                offsetX = newOffset;
                break;
            case Y:
                offsetY = newOffset;
                break;
            case Z:
                offsetZ = newOffset;
                break;
        }
    }

    /**
     * Set the new offset configuration
     *
     * @param x the X offset
     * @param y the Y offset
     * @param z the Z offset
     */
    public final void setOffset(final double x, final double y, final double z) {
        offsetX = x;
        offsetY = y;
        offsetZ = z;
    }

    /**
     * Set the new separation size between text and item
     * lines
     *
     * @param separationSize the new separation size
     */
    public final void lineSeparation(final double separationSize) {
        lineSeparation = separationSize;
    }

    /**
     * Set the new separation size between text and item lines
     *
     * @param separationSize the new separation size
     */
    public final void itemSeparation(final double separationSize) {
        itemSeparation = separationSize;
    }

    /**
     * Set if the hologram should try to center
     * himself at block center when created
     *
     * @param status the auto center status
     */
    public final void centerAutomatically(final boolean status) {
        autoCenter = status;
    }

    /**
     * Set if the hologram item velocity should
     * be changed with the current ones
     *
     * @param status the item velocity changer status
     */
    public final void setCustomVelocity(final boolean status) {
        changeVelocity = status;
    }

    /**
     * Set the new item velocity configuration
     *
     * @param axis the axis
     * @param newOffset the new velocity for the
     *                  specified axis
     */
    public final void setItemVelocity(final Axis axis, final double newOffset) {
        switch (axis) {
            case X:
                itemVelocityX = newOffset;
                break;
            case Y:
                itemVelocityY = newOffset;
                break;
            case Z:
                itemVelocityZ = newOffset;
                break;
        }
    }

    /**
     * Set the new item velocity configuration
     *
     * @param x the X velocity
     * @param y the Y velocity
     * @param z the Z velocity
     */
    public final void setItemVelocity(final double x, final double y, final double z) {
        itemVelocityX = x;
        itemVelocityY = y;
        itemVelocityZ = z;
    }

    /**
     * Set the new item velocity configuration
     *
     * @param velocity the vector velocity
     */
    public final void setItemVelocity(final Vector velocity) {
        itemVelocityX = velocity.getX();
        itemVelocityY = velocity.getY();
        itemVelocityZ = velocity.getZ();
    }

    /**
     * Get the offset configuration
     *
     * @return the offset configuration
     */
    public final OffsetConfiguration getOffsetConfiguration() {
        return new OffsetConfiguration(this);
    }

    /**
     * Get the hologram separation between lines
     *
     * @return the hologram separation between lines
     */
    public final double getLineSeparation() {
        return lineSeparation;
    }

    /**
     * Get the hologram separation between items
     *
     * @return the hologram separation between items
     */
    public final double getItemSeparation() {
        return itemSeparation;
    }

    /**
     * Get if the hologram should center himself
     *
     * @return if the hologram centers himself
     */
    public final boolean isAutoCenter() {
        return autoCenter;
    }

    /**
     * Get if the hologram should change item
     * velocity
     *
     * @return if the hologram changes item velocity
     */
    public final boolean changeVelocity() {
        return changeVelocity;
    }

    /**
     * Get the item velocity configuration as vector
     *
     * @return the item velocity vector
     */
    public final Vector getItemVelocity() {
        return new Vector(itemVelocityX, itemVelocityY, itemVelocityZ);
    }
}
