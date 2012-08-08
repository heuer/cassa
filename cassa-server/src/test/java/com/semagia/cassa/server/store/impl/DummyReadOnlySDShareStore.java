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

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IResource;
import com.semagia.cassa.common.dm.IWritableRepresentation;
import com.semagia.cassa.common.dm.impl.DefaultResource;
import com.semagia.cassa.server.store.GraphNotExistsException;
import com.semagia.cassa.server.store.IFragmentInfo;
import com.semagia.cassa.server.store.IGraphInfo;
import com.semagia.cassa.server.store.IStore;
import com.semagia.cassa.server.store.StoreException;
import com.semagia.cassa.server.store.UnsupportedMediaTypeException;

/**
 * Dummy implementation of {@link IStore}. This store is read only.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class DummyReadOnlySDShareStore extends AbstractSDShareStore {

    public static URI FRAGMENT_GRAPH =  URI.create("http://www.example.org/fragments");

    public static URI FRAGMENT_INFO_1_URI = URI.create("http://www.example.org/fragment-1");
    public static URI FRAGMENT_INFO_2_URI = URI.create("http://www.example.org/fragment-2");
    public static URI FRAGMENT_INFO_1_RESOURCE_URI = URI.create("http://www.example.org/resource-1");
    public static URI FRAGMENT_INFO_2_RESOURCE_URI = URI.create("http://www.example.org/resource-2");
    public static IResource FRAGMENT_INFO_1_RESOURCE = new DefaultResource(FRAGMENT_INFO_1_RESOURCE_URI);
    public static IResource FRAGMENT_INFO_2_RESOURCE = new DefaultResource(FRAGMENT_INFO_2_RESOURCE_URI);
    public static MediaType FRAGMENT_INFO_1_MEDIATYPE = MediaType.RDF_XML;
    public static MediaType FRAGMENT_INFO_2_MEDIATYPE = MediaType.TURTLE;
    public static long FRAGMENT_INFO_1_UPDATED = new Date().getTime();
    public static long FRAGMENT_INFO_2_UPDATED = FRAGMENT_INFO_1_UPDATED + 1000L;

    public static IFragmentInfo FRAGMENT_INFO_1 = new DefaultFragmentInfo(FRAGMENT_INFO_1_URI, FRAGMENT_INFO_1_RESOURCE, FRAGMENT_INFO_1_MEDIATYPE, FRAGMENT_INFO_1_UPDATED);
    public static IFragmentInfo FRAGMENT_INFO_2 = new DefaultFragmentInfo(FRAGMENT_INFO_2_URI, FRAGMENT_INFO_2_RESOURCE, FRAGMENT_INFO_2_MEDIATYPE, FRAGMENT_INFO_2_UPDATED);

    private IStore _store;

    public DummyReadOnlySDShareStore() {
        _store = new DummyReadOnlyStore();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.ISDShareStore#getLastModificationFragments(java.net.URI)
     */
    @Override
    public long getLastModificationFragments(URI graphURI)
            throws GraphNotExistsException, StoreException {
        return FRAGMENT_INFO_2_UPDATED;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.ISDShareStore#getLastModificationSnapshots(java.net.URI)
     */
    @Override
    public long getLastModificationSnapshots(URI graphURI)
            throws GraphNotExistsException, StoreException {
        return -1;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.ISDShareStore#getFragments(java.net.URI)
     */
    @Override
    public Iterable<IFragmentInfo> getFragments(URI graphURI)
            throws GraphNotExistsException, StoreException {
        return Arrays.asList(FRAGMENT_INFO_1, FRAGMENT_INFO_2);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.ISDShareStore#getSnapshots(java.net.URI)
     */
    @Override
    public Iterable<IGraphInfo> getSnapshots(URI graphURI)
            throws GraphNotExistsException, StoreException {
        return Collections.emptyList();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#getLastModification()
     */
    @Override
    public long getLastModification() throws StoreException {
        return _store.getLastModification();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#getGraphInfos()
     */
    @Override
    public Iterable<IGraphInfo> getGraphInfos() throws StoreException {
        return _store.getGraphInfos();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#getGraph(java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public IWritableRepresentation getGraph(URI graphURI, MediaType mediaType)
            throws GraphNotExistsException, UnsupportedMediaTypeException,
            IOException, StoreException {
        return _store.getGraph(graphURI, mediaType);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#getGraphInfo(java.net.URI)
     */
    @Override
    public IGraphInfo getGraphInfo(URI graphURI)
            throws GraphNotExistsException, StoreException {
        return _store.getGraphInfo(graphURI);
    }

}
