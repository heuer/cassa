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
package com.semagia.cassa.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IWritableRepresentation;

/**
 * Wraps a {@link InputStream} providing some convenient methods.
 * 
 * Note: The caller which receives a Graph instance MUST call {@link #close()}
 * or {@code getInputStream().close()} unless the input stream is written to an
 * output stream via {@link #write(OutputStream)} (closing the graph after it has
 * been written to an output stream is simply ignored).
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class Graph implements IWritableRepresentation, Closeable {

    private final InputStream _in;
    private final MediaType _mediaType;
    private final String _encoding;

    Graph(final InputStream in, final MediaType mediaType, final String encoding) {
        _in = in;
        _mediaType = mediaType;
        _encoding = encoding;
    }

    /**
     * Returns the input stream.
     * 
     * The input stream or this Graph instance MUST be closed when done.
     *
     * @return The input stream.
     */
    public InputStream getInputStream() {
        return _in;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.common.dm.IWritable#write(java.io.OutputStream)
     */
    @Override
    public void write(OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = _in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
        finally {
            _in.close();
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.common.dm.IWritableRepresentation#getMediaType()
     */
    @Override
    public MediaType getMediaType() {
        return _mediaType;
    }

    /**
     * Returns the encoding, like "utf-8".
     *
     * @return The encoding or {@code null} if it is unknown.
     */
    public String getEncoding() {
        return _encoding;
    }

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        _in.close();
    }

}
