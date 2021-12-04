package ml.karmaconfigs.api.common.utils.string.util.time;

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

import ml.karmaconfigs.api.common.utils.string.util.KarmaUnit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Karma time name
 */
public class TimeName {

    /**
     * A map containing karma unit => unit name
     */
    private final Map<KarmaUnit, String> names = new ConcurrentHashMap<>();

    /**
     * Initialize the time name
     */
    TimeName() {
        for (KarmaUnit unit : KarmaUnit.values())
            names.put(unit, unit.getUnit());
    }

    /**
     * Add a custom karma unit name
     *
     * @param unit the unit
     * @param name the unit name
     * @return this instance
     */
    public TimeName add(final KarmaUnit unit, final String name) {
        names.put(unit, name);

        return this;
    }

    /**
     * Get a custom unit name
     *
     * @param unit the unit
     * @return the unit name
     */
    public String get(final KarmaUnit unit) {
        return names.getOrDefault(unit, unit.getUnit());
    }

    /**
     * Create a new time name instance
     *
     * @return a new time name instance
     */
    public static TimeName create() {
        return new TimeName();
    }
}
