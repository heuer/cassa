/*
 * Copyright 2011 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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
package com.semagia.cassa.common.dm.impl;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IWritableRepresentation;

/**
 * {@link IWritableRepresentation} which wraps an {@link InputStream} which is
 * writen onto the outputstream within {@link #write(OutputStream)}.
 * 
 * Note: The caller which receives an instance MUST call {@link #close()}
 * or {@code getInputStream().close()} unless the input stream is written to an
 * output stream via {@link #write(OutputStream)} (closing the instance after it 
 * has been written to an output stream is simply ignored).
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class InputStreamWritableRepresentation implements
        IWritableRepresentation, Closeable  {

    private final static int _BUFFER_SIZE = 2048;

    private final InputStream _stream;
    private final MediaType _mediaType;
    private final String _encoding;
    private final long _length;

    /**
     * Creates an instance with the provided stream and media type.
     * 
     * @param stream An input stream.
     * @param mediaType A media type.
     */
    public InputStreamWritableRepresentation(final InputStream stream, final MediaType mediaType) {
        this(stream, mediaType, null);
    }

    /**
     * Creates an instance with the provided input stream, media type and
     * an optional encoding.
     *  
     * @param stream An input stream.
     * @param mediaType A media type.
     * @param encoding A string like "UTF-8" or {@code null}.
     */
    public InputStreamWritableRepresentation(final InputStream stream, final MediaType mediaType, final String encoding) {
        this(stream, mediaType, encoding, -1);
    }

    /**
     * Creates an instance with the provided input stream, media type and
     * an optional encoding and content length.
     *  
     * @param stream An input stream.
     * @param mediaType A media type.
     * @param encoding A string like "UTF-8" or {@code null}.
     * @param length The content length or {@code -1} if it's unknown.
     */
    public InputStreamWritableRepresentation(final InputStream stream,
            final MediaType mediaType, final String encoding, final long length) {
        _stream = stream;
        _mediaType = mediaType;
        _encoding = encoding;
        _length = length;
    }

    /**
     * Returns the input stream.
     * 
     * The input stream MUST be closed when done.
     *
     * @return The input stream.
     * @throws IOException In case of an I/O error.
     */
    public InputStream getInputStream() throws IOException {
        return _stream;
    }

    /**
     * Returns the length of the content.
     * 
     * Returns {@code -1} if the length is unknown.
     * 
     * @return The length of the content or {@code -1}
     */
    public long getContentLength() {
        return _length;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.common.dm.IWritable#write(java.io.OutputStream)
     */
    @Override
    public void write(final OutputStream out) throws IOException {
        byte[] buffer = new byte[_BUFFER_SIZE];
        int len;
        try {
            if (_length < 0) {
                while ((len = _stream.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }
            else {
                long remaining = _length;
                while (remaining > 0) {
                    len = _stream.read(buffer, 0, (int)Math.min(_BUFFER_SIZE, remaining));
                    if (len == -1) {
                        break;
                    }
                    out.write(buffer, 0, len);
                    remaining -= len;
                }
            }
        }
        finally {
            _stream.close();
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.common.dm.IWritableRepresentation#getMediaType()
     */
    @Override
    public MediaType getMediaType() {
        return _mediaType;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.common.dm.IWritableRepresentation#getEncoding()
     */
    @Override
    public String getEncoding() {
        return _encoding;
    }

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        _stream.close();
    }

}
