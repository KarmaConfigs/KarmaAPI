package ml.karmaconfigs.api.bukkit.region.flag;

/**
 * Region flag
 */
public abstract class RegionFlag {

    /**
     * Get the region flag name
     *
     * @return the region flag name
     */
    public abstract String name();

    /**
     * Get the region flag value
     *
     * @return the region flag value
     */
    public abstract Object value();
}
