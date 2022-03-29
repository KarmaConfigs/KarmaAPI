package ml.karmaconfigs.api.bukkit.util.spigot;

/*
 * This file is part of KarmaAPI, licensed under the MIT License.
 *
 *  Copyright (c) karma (KarmaDev) <karmaconfigs@gmail.com>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import ml.karmaconfigs.api.common.utils.url.HttpUtil;
import ml.karmaconfigs.api.common.utils.url.URLUtils;

import java.net.URL;

/**
 * Karma implementation of XenforoResourceManagerAPI
 */
@SuppressWarnings("unused")
public final class SpigotRequest {

    /**
     * Google gson
     */
    private final static Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();

    /**
     * Request method
     */
    private Request method = Request.DEFAULT;

    /**
     * Set the spigot request method
     *
     * @param request the request
     * @return this instance
     */
    public SpigotRequest request(final Request request) {
        method = request;

        return this;
    }

    /**
     * Push the request
     *
     * @return the request result
     * @throws IllegalStateException if the request method does not
     * match with this push or url/response is null
     */
    public JsonElement push() throws IllegalStateException {
        URL url;

        switch (method) {
            case DEFAULT:
            case LIST_RESOURCES:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=listResources");
                break;
            case LIST_CATEGORIES:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=listResourceCategories");
                break;
            default:
                throw new IllegalStateException("Cannot push empty data to request " + method.name());
        }

        if (url != null) {
            HttpUtil utils = URLUtils.extraUtils(url);
            if (utils != null) {
                String response = utils.getResponse();

                if (response != null) {
                    return gson.fromJson(response, JsonElement.class);
                } else {
                    throw new IllegalStateException("Fetched request " + method.name() + " but response was null");
                }
            } else {
                throw new IllegalStateException("Couldn't fetch for request " + method.name() + " because something went internally wrong");
            }
        } else {
            throw new IllegalStateException("Couldn't fetch for request " + method.name() + " because URL is null");
        }
    }

    /**
     * Push the request
     *
     * @param data the data
     * @return the request result
     * @throws IllegalStateException if the request method does not
     * match with this push or url/response is null
     */
    public JsonElement push(final int data) throws IllegalStateException {
        URL url;

        switch (method) {
            case DEFAULT:
            case GET_RESOURCE:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=getResource&id=" + data);
                break;
            case GET_BY_AUTHOR:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=getResourcesByAuthor&id=" + data);
                break;
            case LIST_CATEGORIES:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=listResourceCategories");
                break;
            case GET_UPDATE:
                url = URLUtils.getOrNull("ttps://api.spigotmc.org/simple/0.2/index.php?action=getResourceUpdate&id=" + data);
                break;
            case GET_UPDATES:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=getResourceUpdates&id=" + data);
                break;
            case GET_AUTHOR:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=getAuthor&id=" + data);
                break;
            default:
                throw new IllegalStateException("Cannot push empty data to request " + method.name());
        }

        if (url != null) {
            HttpUtil utils = URLUtils.extraUtils(url);
            if (utils != null) {
                String response = utils.getResponse();

                if (response != null) {
                    return gson.fromJson(response, JsonElement.class);
                } else {
                    throw new IllegalStateException("Fetched request " + method.name() + " but response was null");
                }
            } else {
                throw new IllegalStateException("Couldn't fetch for request " + method.name() + " because something went internally wrong");
            }
        } else {
            throw new IllegalStateException("Couldn't fetch for request " + method.name() + " because URL is null");
        }
    }

    /**
     * Push the request
     *
     * @param data the data
     * @param type the argument type
     * @return the request result
     * @throws IllegalStateException if the request method does not
     * match with this push or url/response is null
     */
    public JsonElement push(final int data, final VarType type) throws IllegalStateException {
        URL url;

        switch (method) {
            case DEFAULT:
            case LIST_RESOURCES:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=listResources&" + type.get() + "=" + data);
                break;
            case GET_RESOURCE:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=getResource&id=" + data);
                break;
            case GET_BY_AUTHOR:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=getResourcesByAuthor&id=" + data);
                break;
            case LIST_CATEGORIES:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=listResourceCategories");
                break;
            case GET_UPDATE:
                url = URLUtils.getOrNull("ttps://api.spigotmc.org/simple/0.2/index.php?action=getResourceUpdate&id=" + data);
                break;
            case GET_UPDATES:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=getResourceUpdates&id=" + data);
                break;
            case GET_AUTHOR:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=getAuthor&id=" + data);
                break;
            default:
                throw new IllegalStateException("Cannot push empty data to request " + method.name());
        }

        if (url != null) {
            HttpUtil utils = URLUtils.extraUtils(url);
            if (utils != null) {
                String response = utils.getResponse();

                if (response != null) {
                    return gson.fromJson(response, JsonElement.class);
                } else {
                    throw new IllegalStateException("Fetched request " + method.name() + " but response was null");
                }
            } else {
                throw new IllegalStateException("Couldn't fetch for request " + method.name() + " because something went internally wrong");
            }
        } else {
            throw new IllegalStateException("Couldn't fetch for request " + method.name() + " because URL is null");
        }
    }

