package ml.karmaconfigs.api.common.version.spigot;

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

import com.google.gson.*;
import ml.karmaconfigs.api.common.karma.KarmaAPI;
import ml.karmaconfigs.api.common.timer.scheduler.LateScheduler;
import ml.karmaconfigs.api.common.timer.scheduler.worker.AsyncLateScheduler;
import ml.karmaconfigs.api.common.utils.URLUtils;
import ml.karmaconfigs.api.common.utils.string.StringUtils;

import java.net.URL;

/**
 * Karma spigot checker
 */
public final class SpigotChecker {

    private final int resource_id;

    private final static String fetch_version = "https://api.spigotmc.org/simple/0.2/index.php?action=getResource&id={0}";
    private final static String fetch_update = "https://api.spigotmc.org/simple/0.2/index.php?action=getResourceUpdates&id={0}&page={1}";
    private final static String update_url = "https://www.spigotmc.org/resources/{0}/update?update={1}";

    /**
     * Initialize the checker
     *
     * @param id the spigot resource id
     */
    public SpigotChecker(final int id) {
        resource_id = id;
    }

    /**
     * Get the project latest version
     *
     * @return the project latest versions
     */
    public String getLatest() {
        try {
            URL url = new URL(StringUtils.formatString(fetch_version, resource_id));

            String response = URLUtils.getResponse(url);
            if (response != null) {
                Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
                JsonObject object = gson.fromJson(response, JsonObject.class);

                if (object.has("current_version")) {
                    return object.get("current_version").getAsString();
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Get the project update URL
     *
     * @return the project update URL
     */
    public LateScheduler<URL> getUpdateURL() {
        LateScheduler<URL> result = new AsyncLateScheduler<>();

        KarmaAPI.source(false).async().queue(() -> {
            try {
                int page = 0;
                URL url = new URL(StringUtils.formatString(fetch_update, resource_id, page));

                String response = URLUtils.getResponse(url);
                if (response != null) {
                    while (!response.equalsIgnoreCase("[]")) {
                        page++;
                        url = new URL(StringUtils.formatString(fetch_update, resource_id, page));

                        response = URLUtils.getResponse(url);
                    }
                }

                page--;
                url = new URL(StringUtils.formatString(fetch_update, resource_id, page));

                response = URLUtils.getResponse(url);

                Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
                JsonArray array = gson.fromJson(response, JsonArray.class);

                JsonElement last = array.get(array.size() - 1);
                if (last.isJsonObject()) {
                    JsonObject object = last.getAsJsonObject();
                    if (object.has("id")) {
                        int updateId = object.get("id").getAsInt();

                        URL updateInfo = new URL(StringUtils.formatString(update_url, resource_id, updateId));
                        result.complete(updateInfo, null);
                    } else {
                        result.complete(null, new Exception("Failed to fetch last update id for project with id " + resource_id));
                    }
                } else {
                    result.complete(null, new Exception("Failed to fetch last update for project with id " + resource_id));
                }
            } catch (Throwable ex) {
                result.complete(null, ex);
            }
        });

        return result;
    }
}
