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
import java.io.InputStream;
import java.io.OutputStream;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IWritableRepresentation;
import com.semagia.cassa.common.dm.impl.InputStreamWritableRepresentation;

/**
 * Wraps a {@link InputStream} providing some convenient methods.
 * 
 * Note: The caller which receives a Graph instance MUST call {@link #close()}
 * or {@code getInputStream().close()} unless the input stream is written to an
 * output stream via {@link #write(OutputStream)} (closing the graph after it has
 * been written to an output stream is ignored).
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class Graph extends InputStreamWritableRepresentation implements IWritableRepresentation, Closeable {

    /**
     * Creates an instance with content length = -1.
     * 
     * @param in The input stream.
     * @param mediaType The media type.
     * @param encoding The encoding or {@code null}.
     */
    Graph(final InputStream in, final MediaType mediaType, final String encoding) {
        this(in, mediaType, encoding, -1);
    }

    /**
     * Creates an instance with the provided inputstream, a mediatype, and 
     * an optional encoding and content length.
     * 
     * @param in The input stream.
     * @param mediaType The media type.
     * @param encoding The encoding or {@code null}.
     * @param length The content length or {@code -1} if the content length is unknown.
     */
    Graph(final InputStream in, final MediaType mediaType, final String encoding, final long length) {
        super(in, mediaType, encoding, length);
    }

}
