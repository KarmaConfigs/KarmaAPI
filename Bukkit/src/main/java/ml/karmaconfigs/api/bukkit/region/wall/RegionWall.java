package ml.karmaconfigs.api.bukkit.region.wall;

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
import ml.karmaconfigs.api.bukkit.region.wall.util.Wall;
import ml.karmaconfigs.api.bukkit.region.wall.util.WallType;
import ml.karmaconfigs.api.bukkit.util.BlockUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Karma region walls utilities
 */
public final class RegionWall extends Wall {

    private final WallType type;
    private final Cuboid region;

    /**
     * Initialize the region wall
     *
     * @param t the wall type
     * @param r the region
     */
    public RegionWall(final WallType t, final Cuboid r) {
        type = t;
        region = r;
    }

    /**
     * Get the wall type
     *
     * @return the wall type
     */
    @Override
    public WallType getType() {
        return type;
    }

    /**
     * Get the region that owns the walls
     *
     * @return the region
     */
    @Override
    public Cuboid getRegion() {
        return region;
    }

    /**
     * Get the wall blocks
     *
     * @return the wall blocks
     */
    @Override
    public Set<Block> getWall() {
        Location bottom = region.getBottom();
        Location top = region.getTop();

        switch (type) {
            case LEFT:
                if (bottom.getX() < top.getX()) {
                    //Bottom location is on the left side
                    top.setX(bottom.getX());
                } else {
                    //Bottom location is on the right side
                    bottom.setX(top.getX());
                }
                break;
            case FRONT:
                if (bottom.getZ() < top.getZ()) {
                    //Bottom location is behind top location
                    bottom.setZ(top.getZ());
                } else {
                    //Bottom location is ahead top location
                    top.setZ(bottom.getZ());
                }
                break;
            case RIGHT:
                if (bottom.getX() < top.getX()) {
                    //Bottom location is on the left side
                    bottom.setX(top.getX());
                } else {
                    //Bottom location is on the right side
                    top.setX(bottom.getX());
                }
                break;
            case BACK:
                if (bottom.getZ() < top.getZ()) {
                    //Bottom location is behind top location
                    top.setZ(bottom.getZ());
                } else {
                    //Bottom location is ahead top location
                    bottom.setZ(top.getZ());
                }
                break;
            case BOTTOM:
                top.setY(bottom.getY());
                break;
            case TOP:
                bottom.setY(top.getY());
                break;
            default:
                break;
        }

        return BlockUtil.getBlocksBetween(bottom, top);
    }

    /**
     * Get nearby entities in the wall
     *
     * @param range the search radius
     * @return the near wall entities
     */
    @Override
    public Set<Entity> getNear(final double range) {
        Set<Entity> entities = new HashSet<>();

        getWall().forEach((block) -> {
            World world = block.getWorld();
            Location location = block.getLocation();

            entities.addAll(world.getNearbyEntities(location, range, range, range));
        });

        return entities;
    }

    /**
     * Get nearby entities in the wall
     *
     * @param range  the search radius
     * @param filter the entities to ignore
     * @return the near wall entities
     */
    @Override
    public Set<Entity> getNear(final double range, final EntityType... filter) {

        Set<Entity> entities = new HashSet<>();
        Set<EntityType> ignore = new HashSet<>(Arrays.asList(filter));

        getWall().forEach((block) -> {
            World world = block.getWorld();
            Location location = block.getLocation();

            world.getNearbyEntities(location, range, range, range).forEach((entity) -> {
                if (!ignore.contains(entity.getType())) {
                    entities.add(entity);
                }
            });
        });


        return entities;
    }
}
