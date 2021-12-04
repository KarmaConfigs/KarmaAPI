package ml.karmaconfigs.api.common.utils.string.util;

/**
 * Karma units
 */
public enum KarmaUnit {
    /**
     * Single millisecond
     */
    MILLISECOND("ms"),
    /**
     * More than 1 millisecond
     */
    MILLISECONDS("ms"),
    /**
     * Single second
     */
    SECOND("sec"),
    /**
     * More than 1 second
     */
    SECONDS("seconds"),
    /**
     * Single minute
     */
    MINUTE("min"),
    /**
     * More than 1 minute
     */
    MINUTES("minutes"),
    /**
     * Single hour
     */
    HOUR("h"),
    /**
     * More than 1 hour
     */
    HOURS("hours"),
    /**
     * Single day
     */
    DAY("d"),
    /**
     * More than 1 day
     */
    DAYS("days"),
    /**
     * Single week
     */
    WEEK("w"),
    /**
     * More than 1 week
     */
    WEEKS("weeks"),
    /**
     * Single month
     */
    MONTH("month"),
    /**
     * More than 1 month
     */
    MONTHS("months"),
    /**
     * Single year
     */
    YEAR("year"),
    /**
     * More than 1 year
     */
    YEARS("years");

    /**
     * The unit name
     */
    private final String unit;

    /**
     * Initialize the karma unit
     *
     * @param name the unit name
     */
    KarmaUnit(final String name) {
        unit = name;
    }

    /**
     * Get the unit name
     *
     * @return the unit name
     */
    public String getUnit() {
        return unit;
    }
}
