package ml.karmaconfigs.api.common.karma.file.element;

import ml.karmaconfigs.api.common.karma.file.error.NotTypeError;

/**
 * Karma element for the new KarmaFile API which
 * is very similar to json
 *
 * @author KarmaDev
 * @since 1.3.2-SNAPSHOT
 */
public abstract class KarmaElement {

    /**
     * Copy the element
     *
     * @return the karma element
     */
    public abstract KarmaElement copy();

    /**
     * Get if the element is an array
     *
     * @return if the element is an array
     */
    public abstract boolean isArray();

    /**
     * Get if the element is an array with keys
     *
     * @return if the element is an array with key and values
     */
    public abstract boolean isKeyArray();

    /**
     * Get if the element is a string
     *
     * @return if the element is a string
     */
    public abstract boolean isString();

    /**
     * Get if the element is a number
     *
     * @return if the element is a number
     */
    public abstract boolean isNumber();

    /**
     * Get if the element is a boolean
     *
     * @return if the element is a boolean
     */
    public abstract boolean isBoolean();

    /**
     * Get if the element is valid
     *
     * @return if the element is valid
     */
    public abstract boolean isValid();

    /**
     * Transforms the object value(s) to lower
     * case
     *
     * @return the lower case value(s)
     */
    public abstract KarmaElement toLowerCase();

    /**
     * Transforms the object value(s) to upper
     * case
     *
     * @return the UPPER CASE value(s)
     */
    public abstract KarmaElement toUpperCase();

    /**
     * Get the element object
     *
     * @return the element as an object
     */
    public final KarmaObject getObjet() throws NotTypeError {
        if (this instanceof KarmaObject) {
            return (KarmaObject) this;
        } else {
            throw new NotTypeError(this, KarmaObject.class);
        }
    }

    /**
     * Get the element array
     *
     * @return the element as an array
     */
    public final KarmaArray getArray() throws NotTypeError {
        if (this instanceof KarmaArray) {
            return (KarmaArray) this;
        } else {
            throw new NotTypeError(this, KarmaArray.class);
        }
    }

    /**
     * Get the element keyed array
     *
     * @return the element as a map
     */
    public final KarmaKeyArray getKeyArray() throws NotTypeError {
        if (this instanceof KarmaKeyArray) {
            return (KarmaKeyArray) this;
        } else {
            throw new NotTypeError(this, KarmaKeyArray.class);
        }
    }
}
