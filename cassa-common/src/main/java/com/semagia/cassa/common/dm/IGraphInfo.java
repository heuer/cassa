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
package com.semagia.cassa.common.dm;

import java.net.URI;
import java.util.List;

import com.semagia.cassa.common.MediaType;

/**
 * Immutable meta data about a graph.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public interface IGraphInfo {

    /**
     * Returns the last modification time of the graph.
     * 
     * In case the modification time is unknown, {@code -1} must be returned.
     *
     * @return The last modification time or -1.
     */
    public long getLastModification();
    
    /**
     * Returns the available serialization formats for this graph.
     * 
     * The list should be ordered, the preferred media type should be
     * the first entry.
     *
     * @return A non-empty list of supported media types.
     */
    public List<MediaType> getSupportedMediaTypes();

    /**
     * Returns the IRI of the graph.
     *
     * @return The IRI of this graph. This must not be {@code null}.
     */
    public URI getURI();

}
