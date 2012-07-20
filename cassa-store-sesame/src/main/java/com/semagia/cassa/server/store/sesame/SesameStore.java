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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IGraphInfo;
import com.semagia.cassa.common.dm.IWritableRepresentation;
import com.semagia.cassa.common.dm.RemovalStatus;
import com.semagia.cassa.common.dm.impl.DefaultGraphInfo;
import com.semagia.cassa.server.store.GraphMismatchException;
import com.semagia.cassa.server.store.GraphNotExistsException;
import com.semagia.cassa.server.store.IStore;
import com.semagia.cassa.server.store.QueryException;
import com.semagia.cassa.server.store.StoreException;
import com.semagia.cassa.server.store.UnsupportedMediaTypeException;

/**
 * {@link IStore} implementation that uses a Sesame repository.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class SesameStore implements IStore {

    private static Resource[] _ALL_CONTEXTS = new Resource[0];
    private static MediaType _TEXT_PLAIN = MediaType.valueOf("text/plain");

    private final Repository _repository;

    public SesameStore(final Repository repository) {
        _repository = repository;
    }

    private RepositoryConnection getConnection() throws StoreException {
        try {
            return _repository.getConnection();
        }
        catch (RepositoryException ex) {
            throw new StoreException(ex);
        }
    }

    private static void rollbackConnection(final RepositoryConnection conn) throws StoreException {
        try {
            conn.rollback();
        }
        catch (RepositoryException ex) {
            throw new StoreException(ex);
        }
    }

    private static void closeConnection(final RepositoryConnection conn) throws StoreException {
        try {
            conn.close();
        }
        catch (RepositoryException ex) {
            throw new StoreException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#getGraphInfos()
     */
    @Override
    public Iterable<IGraphInfo> getGraphInfos() throws StoreException {
        final RepositoryConnection conn = getConnection();
        try {
            return getGraphInfos(conn);
        }
        catch (OpenRDFException ex) {
            throw new StoreException(ex);
        }
        finally {
            closeConnection(conn);
        }
    }

    private Iterable<IGraphInfo> getGraphInfos(final RepositoryConnection conn) throws OpenRDFException {
        final List<IGraphInfo> uris = new ArrayList<IGraphInfo>();
        RepositoryResult<Resource> res = null;
        try {
            res  = conn.getContextIDs();
            while(res.hasNext()) {
                uris.add(new GraphInfo(URI.create(res.next().stringValue())));
            }
        }
        finally {
            if (res != null) {
                res.close();
            }
        }
        return uris;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#getGraph(java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public IWritableRepresentation getGraph(final URI graphURI, MediaType mediaType)
            throws GraphNotExistsException, UnsupportedMediaTypeException,
            StoreException {
        final RepositoryConnection conn = getConnection();
        try {
            ensureGraphExists(conn, graphURI);
        }
        catch (OpenRDFException ex) {
            closeConnection(conn);
            throw new StoreException(ex);
        }
        if (mediaType == null) {
            mediaType = MediaType.RDF_XML;
        }
        return new WritableRepresentation(conn, 
                SesameUtils.asWritableRDFFormat(mediaType),
                mediaType, getContext(graphURI));
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#containsGraph(java.net.URI)
     */
    @Override
    public boolean containsGraph(final URI graphURI) throws StoreException {
        if (graphURI == IStore.DEFAULT_GRAPH) {
            return true;
        }
        final RepositoryConnection conn = getConnection();
        try {
            return containsGraph(conn, graphURI);
        }
        catch (OpenRDFException ex) {
            throw new StoreException(ex);
        }
        finally {
            closeConnection(conn);
        }
    }

    private boolean containsGraph(final RepositoryConnection conn, 
            final URI graphURI) throws OpenRDFException {
        if (graphURI == IStore.DEFAULT_GRAPH) {
            return true;
        }
        RepositoryResult<Resource> res = null;
        try {
            res  = conn.getContextIDs();
            while(res.hasNext()) {
                if (graphURI.equals(URI.create(res.next().stringValue()))) {
                    return true;
                }
            }
        }
        finally {
            if (res != null) {
                res.close();
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#getGraphInfo(java.net.URI)
     */
    @Override
    public IGraphInfo getGraphInfo(final URI graphURI)
            throws GraphNotExistsException, StoreException {
        final RepositoryConnection conn = getConnection();
        try {
            ensureGraphExists(conn, graphURI);
        }
        catch (OpenRDFException ex) {
            throw new StoreException(ex);
        }
        finally {
            closeConnection(conn);
        }
        return new GraphInfo(graphURI);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#deleteGraph(java.net.URI)
     */
    @Override
    public RemovalStatus deleteGraph(final URI graphURI)
            throws GraphNotExistsException, StoreException {
        final RepositoryConnection conn = getConnection();
        try {
            ensureGraphExists(conn, graphURI);
            conn.setAutoCommit(false);
            conn.clear(getContext(graphURI));
            conn.commit();
        }
        catch (OpenRDFException ex) {
            rollbackConnection(conn);
            throw new StoreException(ex);
        }
        finally {
            closeConnection(conn);
        }
        return RemovalStatus.IMMEDIATELY;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#updateGraph(java.net.URI, java.io.InputStream, java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public IGraphInfo updateGraph(final URI graphURI, final InputStream in, final URI baseURI,
            final MediaType mediaType) throws UnsupportedMediaTypeException,
            IOException, StoreException {
        final RepositoryConnection conn = getConnection();
        try {
            ensureGraphExists(conn, graphURI);
            conn.setAutoCommit(false);
            conn.add(in, baseURI.toString(), SesameUtils.asReadableRDFFormat(mediaType, MediaType.RDF_XML), getContext(graphURI));
            conn.commit();
        } 
        catch (OpenRDFException ex) {
            rollbackConnection(conn);
            throw new StoreException(ex);
        }
        finally {
            closeConnection(conn);
        }
        return new GraphInfo(graphURI);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#createGraph(java.io.InputStream, java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public IGraphInfo createGraph(final InputStream in, final URI baseURI,
            final MediaType mediaType) throws UnsupportedMediaTypeException,
            IOException, StoreException {
        final URI graphURI = baseURI.resolve(UUID.randomUUID().toString());
        final RepositoryConnection conn = getConnection();
        try {
            conn.setAutoCommit(false);
            conn.add(in, baseURI.toString(), SesameUtils.asReadableRDFFormat(mediaType, MediaType.RDF_XML), getContext(graphURI));
            conn.commit();
        } 
        catch (OpenRDFException ex) {
            rollbackConnection(conn);
            throw new StoreException(ex);
        }
        finally {
            closeConnection(conn);
        }
        return new GraphInfo(graphURI);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#createOrReplaceGraph(java.net.URI, java.io.InputStream, java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public IGraphInfo createOrReplaceGraph(final URI graphURI, final InputStream in,
            final URI baseURI, final MediaType mediaType)
            throws UnsupportedMediaTypeException, IOException, StoreException {
        final RDFFormat format = SesameUtils.asReadableRDFFormat(mediaType, MediaType.RDF_XML);
        final Resource[] contexts = getContext(graphURI);
        final RepositoryConnection conn = getConnection();
        try {
            conn.setAutoCommit(false);
            conn.clear(contexts);
            conn.add(in, baseURI.toString(), format, contexts);
            conn.commit();
        } 
        catch (OpenRDFException ex) {
            rollbackConnection(conn);
            throw new StoreException(ex);
        }
        finally {
            closeConnection(conn);
        }
        return new GraphInfo(graphURI);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.IStore#modifyGraph(java.net.URI, java.io.InputStream, java.net.URI, com.semagia.cassa.common.MediaType)
     */
    @Override
    public boolean modifyGraph(final URI graphURI, final InputStream in, final URI baseURI,
            final MediaType mediaType) throws UnsupportedMediaTypeException,
            IOException, StoreException, QueryException, GraphMismatchException {
        if (mediaType != null && !(MediaType.SPARQL_UPDATE.equals(mediaType) || _TEXT_PLAIN.isCompatible(mediaType))) {
            throw new UnsupportedMediaTypeException(mediaType, MediaType.SPARQL_QUERY);
        }
        final Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        final Reader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        int n;
        while ((n = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, n);
        }
        boolean result = false;
        final RepositoryConnection conn = getConnection();
        try {
            conn.setAutoCommit(false);
            final Update update = conn.prepareUpdate(QueryLanguage.SPARQL, writer.toString(), baseURI.toASCIIString());
            if (graphURI != IStore.DEFAULT_GRAPH) {
                final DatasetImpl ds = new DatasetImpl();
                ds.addDefaultGraph(_repository.getValueFactory().createURI(graphURI.toString()));
                update.setDataset(ds);
            }
            update.execute();
            conn.commit();
            result = true;
        }
        catch (RepositoryException ex) {
            throw new StoreException(ex);
        } 
        catch (MalformedQueryException ex) {
            throw new QueryException(ex.getMessage());
        } 
        catch (UpdateExecutionException ex) {
            throw new QueryException(ex.getMessage());
        }
        finally {
            closeConnection(conn);
        }
        return result;
    }

    /**
     * Raises an exception if the graph URI is not in the repository.
     *
     * @param graphURI The graph URI to check.s
     * @throws GraphNotExistsException In case the graph URI does not exist.
     * @throws StoreException In case of an error.
     */
    private void ensureGraphExists(final RepositoryConnection conn, final URI graphURI) throws GraphNotExistsException, OpenRDFException {
        if (graphURI != IStore.DEFAULT_GRAPH && !containsGraph(conn, graphURI)) {
            throw new GraphNotExistsException(graphURI);
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
        return _repository.getValueFactory().createURI(uri.toString());
    }


    private static class GraphInfo extends DefaultGraphInfo {
       
        public GraphInfo(final URI uri) {
            super(uri);
        }

        /* (non-Javadoc)
         * @see com.semagia.cassa.common.dm.impl.DefaultGraphInfo#getSupportedMediaTypes()
         */
        @Override
        public List<MediaType> getSupportedMediaTypes() {
            return SesameUtils.getWritableMediaTypes();
        }
    }


    private static class WritableRepresentation implements IWritableRepresentation {

        private final RepositoryConnection _conn;
        private final RDFFormat _format;
        private final MediaType _mediaType;
        private Resource[] _resources;

        public WritableRepresentation(final RepositoryConnection conn, 
                final RDFFormat format, 
                final MediaType mediaType,
                final Resource[] resources) {
            _conn = conn;
            _format = format;
            _mediaType = mediaType;
            _resources = resources;
        }

        /* (non-Javadoc)
         * @see com.semagia.cassa.common.dm.IWritable#write(java.io.OutputStream)
         */
        @Override
        public void write(final OutputStream out) throws IOException {
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
            finally {
                try {
                    _conn.close();
                }
                catch (RepositoryException ex) {
                    throw new IOException(ex);
                }
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
