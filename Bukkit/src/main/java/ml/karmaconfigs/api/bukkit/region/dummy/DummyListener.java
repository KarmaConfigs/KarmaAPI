package ml.karmaconfigs.api.bukkit.region.dummy;

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

import com.bergerkiller.bukkit.common.events.EntityMoveEvent;
import ml.karmaconfigs.api.bukkit.region.Cuboid;
import ml.karmaconfigs.api.bukkit.region.event.InteractAction;
import ml.karmaconfigs.api.bukkit.region.event.death.Forensics;
import ml.karmaconfigs.api.bukkit.region.event.entity.*;
import ml.karmaconfigs.api.bukkit.region.event.player.PlayerActionWithRegionEvent;
import ml.karmaconfigs.api.bukkit.region.event.player.PlayerInteractAtRegionEvent;
import ml.karmaconfigs.api.common.utils.ConcurrentList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dummy listener for regions entity join/leave
 * event
 */
@SuppressWarnings({"unused"})
public class DummyListener implements Listener {

    private final static Map<Cuboid, Set<Entity>> entity_cache = new ConcurrentHashMap<>();
    private final static Map<UUID, Object> damage_data = new ConcurrentHashMap<>();
    private final static Map<ItemStack, UUID> drop_data = new ConcurrentHashMap<>();

    private final static List<UUID> items_cache = new ConcurrentList<>();
    private final static List<UUID> drop_handled = new ConcurrentList<>();

    private final Plugin plugin;

