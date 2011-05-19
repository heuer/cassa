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
package com.semagia.cassa.server.store.sesame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IGraphInfo;
import com.semagia.cassa.common.dm.IWritableRepresentation;
import com.semagia.cassa.common.dm.impl.DefaultGraphInfo;
import com.semagia.cassa.server.store.GraphNotExistsException;
import com.semagia.cassa.server.store.IStore;
import com.semagia.cassa.server.store.RemovalStatus;
import com.semagia.cassa.server.store.StoreException;
import com.semagia.cassa.server.store.UnsupportedMediaTypeException;

/**
 * {@link IStore} implementation that uses a Sesame repository.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class SesameStore implements IStore {

    private static Resource[] _ALL_CONTEXTS = new Resource[0];

    final RepositoryConnection _conn;

    public SesameStore(final RepositoryConnection conn) {
        _conn = conn;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#getGraphInfos()
     */
    @Override
    public Iterable<IGraphInfo> getGraphInfos() throws StoreException {
        final List<IGraphInfo> uris = new ArrayList<IGraphInfo>();
        try {
            final RepositoryResult<Resource> res = _conn.getContextIDs();
            while(res.hasNext()) {
                uris.add(new GraphInfo(URI.create(res.next().stringValue())));
            }
            res.close();
        }
        catch (OpenRDFException ex) {
            throw new StoreException(ex);
        }
        return uris;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#getGraph(java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public IWritableRepresentation getGraph(URI graphURI, MediaType mediaType)
            throws GraphNotExistsException, UnsupportedMediaTypeException,
            StoreException {
        ensureGraphAvailable(graphURI);
        return new WritableRepresentation(SesameUtils.asWritableRDFFormat(mediaType),
                mediaType, getContext(graphURI));
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#containsGraph(java.net.URI)
     */
    @Override
    public boolean containsGraph(URI graphURI) throws StoreException {
        if (graphURI == IStore.DEFAULT_GRAPH) {
            return true;
        }
        for (IGraphInfo info: getGraphInfos()) {
            if (graphURI.equals(info.getURI())) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#getGraphInfo(java.net.URI)
     */
    @Override
    public IGraphInfo getGraphInfo(URI graphURI)
            throws GraphNotExistsException, StoreException {
        ensureGraphAvailable(graphURI);
        return new GraphInfo(graphURI);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#deleteGraph(java.net.URI)
     */
    @Override
    public RemovalStatus deleteGraph(URI graphURI)
            throws GraphNotExistsException, StoreException {
        ensureGraphAvailable(graphURI);
        try {
            _conn.clear(getContext(graphURI));
        }
        catch (OpenRDFException ex) {
            throw new StoreException(ex);
        }
        return RemovalStatus.IMMEDIATELY;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#createOrUpdateGraph(java.net.URI, java.io.InputStream, java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public IGraphInfo createOrUpdateGraph(URI graphURI, InputStream in,
            URI baseURI, MediaType mediaType)
            throws UnsupportedMediaTypeException, IOException, StoreException {
        try {
            _conn.add(in, baseURI.toString(), SesameUtils.asReadableRDFFormat(mediaType), getContext(graphURI));
        } 
        catch (OpenRDFException ex) {
            throw new StoreException(ex);
        }
        return new GraphInfo(graphURI);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#createOrReplaceGraph(java.net.URI, java.io.InputStream, java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public IGraphInfo createOrReplaceGraph(URI graphURI, InputStream in,
            URI baseURI, MediaType mediaType)
            throws UnsupportedMediaTypeException, IOException, StoreException {
        final RDFFormat format = SesameUtils.asReadableRDFFormat(mediaType);
        final Resource[] contexts = getContext(graphURI);
        try {
            _conn.clear(contexts);
            _conn.add(in, baseURI.toString(), format, contexts);
        } 
        catch (OpenRDFException ex) {
            throw new StoreException(ex);
        }
        return new GraphInfo(graphURI);
    }

    /**
     * Raises an exception if the graph URI is not in the repository.
     *
     * @param graphURI The graph URI to check.s
     * @throws GraphNotExistsException In case the graph URI does not exist.
     * @throws StoreException In case of an error.
     */
    private void ensureGraphAvailable(final URI graphURI) throws GraphNotExistsException, StoreException {
        try {
            if (_conn.isEmpty() || !containsGraph(graphURI)) {
                final String graph = graphURI == IStore.DEFAULT_GRAPH ? "'default'" : "<" + graphURI.toString() + ">";
                throw new GraphNotExistsException("The graph " + graph + " does not exist");
            }
        }
        catch (OpenRDFException ex) {
            throw new StoreException(ex);
        }
    }

    /**
     * 
     *
     * @param graphURI
     * @return
     */
    private Resource[] getContext(final URI graphURI) {
        return graphURI == IStore.DEFAULT_GRAPH ? _ALL_CONTEXTS 
                                                : new Resource[] { asResource(graphURI) };
    }

    /**
     * Converts the provided URI into a {@link Resource}.
     *
     * @param uri The URI to convert.
     * @return A resource which represents the URI.
     */
    private Resource asResource(final URI uri) {
        return _conn.getValueFactory().createURI(uri.toString());
    }


    private static class GraphInfo extends DefaultGraphInfo {
       
        public GraphInfo(URI uri) {
            super(uri);
        }

        /* (non-Javadoc)
         * @see com.semagia.cassa.common.dm.impl.DefaultGraphInfo#getSupportedMediaTypes()
         */
        @Override
        public List<MediaType> getSupportedMediaTypes() {
            return SesameUtils.getReadableMediaTypes();
        }
    }


    private class WritableRepresentation implements IWritableRepresentation {

        private final RDFFormat _format;
        private final MediaType _mediaType;
        private Resource[] _resources;

        public WritableRepresentation(final RDFFormat format, 
                final MediaType mediaType,
                final Resource[] resources) {
            _format = format;
            _mediaType = mediaType;
            _resources = resources;
        }

        /* (non-Javadoc)
         * @see com.semagia.cassa.common.dm.IWritable#write(java.io.OutputStream)
         */
        @Override
        public void write(OutputStream out) throws IOException {
            final RDFWriter writer = Rio.createWriter(_format, out);
            try {
                _conn.export(writer, _resources);
            }
            catch (OpenRDFException ex) {
                if (ex.getCause() instanceof IOException) {
                    throw (IOException) ex.getCause();
                }
                throw new IOException(ex);
            }
        }

        /* (non-Javadoc)
         * @see com.semagia.cassa.common.dm.IWritableRepresentation#getMediaType()
         */
        @Override
        public MediaType getMediaType() {
            return _mediaType;
        }
    }

}
