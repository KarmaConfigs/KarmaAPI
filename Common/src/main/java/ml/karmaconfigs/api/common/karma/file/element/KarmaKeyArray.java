package ml.karmaconfigs.api.common.karma.file.element;

import ml.karmaconfigs.api.common.utils.string.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Karma element for the new KarmaFile API which
 * is very similar to json
 *
 * @author KarmaDev
 * @since 1.3.2-SNAPSHOT
 */
public class KarmaKeyArray extends KarmaElement implements Iterable<KarmaElement>, Iterator<String> {

    private int current = 0;

    private final Map<String, KarmaElement> elements = new LinkedHashMap<>();
    private final Map<KarmaElement, String> reverse = new LinkedHashMap<>();

    /**
     * Add an element
     *
     * @param element the element to add
     */
    public void add(final String key, final KarmaElement element, final boolean recursively) {
        elements.put(key, element);
        if (recursively)
            reverse.put(element, key);
    }

    /**
     * Remove an element
     *
     * @param key the element key to remove
     */
    public void remove(final String... key) {
        for (String k : key) {
            if (elements.containsKey(k)) {
                KarmaElement element = elements.remove(k);
                if (element != null) {
                    reverse.remove(element);
                }
            }
        }
    }

    /**
     * Remove an element
     *
     * @param element the elements to remove
     */
    public void remove(final KarmaElement... element) {
        for (KarmaElement e : element) {
            if (reverse.containsKey(e)) {
                String key = reverse.remove(e);
                if (key != null) {
                    elements.remove(key);
                }
            }
        }
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

        for (KarmaElement item : elements.values()) {
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

        for (KarmaElement item : elements.values()) {
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

        for (KarmaElement item : elements.values()) {
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
     * Get the array size
     *
     * @return the array size
     */
    public final int size() {
        return elements.size();
    }

    /**
     * Get the array elements
     *
     * @return the array elements
     */
    public final Map<String, KarmaElement> getElements() {
        return new LinkedHashMap<>(elements);
    }

    /**
     * Get if the array contains a key
     *
     * @param key the key
     * @return if the array contains the key
     */
    public final boolean containsKey(final String key) {
        for (String k : elements.keySet()) {
            if (k.equalsIgnoreCase(key)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the element at that index
     *
     * @param index the element index
     * @return the element index
     */
    public final KarmaElement get(final int index) {
        int curr = 0;
        for (KarmaElement element : elements.values()) {
            curr++;

            if (curr == index || curr == elements.size()) {
                return element;
            }
        }

        return null;
    }

    /**
     * Get the element
     *
     * @param key the element key
     * @return the element
     */
    public final KarmaElement get(final String key) {
        return elements.getOrDefault(key, null);
    }

    /**
     * Get the key
     *
     * @param element the key element
     * @return the key
     */
    public final String get(final KarmaElement element) {
        return reverse.getOrDefault(element, null);
    }

    /**
     * Get the array keys
     *
     * @return the array keys
     */
    public final Set<String> getKeys() {
        return new LinkedHashSet<>(elements.keySet());
    }

    /**
     * Get if a key element is recursive
     *
     * @param key the key
     * @return if the key element is recursive
     */
    public boolean isRecursive(final String key) {
        KarmaElement element = elements.getOrDefault(key, null);
        if (element != null) {
            //Return true if the key element can be retrieved with the key and vice versa
            String tmp = reverse.getOrDefault(element, null);
            return tmp != null && tmp.equals(key);
        }

        return false;
    }

    /**
     * Get if a element is recursive
     *
     * @param element the element
     * @return if the element is recursive
     */
    public boolean isRecursive(final KarmaElement element) {
        String key = reverse.getOrDefault(element, null);
        if (key != null) {
            //Return true if the element key can be retrieved with the element and vice versa
            KarmaElement tmp = elements.getOrDefault(key, null);
            return tmp != null && tmp == element;
        }

        return false;
    }

    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public final boolean hasNext() {
        return current < elements.size();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     */
    @Override
    public final @Nullable String next() {
        int index = 0;
        for (String str : elements.keySet()) {
            index++;

            if (index == current + 1) {
                current = index;
                return str;
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
        for (KarmaElement current : elements.values()) {
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
        for (KarmaElement current : elements.values()) {
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
        return new LinkedHashSet<>(elements.values()).iterator();
    }

    /**
     * Copy the element
     *
     * @return the karma element
     */
    @Override
    public final KarmaElement copy() {
        KarmaKeyArray key = new KarmaKeyArray();
        for (String str : elements.keySet()) {
            key.add(str, elements.getOrDefault(str, null), isRecursive(str));
        }

        return key;
    }

    /**
     * Get if the element is an array
     *
     * @return if the element is an array
     */
    @Override
    public final boolean isArray() {
        return false;
    }

    /**
     * Get if the element is an array with keys
     *
     * @return if the element is an array with key and values
     */
    @Override
    public final boolean isKeyArray() {
        return true;
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
        for (KarmaElement element : elements.values())
            if (element == null)
                return false;

        for (String key : reverse.values())
            if (key == null)
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
        StringBuilder builder = new StringBuilder();

        elements.forEach((k, e) -> builder.append("[@").append(e.getClass().getSimpleName()).append("/").append(k).append("]:").append(e).append(", "));

        return StringUtils.replaceLast(builder.toString(), ", ", "");
    }
}
