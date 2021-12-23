package ml.karmaconfigs.api.bukkit.region.event.block;

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

import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

/**
 * Valid {@link GenericBlockEvent} actions
 */
public enum BlockAction {
    /**
     * Valid event action
     */
    PLACE,
    /**
     * Valid event action
     */
    BREAK,
    /**
     * Valid event action
     */
    BURN,
    /**
     * Valid event action
     */
    COOK,
    /**
     * Valid event action
     */
    DAMAGE,
    /**
     * Valid event action
     */
    DISPENSE,
    /**
     * Valid event action
     */
    EXP,
    /**
     * Valid event action
     */
    EXPLODE,
    /**
     * Valid event action
     */
    FADE,
    /**
     * Valid event action
     */
    FERTILIZE,
    /**
     * Valid event action
     */
    FORM,
    /**
     * Valid event action
     */
    GROW,
    /**
     * Valid event action
     */
    IGNITE,
    /**
     * Valid event action
     */
    PHYSICS,
    /**
     * Valid event action
     */
    REDSTONE,
    /**
     * Valid event action
     */
    SPREAD,
    /**
     * Valid event action
     */
    CAN_BUILD,
    /**
     * Valid event action
     */
    DISPENSE_ARMOR,
    /**
     * Valid event action
     */
    DROP_ITEM,
    /**
     * Valid event action
     */
    FROM_TO,
    /**
     * Valid event action
     */
    MULTI_PLACE,
    /**
     * Valid event action
     */
    PISTON_EXTEND,
    /**
     * Valid event action
     */
    PISTON_RETRACT,
    /**
     * Valid event action
     */
    RECEIVE_GAME,
    /**
     * Valid event action
     */
    SHEAR_ENTITY,
    /**
     * Valid event action
     */
    COMBUST_BY,
    /**
     * Valid event action
     */
    ENTER,
    /**
     * Valid event action
     */
    CHANGE,
    /**
     * Valid event action
     */
    HARVEST,
    /**
     * Valid event action
     */
    ENTITY_FORM,
    /**
     * Valid event action
     */
    VEHICLE_COLLISION;

