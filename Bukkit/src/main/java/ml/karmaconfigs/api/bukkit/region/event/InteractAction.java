package ml.karmaconfigs.api.bukkit.region.event;

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

import ml.karmaconfigs.api.bukkit.region.event.player.PlayerInteractAtRegionEvent;

/**
 * Valid {@link PlayerInteractAtRegionEvent} interact actions
 */
public enum InteractAction {

    /**
     * Default action, should be never
     * seen
     */
    UNKNOWN,
    /**
     * Left-clicked a block
     */
    LEFT_CLICK_BLOCK,
    /**
     * Right-clicked a block
     */
    RIGHT_CLICK_BLOCK,
    /**
     * Left-clicked in air
     */
    LEFT_CLICK_AIR,
    /**
     * Right-clicked in air
     */
    RIGHT_CLICK_AIR,
    /**
     * Item dropped
     */
    DROP_ITEM,
    /**
     * Item pickup-ed
     */
    PICKUP_ITEM,
    /**
     * Entity jumped on soil
     */
    JUMP_SOIL,
    /**
     * Entity press pressure plate
     */
    PRESSURE_PLATE,
    /**
     * Entity triggers redstone ore
     */
    REDSTONE_ORE,
    /**
     * Entity triggers tripwire
     */
    TRIPWIRE
}
