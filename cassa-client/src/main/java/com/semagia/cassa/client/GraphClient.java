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
import com.semagia.cassa.common.dm.RemovalStatus;

/**
 * EXPERIMENTAL Client implementation to interact with a HTTP graph store.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class GraphClient {

    private static final URI _DEFAULT_GRAPH = URI.create("");

    private final String _endpoint;
    private MediaType _mediaType;
    private final HttpClient _client;

    public GraphClient(final URI endpoint) {
        if (endpoint == null) {
            throw new IllegalArgumentException("The endpoint URI must not be null");
        }
        _endpoint = endpoint.toString();
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

    public Graph getGraph() throws IOException {
        return getGraph(_mediaType);
    }

    public Graph getGraph(final MediaType mediaType) throws IOException {
        return _getGraph(_DEFAULT_GRAPH, mediaType);
    }

    public Graph getGraph(final URI graphURI) throws IOException {
        return getGraph(graphURI, _mediaType);
    }

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

    public boolean createGraph(final URI graphURI, final File file) throws IOException {
        return createGraph(graphURI, file, MediaTypeUtils.guessMediaType(file.toURI()));
    }

    public boolean createGraph(final URI graphURI, final File file, final MediaType mediaType) throws IOException {
        return _createGraph(graphURI, file, mediaType);
    }

    public boolean createGraph(final URI graphURI, final InputStream in) throws IOException {
        return createGraph(graphURI, in, _mediaType);
    }

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

    public boolean existsGraph() throws IOException {
        return _existsGraph(_DEFAULT_GRAPH);
    }

    public boolean existsGraph(final URI graphURI) throws IOException {
        return _existsGraph(graphURI);
    }

    private boolean _existsGraph(final URI graphURI) throws IOException {
        final HttpHead head = new HttpHead(getGraphURI(graphURI));
        return getStatusCode(head) == 200;
    }

    public RemovalStatus deleteGraph() throws IOException {
        return _deleteGraph(_DEFAULT_GRAPH);
    }

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

    public boolean updateGraph(final InputStream in) throws IOException {
        return updateGraph(in, _mediaType);
    }

    public boolean updateGraph(final File file) throws IOException {
        return _updateGraph(_DEFAULT_GRAPH, file);
    }

    public boolean updateGraph(final File file, final MediaType mediaType) throws IOException {
        return _updateGraph(_DEFAULT_GRAPH, file, mediaType);
    }

    public boolean updateGraph(final InputStream in, final MediaType mediaType) throws IOException {
        return _updateGraph(_DEFAULT_GRAPH, in, mediaType);
    }

    public boolean updateGraph(final URI graphURI, final InputStream in) throws IOException {
        return updateGraph(graphURI, in, _mediaType);
    }

    public boolean updateGraph(final URI graphURI, final File file) throws IOException {
        return _updateGraph(graphURI, file);
    }

    public boolean updateGraph(final URI graphURI, final File file, final MediaType mediaType) throws IOException {
        return _updateGraph(graphURI, file, mediaType);
    }

    public boolean updateGraph(final URI graphURI, final InputStream in, final MediaType mediaType) throws IOException {
        return _updateGraph(graphURI, in, mediaType);
    }

    private boolean _updateGraph(final URI graphURI, final File file) throws IOException {
        return _updateGraph(graphURI, file, MediaTypeUtils.guessMediaType(file.toURI()));
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
        HttpResponse response = _client.execute(request);
        final int status = response.getStatusLine().getStatusCode();
        request.abort();
        return status;
    }

}
