package ml.karmaconfigs.api.bukkit;

import ml.karmaconfigs.api.bukkit.scheduler.BukkitAsyncScheduler;
import ml.karmaconfigs.api.bukkit.scheduler.BukkitSyncScheduler;
import ml.karmaconfigs.api.common.Console;
import ml.karmaconfigs.api.common.karma.APISource;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.timer.AsyncScheduler;
import ml.karmaconfigs.api.common.timer.SyncScheduler;
import ml.karmaconfigs.api.common.timer.scheduler.Scheduler;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class KarmaPlugin extends JavaPlugin implements KarmaSource {

    private final Console console;
    private Scheduler async;
    private Scheduler sync;

    /**
     * Initialize the KarmaPlugin
     */
    public KarmaPlugin() {
        console = new Console(this, (msg) -> Bukkit.getServer().getConsoleSender().sendMessage(StringUtils.toColor(msg)));

        async = new AsyncScheduler(this);
        sync = new SyncScheduler(this);

        if (!APISource.hasProvider(name())) {
            APISource.addProvider(this);
        }
    }

    /**
     * Initialize the KarmaPlugin
     *
     * @param defineDefault if this source should be defined
     *                      as the default source
     * @throws SecurityException if the default module is already
     * set
     */
    public KarmaPlugin(final boolean defineDefault) throws SecurityException {
        console = new Console(this, (msg) -> Bukkit.getServer().getConsoleSender().sendMessage(StringUtils.toColor(msg)));

        async = new AsyncScheduler(this);
        sync = new SyncScheduler(this);

        if (!APISource.hasProvider(name())) {
            APISource.addProvider(this);
            if (defineDefault) {
                APISource.defineDefault(this);
            }
        }
    }

    /**
     * Enable the KarmaPlugin
     */
    public abstract void enable();

    /**
     * On plugin enable
     */
    @Override
    public final void onEnable() {
        async = new BukkitAsyncScheduler<>(this);
        sync = new BukkitSyncScheduler<>(this);

        enable();
    }

    /**
     * Get the source async scheduler
     *
     * @return the source async scheduler
     */
    @Override
    public Scheduler async() {
        return async;
    }

    /**
     * Get the source sync scheduler
     *
     * @return the source sync scheduler
     */
    @Override
    public Scheduler sync() {
        return sync;
    }

    /**
     * Get the source out
     *
     * @return the source out
     */
    @Override
    public Console console() {
        return console;
    }
}