    /**
     * Transform the event action into
     * its corresponding class
     *
     * @return the action class
     */
    @Nullable
    public Class<?> toClass() {
        Class<?> clazz = null;

        try {
            switch (this) {
                case PLACE:
                    clazz = Class.forName("org.bukkit.event.block.BlockPlaceEvent");
                    break;
                case BREAK:
                    clazz = Class.forName("org.bukkit.event.block.BlockBreakEvent");
                    break;
                case BURN:
                    clazz = Class.forName("org.bukkit.event.block.BlockBurnEvent");
                    break;
                case COOK:
                    clazz = Class.forName("org.bukkit.event.block.BlockCookEvent");
                    break;
                case DAMAGE:
                    clazz = Class.forName("org.bukkit.event.block.BlockDamageEvent");
                    break;
                case DISPENSE:
                    clazz = Class.forName("org.bukkit.event.block.BlockDispenseEvent");
                    break;
                case EXP:
                    clazz = Class.forName("org.bukkit.event.block.BlockExpEvent");
                    break;
                case EXPLODE:
                    clazz = Class.forName("org.bukkit.event.block.BlockExplodeEvent");
                    break;
                case FADE:
                    clazz = Class.forName("org.bukkit.event.block.BlockFadeEvent");
                    break;
                case FERTILIZE:
                    clazz = Class.forName("org.bukkit.event.block.BlockFertilizeEvent");
                    break;
                case FORM:
                    clazz = Class.forName("org.bukkit.event.block.BlockFormEvent");
                    break;
                case GROW:
                    clazz = Class.forName("org.bukkit.event.block.BlockGrowEvent");
                    break;
                case IGNITE:
                    clazz = Class.forName("org.bukkit.event.block.BlockIgniteEvent");
                    break;
                case PHYSICS:
                    clazz = Class.forName("org.bukkit.event.block.BlockPhysicsEvent");
                    break;
                case REDSTONE:
                    clazz = Class.forName("org.bukkit.event.block.BlockRedstoneEvent");
                    break;
                case SPREAD:
                    clazz = Class.forName("org.bukkit.event.block.BlockSpreadEvent");
                    break;
                case CAN_BUILD:
                    clazz = Class.forName("org.bukkit.event.block.BlockCanBuildEvent");
                    break;
                case DISPENSE_ARMOR:
                    clazz = Class.forName("org.bukkit.event.block.BlockDispenseArmorEvent");
                    break;
                case DROP_ITEM:
                    clazz = Class.forName("org.bukkit.event.block.BlockDropItemEvent");
                    break;
                case FROM_TO:
                    clazz = Class.forName("org.bukkit.event.block.BlockFromToEvent");
                    break;
                case MULTI_PLACE:
                    clazz = Class.forName("org.bukkit.event.block.BlockMultiPlaceEvent");
                    break;
                case PISTON_EXTEND:
                    clazz = Class.forName("org.bukkit.event.block.BlockPistonExtendEvent");
                    break;
                case PISTON_RETRACT:
                    clazz = Class.forName("org.bukkit.event.block.BlockPistonRetractEvent");
                    break;
                case RECEIVE_GAME:
                    clazz = Class.forName("org.bukkit.event.block.BlockReceiveGameEvent");
                    break;
                case SHEAR_ENTITY:
                    clazz = Class.forName("org.bukkit.event.block.BlockShearEntityEvent");
                    break;
                case COMBUST_BY:
                    clazz = Class.forName("org.bukkit.event.entity.EntityCombustByBlockEvent");
                    break;
                case ENTER:
                    clazz = Class.forName("org.bukkit.event.entity.EntityEnterBlockEvent");
                    break;
                case CHANGE:
                    clazz = Class.forName("org.bukkit.event.entity.EntityChangeBlockEvent");
                    break;
                case HARVEST:
                    clazz = Class.forName("org.bukkit.event.player.PlayerHarvestBlockEvent");
                    break;
                case ENTITY_FORM:
                    clazz = Class.forName("org.bukkit.event.block.EntityBlockFormEvent");
                    break;
                case VEHICLE_COLLISION:
                    clazz = Class.forName("org.bukkit.event.vehicle.VehicleBlockCollisionEvent");
                    break;
                default:
                    break;
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return clazz;
    }

    /**
     * Transform the instance event into the
     * correspondent event
     *
     * @param instance the event instance
     * @param <T> the correct event
     * @return the correct event instance
     */
    @SuppressWarnings("unchecked")
    public <T> T toEvent(final Event instance) {
        Class<?> clazz = null;

        try {
            switch (this) {
                case PLACE:
                    clazz = Class.forName("org.bukkit.event.block.BlockPlaceEvent");
                    break;
                case BREAK:
                    clazz = Class.forName("org.bukkit.event.block.BlockBreakEvent");
                    break;
                case BURN:
                    clazz = Class.forName("org.bukkit.event.block.BlockBurnEvent");
                    break;
                case COOK:
                    clazz = Class.forName("org.bukkit.event.block.BlockCookEvent");
                    break;
                case DAMAGE:
                    clazz = Class.forName("org.bukkit.event.block.BlockDamageEvent");
                    break;
                case DISPENSE:
                    clazz = Class.forName("org.bukkit.event.block.BlockDispenseEvent");
                    break;
                case EXP:
                    clazz = Class.forName("org.bukkit.event.block.BlockExpEvent");
                    break;
                case EXPLODE:
                    clazz = Class.forName("org.bukkit.event.block.BlockExplodeEvent");
                    break;
                case FADE:
                    clazz = Class.forName("org.bukkit.event.block.BlockFadeEvent");
                    break;
                case FERTILIZE:
                    clazz = Class.forName("org.bukkit.event.block.BlockFertilizeEvent");
                    break;
                case FORM:
                    clazz = Class.forName("org.bukkit.event.block.BlockFormEvent");
                    break;
                case GROW:
                    clazz = Class.forName("org.bukkit.event.block.BlockGrowEvent");
                    break;
                case IGNITE:
                    clazz = Class.forName("org.bukkit.event.block.BlockIgniteEvent");
                    break;
                case PHYSICS:
                    clazz = Class.forName("org.bukkit.event.block.BlockPhysicsEvent");
                    break;
                case REDSTONE:
                    clazz = Class.forName("org.bukkit.event.block.BlockRedstoneEvent");
                    break;
                case SPREAD:
                    clazz = Class.forName("org.bukkit.event.block.BlockSpreadEvent");
                    break;
                case CAN_BUILD:
                    clazz = Class.forName("org.bukkit.event.block.BlockCanBuildEvent");
                    break;
                case DISPENSE_ARMOR:
                    clazz = Class.forName("org.bukkit.event.block.BlockDispenseArmorEvent");
                    break;
                case DROP_ITEM:
                    clazz = Class.forName("org.bukkit.event.block.BlockDropItemEvent");
                    break;
                case FROM_TO:
                    clazz = Class.forName("org.bukkit.event.block.BlockFromToEvent");
                    break;
                case MULTI_PLACE:
                    clazz = Class.forName("org.bukkit.event.block.BlockMultiPlaceEvent");
                    break;
                case PISTON_EXTEND:
                    clazz = Class.forName("org.bukkit.event.block.BlockPistonExtendEvent");
                    break;
                case PISTON_RETRACT:
                    clazz = Class.forName("org.bukkit.event.block.BlockPistonRetractEvent");
                    break;
                case RECEIVE_GAME:
                    clazz = Class.forName("org.bukkit.event.block.BlockReceiveGameEvent");
                    break;
                case SHEAR_ENTITY:
                    clazz = Class.forName("org.bukkit.event.block.BlockShearEntityEvent");
                    break;
                case COMBUST_BY:
                    clazz = Class.forName("org.bukkit.event.entity.EntityCombustByBlockEvent");
                    break;
                case ENTER:
                    clazz = Class.forName("org.bukkit.event.entity.EntityEnterBlockEvent");
                    break;
                case CHANGE:
                    clazz = Class.forName("org.bukkit.event.entity.EntityChangeBlockEvent");
                    break;
                case HARVEST:
                    clazz = Class.forName("org.bukkit.event.player.PlayerHarvestBlockEvent");
                    break;
                case ENTITY_FORM:
                    clazz = Class.forName("org.bukkit.event.block.EntityBlockFormEvent");
                    break;
                case VEHICLE_COLLISION:
                    clazz = Class.forName("org.bukkit.event.vehicle.VehicleBlockCollisionEvent");
                    break;
                default:
                    break;
            }

            if (clazz != null) {
                return (T) clazz.cast(instance);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
