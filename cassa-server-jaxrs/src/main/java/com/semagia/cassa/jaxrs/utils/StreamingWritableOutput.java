/*
 * Copyright 2008 - 2011 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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
package com.semagia.cassa.jaxrs.utils;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import com.semagia.cassa.common.dm.IWritable;

/**
 * {@link StreamingOutput} implementation that wraps a {@link IWritable}
 * instance.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class StreamingWritableOutput implements StreamingOutput {

    private final IWritable _writable;

    public StreamingWritableOutput(final IWritable writable) {
        if (writable == null) {
            throw new IllegalArgumentException("The writable must not be null");
        }
        _writable = writable;
    }

    /* (non-Javadoc)
     * @see javax.ws.rs.core.StreamingOutput#write(java.io.OutputStream)
     */
    @Override
    public void write(final OutputStream out) throws IOException,
            WebApplicationException {
        _writable.write(out);
    }

}
