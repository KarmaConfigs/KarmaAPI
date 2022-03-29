package ml.karmaconfigs.api.common.utils.uuid;

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

import ml.karmaconfigs.api.common.utils.UUIDUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Online Karma API response ( OKA Response )
 */
public final class OKAResponse {

    private final String nick;
    private final UUID offline;
    private final UUID online;

    /**
     * Initialize the response
     *
     * @param name the response name
     * @param off the response offline uuid
     * @param on the response online uuid
     */
    public OKAResponse(final String name, final UUID off, final UUID on) {
        nick = name;
        offline = off;
        online = on;
    }

    /**
     * Get the response name
     *
     * @return the response name
     */
    public String getNick() {
        return nick;
    }

    /**
     * Get the response uuid
     *
     * @param type the uuid type
     * @return the response uuid
     */
    @Nullable
    public UUID getId(final UUIDType type) {
        switch (type) {
            case ONLINE:
                return online;
            case OFFLINE:
            default:
                return offline;
        }
    }

    /**
     * Get the trimmed UUID
     *
     * @param type the uuid type
     * @return the trimmed uuid
     */
    @NotNull
    public String getTrimmed(final UUIDType type) {
        switch (type) {
            case ONLINE:
                return (online != null ? online.toString() : "");
            case OFFLINE:
            default:
                return (offline != null ? offline.toString() : "");
        }
    }
}
