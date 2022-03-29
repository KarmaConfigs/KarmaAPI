package ml.karmaconfigs.api.common.karma.file.element;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Karma element for the new KarmaFile API which
 * is very similar to json
 *
 * @author KarmaDev
 * @since 1.3.2-SNAPSHOT
 */
public class KarmaArray extends KarmaElement implements Iterable<KarmaElement> {

    private final Set<KarmaElement> elements = new LinkedHashSet<>();

    /**
     * Initialize the karma array
     *
     * @param element the default elements
     */
    public KarmaArray(final KarmaElement... element) {
        elements.addAll(Arrays.asList(element));
    }

    /**
     * Add an element
     *
     * @param element the element to add
     */
    public void add(final KarmaElement... element) {
        elements.addAll(Arrays.asList(element));
    }

    /**
     * Remove an element
     *
     * @param element the element to remove
     */
    public void remove(final KarmaElement... element) {
        Arrays.asList(element).forEach(elements::remove);
    }

    /**
     * Check if the array contains an element
     *
     * @param element the element
     * @return if the array contains an element
     */
    public boolean contains(final KarmaElement... element) {
        Set<String> strings = new HashSet<>();
        Set<Number> numbers = new HashSet<>();
        Set<Boolean> booleans = new HashSet<>();

        for (KarmaElement item : elements) {
            if (!item.isArray() && !item.isKeyArray()) {
                KarmaObject obj = item.getObjet();
                if (obj.isString()) {
                    strings.add(obj.getString());
                }
                if (obj.isNumber()) {
                    numbers.add(obj.getNumber());
                }
                if (obj.isBoolean()) {
                    booleans.add(obj.getBoolean());
                }
            } else {
                fill(item, strings, numbers, booleans);
            }
        }

        for (KarmaElement check : element) {
            if (check.isValid()) {
                if (check instanceof KarmaObject) {
                    KarmaObject obj = check.getObjet();

                    if (check.isString()) {
                        String str = obj.getString();

                        if (strings.contains(str))
                            return true;
                    }
                    if (check.isNumber()) {
                        Number number = obj.getNumber();
                        if (numbers.contains(number))
                            return true;
                    }
                    if (check.isBoolean()) {
                        boolean bool = obj.getBoolean();
                        if (booleans.contains(bool))
                            return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Check if the array contains an element
     *
     * @param element the element
     * @return if the array contains an element
     */
    public KarmaElement containsAny(final KarmaElement... element) {
        Set<String> strings = new HashSet<>();
        Set<Number> numbers = new HashSet<>();
        Set<Boolean> booleans = new HashSet<>();

        for (KarmaElement item : elements) {
            if (!item.isArray() && !item.isKeyArray()) {
                KarmaObject obj = item.getObjet();
                if (obj.isString()) {
                    strings.add(obj.toString());
                }
                if (obj.isNumber()) {
                    numbers.add(obj.getNumber());
                }
                if (obj.isBoolean()) {
                    booleans.add(obj.getBoolean());
                }
            } else {
                fill(item, strings, numbers, booleans);
            }
        }

        for (KarmaElement check : element) {
            if (check.isValid()) {
                if (check instanceof KarmaObject) {
                    KarmaObject obj = check.getObjet();

                    if (check.isString()) {
                        String str = obj.getString();
                        if (strings.contains(str))
                            return check;
                    }
                    if (check.isNumber()) {
                        Number number = obj.getNumber();
                        if (numbers.contains(number))
                            return check;
                    }
                    if (check.isBoolean()) {
                        boolean bool = obj.getBoolean();
                        if (booleans.contains(bool))
                            return check;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Check if the array contains the elements
     *
     * @param element the element
     * @return if the array contains the elements
     */
    public boolean containsAll(final KarmaElement... element) {
        Set<String> strings = new HashSet<>();
        Set<Number> numbers = new HashSet<>();
        Set<Boolean> booleans = new HashSet<>();

        for (KarmaElement item : elements) {
            if (!item.isArray() && !item.isKeyArray()) {
                KarmaObject obj = item.getObjet();
                if (obj.isString()) {
                    strings.add(obj.toString());
                }
                if (obj.isNumber()) {
                    numbers.add(obj.getNumber());
                }
                if (obj.isBoolean()) {
                    booleans.add(obj.getBoolean());
                }
            } else {
                fill(item, strings, numbers, booleans);
            }
        }

        for (KarmaElement check : element) {
            if (check.isValid()) {
                if (check instanceof KarmaObject) {
                    KarmaObject obj = check.getObjet();

                    if (check.isString()) {
                        String str = obj.getString();
                        if (!strings.contains(str))
                            return false;
                    }
                    if (check.isNumber()) {
                        Number number = obj.getNumber();
                        if (!numbers.contains(number))
                            return false;
                    }
                    if (check.isBoolean()) {
                        boolean bool = obj.getBoolean();
                        if (!booleans.contains(bool))
                            return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Get the element at that index
     *
     * @param index the element index
     * @return the element index
     */
    public final KarmaElement get(final int index) {
        int curr = 0;
        for (KarmaElement element : elements) {
            curr++;

            if (curr == index || curr == elements.size()) {
                return element;
            }
        }

        return null;
    }

    /**
     * Transforms the object value(s) to lower
     * case
     *
     * @return the lower case value(s)
     */
    @Override
    public final KarmaElement toLowerCase() {
        Set<KarmaElement> result = new LinkedHashSet<>();
        for (KarmaElement current : elements) {
            KarmaElement tmp;

            if (!current.isArray() && !current.isKeyArray()) {
                KarmaObject object = current.getObjet();
                tmp = object.toLowerCase();
            } else {
                if (current.isArray()) {
                    KarmaArray ar = current.getArray();
                    tmp = ar.toLowerCase();
                } else {
                    KarmaKeyArray ark = current.getKeyArray();
                    tmp = ark.toLowerCase();
                }
            }

            result.add(tmp);
        }

        return new KarmaArray(result.toArray(new KarmaElement[0]));
    }

    /**
     * Transforms the object value(s) to upper
     * case
     *
     * @return the UPPER CASE value(s)
     */
    @Override
    public final KarmaElement toUpperCase() {
        Set<KarmaElement> result = new LinkedHashSet<>();
        for (KarmaElement current : elements) {
            KarmaElement tmp;

            if (!current.isArray() && !current.isKeyArray()) {
                KarmaObject object = current.getObjet();
                tmp = object.toUpperCase();
            } else {
                if (current.isArray()) {
                    KarmaArray ar = current.getArray();
                    tmp = ar.toUpperCase();
                } else {
                    KarmaKeyArray ark = current.getKeyArray();
                    tmp = ark.toUpperCase();
                }
            }

            result.add(tmp);
        }

        return new KarmaArray(result.toArray(new KarmaElement[0]));
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @NotNull
    @Override
    public final Iterator<KarmaElement> iterator() {
        return new LinkedHashSet<>(elements).iterator();
    }

    /**
     * Copy the element
     *
     * @return the karma element
     */
    @Override
    public final KarmaElement copy() {
        return new KarmaArray(elements.toArray(new KarmaElement[0]));
    }

    /**
     * Get if the element is an array
     *
     * @return if the element is an array
     */
    @Override
    public final boolean isArray() {
        return true;
    }

    /**
     * Get if the element is an array with keys
     *
     * @return if the element is an array with key and values
     */
    @Override
    public final boolean isKeyArray() {
        return false;
    }

    /**
     * Get if the element is a string
     *
     * @return if the element is a string
     */
    @Override
    public final boolean isString() {
        return false;
    }

    /**
     * Get if the element is a number
     *
     * @return if the element is a number
     */
    @Override
    public final boolean isNumber() {
        return false;
    }

    /**
     * Get if the element is a boolean
     *
     * @return if the element is a boolean
     */
    @Override
    public final boolean isBoolean() {
        return false;
    }

    /**
     * Get if the element is valid
     *
     * @return if the element is valid
     */
    @Override
    public final boolean isValid() {
        for (KarmaElement element : elements)
            if (element == null)
                return false;

        return true;
    }

    /**
     * Fill the array info
     *
     * @param array the array
     * @param strings the array strings
     * @param numbers the array numbers
     * @param booleans the array booleans
     */
    private void fill(final KarmaElement array, final Set<String> strings, final Set<Number> numbers, final Set<Boolean> booleans) {
        if (array instanceof KarmaArray) {
            KarmaArray r = (KarmaArray) array;
            r.forEach((item) -> {
                if (!item.isArray() && !item.isKeyArray()) {
                    KarmaObject obj = item.getObjet();
                    if (obj.isString()) {
                        strings.add(obj.toString());
                    }
                    if (obj.isNumber()) {
                        numbers.add(obj.getNumber());
                    }
                    if (obj.isBoolean()) {
                        booleans.add(obj.getBoolean());
                    }
                } else {
                    fill(item, strings, numbers, booleans);
                }
            });
        }
        if (array instanceof KarmaKeyArray) {
            KarmaKeyArray rk = (KarmaKeyArray) array;
            rk.forEach((item) -> {
                if (!item.isArray() && !item.isKeyArray()) {
                    KarmaObject obj = item.getObjet();
                    if (obj.isString()) {
                        strings.add(obj.toString());
                    }
                    if (obj.isNumber()) {
                        numbers.add(obj.getNumber());
                    }
                    if (obj.isBoolean()) {
                        booleans.add(obj.getBoolean());
                    }
                } else {
                    fill(item, strings, numbers, booleans);
                }
            });
        }
    }
}
