package ml.karmaconfigs.api.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
 * Serialize a location or load a serialized location
 */
public final class SerializableLocation implements Serializable {

    private final double x,y,z;
    private final float yaw,pitch;
    private final String worldName;

    /**
     * Initialize the serializable location
     *
     * @param posX the position x
     * @param posY the position y
     * @param posZ the position z
     * @param locYaw the location yaw
     * @param locPitch the location pitch
     * @param world the world
     */
    public SerializableLocation(final double posX, final double posY, final double posZ, final float locYaw, final float locPitch, final @Nullable World world) {
        x = posX;
        y = posY;
        z = posZ;
        yaw = locYaw;
        pitch = locPitch;
        if (world == null)
            worldName = "";
        else
            worldName = world.getName();
    }

    /**
     * Get the location X
     *
     * @return the location X
     */
    public final double getX() {
        return x;
    }

    /**
     * Get the location Y
     *
     * @return the location Y
     */
    public final double getY() {
        return y;
    }

    /**
     * Get the location Z
     *
     * @return the location Z
     */
    public final double getZ() {
        return z;
    }

    /**
     * Get the location yaw
     *
     * @return the location yaw
     */
    public final double getYaw() {
        return yaw;
    }

    /**
     * Get the location pitch
     *
     * @return the location pitch
     */
    public final double getPitch() {
        return pitch;
    }

    /**
     * Get the location world
     *
     * @return the location world
     */
    @Nullable
    public final World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    /**
     * Parse the serializable location
     * to location
     *
     * @return the serialized location
     */
    @NotNull
    public final Location toLocation() {
        Location location = new Location(getWorld(), x, y, z);
        location.setYaw(yaw);
        location.setPitch(pitch);

        return location;
    }
}
