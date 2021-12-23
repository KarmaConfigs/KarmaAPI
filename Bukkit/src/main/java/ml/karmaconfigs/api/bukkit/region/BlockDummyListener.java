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

import ml.karmaconfigs.api.bukkit.region.event.block.BlockAction;
import ml.karmaconfigs.api.bukkit.region.event.block.GenericBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.*;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Dummy block events listener
 */
class BlockDummyListener implements Listener {

    /**
     * Initialize the dummy listener
     *
     * @param channel the dummy plugin channel
     */
    public BlockDummyListener(final Plugin channel) {
        /*ClassInfoList events = new ClassGraph()
                .enableClassInfo()
                .scan() //you should use try-catch-resources instead
                .getClassInfo(Event.class.getName())
                .getSubclasses()
                .filter(info -> !info.isAbstract());

        Listener listener = new Listener() {};
        EventExecutor executor = (ignored, event) -> handle(event);

        try {
            for (ClassInfo event : events) {
                //noinspection unchecked
                Class<? extends Event> eventClass = (Class<? extends Event>) Class.forName(event.getName());

                if (Arrays.stream(eventClass.getDeclaredMethods()).anyMatch(method ->
                        method.getParameterCount() == 0 && method.getName().equals("getHandlers"))) {
                    //We could do this further filtering on the ClassInfoList instance instead,
                    //but that would mean that we have to enable method info scanning.
                    //I believe the overhead of initializing ~20 more classes
                    //is better than that alternative.

                    Bukkit.getPluginManager().registerEvent(eventClass, listener,
                            EventPriority.NORMAL, executor, channel);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Scanned class wasn't found", e);
        }*/

        RegisteredListener registered = new RegisteredListener(this, (listener, event) -> handle(event), EventPriority.HIGHEST, channel, false);
        HandlerList.getHandlerLists().forEach(handler -> {
            handler.register(registered);
        });
    }

    /**
     * Handle the event
     *
     * @param e the event to handle
     */
    public void handle(final Event e) {
        String name = e.getEventName();

        if (name.contains("Block")) {
            Package pack = e.getClass().getPackage();
            //Ignore karma events
            if (!pack.getName().startsWith("ml.karmaconfigs.api.bukkit.region.event")) {
                String type = name
                        .replace("Block", "")
                        .replace("Event", "")
                        .replace("Entity", "")
                        .replace("Player", "");

                if (e instanceof EntityBlockFormEvent) {
                    type = "ENTITY_FORM";
                }

                try {
                    BlockAction action = BlockAction.valueOf(type);
                    Event instance = action.toEvent(e);

                    if (instance != null) {
                        Class<?> clazz = instance.getClass();

                        /*Method getBlock = null;
                        try {
                            getBlock = clazz.getMethod("getBlock");
                        } catch (Throwable ex) {
                            try {
                                getBlock = clazz.getDeclaredMethod("getBlock");
                            } catch (Throwable ignored) {}
                        }*/

                        Field blockField = null;
                        try {
                            Field[] fields = clazz.getFields();
                            for (Field field : fields) {
                                if (field.getType().equals(Block.class)) {
                                    blockField = field;
                                    break;
                                }
                            }

                            if (blockField == null) {
                                fields = clazz.getDeclaredFields();
                                for (Field field : fields) {
                                    if (field.getType().equals(Block.class)) {
                                        blockField = field;
                                        break;
                                    }
                                }
                            }
                        } catch (Throwable ignored) {};

                        Block block = null;
                        if (blockField != null) {
                            try {
                                block = (Block) blockField.get(instance);
                            } catch (Throwable ignored) {}
                        }

                        if (block != null) {
                            Block valid = block;

                            Cuboid.getRegions().forEach((region) -> {
                                if (region.isInside(valid)) {
                                    GenericBlockEvent event = new GenericBlockEvent(action, instance, region);
                                    Bukkit.getServer().getPluginManager().callEvent(event);
                                    if (e instanceof Cancellable) {
                                        Cancellable cancellable = (Cancellable) e;
                                        cancellable.setCancelled(event.isCancelled());
                                    }
                                }
                            });
                        }
                    }
                } catch (Throwable ignored) {}
            }
        }
    }
}
