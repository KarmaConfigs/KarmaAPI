package ml.karmaconfigs.api.common.karma;

/**
 * Valid number types
 */
public enum NumType {
    /**
     * Not a number
     */
    NOT_NUMBER,
    /**
     * Byte value
     */
    BYTE,
    /**
     * Double value
     */
    DOUBLE,
    /**
     * Float value
     */
    FLOAT,
    /**
     * Integer value
     */
    INTEGER,
    /**
     * Long value
     */
    LONG,
    /**
     * Short value
     */
    SHORT;

    /**
     * Detect the number type
     *
     * @param number the number
     * @return the number type
     */
    public static NumType detect(final Number number) {
        if (number instanceof Byte) {
            return BYTE;
        }
        if (number instanceof Double) {
            return DOUBLE;
        }
        if (number instanceof Float) {
            return FLOAT;
        }
        if (number instanceof Integer) {
            return INTEGER;
        }
        if (number instanceof Long) {
            return LONG;
        }
        if (number instanceof Short) {
            return SHORT;
        }

        return NOT_NUMBER;
    }
}
