package ml.karmaconfigs.api.bukkit.karmaserver;

import ml.karmaconfigs.api.common.Console;
import ml.karmaconfigs.api.common.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

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

/**
 * Minecraft bukkit server
 * version utils. Check versions
 * and more...
 */
public abstract class VersionUtils {

    private final Server server;

    /**
     * Get the minecraft server version
     * of the server
     *
     * @param plugin the plugin to get the server
     *               from
     * @deprecated The API now uses {@link Bukkit#getServer()}
     */
    @Deprecated
    VersionUtils(final JavaPlugin plugin) {
        server = Bukkit.getServer();
    }

    /**
     * Get the minecraft server version
     * of the server
     */
    VersionUtils() {
        server = Bukkit.getServer();
    }

    /**
     * Get the full version string of
     * the server
     *
     * @return a String
     */
    public final String getRealVersion() {
        return server.getBukkitVersion();
    }

    /**
     * Get the version full version
     * as string
     *
     * @return a String
     */
    public final String getFullVersion() {
        return server.getBukkitVersion().split("-")[0];
    }

    /**
     * Get the server version package type
     *
     * @return a string
     */
    public final String getPackageType() {
        return server.getBukkitVersion().split("-")[2];
    }

    /**
     * Get the server version package build
     *
     * @return a string
     */
    public final String getPackageBuild() {
        return server.getBukkitVersion().split("-")[1];
    }

    /**
     * Get the server version
     *
     * @return a float
     */
    public final float getVersion() {
        String[] versionData = server.getBukkitVersion().split("-");
        String version_head = versionData[0].split("\\.")[0];
        String version_sub = versionData[0].split("\\.")[1];

        return Float.parseFloat(version_head + "." + version_sub);
    }

    /**
     * Get the server version update
     * (Example: 1.16.2 will return "2")
     *
     * @return an integer
     */
    public final int getVersionUpdate() {
        String[] versionData = server.getBukkitVersion().split("-");
        String version = versionData[0];
        versionData = version.split("\\.");
        if (versionData.length >= 3) {
            return Integer.parseInt(versionData[2]);
        }
        return -1;
    }

    /**
     * Get the version in enumeration type
     *
     * @return a Version instance
     */
    public final Version version() {
        String full = getFullVersion();
        full = "v" + full.replace(".", "_");

        return Version.valueOf(full);
    }

    /**
     * Check if the current server version is over the specified
     * one
     *
     * @param version the check version
     * @return if current version is over the
     * specified one
     */
    public final boolean isOver(final Version version) {
        String current_version = version().name().replace("v", "").replace("_", ".");
        String check_version = version.name().replace("v", "").replace("_", ".");

        return StringUtils.compareTo(current_version, check_version) > 0;
    }

    /**
     * Check if the current server version is under the specified
     * one
     *
     * @param v the server version
     * @return if current version is over the
     * specified one
     */
    public final boolean isUnder(final Version v) {
        String current_version = version().name().replace("v", "").replace("_", ".");
        String check_version = v.name().replace("v", "").replace("_", ".");

        return StringUtils.compareTo(current_version, check_version) < 0;
    }

