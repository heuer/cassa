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

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import com.semagia.cassa.common.MediaType;

/**
 * EXPERIMENTAL: Abstract common HTTP client which provides common
 * methods useful for concrete implementations.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
abstract class AbstractClient {

    protected final URI _endpoint;
    protected MediaType[] _preferredMediaTypes;
    protected final HttpClient _client;

    AbstractClient(final URI endpoint) {
        if (endpoint == null) {
            throw new IllegalArgumentException("The endpoint URI must not be null");
        }
        _endpoint = endpoint;
        _client = new DefaultHttpClient();
    }

    /**
     * Should be called if the client is no longer needed.
     */
    public void close() {
        _client.getConnectionManager().shutdown();
    }

    /**
     * Returns the default media types.
     *
     * @return The default media types or {@code null} if the default media types
     *          isn't specified.
     */
    public MediaType[] getPreferredGraphMediaTypes() {
        return _preferredMediaTypes;
    }

    /**
     * Sets the default media types.
     *
     * @param mediaTypes A {@link MediaType} instance or {@code null}.
     */
    public void setPreferredGraphMediaTypes(final MediaType... mediaTypes) {
        _preferredMediaTypes = mediaTypes;
    }

    /**
     * Returns a graph from the provided graph URI using the {@link #getPreferredGraphMediaTypes()}
     * (if any).
     * 
     * @param graphURI The URI to retrieve the graph from.
     * @return A graph.
     * @throws IOException In case of an I/O error.
     */
    protected Graph getGraph(URI graphURI) throws IOException {
        return getGraph(graphURI, _preferredMediaTypes);
    }

    /**
     * Returns a graph from the provided graph URI.
     * 
     * @param graphURI The URI to retrieve the graph from.
     * @param mediaTypes The preferred media types.
     * @return A graph.
     * @throws IOException In case of an I/O error.
     */
    protected Graph getGraph(URI graphURI, MediaType... mediaTypes) throws IOException {
        final HttpGet request = new HttpGet(graphURI);
        if (mediaTypes != null) {
            final StringBuilder buff = new StringBuilder();
            for (int i=0; i < mediaTypes.length; i++) {
                if (i > 0) {
                    buff.append(',');
                }
                if (mediaTypes[i] != null) {
                    buff.append(mediaTypes[i].toString());
                }
            }
            request.setHeader("Accept", buff.toString());
        }
        final HttpResponse response = _client.execute(request);
        if (response.getStatusLine().getStatusCode() != 200) {
            request.abort();
            return null;
        }
        final HttpEntity entity = response.getEntity();
        final String encoding = entity.getContentEncoding() != null ? entity.getContentEncoding().getValue() : null;
        final MediaType mt = entity.getContentType() != null ? MediaType.valueOf(entity.getContentType().getValue()) : null;
        return new Graph(entity.getContent(), mt, encoding, entity.getContentLength());
    }

    protected boolean existsGraph(final URI graphURI) throws IOException {
        final HttpHead head = new HttpHead(graphURI);
        return getStatusCode(head) == 200;
    }

    /**
     * Executes the request and returns the status code.
     * 
     * @param request The request to execute.
     * @return The status code.
     * @throws IOException In case of an error.
     */
    protected int getStatusCode(final HttpUriRequest request) throws IOException {
        final HttpResponse response = _client.execute(request);
        final int status = response.getStatusLine().getStatusCode();
        request.abort();
        return status;
    }
}
