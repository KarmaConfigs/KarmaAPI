package ml.karmaconfigs.api.common.utils.url;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import ml.karmaconfigs.api.common.utils.string.StringUtils;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Post request
 */
public final class Post {

    private final Map<String, String> data;

    private String json = "";

    /**
     * Initialize the post request
     */
    Post() {
        data = new ConcurrentHashMap<>();
        data.put("KarmaAPI_" + StringUtils.generateString().create(), Instant.now().toString());
    }

    /**
     * Add data to the post data
     *
     * @param key the data key
     * @param value the data value
     * @return this instance
     */
    public Post add(final String key, final Object value) {
        String v;
        try {
            v = value.toString();
        } catch (Throwable ex) {
            v = String.valueOf(value);
        }

        data.put(key, v);
        return this;
    }

    /**
     * Remove data from the post data
     *
     * @param key the data key
     * @return this instance
     */
    public Post remove(final String key) {
        data.remove(key);
        return this;
    }

    /**
     * Set the post json
     *
     * @param j the post json
     * @return the post json
     */
    public Post setJson(final JsonObject j) {
        json = new GsonBuilder().setLenient().setPrettyPrinting().create().toJson(j);

        return this;
    }

    /**
     * Get a post value
     *
     * @param key the post key
     * @return this instance
     */
    public String get(final String key) {
        return data.get(key);
    }

    /**
     * Get the post json if set
     *
     * @return the post json
     */
    public String getJson() {
        return json;
    }

    /**
     * Get all the keys
     *
     * @return all the post keys
     */
    public Set<String> getData() {
        return data.keySet();
    }

    /**
     * Create a new post instance
     *
     * @return the post instance
     */
    public static Post newPost() {
        return new Post();
    }
}
