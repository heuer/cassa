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
package com.semagia.cassa.server.store.tmapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IWritableRepresentation;
import com.semagia.cassa.common.dm.RemovalStatus;
import com.semagia.cassa.server.store.GraphMismatchException;
import com.semagia.cassa.server.store.GraphNotExistsException;
import com.semagia.cassa.server.store.IGraphInfo;
import com.semagia.cassa.server.store.IStore;
import com.semagia.cassa.server.store.ParseException;
import com.semagia.cassa.server.store.QueryException;
import com.semagia.cassa.server.store.StoreException;
import com.semagia.cassa.server.store.UnsupportedMediaTypeException;
import com.semagia.cassa.server.store.impl.AbstractStore;
import com.semagia.cassa.server.store.impl.DefaultGraphInfo;

/**
 * {@link IStore} implementation that uses a TMAPI-compatible Topic Maps engine.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class TMAPIStore extends AbstractStore {

    private static final String _DEFAULT_GRAPH_IRI_BASE = "urn:x-cassa-tmapi:";
    private final TopicMapSystem _sys;
    private final Locator _defaultGraphLocator;

    public TMAPIStore(final TopicMapSystem sys) {
        _sys = sys;
        _defaultGraphLocator = _sys.createLocator(_DEFAULT_GRAPH_IRI_BASE + UUID.randomUUID().toString());
        try {
            _sys.createTopicMap(_defaultGraphLocator);
        }
        catch (TMAPIException ex) {
            throw new IllegalStateException("Expected an empty topic map system");
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#getLastModification()
     */
    @Override
    public long getLastModification() {
        return -1;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#getGraphInfos()
     */
    @Override
    public Iterable<IGraphInfo> getGraphInfos() throws StoreException {
        final List<IGraphInfo> graphs = new ArrayList<IGraphInfo>();
        for (Locator loc: _sys.getLocators()) {
            if (loc.equals(_defaultGraphLocator)) {
                continue;
            }
            graphs.add(new GraphInfo(loc));
        }
        return graphs;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#getGraph(java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public IWritableRepresentation getGraph(URI graphURI, MediaType mediaType)
            throws GraphNotExistsException, UnsupportedMediaTypeException,
            StoreException {
        ensureGraphExists(graphURI);
        if (mediaType != null) {
            TMAPIUtils.ensureWritableMediaType(mediaType);
        }
        else {
            mediaType = MediaType.XTM;
        }
        return new WritableRepresentation(_sys.getTopicMap(asLocator(graphURI)), mediaType);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#containsGraph(java.net.URI)
     */
    @Override
    public boolean containsGraph(URI graphURI) throws StoreException {
        return _sys.getLocators().contains(asLocator(graphURI));
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#getGraphInfo(java.net.URI)
     */
    @Override
    public IGraphInfo getGraphInfo(URI graphURI)
            throws GraphNotExistsException, StoreException {
        ensureGraphExists(graphURI);
        return new GraphInfo(graphURI);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#deleteGraph(java.net.URI)
     */
    @Override
    public RemovalStatus deleteGraph(URI graphURI)
            throws GraphNotExistsException, StoreException {
        ensureGraphExists(graphURI);
        final Locator loc = asLocator(graphURI);
        _sys.getTopicMap(loc).remove();
        if (_defaultGraphLocator.equals(loc)) {
            makeTopicMap(_defaultGraphLocator);
        }
        return RemovalStatus.IMMEDIATELY;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#updateGraph(java.net.URI, java.io.InputStream, java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public IGraphInfo updateGraph(URI graphURI, InputStream in, URI baseURI,
            MediaType mediaType) throws UnsupportedMediaTypeException,
            IOException, StoreException {
        ensureGraphExists(graphURI);
        if (mediaType != null) {
            TMAPIUtils.ensureReadableMediaType(mediaType);
        }
        else {
            mediaType = MediaType.XTM;
        }
        TMAPIUtils.read(_sys.getTopicMap(asLocator(graphURI)), baseURI, in, mediaType);
        return new GraphInfo(graphURI);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#createGraph(java.io.InputStream, java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public IGraphInfo createGraph(InputStream in, URI baseURI,
            MediaType mediaType) throws UnsupportedMediaTypeException,
            IOException, StoreException {
        if (mediaType != null) {
            TMAPIUtils.ensureReadableMediaType(mediaType);
        }
        else {
            mediaType = MediaType.XTM;
        }
        final URI graphURI = baseURI.resolve(UUID.randomUUID().toString());
        TMAPIUtils.read(makeTopicMap(asLocator(graphURI)), baseURI, in, mediaType);
        return new GraphInfo(graphURI);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#createOrReplaceGraph(java.net.URI, java.io.InputStream, java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public IGraphInfo createOrReplaceGraph(URI graphURI, InputStream in,
            URI baseURI, MediaType mediaType)
            throws UnsupportedMediaTypeException, IOException, StoreException {
        if (mediaType != null) {
            TMAPIUtils.ensureReadableMediaType(mediaType);
        }
        else {
            mediaType = MediaType.XTM;
        }
        final Locator loc = asLocator(graphURI);
        if (containsGraph(graphURI)) {
            _sys.getTopicMap(loc).remove();
        }
        TMAPIUtils.read(makeTopicMap(loc), baseURI, in, mediaType);
        return new GraphInfo(graphURI);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#modifyGraph(java.net.URI, java.io.InputStream, java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public boolean modifyGraph(URI graphURI, InputStream in, URI baseURI,
            MediaType mediaType) throws UnsupportedMediaTypeException,
            IOException, QueryException, GraphMismatchException, StoreException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public RemovalStatus deleteSubject(URI graphURI, URI subjectURI)
            throws GraphNotExistsException, IOException, StoreException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public IGraphInfo createOrReplaceSubject(URI graphURI, URI subjectURI,
            InputStream in, URI baseURI, MediaType mediaType)
            throws UnsupportedMediaTypeException, IOException, ParseException,
            StoreException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /**
     * Converts the URI into a Locator.
     * 
     * @param uri The URI to convert.
     * @return The equivalent Locator instance.
     */
    private Locator asLocator(final URI uri) {
        return uri == IStore.DEFAULT_GRAPH ? _defaultGraphLocator 
                                           : _sys.createLocator(uri.toString());
    }

    private TopicMap makeTopicMap(final Locator loc) throws StoreException {
        try {
            return _sys.createTopicMap(loc);
        }
        catch (TMAPIException ex) {
            throw new StoreException(ex);
        }
    }


    private static class GraphInfo extends DefaultGraphInfo {

        public GraphInfo(final Locator loc) {
            this(URI.create(loc.toExternalForm()));
        }

        public GraphInfo(final URI uri) {
            super(uri, TMAPIUtils.getWritableMediaTypes());
        }

    }


    private static class WritableRepresentation implements IWritableRepresentation {

        private final MediaType _mediaType;
        private final TopicMap _tm;

        public WritableRepresentation(final TopicMap tm, final MediaType mt) {
            _tm = tm;
            _mediaType = mt;
        }

        /* (non-Javadoc)
         * @see com.semagia.cassa.common.dm.IWritable#write(java.io.OutputStream)
         */
        @Override
        public void write(OutputStream out) throws IOException {
            TMAPIUtils.write(_tm, _mediaType, out);
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
            return null;
        }

    }
}
