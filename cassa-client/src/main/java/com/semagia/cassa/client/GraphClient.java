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
package com.semagia.cassa.client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.Syntax;
import com.semagia.cassa.common.dm.RemovalStatus;

/**
 * EXPERIMENTAL Client implementation to interact with a HTTP graph store.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class GraphClient extends AbstractClient {

    private static final URI _DEFAULT_GRAPH = URI.create("");

    /**
     * Creates a client which connects to the provided service endpoint.
     * 
     * @param endpoint The service endpoint.
     */
    public GraphClient(final URI endpoint) {
        super(endpoint);
    }

    /**
     * Returns the default graph using the preferred media types (if any).
     *
     * @return A graph or {@code null} if the graph does not exist (Note: graph
     *          stores are required to have always a default graph).
     * @throws IOException In case of an error.
     */
    public Graph getGraph() throws IOException {
        return getGraph(_preferredMediaTypes);
    }

    /**
     * Returns the default graph using the provided media type(s).
     *
     * @param mediaTypes The requested media types.
     * @return A graph or {@code null} if the graph does not exist (Note: graph
     *          stores are required to have always a default graph).
     * @throws IOException In case of an error.
     */
    public Graph getGraph(final MediaType... mediaTypes) throws IOException {
        return _getGraph(_DEFAULT_GRAPH, mediaTypes);
    }

    /**
     * Returns the graph with the provided URI using the preferred media types (if any).
     *
     * @param graphURI The graph URI.
     * @return A graph or {@code null} if the graph does not exist.
     * @throws IOException In case of an error.
     */
    public Graph getGraph(final URI graphURI) throws IOException {
        return getGraph(graphURI, _preferredMediaTypes);
    }

    /**
     * Returns the graph with the provided URI using the provided media type.
     *
     * @param graphURI The graph URI.
     * @param mediaTypes The requested media types.
     * @return A graph or {@code null} if the graph does not exist.
     * @throws IOException In case of an error.
     */
    public Graph getGraph(final URI graphURI, final MediaType... mediaTypes) throws IOException {
        return _getGraph(graphURI, mediaTypes);
    }

    private Graph _getGraph(URI graphURI, MediaType... mediaTypes) throws IOException {
        return super.getGraph(getGraphURI(graphURI), mediaTypes);
    }

    /**
     * Creates a graph under the specified URI using the content of the provided
     * file.
     * 
     * This method tries to detect the media type of the graph content by the
     * file name extension.
     *
     * @param graphURI The graph URI.
     * @param file The file to read the graph from.
     * @return {@code true} indicating that the graph was created sucessfully,
     *          otherwise {@code false}.
     * @throws IOException In case of an error.
     */
    public boolean createGraph(final URI graphURI, final File file) throws IOException {
        return createGraph(graphURI, file, guessMediaType(file));
    }

    /**
     * Creates a graph under the specified URI using the content of the provided
     * file.
     *
     * @param graphURI The graph URI.
     * @param file The file to read the graph from.
     * @param mediaType The content type of the file.
     * @return {@code true} indicating that the graph was created sucessfully,
     *          otherwise {@code false}.
     * @throws IOException In case of an error.
     */
    public boolean createGraph(final URI graphURI, final File file, final MediaType mediaType) throws IOException {
        return _createGraph(graphURI, file, mediaType);
    }

    /**
     * Creates a graph under the specified URI using the content of the stream.
     * 
     * Note: This method does not close the input stream. The caller should
     * close it.
     *
     * @param graphURI The graph URI.
     * @param in The input stream to read the graph from.
     * @return {@code true} indicating that the graph was created sucessfully,
     *          otherwise {@code false}.
     * @throws IOException In case of an error.
     */
    public boolean createGraph(final URI graphURI, final InputStream in) throws IOException {
        return createGraph(graphURI, in, _preferredMediaTypes != null ? _preferredMediaTypes[0] : null);
    }

    /**
     * Creates a graph under the specified URI using the content of the stream.
     *
     * Note: This method does not close the input stream. The caller should
     * close it.
     *
     * @param graphURI The graph URI.
     * @param in The input stream to read the graph from.
     * @param mediaType The content type of the input stream.
     * @return {@code true} indicating that the graph was created sucessfully,
     *          otherwise {@code false}.
     * @throws IOException In case of an error.
     */
    public boolean createGraph(final URI graphURI, final InputStream in, final MediaType mediaType) throws IOException {
        return _createGraph(graphURI, in, mediaType);
    }

    private boolean _createGraph(final URI graphURI, final File file, final MediaType mediaType) throws IOException {
        final InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            return _createGraph(graphURI, in, mediaType);
        }
        finally {
            in.close();
        }
    }

    private boolean _createGraph(final URI graphURI, final InputStream in, final MediaType mediaType) throws IOException {
        final HttpPut put = new HttpPut(getGraphURI(graphURI));
        final InputStreamEntity entity = new InputStreamEntity(in, -1);
        entity.setContentType(mediaType != null ? mediaType.toString() : null);
        put.setEntity(entity);
        final int status = getStatusCode(put);
        return status == 200 || status == 201 || status == 204;
    }

    /**
     * Creates a graph using the content of the provided file.
     * 
     * This method tries to detect the media type of the graph content by the
     * file name extension.
     *
     * @param file The file to read the graph from.
     * @return The URI of the created graph or {@code null} if the graph was not created.
     * @throws IOException In case of an error.
     */
    public URI createGraph(final File file) throws IOException {
        return createGraph(file, guessMediaType(file));
    }

    /**
     * Creates a graph using the content of the provided file.
     *
     * @param file The file to read the graph from.
     * @param mediaType The content type of the file.
     * @return The URI of the created graph or {@code null} if the graph was not created.
     * @throws IOException In case of an error.
     */
    public URI createGraph(final File file, final MediaType mediaType) throws IOException {
        return _createGraph(file, mediaType);
    }

    private URI _createGraph(final File file, final MediaType mediaType) throws IOException {
        final InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            return _createGraph(in, mediaType);
        }
        finally {
            in.close();
        }
    }

    private URI _createGraph(final InputStream in, final MediaType mediaType) throws IOException {
        final HttpPost request = new HttpPost(_endpoint);
        final InputStreamEntity entity = new InputStreamEntity(in, -1);
        entity.setContentType(mediaType != null ? mediaType.toString() : null);
        request.setEntity(entity);
        final HttpResponse response = execute(request);
        URI graphURI = null;
        if (response.getStatusLine().getStatusCode() == 201) {
            final Header location = response.getFirstHeader("location");
            graphURI = location != null ? URI.create(location.getValue()) : null;
        }
        request.abort();
        return graphURI;
    }

    /**
     * Creates a graph using the content of the stream.
     * 
     * Note: This method does not close the input stream. The caller should
     * close it.
     *
     * @param in The input stream to read the graph from.
     * @return The URI of the created graph or {@code null} if the graph was not created.
     * @throws IOException In case of an error.
     */
    public URI createGraph(final InputStream in) throws IOException {
        return createGraph(in, _preferredMediaTypes != null ? _preferredMediaTypes[0] : null);
    }

    /**
     * Creates a graph using the content of the stream.
     *
     * Note: This method does not close the input stream. The caller should
     * close it.
     *
     * @param in The input stream to read the graph from.
     * @param mediaType The content type of the input stream.
     * @return The URI of the created graph or {@code null} if the graph was not created.
     * @throws IOException In case of an error.
     */
    public URI createGraph(final InputStream in, final MediaType mediaType) throws IOException {
        return _createGraph(in, mediaType);
    }

    /**
     * Returns if the default graph exists.
     * 
     * This method should always return {@code true} if the graph store is
     * standards-compilant.
     * 
     * @return {@code true} if the default graph exists, otherwise {@code false}.
     * @throws IOException In case of an error.
     */
    public boolean existsGraph() throws IOException {
        return _existsGraph(_DEFAULT_GRAPH);
    }

    /**
     * Returns if a graph with the provided URI exists. 
     *
     * @param graphURI The graph URI.
     * @return {@code true} if the graph exists, otherwise {@code false}.
     * @throws IOException In case of an error.
     */
    public boolean existsGraph(final URI graphURI) throws IOException {
        return _existsGraph(graphURI);
    }

    private boolean _existsGraph(final URI graphURI) throws IOException {
        return super.existsGraph(getGraphURI(graphURI));
    }

    /**
     * Deletes the default graph.
     * 
     * Note: The default graph always exists. After deletion a new default
     * graph is created:
     * <pre>
     *      client.deleteGraph()
     *      client.existsGraph() == true
     * </pre>
     *
     * @return The removal status or {@code null} if the graph does not exist
     *          or wasn't deleted.
     * @throws IOException In case of an error.
     */
    public RemovalStatus deleteGraph() throws IOException {
        return _deleteGraph(_DEFAULT_GRAPH);
    }

    /**
     * Deletes the graph under the provided URI.
     *
     * @param graphURI The graph URI.
     * @return The removal status or {@code null} if the graph does not exist
     *          or wasn't deleted.
     * @throws IOException In case of an error.
     */
    public RemovalStatus deleteGraph(final URI graphURI) throws IOException {
        return _deleteGraph( graphURI);
    }

    private RemovalStatus _deleteGraph(final URI graphURI) throws IOException {
        final HttpDelete delete = new HttpDelete(getGraphURI(graphURI));
        final int status = getStatusCode(delete);
        if (status == 202) {
            return RemovalStatus.DELAYED;
        }
        else if (status == 200 || status == 204) {
            return RemovalStatus.IMMEDIATELY;
        }
        return null;
    }

    /**
     * Updates the default graph by reading the provided stream, assuming
     * the preferred media types (if any).
     *
     * Note: This method does not close the input stream. The caller should
     * close it.
     *
     * @param in The input stream to read the graph from.
     * @return {@code true} indicating that the graph was updated sucessfully,
     *          otherwise {@code false}.
     * @throws IOException In case of an error.
     */
    public boolean updateGraph(final InputStream in) throws IOException {
        return updateGraph(in, _preferredMediaTypes != null ? _preferredMediaTypes[0] : null);
    }

    /**
     * Updates the default graph by reading the provided file, assuming
     * the preferred media types (if any).
     *
     * @param file The file to read the graph from.
     * @return {@code true} indicating that the graph was updated sucessfully,
     *          otherwise {@code false}.
     * @throws IOException In case of an error.
     */
    public boolean updateGraph(final File file) throws IOException {
        return _updateGraph(_DEFAULT_GRAPH, file);
    }

    /**
     * Updates the default graph by reading the provided file, using the 
     * provided media type.
     *
     * @param file The file to read the graph from.
     * @param mediaType The content type of the file.
     * @return {@code true} indicating that the graph was updated sucessfully,
     *          otherwise {@code false}.
     * @throws IOException In case of an error.
     */
    public boolean updateGraph(final File file, final MediaType mediaType) throws IOException {
        return _updateGraph(_DEFAULT_GRAPH, file, mediaType);
    }

    /**
     * Updates the default graph by reading the provided stream, using
     * the provided media type.
     * 
     * Note: This method does not close the input stream. The caller should
     * close it.
     *
     * @param in The input stream to read the graph from.
     * @param mediaType The content type of the input stream.
     * @return {@code true} indicating that the graph was updated sucessfully,
     *          otherwise {@code false}.
     * @throws IOException In case of an error.
     */
    public boolean updateGraph(final InputStream in, final MediaType mediaType) throws IOException {
        return _updateGraph(_DEFAULT_GRAPH, in, mediaType);
    }

    /**
     * Updates the provided graph by reading the provided stream, using the 
     * preferred media types (if any).
     * 
     * Note: This method does not close the input stream. The caller should
     * close it.
     *
     * @param graphURI The graph URI.
     * @param in The input stream to read the graph from.
     * @return {@code true} indicating that the graph was updated sucessfully,
     *          otherwise {@code false}.
     * @throws IOException In case of an error.
     */
    public boolean updateGraph(final URI graphURI, final InputStream in) throws IOException {
        return updateGraph(graphURI, in, _preferredMediaTypes != null ? _preferredMediaTypes[0] : null);
    }

    /**
     * Updates the provided graph by reading the provided file, using the 
     * preferred media types (if any).
     * 
     * @param graphURI The graph URI.
     * @param file The file to read the graph from.
     * @return {@code true} indicating that the graph was updated sucessfully,
     *          otherwise {@code false}.
     * @throws IOException In case of an error.
     */
    public boolean updateGraph(final URI graphURI, final File file) throws IOException {
        return _updateGraph(graphURI, file);
    }

    /**
     * Updates the provided graph by reading the provided file, using the 
     * provided media type.
     * 
     * @param graphURI The graph URI.
     * @param file The file to read the graph from.
     * @param mediaType The content type of the file.
     * @return {@code true} indicating that the graph was updated sucessfully,
     *          otherwise {@code false}.
     * @throws IOException In case of an error.
     */
    public boolean updateGraph(final URI graphURI, final File file, final MediaType mediaType) throws IOException {
        return _updateGraph(graphURI, file, mediaType);
    }

    /**
     * Updates the provided graph by reading the provided stream, using the 
     * provided media type.
     *
     * Note: This method does not close the input stream. The caller should
     * close it.
     *
     * @param graphURI The graph URI.
     * @param in The input stream to read the graph from.
     * @param mediaType The content type of the input stream.
     * @return {@code true} indicating that the graph was updated sucessfully,
     *          otherwise {@code false}.
     * @throws IOException In case of an error.
     */
    public boolean updateGraph(final URI graphURI, final InputStream in, final MediaType mediaType) throws IOException {
        return _updateGraph(graphURI, in, mediaType);
    }

    private boolean _updateGraph(final URI graphURI, final File file) throws IOException {
        return _updateGraph(graphURI, file, guessMediaType(file.toURI()));
    }

    private boolean _updateGraph(final URI graphURI, final File file, final MediaType mediaType) throws IOException {
        final InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            return _updateGraph(graphURI, in, mediaType);
        }
        finally {
            in.close();
        }
    }

    private boolean _updateGraph(final URI graphURI, final InputStream in, final MediaType mediaType) throws IOException {
        final HttpPost post = new HttpPost(getGraphURI(graphURI));
        final InputStreamEntity entity = new InputStreamEntity(in, -1);
        entity.setContentType(mediaType != null ? mediaType.toString() : null);
        post.setEntity(entity);
        final int status = getStatusCode(post);
        return status == 201 || status == 204;
    }

    /**
     * Modifies the default graph using a query.
     * 
     * @param query A query, i.e. SPARQL 1.1 Update.
     * @return {@code true} indicating that the graph was updated sucessfully,
     *          otherwise {@code false}.
     * @throws IOException In case of an error.
     */
    public boolean modifyGraph(final String query) throws IOException {
        return modifyGraph(query, null);
    }

    /**
     * Modifies the default graph using a query.
     * 
     * @param query A query, i.e. SPARQL 1.1 Update.
     * @param mediaType The the content type of the query.
     * @return {@code true} indicating that the graph was updated sucessfully,
     *          otherwise {@code false}.
     * @throws IOException In case of an error.
     */
    public boolean modifyGraph(final String query, final MediaType mediaType) throws IOException {
        return modifyGraph(_DEFAULT_GRAPH, query, mediaType);
    }

    /**
     * Modifies a graph on the server using a query.
     * 
     * @param graphURI The graph URI.
     * @param query A query, i.e. SPARQL 1.1 Update.
     * @return {@code true} indicating that the graph was updated sucessfully,
     *          otherwise {@code false}.
     * @throws IOException In case of an error.
     */
    public boolean modifyGraph(final URI graphURI, final String query) throws IOException {
        return modifyGraph(graphURI, query, null);
    }

    /**
     * Modifies a graph on the server using a query.
     * 
     * @param graphURI The graph URI.
     * @param query A query, i.e. SPARQL 1.1 Update.
     * @param mediaType The the content type of the query.
     * @return {@code true} indicating that the graph was updated sucessfully,
     *          otherwise {@code false}.
     * @throws IOException In case of an error.
     */
    public boolean modifyGraph(final URI graphURI, final String query, final MediaType mediaType) throws IOException {
        final HttpPatch request = new HttpPatch(getGraphURI(graphURI));
        final StringEntity entity = new StringEntity(query);
        // From the spec it's unclear if a media type is required
        // <http://www.w3.org/TR/2012/WD-sparql11-http-rdf-update-20120501/#http-patch>
        // Other methods do not require a media type and the server should guess it/assume a default
        // so we don't mandate a media type here.
        entity.setContentType(mediaType != null ? mediaType.toString() : null);
        request.setEntity(entity);
        final int status = getStatusCode(request);
        return status == 200 || status == 204;
    }

    private URI getGraphURI(final URI graphURI) {
        if (graphURI == _DEFAULT_GRAPH) {
            return _endpoint.resolve("?default");
        }
        if (!_endpoint.relativize(graphURI).isAbsolute()) {
            // local graph
            return graphURI;
        }
        return _endpoint.resolve("?graph=" + graphURI.toASCIIString());
    }

    private static MediaType guessMediaType(final File file) {
        return guessMediaType(file.toURI());
   }

    private static MediaType guessMediaType(final URI uri) {
        MediaType result = null;
        final String uri_ = uri.toString();
        final int dotIdx = uri_.lastIndexOf('.');
        final String ext = dotIdx > -1 ? uri_.substring(dotIdx+1) : null;
        if (ext != null) {
            final Syntax syntax = Syntax.forFileExtension(ext);
            result = syntax != null ? MediaType.valueOf(syntax.getDefaultMIMEType()) : null;
        }
        return result;
   }

}
