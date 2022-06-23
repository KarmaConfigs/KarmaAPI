package ml.karmaconfigs.api.bukkit.region.flag;

/**
 * Simple region flag
 */
public final class SimpleFlag extends RegionFlag {

    private final String name;
    private final Object value;

    /**
     * Initialize the simple flag
     *
     * @param n the flag name
     * @param v the flag value
     */
    public SimpleFlag(final String n ,final Object v) {
        name = n;
        value = v;
    }

    /**
     * Get the region flag name
     *
     * @return the region flag name
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * Get the region flag value
     *
     * @return the region flag value
     */
    @Override
    public Object value() {
        return value;
    }
}
