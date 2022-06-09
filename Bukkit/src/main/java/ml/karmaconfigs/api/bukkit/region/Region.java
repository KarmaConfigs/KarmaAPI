package ml.karmaconfigs.api.bukkit.region;

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

import ml.karmaconfigs.api.bukkit.KarmaPlugin;
import ml.karmaconfigs.api.bukkit.region.corner.BottomCorner;
import ml.karmaconfigs.api.bukkit.region.corner.TopCorner;
import ml.karmaconfigs.api.bukkit.region.corner.util.Corner;
import ml.karmaconfigs.api.bukkit.region.error.RegionNotFound;
import ml.karmaconfigs.api.bukkit.region.wall.RegionWall;
import ml.karmaconfigs.api.bukkit.region.wall.util.Wall;
import ml.karmaconfigs.api.bukkit.region.wall.util.WallType;
import ml.karmaconfigs.api.common.karma.file.KarmaMain;
import ml.karmaconfigs.api.common.karma.file.element.KarmaElement;
import ml.karmaconfigs.api.common.karma.file.element.KarmaObject;
import ml.karmaconfigs.api.common.utils.security.token.TokenGenerator;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/**
 * Karma region
 */
@SuppressWarnings("unused")
public class Region extends Cuboid implements Serializable {

    /**
     * The region name
     */
    private final String name;
    /**
     * The region internal name
     */
    private final String internal;

    /**
     * The region token
     */
    private final String token = TokenGenerator.generateLiteral(16);

    /**
     * Region block x min
     */
    private final int xMin;
    /**
     * Region block x max
     */
    private final int xMax;
    /**
     * Region block y min
     */
    private final int yMin;
    /**
     * Region block y max
     */
    private final int yMax;
    /**
     * Region block z min
     */
    private final int zMin;
    /**
     * Region block z max
     */
    private final int zMax;

    /**
     * Region centered block x min
     */
    private final double xMinCentered;
    /**
     * Region centered block x max
     */
    private final double xMaxCentered;
    /**
     * Region centered block y min
     */
    private final double yMinCentered;
    /**
     * Region centered block y max
     */
    private final double yMaxCentered;
    /**
     * Region centered z block min
     */
    private final double zMinCentered;
    /**
     * Region centered z block max
     */
    private final double zMaxCentered;

    /**
     * Region unique id
     */
    private final UUID uniqueId;
    /**
     * Region world id
     */
    private final UUID worldId;

    /**
     * Initialize the region
     *
     * @param owner the region owner
     * @param source the region source
     */
    Region(final KarmaPlugin owner, final Region source) {
        super(owner);

        name = source.name;
        internal = source.internal;

        xMin = source.xMin;
        xMax = source.xMax;
        yMin = source.yMin;
        yMax = source.yMax;
        zMin = source.zMin;
        zMax = source.zMax;

        xMinCentered = source.xMinCentered;
        xMaxCentered = source.xMaxCentered;
        yMinCentered = source.yMinCentered;
        yMaxCentered = source.yMaxCentered;
        zMinCentered = source.zMinCentered;
        zMaxCentered = source.zMaxCentered;

        uniqueId = source.uniqueId;
        worldId = source.worldId;
    }

    /**
     * Initialize the region
     *
     * @param owner the region owner
     * @param n the region name
     * @param point1 the first point
     * @param point2 the second point
     * @throws IllegalArgumentException if the locations doesn't have the same world
     * or if any of the worlds are null
     */
    public Region(final KarmaPlugin owner, final String n, final Location point1, final Location point2) throws IllegalArgumentException {
        super(owner);
        name = n;

        StringBuilder internalNameBuilder = new StringBuilder();
        String stripped = StringUtils.stripColor(n);
        boolean lastWasSpace = false;
        for (int i = 0; i < stripped.length(); i++) {
            char character = stripped.charAt(i);
            if (Character.isLetterOrDigit(character) || character == '_') {
                internalNameBuilder.append(character);
                lastWasSpace = character == '_';
            } else {
                if (Character.isSpaceChar(character)) {
                    if (!lastWasSpace) {
                        internalNameBuilder.append('_');
                        lastWasSpace = true;
                    }
                }
            }
        }
        internal = internalNameBuilder.toString().toLowerCase();
        uniqueId = UUID.nameUUIDFromBytes(("Region:" + internal).getBytes());

        xMin = Math.min(point1.getBlockX(), point2.getBlockX());
        xMax = Math.max(point1.getBlockX(), point2.getBlockX());
        yMin = Math.min(point1.getBlockY(), point2.getBlockY());
        yMax = Math.max(point1.getBlockY(), point2.getBlockY());
        zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
        zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());

