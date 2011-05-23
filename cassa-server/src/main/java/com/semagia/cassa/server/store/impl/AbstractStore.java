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
package com.semagia.cassa.server.store.impl;

import java.net.URI;

import com.semagia.cassa.common.dm.IGraphInfo;
import com.semagia.cassa.server.store.GraphNotExistsException;
import com.semagia.cassa.server.store.IStore;
import com.semagia.cassa.server.store.StoreException;

/**
 * Abstract store which implements some common methods.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public abstract class AbstractStore implements IStore {

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#containsGraph(java.net.URI)
     */
    @Override
    public boolean containsGraph(URI graphURI) throws StoreException {
        if (graphURI == IStore.DEFAULT_GRAPH) {
            return true;
        }
        for (IGraphInfo info: getGraphInfos()) {
            if (info.getURI().equals(graphURI)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Raises an exception if the graph URI is not in the store.
     *
     * @param graphURI The graph URI to check.
     * @throws GraphNotExistsException In case the graph URI does not exist.
     * @throws StoreException In case of an error.
     */
    protected void ensureGraphExists(final URI graphURI) throws GraphNotExistsException, StoreException {
        if (graphURI != IStore.DEFAULT_GRAPH && !containsGraph(graphURI)) {
            throw new GraphNotExistsException(graphURI);
        }
    }

}
