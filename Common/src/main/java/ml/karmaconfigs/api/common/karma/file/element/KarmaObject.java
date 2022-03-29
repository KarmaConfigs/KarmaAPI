package ml.karmaconfigs.api.common.karma.file.element;

import ml.karmaconfigs.api.common.karma.NumType;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Karma element for the new KarmaFile API which
 * is very similar to json
 *
 * @author KarmaDev
 * @since 1.3.2-SNAPSHOT
 */
public class KarmaObject extends KarmaElement {

    private final String str;
    private final Number num;
    private final Boolean boo;

    private final NumType type;

    /**
     * Initialize the karma object
     *
     * @param obj the object
     */
    public KarmaObject(final String obj) {
        str = obj;
        num = null;
        boo = null;
        type = NumType.NOT_NUMBER;
    }

    /**
     * Initialize the karma object
     *
     * @param obj the object
     */
    public KarmaObject(final Number obj) {
        str = null;
        num = obj;
        boo = null;
        type = NumType.detect(obj);
    }

    /**
     * Initialize the karma object
     *
     * @param obj the object
     */
    public KarmaObject(final Boolean obj) {
        str = null;
        num = null;
        boo = obj;
        type = NumType.NOT_NUMBER;
    }
    /**
     * Get the object string
     *
     * @return the object string
     */
    public String getString() {
        return str;
    }

    /**
     * Get the object number
     *
     * @return the object number
     */
    public Number getNumber() {
        return num;
    }

    /**
     * Get the object boolean
     *
     * @return the object boolean
     */
    public Boolean getBoolean() {
        return boo;
    }

    /**
     * Copy the element
     *
     * @return the karma element
     */
    @Override
    public KarmaElement copy() {
        return null;
    }

    /**
     * Get if the element is an array
     *
     * @return if the element is an array
     */
    @Override
    public boolean isArray() {
        return false;
    }

    /**
     * Get if the element is an array with keys
     *
     * @return if the element is an array with key and values
     */
    @Override
    public boolean isKeyArray() {
        return false;
    }

    /**
     * Get if the element is a string
     *
     * @return if the element is a string
     */
    @Override
    public boolean isString() {
        return str != null;
    }

    /**
     * Get if the element is a number
     *
     * @return if the element is a number
     */
    @Override
    public boolean isNumber() {
        return num != null;
    }

    /**
     * Get if the element is a boolean
     *
     * @return if the element is a boolean
     */
    @Override
    public boolean isBoolean() {
        return boo != null;
    }

    /**
     * Get if the number is a float
     *
     * @return if the number is a float
     */
    public NumType getNumType() {
        return type;
    }

    /**
     * Transforms the object value(s) to lower
     * case
     *
     * @return the lower case value(s)
     */
    @Override
    public final KarmaElement toLowerCase() {
        if (str != null) {
            return new KarmaObject(str.toLowerCase());
        } else {
            if (num != null) {
                return new KarmaObject(num);
            } else {
                return new KarmaObject(boo);
            }
        }
    }

    /**
     * Transforms the object value(s) to upper
     * case
     *
     * @return the UPPER CASE value(s)
     */
    @Override
    public final KarmaElement toUpperCase() {
        if (str != null) {
            return new KarmaObject(str.toUpperCase());
        } else {
            if (num != null) {
                return new KarmaObject(num);
            } else {
                return new KarmaObject(boo);
            }
        }
    }

    /**
     * Get if the element is valid
     *
     * @return if the element is valid
     */
    @Override
    public boolean isValid() {
        return str != null || num != null || boo != null;
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        if (str != null)
            return "\"" + str + "\"";

        if (num != null)
            return String.valueOf(num);

        if (boo != null)
            return String.valueOf(boo);

        return "[Unknown]";
    }
}
