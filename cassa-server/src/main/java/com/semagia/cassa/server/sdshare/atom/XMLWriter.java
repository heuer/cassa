/*
 * Copyright 2008 - 2009 Lars Heuer (heuer[at]semagia.com)
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
package com.semagia.cassa.server.sdshare.atom;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Simple SAX-alike XML writer.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class XMLWriter {

    public static final Attributes EMPTY_ATTRS = new AttributesImpl(); 

    private static final char _NL = '\n';

    private OutputStreamWriter _out;

    private final String _encoding;

    private int _depth;

    private boolean _prettify;

    private int _bits = -1;

    public XMLWriter(final OutputStream out) throws IOException {
        this(out, "UTF-8");
    }

    public XMLWriter(final OutputStream out, final String encoding) throws IOException {
        _out = new OutputStreamWriter(out, encoding);
        _encoding = encoding;
        if ("UTF-8".equalsIgnoreCase(encoding) 
                || "UTF-16".equalsIgnoreCase(encoding)) {
            _bits = 16;
        }
        else if ("ISO-8859-1".equalsIgnoreCase(encoding) 
                    || "Latin1".equalsIgnoreCase(encoding)) {
            _bits = 8;
        }
        else if ("US-ASCII".equalsIgnoreCase(encoding) 
                    || "ASCII".equalsIgnoreCase(encoding)) {
            _bits = 7;
        }
    }

    /**
     * Enables / disables prettifying the XML.
     *
     * @param prettify {@code true} to prettify, otherwise {@code false}.
     */
    public void setPrettify(final boolean prettify) {
        _prettify = prettify;
    }

    /**
     * Returns if the XML is prettified.
     *
     * @return {@code true} if the XML is prettified, otherwise {@code false}.
     */
    public boolean getPrettify() {
        return _prettify;
    }

    /**
     * Writes a comment.
     *
     * @param comment The comment.
     * @throws IOException In case of an error.
     */
    public void comment(final String comment) throws IOException {
        final char[] ch = comment.toCharArray();
        comment(ch, 0, ch.length);
    }

    

    /**
     * @see org.xml.sax.ext LexicalHandler#comment(char[] ch, int start, int length) 
     *
     * @param ch An array holding the characters in the comment.
     * @param start The starting position in the array.
     * @param length The number of characters to use from the array. 
     * @throws IOException In case of an error.
     */
    public void comment(final char[] ch, final int start, final int length) throws IOException {
        _out.write("<!-- ");
        final int len = start + length;
        for (int i=start; i < len; i++) {
            // -- is converted into - - 
            if (ch[i] == '-' && i+1 < len && ch[i+1] == '-') {
                _out.write("- ");
            }
            else {
                _out.write(ch[i]);
            }
        }
        _out.write(" -->");
    }

    /**
     * @see org.xml.sax.DocumentHandler#startDocument()
     */
    public void startDocument() throws IOException {
        _out.write("<?xml version=\"1.0\" encoding=\"");
        _out.write(_encoding);
        _out.write("\" standalone=\"yes\"?>");
        if (!_prettify) {
            _newline();
        }
        _depth = 0;
    }

    /**
     * @see org.xml.sax.DocumentHandler#endDocument()
     */
    public void endDocument() throws IOException {
        _newline();
        try {
            _out.flush();
        }
        finally {
            _out = null;
        }
    }

    /**
     * @see org.xml.sax.DocumentHandler#processingInstruction(String, String)
     * 
     * Note: Neither the target nor the data is checked for validity.
     * This method MUST NOT be used to create <?xml ...?> header!
     *
     * @param target The target.
     * @param data The data.
     */
    public void processingInstruction(final String target, final String data) throws IOException {
        _out.write("<?");
        _out.write(target);
        _out.write(' ');
        _out.write(data);
        _out.write("?>");
    }

    /**
     * Writes an element start with no attributes.
     */
    public void startElement(final String name) throws IOException {
        startElement(name, EMPTY_ATTRS);
    }

    /**
     * @see org.xml.sax.DocumentHandler#startElement(java.lang.String, org.xml.sax.AttributeList)
     */
    public void startElement(final String name, final Attributes attrs) throws IOException {
        _indent();
        _out.write('<');
        _out.write(name);
        _writeAttributes(attrs);
        _out.write('>');
        _depth++;
    }

    /**
     * @see org.xml.sax.DocumentHandler#endElement(java.lang.String)
     */
    public void endElement(final String name) throws IOException {
        _endElement(name, true);
    }

    private void _endElement(final String name, final boolean indent) throws IOException {
        _depth--;
        if (indent) {
            _indent();
        }
        _out.write("</");
        _out.write(name);
        _out.write('>');
    }

    public void emptyElement(final String name) throws IOException {
        emptyElement(name, EMPTY_ATTRS);
    }

    public void emptyElement(final String name, final Attributes attrs) throws IOException {
        _indent();
        _out.write('<');
        _out.write(name);
        _writeAttributes(attrs);
        _out.write("/>");
    }

    public void dataElement(final String name, final String data) throws IOException {
        dataElement(name, EMPTY_ATTRS, data);
    }

    public void dataElement(final String name, final Attributes attrs, final String data) throws IOException {
        startElement(name, attrs);
        characters(data);
        _endElement(name, false);
    }

    private void _writeAttributes(final Attributes attrs) throws IOException {
        char[] chars;
        for (int i=0; i < attrs.getLength(); i++) {
            _out.write(' ');
            _out.write(attrs.getLocalName(i));
            _out.write("=\"");
            chars = attrs.getValue(i).toCharArray();
            _writeEscapedCharacters(chars, 0, chars.length, true);
            _out.write('"');
        }
    }

    /**
     * Writes a <tt>#x0A</tt> to the output.
     *
     * @throws IOException If an error occurs.
     */
    private void _newline() throws IOException {
        _out.write(_NL);
    }

    private void _indent() throws IOException {
        if (!_prettify) {
            return;
        }
        _newline();
        final char[] chars = new char[_depth*2];
        Arrays.fill(chars, ' ');
        _out.write(chars);
    }

    /**
     * Writes the specified characters to the output.
     *
     * @param data The data to write.
     * @throws IOException If an error occurs.
     */
    public void characters(final String data) throws IOException {
        final char[] chars = data.toCharArray();
        characters(chars, 0, chars.length);
    }

    /**
     * @see org.xml.sax.DocumentHandler#characters(char[], int, int)
     */
    public void characters(final char[] chars, final int start, final int length) throws IOException {
        _writeEscapedCharacters(chars, start, length, false);
    }
    
    private static boolean _isHighSurrogate(final char c) {
        return c >= 0xD800 && c <= 0xDBFF;
    }

    private static boolean _isLowSurrogate(final char c) {
        return c >= 0xDC00 && c <= 0xDFFF;
    }
    
    private boolean _shouldEscape(final char c) {
        if (_bits == 16) {
            return false;//return _isHighSurrogate(c);
        }
        if (_bits == 8) {
            return ((int) c > 255);
        }
        if (_bits == 7) {
            return ((int) c > 127);
        }
        else {
            return _isHighSurrogate(c);
        }
    }

    private static int _combineSurrogatePair(final char highSurrogate, final char lowSurrogate) {
        int high = highSurrogate & 0x7FF;
        int low  = lowSurrogate - 0xDC00;
        int highShifted = high << 10;
        int combined = highShifted | low; 
        int codePoint = combined + 0x10000;
        return codePoint;
    }

    private void _writeEscapedCharacters(final char[] ch, final int start, final int length,
            final boolean attributeValue) throws IOException {
        final int len = start + length;
        for (int i=start; i < len; i++) {
            switch (ch[i]) {
            case '&':
                _out.write("&amp;");
                break;
            case '<':
                _out.write("&lt;");
                break;
            case '>':
                _out.write("&gt;");
                break;
            case '\"':
                if (attributeValue) {
                    _out.write("&quot;");
                }
                else {
                    _out.write('\"');
                }
                break;
            default:
                if (_shouldEscape(ch[i])) {
                    char c = ch[i];
                    int uc = c;
                    if (_isHighSurrogate((char)c)) {
                        i++;
                        if (i < len) {
                            char low = ch[i];
                            if (!_isLowSurrogate(low)) {
                                throw new IOException("Could not decode surrogate pair 0x" + Integer.toHexString(c) + " / 0x" + Integer.toHexString(low));
                            }
                            else {
                                uc = _combineSurrogatePair((char)c, low);
                            }
                        }
                        else {
                            throw new IOException("Surrogate pair 0x" + Integer.toHexString(c) + " truncated");
                        }
                    }
                    String s = "&#x" + Integer.toHexString(uc).toUpperCase() + ';';
                    _out.write(s);
                }
                else {
                    _out.write(ch[i]);
                }
            }
        }
    }

}
