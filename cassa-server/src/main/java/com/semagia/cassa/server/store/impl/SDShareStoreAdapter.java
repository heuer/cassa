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
import java.util.Collections;

import com.semagia.cassa.common.dm.IFragmentInfo;
import com.semagia.cassa.common.dm.IGraphInfo;
import com.semagia.cassa.server.store.GraphNotExistsException;
import com.semagia.cassa.server.store.ISDShareStore;
import com.semagia.cassa.server.store.IStore;
import com.semagia.cassa.server.store.StoreException;

/**
 * Adapts a {@link IStore} instance to a {@link ISDShareStore}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class SDShareStoreAdapter extends DelegatingStore implements ISDShareStore {

    public SDShareStoreAdapter(final IStore store) {
        super(store);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.ISDShareStore#getLastModificationFragments(java.net.URI)
     */
    @Override
    public long getLastModificationFragments(URI graphURI)
            throws GraphNotExistsException, StoreException {
        return _store.getLastModification();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.ISDShareStore#getLastModificationSnapshots(java.net.URI)
     */
    @Override
    public long getLastModificationSnapshots(URI graphURI)
            throws GraphNotExistsException, StoreException {
        return _store.getLastModification();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.ISDShareStore#getFragments(java.net.URI)
     */
    @Override
    public Iterable<IFragmentInfo> getFragments(URI graphURI)
            throws GraphNotExistsException, StoreException {
        return Collections.emptyList();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.ISDShareStore#getFragments(java.net.URI, long)
     */
    @Override
    public Iterable<IFragmentInfo> getFragments(URI graphURI, long since)
            throws GraphNotExistsException, StoreException {
        return Collections.emptyList();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.ISDShareStore#getSnapshots(java.net.URI)
     */
    @Override
    public Iterable<IGraphInfo> getSnapshots(URI graphURI)
            throws GraphNotExistsException, StoreException {
        return Collections.singleton(_store.getGraphInfo(graphURI));
    }

}
