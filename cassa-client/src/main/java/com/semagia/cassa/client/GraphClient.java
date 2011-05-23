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
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.Syntax;
import com.semagia.cassa.common.dm.RemovalStatus;

/**
 * EXPERIMENTAL Client implementation to interact with a HTTP graph store.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class GraphClient {

    private static final URI _DEFAULT_GRAPH = URI.create("");

    private final String _endpoint;
    private final String _graphsEndpoint;
    private MediaType _mediaType;
    private final HttpClient _client;

    public GraphClient(final URI endpoint) {
        this(endpoint, null);
    }

    public GraphClient(final URI endpoint, final URI graphsURI) {
        if (endpoint == null) {
            throw new IllegalArgumentException("The endpoint URI must not be null");
        }
        _endpoint = endpoint.toString();
        _graphsEndpoint = graphsURI != null ? graphsURI.toString() : null;
        _client = new DefaultHttpClient();
    }

    /**
     * Returns the default media type.
     *
     * @return The default media type or {@code null} if the default media type
     *          isn't specified.
     */
    public MediaType getDefaultMediaType() {
        return _mediaType;
    }

    /**
     * Sets the default media type.
     *
     * @param mediaType A {@link MediaType} instance or {@code null}.
     */
    public void setDefaultMediaType(final MediaType mediaType) {
        _mediaType = mediaType;
    }

    /**
     * Returns the default graph using the {@link #getDefaultMediaType()}.
     *
     * @return A graph or {@code null} if the graph does not exist (Note: graph
     *          stores are required to have always a default graph).
     * @throws IOException In case of an error.
     */
    public Graph getGraph() throws IOException {
        return getGraph(_mediaType);
    }

    /**
     * Returns the default graph using the provided media type.
     *
     * @param mediaType The requested media type.
     * @return A graph or {@code null} if the graph does not exist (Note: graph
     *          stores are required to have always a default graph).
     * @throws IOException In case of an error.
     */
    public Graph getGraph(final MediaType mediaType) throws IOException {
        return _getGraph(_DEFAULT_GRAPH, mediaType);
    }

    /**
     * Returns the graph with the provided URI using the {@link #getDefaultMediaType()}.
     *
     * @param graphURI The graph URI.
     * @return A graph or {@code null} if the graph does not exist.
     * @throws IOException In case of an error.
     */
    public Graph getGraph(final URI graphURI) throws IOException {
        return getGraph(graphURI, _mediaType);
    }

    /**
     * Returns the graph with the provided URI using the provided media type.
     *
     * @param graphURI The graph URI.
     * @param mediaType The requested media type.
     * @return A graph or {@code null} if the graph does not exist.
     * @throws IOException In case of an error.
     */
    public Graph getGraph(final URI graphURI, final MediaType mediaType) throws IOException {
        return _getGraph(graphURI, mediaType);
    }

    private Graph _getGraph(URI graphURI, MediaType mediaType) throws IOException {
        if (mediaType == null) {
            throw new IllegalArgumentException("The media type must not be null");
        }
        final HttpGet request = new HttpGet(this.getGraphURI(graphURI));
        request.setHeader("Accept", mediaType.toString());
        final HttpResponse response = _client.execute(request);
        if (response.getStatusLine().getStatusCode() != 200) {
            request.abort();
            return null;
        }
        final HttpEntity entity = response.getEntity();
        final String encoding = entity.getContentEncoding() != null ? entity.getContentEncoding().getValue() : null;
        final MediaType mt = entity.getContentType() != null ? MediaType.valueOf(entity.getContentType().getValue()) : null;
        return new Graph(entity.getContent(), mt, encoding);
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
        return createGraph(graphURI, in, _mediaType);
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
        if (mediaType == null) {
            throw new IllegalArgumentException("The media type must not be null");
        }
        final HttpPut put = new HttpPut(getGraphURI(graphURI));
        final InputStreamEntity entity = new InputStreamEntity(in, -1);
        entity.setContentType(mediaType.toString());
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
        if (mediaType == null) {
            throw new IllegalArgumentException("The media type must not be null");
        }
        if (_graphsEndpoint == null) {
            throw new IllegalStateException("The endpoint for graphs is unknown.");
        }
        final HttpPost request = new HttpPost(_graphsEndpoint);
        final InputStreamEntity entity = new InputStreamEntity(in, -1);
        entity.setContentType(mediaType.toString());
        request.setEntity(entity);
        final HttpResponse response = _client.execute(request);
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
        return createGraph(in, _mediaType);
    }

    /**
     * Creates a graph using the content of the stream.
     *
     * Note: This method does not close the input stream. The caller should
     * close it.
     *
     * @param graphURI The graph URI.
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
        final HttpHead head = new HttpHead(getGraphURI(graphURI));
        return getStatusCode(head) == 200;
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
     * the {@link #getDefaultMediaType()}.
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
        return updateGraph(in, _mediaType);
    }

    /**
     * Updates the default graph by reading the provided file, assuming
     * the {@link #getDefaultMediaType()}.
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
     * default media type {@link #getDefaultMediaType()}.
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
        return updateGraph(graphURI, in, _mediaType);
    }

    /**
     * Updates the provided graph by reading the provided file, using the 
     * default media type {@link #getDefaultMediaType()}.
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
        if (mediaType == null) {
            throw new IllegalArgumentException("The media type must not be null");
        }
        final HttpPost post = new HttpPost(getGraphURI(graphURI));
        final InputStreamEntity entity = new InputStreamEntity(in, -1);
        entity.setContentType(mediaType.toString());
        post.setEntity(entity);
        final int status = getStatusCode(post);
        return status == 201 || status == 204;
    }

    private String getGraphURI(final URI graphURI) {
        return _endpoint + (graphURI == _DEFAULT_GRAPH ? "?default" : "?graph=" + graphURI.toASCIIString());
    }

    private int getStatusCode(final HttpUriRequest request) throws IOException {
        final HttpResponse response = _client.execute(request);
        final int status = response.getStatusLine().getStatusCode();
        request.abort();
        return status;
    }

    private static MediaType guessMediaType(final File file) {
        return guessMediaType(file.toURI());
   }

    private static MediaType guessMediaType(final URI uri) {
        final String uri_ = uri.toString();
        final int dotIdx = uri_.lastIndexOf('.');
        final String ext = dotIdx > -1 ? uri_.substring(dotIdx+1).toLowerCase() : null;
        if (ext == null) {
            return null;
        }
        final Syntax syntax = Syntax.forFileExtension(ext);
        return syntax != null ? MediaType.valueOf(syntax.getDefaultMIMEType()) : null;
   }

}