        xMinCentered = this.xMin + 0.5;
        xMaxCentered = this.xMax + 0.5;
        yMinCentered = this.yMin + 0.5;
        yMaxCentered = this.yMax + 0.5;
        zMinCentered = this.zMin + 0.5;
        zMaxCentered = this.zMax + 0.5;

        if (point1.getWorld() != null && point2.getWorld() != null && point1.getWorld().getUID().equals(point2.getWorld().getUID())) {
            worldId = point1.getWorld().getUID();
        } else {
            throw new IllegalArgumentException("Cannot initialize because point1 or point2 location world's are null or not the same");
        }
    }

    /**
     * Get the region unique id
     *
     * @return the region unique id
     */
    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    /**
     * Get the region name
     *
     * @return the region name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the region internal name
     *
     * @return the region internal name
     */
    @Override
    public String getInternalName() {
        return internal;
    }

    /**
     * Get if the region already exists
     *
     * @param owner the region owner
     * @return if the region already exists
     */
    @Override
    public boolean exists(KarmaPlugin owner) {
        Path regionFile = owner.getDataPath().resolve("cache").resolve("regions").resolve(token + ".region");
        if (Files.exists(regionFile)) {
            KarmaMain file = new KarmaMain(regionFile);
            if (file.isSet("region")) {
                KarmaElement result = file.get("region");
                if (result.isString()) {
                    Object serialized = StringUtils.load(result.getObjet().getString());

                    return serialized instanceof Region;
                }
            }
        }

        return false;
    }


    /**
     * Get the region blocks
     *
     * @return the region blocks
     */
    @Override
    public Iterator<Block> getBlocks() {
        ArrayList<Block> blocks = new ArrayList<>(getBlocksAmount());
        World world = Bukkit.getWorld(worldId);

        if (world != null) {
            for (int x = this.xMin; x <= this.xMax; ++x) {
                for (int y = this.yMin; y <= this.yMax; ++y) {
                    for (int z = this.zMin; z <= this.zMax; ++z) {
                        Block block = world.getBlockAt(x, y, z);
                        blocks.add(block);
                    }
                }
            }
        }

        return blocks.iterator();
    }

    /**
     * Get the region center location
     *
     * @return the region center location
     */
    @Override
    public Location getCenter() {
        World world = Bukkit.getWorld(worldId);

        return new Location(world,
                (double) (this.xMax - this.xMin) / 2 + this.xMin,
                (double) (this.yMax - this.yMin) / 2 + this.yMin,
                (double) (this.zMax - this.zMin) / 2 + this.zMin);
    }

    /**
     * Get the region size
     *
     * @return the region size
     */
    @Override
    public double getSize() {
        return getBottom().distance(getTop());
    }

    /**
     * Get the region size
     *
     * @return the region size
     */
    @Override
    public double getSizeSquared() {
        return getBottom().distanceSquared(getTop());
    }

    /**
     * Get the top location
     *
     * @return the top location
     */
    @Override
    public Location getTop() {
        World world = Bukkit.getWorld(worldId);

        return new Location(world, this.xMax, this.yMax, this.zMax);
    }

    /**
     * Get the bottom location
     *
     * @return the bottom location
     */
    @Override
    public Location getBottom() {
        World world = Bukkit.getWorld(worldId);

        return new Location(world, this.xMin, this.yMin, this.zMin);
    }

    /**
     * Get the region world
     *
     * @return the region world
     */
    @Override
    public World getWorld() {
        return Bukkit.getWorld(worldId);
    }

    /**
     * Get the amount of blocks inside the region
     *
     * @return the region blocks
     */
    @Override
    public int getBlocksAmount() {
        return getHeight() * getWidth() * getLength();
    }

    /**
     * Get the region height
     *
     * @return the region height
     */
    @Override
    public int getHeight() {
        return yMax - yMin + 1;
    }

    /**
     * Get the region width
     *
     * @return the region width
     */
    @Override
    public int getWidth() {
        return xMax - xMin + 1;
    }

    /**
     * Get the region length
     *
     * @return the region length
     */
    @Override
    public int getLength() {
        return zMax - zMin + 1;
    }

    /**
     * Get if the entity is inside the
     * region
     *
     * @param entity the entity
     * @return if the entity is inside the
     * region
     */
    @Override
    public boolean isInside(final Entity entity) {
        Location loc = entity.getLocation();
        World world = Bukkit.getWorld(worldId);

        return loc.getWorld() == world
                && loc.getBlockX() >= xMin
                && loc.getBlockX() <= xMax
                && loc.getBlockY() >= yMin
                && loc.getBlockY() <= yMax
                && loc.getBlockZ() >= zMin
                && loc.getBlockZ() <= zMax;
    }

    /**
     * Get if the block is inside the
     * region
     *
     * @param block the block
     * @return if the block is inside the
     * region
     */
    @Override
    public boolean isInside(final Block block) {
        World world = Bukkit.getWorld(worldId);

        return block.getWorld() == world
                && block.getX() >= xMin
                && block.getX() <= xMax
                && block.getY() >= yMin
                && block.getY() <= yMax
                && block.getZ() >= zMin
                && block.getZ() <= zMax;
    }

    /**
     * Get if the entity is inside the
     * region
     *
     * @param entity the entity
     * @param marge  the marge out of
     *               region frontier
     * @return if the entity is inside
     * the region
     */
    @Override
    public boolean isInside(final Entity entity, final double marge) {
        Location loc = entity.getLocation();
        World world = Bukkit.getWorld(worldId);

        return loc.getWorld() == world
                && loc.getX() >= xMinCentered - marge
                && loc.getX() <= xMaxCentered + marge
                && loc.getY() >= yMinCentered - marge
                && loc.getY() <= yMaxCentered + marge
                && loc.getZ() >= zMinCentered - marge
                && loc.getZ() <= zMaxCentered + marge;
    }

    /**
     * Get the region token, this is another
     * unique identifier for the region ( UUID
     * is also used as identifier ).
     * <p>
     * The difference between this one and UUID one
     * is that this token is randomly generated, and
     * UUID name-base generated
     *
     * @return the region token
     */
    @Override
    public String getToken() {
        return token;
    }

    /**
     * Get the top corners of the region
     *
     * @return the top region corners
     */
    @Override
    public Corner getTopCorners() {
        return new TopCorner(this);
    }

    /**
     * Get the bottom corners of the region
     *
     * @return the bottom region corners
     */
    @Override
    public Corner getBottomCorners() {
        return new BottomCorner(this);
    }

    /**
     * Get the region walls
     *
     * @param type the wall type
     * @return the region walls
     */
    @Override
    public Wall getWalls(final WallType type) {
        return new RegionWall(type, this);
    }

    /**
     * Save the region to memory
     *
     * @param owner the region owner
     */
    @Override
    public void saveToMemory(final KarmaPlugin owner) {
        if (!exists(owner)) {
            Path regionFile = owner.getDataPath().resolve("cache").resolve("regions").resolve(token + ".region");

            KarmaMain file = new KarmaMain(regionFile);
            file.set("region", new KarmaObject(StringUtils.serialize(this)));
        }
    }

    /**
     * Load the region from the memory location
     *
     * @param owner the region owner
     * @param token the region token
     * @return the region
     * @throws RegionNotFound if the region couldn't be loaded
     */
    public static Region fromMemory(final KarmaPlugin owner, final String token) throws RegionNotFound {
        Path regionFile = owner.getDataPath().resolve("cache").resolve("regions").resolve(token + ".region");

        Region region = null;
        String reason = "";

        if (Files.exists(regionFile)) {
            KarmaMain file = new KarmaMain(regionFile);
            if (file.isSet("region")) {
                KarmaElement result = file.get("region", null);
                if (result.isString()) {
                    Object serialized = StringUtils.load(result.getObjet().getString());

                    if (serialized instanceof Region) {
                        region = (Region) serialized;
                    } else {
                        reason = "Region file does not contain valid region data";
                    }
                }
            } else {
                reason = "Region file does not contain region data";
            }
        } else {
            reason = "Region file does not exist";
        }

        if (region != null) {
            return new Region(owner, region);
        } else {
            throw new RegionNotFound(owner, token, reason);
        }
    }
}
