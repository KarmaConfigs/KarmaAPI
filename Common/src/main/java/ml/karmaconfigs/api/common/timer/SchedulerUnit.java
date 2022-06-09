package ml.karmaconfigs.api.common.timer;

/**
 * Valid change units
 */
public enum SchedulerUnit {
    /**
     * Milliseconds
     */
    MILLISECOND,
    /**
     * Seconds
     */
    SECOND,
    /**
     * Minutes
     */
    MINUTE,
    /**
     * Hours
     */
    HOUR,
    /**
     * Days
     */
    DAY;

    /**
     * Get the java time unit of the scheduler time unit
     *
     * @return the java unit
     */
    public java.util.concurrent.TimeUnit toJavaUnit() {
        switch (this) {
            case DAY:
                return java.util.concurrent.TimeUnit.DAYS;
            case HOUR:
                return java.util.concurrent.TimeUnit.HOURS;
            case MINUTE:
                return java.util.concurrent.TimeUnit.MINUTES;
            case SECOND:
                return java.util.concurrent.TimeUnit.SECONDS;
            default:
                return java.util.concurrent.TimeUnit.MILLISECONDS;
        }
    }
}