    /**
     * Initialize the dummy listener
     *
     * @param channel the dummy plugin channel
     */
    public DummyListener(final Plugin channel) {
        plugin = channel;

        //Remove invalid entities cache every 5 minutes
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (UUID id : items_cache) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    Entity entity = plugin.getServer().getEntity(id);
                    if (entity == null || !entity.isValid() || entity.isDead()) {
                        items_cache.remove(id);
                    }
                });
            }
        }, 0, 20 * 300);
    }

    /**
     * Event listener
     *
     * @param e the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(EntityMoveEvent e) {
        Entity entity = e.getEntity();

        Cuboid.getRegions().forEach((region) -> {
            Set<Entity> cached = entity_cache.getOrDefault(region, Collections.newSetFromMap(new ConcurrentHashMap<>()));
            Set<Entity> insert = new HashSet<>();
            Set<Entity> remove = new HashSet<>();

            boolean changes = false;
            if (region.isInside(entity)) {
                if (!cached.contains(entity)) {
                    EntityJoinRegionEvent event = new EntityJoinRegionEvent(entity, region);
                    Bukkit.getServer().getPluginManager().callEvent(event);

                    insert.add(entity);

                    changes = true;
                }
            } else {
                if (cached.contains(entity)) {
                    EntityLeaveRegionEvent event = new EntityLeaveRegionEvent(entity, region);
                    Bukkit.getServer().getPluginManager().callEvent(event);

                    remove.add(entity);

                    changes = true;
                }
            }

            if (changes) {
                cached.removeAll(remove);
                cached.addAll(insert);

                entity_cache.put(region, cached);
            }
        });
    }

    /**
     * Event listener
     *
     * @param e the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent e) {
        if (!e.isCancelled()) {
            Player entity = e.getPlayer();

            Cuboid.getRegions().forEach((region) -> {
                    Set<Entity> cached = entity_cache.getOrDefault(region, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                    Set<Entity> insert = new HashSet<>();
                    Set<Entity> remove = new HashSet<>();

                boolean changes = false;
                if (region.isInside(entity)) {
                    if (!cached.contains(entity)) {
                        EntityJoinRegionEvent event = new EntityJoinRegionEvent(entity, region);
                        Bukkit.getServer().getPluginManager().callEvent(event);

                        insert.add(entity);

                        changes = true;
                    }
                } else {
                    if (cached.contains(entity)) {
                        EntityLeaveRegionEvent event = new EntityLeaveRegionEvent(entity, region);
                        Bukkit.getServer().getPluginManager().callEvent(event);

                        remove.add(entity);

                        changes = true;
                    }
                }

                if (changes) {
                    cached.removeAll(remove);
                    cached.addAll(insert);

                    entity_cache.put(region, cached);
                }
            });
        }
    }

    /**
     * Event listener
     *
     * @param e the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractAtEntity(PlayerInteractEntityEvent e) {
        if (!e.isCancelled()) {
            Player player = e.getPlayer();
            Entity target = e.getRightClicked();

            Cuboid.getRegions().forEach((region) -> {
                if (region.isInside(target)) {
                    PlayerInteractAtRegionEvent event = new PlayerInteractAtRegionEvent(target, player, region);
                    Bukkit.getServer().getPluginManager().callEvent(event);

                    e.setCancelled(event.isCancelled());
                }
            });
        }
    }

    /**
     * Event listener
     *
     * @param e the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("deprecation")
    public void onInteractPlayer(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();

        if (block != null && !drop_handled.contains(player.getUniqueId())) {
            Cuboid.getRegions().forEach((region) -> {
                if (region.isInside(block)) {
                    Event.Result blockUse = e.useInteractedBlock();
                    Event.Result itemUse = e.useItemInHand();

                    if (!blockUse.equals(Event.Result.DENY) && !itemUse.equals(Event.Result.DENY)) {
                        InteractAction action = InteractAction.UNKNOWN;

                        switch (e.getAction()) {
                            case RIGHT_CLICK_BLOCK:
                                action = InteractAction.RIGHT_CLICK_BLOCK;
                                break;
                            case LEFT_CLICK_BLOCK:
                                action = InteractAction.LEFT_CLICK_BLOCK;
                                break;
                            case LEFT_CLICK_AIR:
                                action = InteractAction.LEFT_CLICK_AIR;
                                break;
                            case RIGHT_CLICK_AIR:
                                action = InteractAction.RIGHT_CLICK_AIR;
                                break;
                            case PHYSICAL:
                                BlockState state = block.getState();

                                //If we can get the crop states, then it's a soil item
                                try {
                                    Crops crop = (Crops) state.getData();
                                    action = InteractAction.JUMP_SOIL;
                                } catch (Throwable ex) {
                                    //Newer minecraft versions...
                                    if (state.getBlockData() instanceof Ageable) {
                                        Ageable ageable = (Ageable) state.getBlockData();
                                        action = InteractAction.JUMP_SOIL;
                                    }
                                }

                                if (action.equals(InteractAction.UNKNOWN)) {
                                    if (block.getType().name().contains("PLATE")) {
                                        action = InteractAction.PRESSURE_PLATE;
                                    } else {
                                        if (block.getType().name().contains("REDSTONE_ORE")) {
                                            action = InteractAction.REDSTONE_ORE;
                                        } else {
                                            action = InteractAction.TRIPWIRE;
                                        }
                                    }
                                }
                                break;
                        }

                        InteractAction finalAction = action;

                        PlayerActionWithRegionEvent event = new PlayerActionWithRegionEvent(player, block, finalAction, region);
                        Bukkit.getServer().getPluginManager().callEvent(event);

                        e.setCancelled(event.isCancelled());
                    }
                }
            });
        } else {
            drop_handled.remove(player.getUniqueId());
        }
    }

    /**
     * Event listener
     *
     * @param e the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityInteract(EntityInteractEvent e) {
        if (!e.isCancelled()) {
            Entity entity = e.getEntity();
            Block block = e.getBlock();

            Cuboid.getRegions().forEach((region) -> {
                if (region.isInside(block)) {
                    EntityInteractWithRegionEvent event = new EntityInteractWithRegionEvent(entity, block, region);
                    Bukkit.getServer().getPluginManager().callEvent(event);

                    e.setCancelled(event.isCancelled());
                }
            });
        }
    }

    /**
     * Event listener
     *
     * @param e the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void entitySpawn(EntitySpawnEvent e) {
        if (!e.isCancelled()) {
            Entity entity = e.getEntity();

            Cuboid.getRegions().forEach((region) -> {
                if (region.isInside(entity)) {
                    Event event = null;
                    if (entity instanceof Item) {
                        Item item = (Item) entity;
                        if (!items_cache.contains(item.getUniqueId())) {
                            items_cache.add(item.getUniqueId());

                            ItemStack stack = item.getItemStack();
                            UUID dropper = drop_data.getOrDefault(stack, null);
                            LivingEntity source = null;
                            if (dropper != null) {
                                Entity tmp = Bukkit.getEntity(dropper);
                                if (tmp instanceof LivingEntity)
                                    source = (LivingEntity) tmp;
                            }

                            event = new ItemSpawnAtRegionEvent(item, source, region);
                        }
                    } else {
                        event = new EntitySpawnAtRegionEvent(entity, region);
                    }
                    if (event != null) {
                        Cancellable cancellable = (Cancellable) event;

                        Bukkit.getServer().getPluginManager().callEvent(event);

                        e.setCancelled(cancellable.isCancelled());

                        Set<Entity> cached = entity_cache.getOrDefault(region, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                        cached.add(entity);

                        entity_cache.put(region, cached);
                    }
                }
            });
        }
    }

    /**
     * Event listener
     *
     * @param e the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        Cuboid.getRegions().forEach((region) -> {
            if (region.isInside(player)) {
                EntitySpawnAtRegionEvent event = new EntitySpawnAtRegionEvent(player, region);
                Bukkit.getServer().getPluginManager().callEvent(event);

                Set<Entity> cached = entity_cache.getOrDefault(region, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                cached.add(player);

                entity_cache.put(region, cached);
            }
        });
    }

    /**
     * Event listener
     *
     * @param e the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDamageEntity(EntityDamageByEntityEvent e) {
        if (!e.isCancelled()) {
            Entity issuer = e.getDamager();
            Entity entity = e.getEntity();

            Cuboid.getRegions().forEach((region) -> {
                if (region.isInside(issuer) || region.isInside(entity)) {
                    damage_data.put(entity.getUniqueId(), issuer);
                }
            });
        }
    }

    /**
     * Event listener
     *
     * @param e the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDamageBlock(EntityDamageByBlockEvent e) {
        if (!e.isCancelled()) {
            Block issuer = e.getDamager();
            Entity entity = e.getEntity();

            Cuboid.getRegions().forEach((region) -> {
                if (region.isInside(issuer) || region.isInside(entity)) {
                    damage_data.put(entity.getUniqueId(), issuer);
                }
            });
        }
    }

    /**
     * Event listener
     *
     * @param e the event
     */
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDie(EntityDeathEvent e) {
        Entity entity = e.getEntity();

        EntityDamageEvent lastDamage = entity.getLastDamageCause();
        EntityDamageEvent.DamageCause tmpCause = EntityDamageEvent.DamageCause.CUSTOM;
        double tmpDamage = 0d;
        if (lastDamage != null) {
            tmpCause = lastDamage.getCause();
            tmpDamage = lastDamage.getFinalDamage();
        }

        EntityDamageEvent.DamageCause cause = tmpCause;
        double damage = tmpDamage;
        Cuboid.getRegions().forEach((region) -> {
            if (region.isInside(entity)) {
                Object killer = damage_data.getOrDefault(entity.getUniqueId(), null);

                Forensics forensics = null;
                if (killer instanceof Block) {
                    Block block = (Block) killer;

                    forensics = new Forensics(null, block, null, cause, damage);
                } else {
                    if (killer instanceof Entity) {
                        Entity assassin = (Entity) killer;
                        ItemStack weapon = null;
                        if (assassin instanceof LivingEntity) {
                            LivingEntity living = (LivingEntity) assassin;
                            EntityEquipment equipment = living.getEquipment();

                            if (equipment != null) {
                                try {
                                    weapon = equipment.getItemInMainHand();

                                    if (weapon.getType().equals(Material.AIR))
                                        weapon = equipment.getItemInOffHand();
                                } catch (Throwable ex) {
                                    weapon = equipment.getItemInHand();
                                }
                            }
                        }

                        forensics = new Forensics(assassin, null, weapon, cause, damage);
                    }
                }

                if (forensics == null)
                    forensics = new Forensics(null, null, null, cause, damage);

                for (ItemStack drop : e.getDrops()) {
                    drop_data.put(drop, entity.getUniqueId());
                }

                EntityDieAtRegionEvent event = new EntityDieAtRegionEvent(entity, forensics, region);
                Bukkit.getServer().getPluginManager().callEvent(event);
            }
        });
    }

    /**
     * Event listener
     *
     * @param e the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void itemDrop(PlayerDropItemEvent e) {
        if (!e.isCancelled()) {
            Player player = e.getPlayer();
            Item drop = e.getItemDrop();

            Cuboid.getRegions().forEach((region) -> {
                if (region.isInside(player) || region.isInside(drop)) {
                    drop_handled.add(player.getUniqueId());
                    items_cache.add(drop.getUniqueId());

                    ItemSpawnAtRegionEvent event = new ItemSpawnAtRegionEvent(drop, player, region);
                    Bukkit.getServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        e.setCancelled(true);
                    } else {
                        Set<Entity> cached = entity_cache.getOrDefault(region, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                        cached.add(drop);

                        entity_cache.put(region, cached);
                    }
                }
            });
        }
    }

    /**
     * Event listener
     *
     * @param e the event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPickup(EntityPickupItemEvent e) {
        if (!e.isCancelled()) {
            LivingEntity entity = e.getEntity();
            Item pickup = e.getItem();

            Cuboid.getRegions().forEach((region) -> {
                if (region.isInside(entity) || region.isInside(pickup)) {
                    EntityPickupItemAtRegionEvent event = new EntityPickupItemAtRegionEvent(pickup, entity, region);
                    Bukkit.getServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        e.setCancelled(true);
                    } else {
                        Set<Entity> cached = entity_cache.getOrDefault(region, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                        cached.remove(pickup);

                        entity_cache.put(region, cached);
                    }
                }
            });
        }
    }


}