    /**
     * Get a nms class directly from the server version
     *
     * @param clazz the class name
     * @return a Class
     */
    @Nullable
    public final Class<?> getMinecraftClass(@NotNull final String clazz) {
        try {
            String version = server.getClass().getPackage().getName().replace(".", ",").split(",")[3];
            return Class.forName("net.minecraft.server." + version + "." + clazz);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * Get an obc class directly from server
     * package
     *
     * @param clazz the class name
     * @return a Class
     */
    @Nullable
    public final Class<?> getBukkitClass(@NotNull final String clazz) {
        try {
            String version = server.getClass().getPackage().getName().replace(".", ",").split(",")[3];
            return Class.forName("org.bukkit.craftbukkit." + version + "." + clazz);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * Get the craft player of a player
     *
     * @param player the player
     * @return the player CraftPlayer
     */
    @Nullable
    public final Object getCraftPlayer(final Player player) {
        try {
            Class<?> craftPlayer = getBukkitClass("entity.CraftPlayer");

            if (craftPlayer != null) {
                return craftPlayer.cast(player);
            } else {
                Console.send("&cTried to get craft player from {0} but couldn't instantiate CraftPlayer!", player.getUniqueId());
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Get the craft entity of an entity
     *
     * @param entity the entity
     * @return the entity CraftEntity
     */
    public final Object getCraftLivingEntity(final Entity entity) {
        try {
            Class<?> craftLiving = entity.getClass();
            return craftLiving.cast(entity);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Get minecraft world server
     *
     * @param world the world
     * @return a minecraft world server
     */
    public final Object getWorldServer(final World world) {
        try {
            Class<?> craftWorld = getBukkitClass("CraftWorld");

            if (craftWorld != null) {
                Method handle = craftWorld.getMethod("getHandle");
                return handle.invoke(world);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Get the entity player of a player
     *
     * @param player the player
     * @return the player entity player
     */
    @Nullable
    public final Object getEntityPlayer(final Player player) {
        try {
            Object craftPlayer = getCraftPlayer(player);

            if (craftPlayer != null) {
                Method getHandle = craftPlayer.getClass().getMethod("getHandle");
                return getHandle.invoke(craftPlayer);
            } else {
                Console.send("&cTried to get entity player from {0} but couldn't instantiate CraftPlayer!", player.getUniqueId());
            }
        } catch (Throwable ex) {
            try {
                Method getHandle = player.getClass().getMethod("getHandle");
                return getHandle.invoke(player);
            } catch (Throwable exc) {
                exc.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Get an entity living from an entity
     *
     * @param entity the entity to use
     * @return the entity living
     */
    public final Object getEntityLiving(final Entity entity) {
        try {
            Object craftLiving = getCraftLivingEntity(entity);
            if (craftLiving != null) {
                Method getHandle = craftLiving.getClass().getMethod("getHandle");
                return getHandle.invoke(entity);
            } else {
                Console.send("&cTried to get living entity but couldn't get craft living entity from the entity");
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Get an item stack
     *
     * @param item the item
     * @return the item stack
     */
    public final Object getItemStack(final ItemStack item) {
        try {
            Class<?> craftItemStack = getBukkitClass("inventory.CraftItemStack");
            if (craftItemStack != null) {
                Method asNMSCopy = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
                return asNMSCopy.invoke(craftItemStack, item);
            } else {
                Console.send("&cTried to get minecraft server item stack but couldn't get CraftItemStack");
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Create a new entity
     *
     * @param livingEntityClass the living entity class
     * @param world the entity spawn world
     * @return a new entity
     */
    public final Object createEntity(final Class<?> livingEntityClass, final World world) {
        try {
            Constructor<?> constructor = livingEntityClass.getConstructor(getMinecraftClass("World"));
            return constructor.newInstance(getWorldServer(world));
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Create a new entity
     *
     * @param livingEntityClass the living entity class
     * @param spawnLoc the entity spawn location
     * @return a new entity
     */
    public final Object createEntity(final Class<?> livingEntityClass, final Location spawnLoc) {
        try {
            Constructor<?> constructor = livingEntityClass.getConstructor(getMinecraftClass("World"), double.class, double.class, double.class);
            return constructor.newInstance(getWorldServer(spawnLoc.getWorld()), spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Create a new entity
     *
     * @param entityLivingClass the living entity class
     * @param entityLiving the already existing living entity
     * @param spawnLoc the entity spawn location
     * @return a new entity
     */
    public final Object createEntity(final Class<?> entityLivingClass, final Object entityLiving, final Location spawnLoc) {
        try {
            Constructor<?> constructor = entityLivingClass.getConstructor(getMinecraftClass("World"), getMinecraftClass("EntityLiving"), double.class, double.class, double.class);
            return constructor.newInstance(getWorldServer(spawnLoc.getWorld()), entityLiving, spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Create a new entity
     *
     * @param entityLivingClass the living entity class
     * @param spawnLoc the entity spawn location
     * @param item the item stack
     * @return a new entity
     */
    public final Object createEntity(final Class<?> entityLivingClass, final Location spawnLoc, final ItemStack item) {
        try {
            Constructor<?> constructor = entityLivingClass.getConstructor(getMinecraftClass("World"), double.class, double.class, double.class, getMinecraftClass("ItemStack"));
            return constructor.newInstance(getWorldServer(spawnLoc.getWorld()), spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ(), getItemStack(item));
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Initialize the packet factory
     *
     * @param name the packet name
     * @param arguments the packet arguments
     * @param parameters the packet constructor parameters
     * @return a new packet factory
     */
    public final Object createPacket(final String name, final Class<?>[] arguments, final Object... parameters) {
        try {
            Class<?> packetClass = getMinecraftClass(name);
            if (packetClass == null)
                packetClass = getBukkitClass(name);

            if (packetClass != null) {
                Constructor<?> constructor = packetClass.getConstructor(arguments);
                return constructor.newInstance(parameters);
            } else {
                Console.send("&cTried to create a packet but the packet class couldn't be found ( " + name + " )!");
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Initialize the packet factory
     *
     * @param name the packet name
     * @param argument the packet arguments
     * @param parameter the packet constructor parameter
     * @return a new packet factory
     */
    public final Object createPacket(final String name, final Class<?> argument, final Object parameter) {
        try {
            Class<?> packetClass = getMinecraftClass(name);
            if (packetClass == null)
                packetClass = getBukkitClass(name);

            if (packetClass != null) {
                Constructor<?> constructor = packetClass.getConstructor(argument);
                return constructor.newInstance(parameter);
            } else {
                Console.send("&cTried to create a packet but the packet class couldn't be found ( " + name + " )!");
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Invoke a method on the specified object
     *
     * @param object the object
     * @param name the method name
     * @param arguments the method arguments
     * @param parameters the method parameters
     * @return the result
     */
    public final Object invoke(final Object object, final String name, final Class<?>[] arguments, final Object... parameters) {
        try {
            Method method = object.getClass().getMethod(name, arguments);
            return method.invoke(object, parameters);
        } catch (Throwable ex) {
            try {
                Method method = object.getClass().getDeclaredMethod(name, arguments);
                return method.invoke(object, parameters);
            } catch (Throwable exc) {
                exc.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Invoke a method on the specified object
     *
     * @param object the object
     * @param name the method name
     * @param argument the method argument
     * @param parameter the method parameter
     * @return the result
     */
    public final Object invoke(final Object object, final String name, final Class<?> argument, final Object parameter) {
        try {
            Method method = object.getClass().getMethod(name, argument);
            return method.invoke(object, parameter);
        } catch (Throwable ex) {
            try {
                Method method = object.getClass().getDeclaredMethod(name, argument);
                return method.invoke(object, parameter);
            } catch (Throwable exc) {
                exc.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Create a new vector 3f class
     *
     * @param x the vector x
     * @param y the vector y
     * @param z the vector z
     * @return a new vector3f
     */
    public final Object createVector3F(final float x, final float y, final float z) {
        try {
            Class<?> vector = getMinecraftClass("Vector3f");
            if (vector != null) {
                Constructor<?> constructor = vector.getConstructor(float.class, float.class, float.class);
                return constructor.newInstance(x, y, z);
            } else {
                Console.send("&cTried to create a vector3f but the vector3f class couldn't be found!");
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Create a new vector 3D class
     *
     * @param x the vector x
     * @param y the vector y
     * @param z the vector z
     * @return a new vector3D
     */
    public final Object createVector3D(final double x, final double y, final double z) {
        try {
            Class<?> vector = getMinecraftClass("Vec3D");
            if (vector != null) {
                Constructor<?> constructor = vector.getConstructor(double.class, double.class, double.class);
                return constructor.newInstance(x, y, z);
            } else {
                Console.send("&cTried to create a vector 3d but the Vec3D class couldn't be found!");
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Create a new IChatBaseComponent
     *
     * @param text the text
     * @return a new IChatBaseComponent
     */
    public final Object createChatMessage(final String text) {
        try {
            Class<?> chatMessage = getMinecraftClass("ChatMessage");
            if (chatMessage != null) {
                Constructor<?> constructor = chatMessage.getConstructor(String.class, Object[].class);
                return constructor.newInstance(text, null);
            } else {
                Console.send("&cTried to create a vector3f but the vector3f class couldn't be found!");
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Get the object as the specified type
     *
     * @param object the object
     * @param type the object return class type
     *
     * @return the object as the specified type
     */
    public final Object getAs(final Object object, final Class<?> type) {
        try {
            return type.cast(object);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Get all the methods of a class
     *
     * @param clazz the class to search on
     * @param declared include declared methods
     * @return all the methods from a class
     */
    public final Set<String> fetchMethods(final Class<?> clazz, final boolean declared) {
        Set<String> methods = new LinkedHashSet<>();
        Method[] clazzMethods = clazz.getMethods();
        for (Method method : clazzMethods) {
            StringBuilder methodStringBuilder = new StringBuilder();
            methodStringBuilder.append(method.getReturnType().getSimpleName()).append(" ");

            StringBuilder argTypeBuilder = new StringBuilder();
            if (method.getParameterCount() > 0) {
                for (Class<?> type : method.getParameterTypes())
                    argTypeBuilder.append(type.getSimpleName()).append(", ");
            }

            methodStringBuilder.append(method.getName()).append("(").append(StringUtils.replaceLast(argTypeBuilder.toString(), ", ", "")).append(")");
            methods.add(methodStringBuilder.toString());
        }
        if (declared) {
            clazzMethods = clazz.getDeclaredMethods();
            for (Method method : clazzMethods) {
                StringBuilder methodStringBuilder = new StringBuilder();
                methodStringBuilder.append(method.getReturnType().getSimpleName()).append(" ");

                StringBuilder argTypeBuilder = new StringBuilder();
                if (method.getParameterCount() > 0) {
                    for (Class<?> type : method.getParameterTypes())
                        argTypeBuilder.append(type.getSimpleName()).append(", ");
                }

                methodStringBuilder.append(method.getName()).append("(").append(StringUtils.replaceLast(argTypeBuilder.toString(), ", ", "")).append(")");
                methods.add(methodStringBuilder.toString());
            }
        }

        return methods;
    }

    /**
     * Change a field value on the specified object
     *
     * @param object the object
     * @param name the field name
     * @param value the field new value
     */
    public final void changeField(final Object object, final String name, final Object value) {
        try {
            Field field = object.getClass().getField(name);
            field.set(object, value);
        } catch (Throwable ex) {
            try {
                Field field = object.getClass().getDeclaredField(name);
                field.set(object, value);
            } catch (Throwable exc) {
                exc.printStackTrace();
            }
        }
    }

    /**
     * Invoke a packet to the player
     *
     * @param player the player
     * @param packet the packet
     */
    public final void invokePacket(final Player player, final Object packet) {
        try {
            Object entityPlayer = getEntityPlayer(player);
            if (entityPlayer != null) {
                Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
                Method sendPacket = playerConnection.getClass().getMethod("sendPacket", getMinecraftClass("Packet"));

                sendPacket.invoke(playerConnection, packet);
            } else {
                Console.send("&cTried to send a packet with a null entity player!");
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
