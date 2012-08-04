/*
 * Copyright 2008 - 2010 Lars Heuer (heuer[at]semagia.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.semagia.cassa.server.sdshare.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.util.Arrays;

/**
 * Simple JSON serializer. This class is not usable as a generic JSON writer 
 * since it is possible to create an invalid JSON representation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class JSONWriter {

    private OutputStreamWriter _out;
    private boolean _wantComma;
    private int _depth;
    private boolean _prettify;

    public JSONWriter(OutputStream out, String encoding) throws IOException {
        _out = new OutputStreamWriter(out, encoding);
    }

    /**
     * Enables / disables newlines and indentation of JSON elements.
     * (newlines and indentation is enabled by default)
     *
     * @param prettify <tt>true</tt> to enable prettified JSON, otherwise <tt>false</tt>.
     */
    public void setPrettify(boolean prettify) {
        _prettify = prettify;
    }

    /**
     * Returns if newlines and indentation are enabled.
     *
     * @return <tt>true</tt> if prettified JSON is enabled, otherwise <tt>false</tt>.
     */
    public boolean getPrettify() {
        return _prettify;
    }

    /**
     * Indicates the start of the serialization.
     */
    public void startDocument() {
        _depth = 0;
    }

    /**
     * Indicates the end of the serialization.
     *
     * @throws IOException If an error occurs.
     */
    public void endDocument() throws IOException {
        _out.write('\n');
        _out.flush();
    }

    /**
     * Indents a line, iff {@link #getPrettify()} is enabled.
     *
     * @throws IOException If an error occurs.
     */
    private void _indent() throws IOException {
        if (_prettify) {
            if (_depth > 0) {
                _out.write('\n');
            }
            final char[] chars = new char[_depth*2];
            Arrays.fill(chars, ' ');
            _out.write(chars);
        }
    }

    /**
     * Start of a JSON object.
     *
     * @throws IOException If an error occurs.
     */
    public void startObject() throws IOException {
        if (_wantComma) {
            _out.write(',');
        }
        _indent();
        _out.write('{');
        _depth++;
        _wantComma = false;
    }

    /**
     * End of a JSON object.
     *
     * @throws IOException If an error occurs.
     */
    public void endObject() throws IOException {
        _out.write('}');
        _depth--;
        _wantComma = true;
    }

    /**
     * Start of a JSON array.
     *
     * @throws IOException If an error occurs.
     */
    public void startArray() throws IOException {
        _out.write('[');
        _depth++;
        _wantComma = false;
    }

    /**
     * End of a JSON array.
     *
     * @throws IOException If an error occurs.
     */
    public void endArray() throws IOException {
        _out.write(']');
        _depth--;
        _wantComma = true;
    }

    /**
     * Writes the key of a <tt>"key": value</tt> pair. 
     *
     * @param key The key to write.
     * @throws IOException If an error occurs.
     */
    public void key(final String key)  throws IOException {
        if (_wantComma) {
            _out.write(',');
            _indent();
        }
        _out.write(escape(key));
        _out.write(':');
        _wantComma = false;
    }

    /**
     * The value to write. The value is written in an escaped form.
     *
     * @param value The value to write.
     * @throws IOException If an error occurs.
     */
    public void value(final String value) throws IOException {
        if (_wantComma) {
            _out.write(',');
        }
        _out.write(escape(value));
        _wantComma = true;
    }

    /**
     * Writes a key/value pair.
     *
     * @param value The value to write.
     * @throws IOException If an error occurs.
     */
    public void keyValue(final String key, final String value) throws IOException {
        key(key);
        value(value);
    }

    /**
     * Escapes a string value.
     *
     * @param value The string to escape.
     * @return An escaped string, usable as JSON value.
     */
    public static String escape(String value) {
        // Code adapted from JSON.org (JSONObject.quote(String))
        // Copyrighted by JSON.org licensed under a BSD-license, see
        // complete copyright notice at the end of this file.
        char b;
        char c = 0;
        char[] chars = value.toCharArray();
        StringBuilder sb = new StringBuilder(chars.length + 4);
        sb.append('"');
        for (int i=0; i<chars.length; i++) {
            b = c;
            c = chars[i];
            switch (c) {
            case '\\':
            case '"':
                sb.append('\\');
                sb.append(c);
                break;
            case '/':
                if (b == '<') {
                    sb.append('\\');
                }
                sb.append(c);
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
                sb.append("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
                               (c >= '\u2000' && c < '\u2100')) {
                    sb.append("\\u000")
                        .append(Integer.toHexString(c));
                }
                else {
                    sb.append(c);
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    /*
    ===========================================================================
    Copyright of the JSONObject code which was used to implement the "escape"
    function.
    ===========================================================================

    Copyright (c) 2002 JSON.org

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    The Software shall be used for Good, not Evil.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
    */
}
