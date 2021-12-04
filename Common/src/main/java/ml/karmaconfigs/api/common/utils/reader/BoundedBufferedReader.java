package ml.karmaconfigs.api.common.utils.reader;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;

/**
 * Karma bounded buffered reader for
 * files with long text lines
 */
public final class BoundedBufferedReader extends BufferedReader implements Serializable {

    /**
     * The reader max lines
     */
    private final int readerMaxLines;
    /**
     * The reader max lines length
     */
    private final int readerMaxLineLen;

    /**
     * The reader current line
     */
    private int currentLine = 1;

    /**
     * Initialize the bounded buffered reader
     *
     * @param reader the reader
     * @param maxLines the maximum amount of lines
     * @param maxLineLen the maximum length of the lines
     */
    public BoundedBufferedReader(final Reader reader, final int maxLines, final int maxLineLen) {
        super(reader);
        if (maxLines <= 0 || maxLineLen <= 0)
            throw new IllegalArgumentException("BoundedBufferedReader - maxLines and maxLineLen must be greater than 0");
        this.readerMaxLines = maxLines;
        this.readerMaxLineLen = maxLineLen;
    }

    /**
     * Initialize the bounded buffered reader
     *
     * @param reader the reader
     */
    public BoundedBufferedReader(final Reader reader) {
        super(reader);
        this.readerMaxLines = 1024;
        this.readerMaxLineLen = 1024;
    }

    /**
     * Read the line
     *
     * @return the next line
     * @throws IOException if something goes wrong
     */
    public String readLine() throws IOException {
        if (this.currentLine > this.readerMaxLines)
            throw new IOException("BoundedBufferedReader - Line read limit has been reached.");

        this.currentLine++;
        int currentPos = 0;
        char[] data = new char[this.readerMaxLineLen];
        int currentCharVal = read();
        while (currentCharVal != 13 && currentCharVal != 10 && currentCharVal >= 0) {
            data[currentPos++] = (char) currentCharVal;
            if (currentPos < this.readerMaxLineLen)
                currentCharVal = read();
        }
        if (currentCharVal < 0) {
            if (currentPos > 0)
                return new String(data, 0, currentPos);
            return null;
        }
        if (currentCharVal == 13) {
            mark(1);
            if (read() != 10)
                reset();
        }

        return new String(data, 0, currentPos);
    }
}
