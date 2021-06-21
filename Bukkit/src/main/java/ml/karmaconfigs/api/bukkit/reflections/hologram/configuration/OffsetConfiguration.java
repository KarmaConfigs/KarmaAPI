package ml.karmaconfigs.api.bukkit.reflections.hologram.configuration;

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

import java.io.Serializable;

/**
 * Offset configuration
 */
public final class OffsetConfiguration implements Serializable {

    private final double x,y,z;

    /**
     * Initialize the offset configuration
     *
     * @param configuration the hologram configuration
     */
    OffsetConfiguration(final HologramConfiguration configuration) {
        x = configuration.offsetX;
        y = configuration.offsetY;
        z = configuration.offsetZ;
    }

    /**
     * Get the Z offset axis
     *
     * @return the Z offset axis
     */
    public final double getX() {
        return x;
    }

    /**
     * Get the Z offset axis
     *
     * @return the Z offset axis
     */
    public final double getY() {
        return y;
    }

    /**
     * Get the Z offset axis
     *
     * @return the Z offset axis
     */
    public final double getZ() {
        return z;
    }
}
