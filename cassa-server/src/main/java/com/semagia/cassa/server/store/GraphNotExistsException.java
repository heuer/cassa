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
package com.semagia.cassa.server.store;

import java.net.URI;

/**
 * Exception thrown if a client tries to access a non-existing graph.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
@SuppressWarnings("serial")
public class GraphNotExistsException extends StoreException {

    private final URI _uri;

    /**
     * 
     *
     * @param uri The non-existing graph URI.
     */
    public GraphNotExistsException(final URI uri) {
        super("The graph " + (uri == IStore.DEFAULT_GRAPH ? "'default'" : "<" + uri.toString() + ">") + " does not exist");
        _uri = uri;
    }

    /**
     * Returns the non-existing graph URI.
     *
     * @return The non-existing graph URI.
     */
    public URI getURI() {
        return _uri;
    }

}
