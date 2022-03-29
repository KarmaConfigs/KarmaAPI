package ml.karmaconfigs.api.common.utils.string.util;

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

/**
 * Karma units
 */
public enum KarmaUnit {
    /**
     * Single millisecond
     */
    MILLISECOND("ms"),
    /**
     * More than 1 millisecond
     */
    MILLISECONDS("ms"),
    /**
     * Single second
     */
    SECOND("sec"),
    /**
     * More than 1 second
     */
    SECONDS("seconds"),
    /**
     * Single minute
     */
    MINUTE("min"),
    /**
     * More than 1 minute
     */
    MINUTES("minutes"),
    /**
     * Single hour
     */
    HOUR("h"),
    /**
     * More than 1 hour
     */
    HOURS("hours"),
    /**
     * Single day
     */
    DAY("d"),
    /**
     * More than 1 day
     */
    DAYS("days"),
    /**
     * Single week
     */
    WEEK("w"),
    /**
     * More than 1 week
     */
    WEEKS("weeks"),
    /**
     * Single month
     */
    MONTH("month"),
    /**
     * More than 1 month
     */
    MONTHS("months"),
    /**
     * Single year
     */
    YEAR("year"),
    /**
     * More than 1 year
     */
    YEARS("years");

    /**
     * The unit name
     */
    private final String unit;

    /**
     * Initialize the karma unit
     *
     * @param name the unit name
     */
    KarmaUnit(final String name) {
        unit = name;
    }

    /**
     * Get the unit name
     *
     * @return the unit name
     */
    public String getUnit() {
        return unit;
    }
}
