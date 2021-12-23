package ml.karmaconfigs.api.bukkit.region.corner;

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
import ml.karmaconfigs.api.bukkit.region.corner.util.Corner;
import ml.karmaconfigs.api.bukkit.region.corner.util.CornerType;
import org.bukkit.Location;

/**
 * Bottom corner
 */
public final class BottomCorner extends Corner {

    private final Location[] corners;
    private final Cuboid region;

    /**
     * Initialize the bottom corner
     *
     * @param r the region
     */
    public BottomCorner(final Cuboid r) {
        region = r;

        Location bottom = region.getBottom();
        Location top = region.getTop();

        /*
        Let's assume there are two positions, A and B, where X is the highest
        and Z is the lowest.

        A has the value of:
        X: 15
        Y: 15
        Z: 15

        B has the value of:
        X: 0
        Y: 0
        Z: 0

        Both of them creating a square

        To get the corners we basically do a
        calculation between these coordinates.

        X ( left and right )
        Y ( up and down )
        Z ( forward and back )
         */

        //Second corner will be the X modified version of the origin
        Location secondCorner = modifyX(bottom, top.getX());

        //Third corner will be the Z modified version of the origin
        Location thirdCorner = modifyZ(bottom, top.getZ());

        //Last corner will be the X and Y modified version of the origin
        Location fourthCorner = modifyZ(modifyX(bottom, top.getX()), top.getZ());

        //See how first value is the original location
        corners = new Location[]{bottom, secondCorner, thirdCorner, fourthCorner};
    }

    /**
     * Get the region that owns the corners
     *
     * @return the region
     */
    @Override
    public Cuboid getRegion() {
        return region;
    }

    /**
     * Get a corner
     *
     * @param type the corner type
     * @return the corner
     */
    @Override
    public Location getCorner(CornerType type) {
        switch (type) {
            case FIRST:
                return corners[0];
            case SECOND:
                return corners[1];
            case THIRD:
                return corners[2];
            case FOURTH:
                return corners[3];
            default:
                return null;
        }
    }

    /**
     * Get all the corners
     *
     * @return all the corners
     */
    @Override
    public Location[] corners() {
        return corners;
    }
}
