package ml.karmaconfigs.api.bukkit.region.corner.util;

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
import org.bukkit.Location;

/**
 * Region corner
 */
public abstract class Corner {

    /**
     * Get the region that owns the corners
     *
     * @return the region
     */
    public abstract Cuboid getRegion();

    /**
     * Get a corner
     *
     * @param type the corner type
     * @return the corner
     */
    public abstract Location getCorner(final CornerType type);

    /**
     * Get all the corners
     *
     * @return all the corners
     */
    public abstract Location[] corners();

    /**
     * Modify the X value of the location
     *
     * @param original the location
     * @param x the new X value
     * @return the new location with the new X value
     */
    public static Location modifyX(final Location original, final double x) {
        //We don't want to modify the original location
        Location tmp = original.clone();
        tmp.setX(x);

        return tmp;
    }

    /**
     * Modify the Z value of the location
     *
     * @param original the location
     * @param z the new Z value
     * @return the new location with the new Z value
     */
    public static Location modifyZ(final Location original, final double z) {
        //We don't want to modify the original location
        Location tmp = original.clone();
        tmp.setZ(z);

        return tmp;
    }
}
