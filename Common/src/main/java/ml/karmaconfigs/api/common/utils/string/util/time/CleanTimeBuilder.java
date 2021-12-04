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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Karma clean time builder
 */
public final class CleanTimeBuilder {

    private final TimeName name;
    private final long milliseconds;

    /**
     * Initialize the clean time builder
     *
     * @param n the time name
     * @param time the time in milliseconds
     */
    public CleanTimeBuilder(final TimeName n, final long time) {
        name = n;
        milliseconds = time;
    }

    /**
     * Create a new clean time text
     * output
     *
     * @return the time as string
     */
    public String create() {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long tmp_weeks = days / 7;
        long tmp_months = tmp_weeks / 4;
        long tmp_years = tmp_months / 12;

        long weeks = Math.round(tmp_weeks);
        long months = Math.round(tmp_months);
        long year = Math.round(tmp_years);

        int month = (int) (year != 0 ? Math.abs(year * 12 - months) : months);
        int week = (int) (months != 0 ? Math.abs(months * 4 - weeks) : weeks);
        int day = (int) (weeks != 0 ? Math.abs(weeks * 7 - days) : days);
        int hour = (int) (days != 0 ? Math.abs(days * 24 - hours) : hours);
        int minute = (int) (hours != 0 ? Math.abs(hours * 60 - minutes) : minutes);
        int second = (int) (minutes != 0 ? Math.abs(minutes * 60 - seconds) : seconds);
        
        int[] numbers = new int[]{(int) year, month, week, day, hour, minute, second};
        int index = 0;

        List<String> times = new ArrayList<>();
        for (int number : numbers) {
            /*
            0 = years
            1 = months
            2 = weeks
            3 = days
            4 = hours
            5 = minutes
            6 = seconds
             */
            switch (index) {
                case 0:
                    if (number > 0) {
                        if (number == 1) {
                            times.add(number + " " + name.get(KarmaUnit.YEAR));
                        } else {
                            times.add(number + " " + name.get(KarmaUnit.YEARS));
                        }
                    }
                    break;
                case 1:
                    if (number > 0) {
                        if (number == 1) {
                            times.add(number + " " + name.get(KarmaUnit.MONTH));
                        } else {
                            times.add(number + " " + name.get(KarmaUnit.MONTHS));
                        }
                    }
                    break;
                case 2:
                    if (number > 0) {
                        if (number == 1) {
                            times.add(number + " " + name.get(KarmaUnit.WEEK));
                        } else {
                            times.add(number + " " + name.get(KarmaUnit.WEEKS));
                        }
                    }
                    break;
                case 3:
                    if (number > 0) {
                        if (number == 1) {
                            times.add(number + " " + name.get(KarmaUnit.DAY));
                        } else {
                            times.add(number + " " + name.get(KarmaUnit.DAYS));
                        }
                    }
                    break;
                case 4:
                    if (number > 0) {
                        if (number == 1) {
                            times.add(number + " " + name.get(KarmaUnit.HOUR));
                        } else {
                            times.add(number + " " + name.get(KarmaUnit.HOURS));
                        }
                    }
                    break;
                case 5:
                    if (number > 0) {
                        if (number == 1) {
                            times.add(number + " " + name.get(KarmaUnit.MINUTE));
                        } else {
                            times.add(number + " " + name.get(KarmaUnit.MINUTES));
                        }
                    }
                    break;
                case 6:
                    if (number > 0) {
                        if (number == 1) {
                            times.add(number + " " + name.get(KarmaUnit.SECOND));
                        } else {
                            times.add(number + " " + name.get(KarmaUnit.SECONDS));
                        }
                    }
                    break;
                default:
                    break;
            }

            index++;
        }

        if (!times.isEmpty()) {
            index = 0;
            StringBuilder builder = new StringBuilder();
            for (String time : times) {
                builder.append(time);

                if (++index != times.size()) {
                    //1 or more than 1 items left
                    if (index == times.size() - 1) {
                        builder.append(" and ");
                    } else {
                        builder.append(", ");
                    }
                }
            }

            return builder.toString();
        } else {
            return String.valueOf(milliseconds);
        }
    }
}