    /**
     * Push the request
     *
     * @param data the data
     * @param second the second data
     * @return the request result
     * @throws IllegalStateException if the request method does not
     * match with this push or url/response is null
     */
    public JsonElement push(final int data, final int second) throws IllegalStateException {
        URL url;

        switch (method) {
            case DEFAULT:
            case LIST_RESOURCES:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=listResources&cat=" + data + "&page=" + second);
                break;
            case GET_RESOURCE:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=getResource&id=" + data + "&page=" + second);
                break;
            case GET_BY_AUTHOR:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=getResourcesByAuthor&id=" + data + "&page=" + second);
                break;
            case LIST_CATEGORIES:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=listResourceCategories");
                break;
            case GET_UPDATE:
                url = URLUtils.getOrNull("ttps://api.spigotmc.org/simple/0.2/index.php?action=getResourceUpdate&id=" + data);
                break;
            case GET_UPDATES:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=getResourceUpdates&id=" + data + "&page=" + second);
                break;
            case GET_AUTHOR:
                url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=getAuthor&id=" + data);
                break;
            default:
                throw new IllegalStateException("Cannot push empty data to request " + method.name());
        }

        if (url != null) {
            HttpUtil utils = URLUtils.extraUtils(url);
            if (utils != null) {
                String response = utils.getResponse();

                if (response != null) {
                    return gson.fromJson(response, JsonElement.class);
                } else {
                    throw new IllegalStateException("Fetched request " + method.name() + " but response was null");
                }
            } else {
                throw new IllegalStateException("Couldn't fetch for request " + method.name() + " because something went internally wrong");
            }
        } else {
            throw new IllegalStateException("Couldn't fetch for request " + method.name() + " because URL is null");
        }
    }

    /**
     * Push the request
     *
     * @param data the data
     * @return the request result
     * @throws IllegalStateException if the request method does not
     * match with this push or url/response is null
     */
    public JsonElement push(final String data) throws IllegalStateException {
        if (method.equals(Request.FIND_AUTHOR)) {
            URL url = URLUtils.getOrNull("https://api.spigotmc.org/simple/0.2/index.php?action=findAuthor&name=" + data);
            if (url != null) {
                HttpUtil utils = URLUtils.extraUtils(url);
                if (utils != null) {
                    String response = utils.getResponse();

                    if (response != null) {
                        return gson.fromJson(response, JsonElement.class);
                    } else {
                        throw new IllegalStateException("Fetched request " + method.name() + " but response was null");
                    }
                } else {
                    throw new IllegalStateException("Couldn't fetch for request " + method.name() + " because something went internally wrong");
                }
            } else {
                throw new IllegalStateException("Couldn't fetch for request " + method.name() + " because URL is null");
            }
        } else {
            throw new IllegalStateException("Cannot push empty data to request " + method.name());
        }
    }

    /**
     * Push the request
     *
     * @return the request result
     * @throws IllegalStateException if the request method does not
     * match with this push or url/response is null
     */
    public String pushString() throws IllegalStateException {
        JsonElement element = push();
        if (element != null) {
            return gson.toJson(element);
        }

        return "";
    }

    /**
     * Push the request
     *
     * @param data the data
     * @return the request result
     * @throws IllegalStateException if the request method does not
     * match with this push or url/response is null
     */
    public String pushString(final int data) throws IllegalStateException {
        JsonElement element = push(data);
        if (element != null) {
            return gson.toJson(element);
        }

        return "";
    }

    /**
     * Push the request
     *
     * @param data the data
     * @param type the argument type
     * @return the request result
     * @throws IllegalStateException if the request method does not
     * match with this push or url/response is null
     */
    public String pushString(final int data, final VarType type) throws IllegalStateException {
        JsonElement element = push(data, type);
        if (element != null) {
            return gson.toJson(element);
        }

        return "";
    }

    /**
     * Push the request
     *
     * @param data the data
     * @param second the second data
     * @return the request result
     * @throws IllegalStateException if the request method does not
     * match with this push or url/response is null
     */
    public String pushString(final int data, final int second) throws IllegalStateException {
        JsonElement element = push(data, second);
        if (element != null) {
            return gson.toJson(element);
        }

        return "";
    }

    /**
     * Push the request
     *
     * @param data the data
     * @return the request result
     * @throws IllegalStateException if the request method does not
     * match with this push or url/response is null
     */
    public String pushString(final String data) throws IllegalStateException {
        JsonElement element = push(data);
        if (element != null) {
            return gson.toJson(element);
        }

        return "";
    }
}
