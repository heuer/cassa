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
import java.io.InputStream;
import java.net.URI;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.RemovalStatus;
import com.semagia.cassa.server.store.GraphMismatchException;
import com.semagia.cassa.server.store.GraphNotExistsException;
import com.semagia.cassa.server.store.IGraphInfo;
import com.semagia.cassa.server.store.ParseException;
import com.semagia.cassa.server.store.QueryException;
import com.semagia.cassa.server.store.StoreException;
import com.semagia.cassa.server.store.UnsupportedMediaTypeException;

/**
 * Abstract {@link IStore} implementation which throws a 
 * {@link UnsupportedOperationException} for all operations which imply a store 
 * modification.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public abstract class AbstractReadOnlyStore extends AbstractStore {

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#deleteGraph(java.net.URI)
     */
    @Override
    public RemovalStatus deleteGraph(URI graphURI)
            throws GraphNotExistsException, IOException, StoreException {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#deleteSubject(java.net.URI, java.net.URI)
     */
    @Override
    public RemovalStatus deleteSubject(URI graphURI, URI subjectURI)
            throws GraphNotExistsException, IOException, StoreException {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#updateGraph(java.net.URI, java.io.InputStream, java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public IGraphInfo updateGraph(URI graphURI, InputStream in, URI baseURI,
            MediaType mediaType) throws UnsupportedMediaTypeException,
            IOException, ParseException, StoreException {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#modifyGraph(java.net.URI, java.io.InputStream, java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public boolean modifyGraph(URI graphURI, InputStream in, URI baseURI,
            MediaType mediaType) throws UnsupportedMediaTypeException,
            IOException, QueryException, GraphMismatchException, StoreException {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#createGraph(java.io.InputStream, java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public IGraphInfo createGraph(InputStream in, URI baseURI,
            MediaType mediaType) throws UnsupportedMediaTypeException,
            IOException, ParseException, StoreException {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#createOrReplaceGraph(java.net.URI, java.io.InputStream, java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public IGraphInfo createOrReplaceGraph(URI graphURI, InputStream in,
            URI baseURI, MediaType mediaType)
            throws UnsupportedMediaTypeException, IOException, ParseException,
            StoreException {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#createOrReplaceSubject(java.net.URI, java.net.URI, java.io.InputStream, java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public IGraphInfo createOrReplaceSubject(URI graphURI, URI subjectURI,
            InputStream in, URI baseURI, MediaType mediaType)
            throws UnsupportedMediaTypeException, IOException, ParseException,
            StoreException {
        throw new UnsupportedOperationException();
    }

}
