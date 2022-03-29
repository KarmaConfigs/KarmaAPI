package ml.karmaconfigs.api.bukkit.region.event.death;

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

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Karma forensics for region
 * API
 */
public final class Forensics {

    private final Entity killer;
    private final Block block;
    private final ItemStack item;
    private final EntityDamageEvent.DamageCause cause;
    private final double damage;

    /**
     * Initialize the forensics
     *
     * @param k the killer
     * @param b another killer reason
     * @param i the item that caused the death
     * @param c the damage cause
     * @param d the damage amount
     */
    public Forensics(final Entity k, final Block b, ItemStack i, final EntityDamageEvent.DamageCause c, final double d) {
        killer = k;
        block = b;
        item = i;
        cause = c;
        damage = d;
    }

    /**
     * Get the entity who killed the main
     * entity
     *
     * @return the entity killer
     */
    @Nullable
    public Entity getKiller() {
        return killer;
    }

    /**
     * Get the physical reason of
     * the main entity death
     *
     * @return the entity block death
     */
    @Nullable
    public Block getPhysical() {
        return block;
    }

    /**
     * Get the weapon that killed the main
     * entity
     *
     * @return the weapon that the killer
     * was using
     */
    @Nullable
    public ItemStack getWeapon() {
        return item;
    }

    /**
     * Get the death info cause
     *
     * @return the death info cause
     */
    @NotNull
    public EntityDamageEvent.DamageCause getCause() {
        return cause;
    }

    /**
     * Get the last damage caused to the main
     * entity
     *
     * @return the last damage caused to the
     * entity
     */
    public double getDamage() {
        return damage;
    }
}
