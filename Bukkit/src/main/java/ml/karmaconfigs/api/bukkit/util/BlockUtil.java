package ml.karmaconfigs.api.bukkit.util;

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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Karma block utilities
 */
public final class BlockUtil {

    /**
     * Get the blocks between two locations
     *
     * @param loc1 the first location
     * @param loc2 the second location
     * @return the blocks between the two locations
     * @throws IllegalArgumentException if some worlds are null or does not match
     */
    public static Set<Block> getBlocksBetween(final Location loc1, final Location loc2) throws IllegalArgumentException {
        World world1 = loc1.getWorld();
        World world2 = loc2.getWorld();

        if (world1 != null && world2 != null && world1.getUID().equals(world2.getUID())) {
            Set<Block> blocks = Collections.newSetFromMap(new ConcurrentHashMap<>());

            //Next we will name each coordinate
            int x1 = loc1.getBlockX();
            int y1 = loc1.getBlockY();
            int z1 = loc1.getBlockZ();

            int x2 = loc2.getBlockX();
            int y2 = loc2.getBlockY();
            int z2 = loc2.getBlockZ();

            //Then we create the following integers
            int xMin, yMin, zMin;
            int xMax, yMax, zMax;
            int x, y, z;

            //Now we need to make sure xMin is always lower then xMax
            if (x1 > x2) { //If x1 is a higher number then x2
                xMin = x2;
                xMax = x1;
            } else {
                xMin = x1;
                xMax = x2;
            }

            //Same with Y
            if (y1 > y2) {
                yMin = y2;
                yMax = y1;
            } else {
                yMin = y1;
                yMax = y2;
            }

            //And Z
            if (z1 > z2) {
                zMin = z2;
                zMax = z1;
            } else {
                zMin = z1;
                zMax = z2;
            }

            //Now it's time for the loop
            for (x = xMin; x <= xMax; x++) {
                for (y = yMin; y <= yMax; y++) {
                    for (z = zMin; z <= zMax; z++) {
                        Block b = new Location(loc1.getWorld(), x, y, z).getBlock();
                        blocks.add(b);
                    }
                }
            }

            return blocks;
        } else {
            throw new IllegalArgumentException("Cannot get blocks between two locations because one or both of them world is null or does not match ( not in the same world )");
        }
    }

    /**
     * Get the lowest block location
     *
     * @param location the start location
     * @param acceptLiquid accept liquid block as
     *                     a valid block
     * @return the lowest block
     */
    public static Block getLowestBlockAt(final Location location, final boolean acceptLiquid) {
        int y = (int) location.getY();
        Block block = location.getBlock().getRelative(BlockFace.DOWN);

        Location tmp = block.getLocation();
        if (!acceptLiquid) {
            do {
                tmp.setY(--y);
                block = tmp.getBlock();
            } while ((block.getType().equals(Material.AIR) || block.getType().equals(Material.WATER) || block.getType().equals(Material.LAVA)) && y > -60);
        } else {
            do {
                tmp.setY(--y);
                block = tmp.getBlock();
            } while (block.getType().equals(Material.AIR) && y > -60);
        }

        return block;
    }

    /**
     * Get if the blocks are the same
     *
     * @param block1 the first block
     * @param block2 the second block
     * @return if both blocks are the same based on its location
     */
    public static boolean equals(final Block block1, final Block block2) {
        Location loc1 = block1.getLocation();
        final Location other = block2.getLocation();

        World world = (loc1.getWorld() == null) ? null : loc1.getWorld();
        World otherWorld = (other.getWorld() == null) ? null : other.getWorld();
        if (!Objects.equals(world, otherWorld)) {
            return false;
        }
        if (Double.doubleToLongBits(loc1.getX()) != Double.doubleToLongBits(other.getX())) {
            return false;
        }
        if (Double.doubleToLongBits(loc1.getY()) != Double.doubleToLongBits(other.getY())) {
            return false;
        }

        return Double.doubleToLongBits(loc1.getZ()) == Double.doubleToLongBits(other.getZ());
    }

    /**
     * Get if the blocks are the same
     *
     * @param block1 the first block
     * @param block2 the second block
     * @return if both blocks are the same based on its location, pitch and yaw
     */
    public static boolean strictEquals(final Block block1, final Block block2) {
        Location loc1 = block1.getLocation();
        final Location other = block2.getLocation();

        World world = (loc1.getWorld() == null) ? null : loc1.getWorld();
        World otherWorld = (other.getWorld() == null) ? null : other.getWorld();
        if (!Objects.equals(world, otherWorld)) {
            return false;
        }
        if (Double.doubleToLongBits(loc1.getX()) != Double.doubleToLongBits(other.getX())) {
            return false;
        }
        if (Double.doubleToLongBits(loc1.getY()) != Double.doubleToLongBits(other.getY())) {
            return false;
        }
        if (Double.doubleToLongBits(loc1.getZ()) != Double.doubleToLongBits(other.getZ())) {
            return false;
        }
        if (Float.floatToIntBits(loc1.getPitch()) != Float.floatToIntBits(other.getPitch())) {
            return false;
        }

        return Float.floatToIntBits(loc1.getYaw()) == Float.floatToIntBits(other.getYaw());
    }
}
