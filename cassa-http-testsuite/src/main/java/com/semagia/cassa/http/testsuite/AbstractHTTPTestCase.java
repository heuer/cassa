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
package com.semagia.cassa.http.testsuite;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import com.semagia.cassa.client.Graph;
import com.semagia.cassa.client.GraphClient;
import com.semagia.cassa.common.MediaType;

import junit.framework.TestCase;

/**
 * Abstract test case which can be used for integration tests.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public abstract class AbstractHTTPTestCase extends TestCase {

    public static final String 
            SERVICE_ENDPOINT = "cassa-service-endpoint",
            GRAPH_BASE = "cassa-graph-base";

    public static final MediaType INVALID_MEDIATYPE = MediaType.valueOf("foo/bar");

    protected static final MediaType MEDIATYPE_ANY = MediaType.valueOf("*/*");

    protected GraphClient _client;

    protected abstract MediaType getDefaultMediaType() throws Exception;

    protected abstract MediaType getDefaultUpdateMediaType() throws Exception;

    protected abstract InputStream getGraphWithDefaultMediaType() throws Exception;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _client = new GraphClient(getServiceEndpoint(), getGraphBase());
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        _client = null;
    }

    private static URI getServiceEndpoint() {
        return URI.create(getSystemProperty(SERVICE_ENDPOINT));
    }

    private static URI getGraphBase() {
        return URI.create(getSystemProperty(GRAPH_BASE));
    }

    private static String getSystemProperty(final String name) {
        final String val = System.getProperty(name);
        if (val == null) {
            throw new IllegalStateException("The system property " + name + " is not defined");
        }
        return val;
    }

    protected File getFile(final String testFile) throws Exception {
        return new File(getResource(testFile).toURI());
    }

    protected URL getResource(final String testFile) throws Exception {
        return getClass().getResource(testFile);
    }

    protected InputStream getInputStream(final String testFile) throws Exception {
        return getClass().getResourceAsStream(testFile);
    }

    protected Graph getGraph(final URI graphURI) throws Exception {
        return _client.getGraph(graphURI);
    }

    protected Graph getGraph(final URI graphURI, final MediaType mediaType) throws Exception {
        return _client.getGraph(graphURI, mediaType);
    }

    protected Graph getGraph() throws Exception {
        return _client.getGraph();
    }

    protected Graph getGraph(final MediaType mediaType) throws Exception {
        return _client.getGraph(mediaType);
    }

    protected InputStream getGraphInputStream(final URI graphURI, final MediaType mediaType) throws IOException {
        final Graph graph = _client.getGraph(graphURI, mediaType);
        assertNotNull(graph);
        return graph.getInputStream();
    }

    protected void assertGraphGET(final URI graphURI) throws Exception {
        assertGraphGET(graphURI, MEDIATYPE_ANY);
    }

    protected void assertGraphGET(final URI graphURI, final MediaType mediaType) throws Exception {
        final Graph graph = _client.getGraph(graphURI, mediaType);
        assertNotNull(graph);
        if (!MEDIATYPE_ANY.equals(mediaType)) {
            assertEquals(mediaType, graph.getMediaType());
        }
        graph.close();
    }

    protected void assertGraphNotExists(final URI graphURI) throws Exception {
        assertFalse("The graph " + graphURI + " should not exist",
                    _client.existsGraph(graphURI));
    }

    protected void assertGraphExists(final URI graphURI) throws Exception {
        assertTrue(_client.existsGraph(graphURI));
    }

    protected void assertGraphGetMediaType(final URI graphURI, final MediaType mediaType) throws Exception {
        final Graph graph = _client.getGraph(graphURI, mediaType);
        assertNotNull(graph);
        assertEquals(mediaType, graph.getMediaType());
        graph.close();
    }

    protected void assertGraphDelete() throws Exception {
        assertTrue(_client.deleteGraph() != null);
    }

    protected void assertGraphDelete(final URI graphURI) throws Exception {
        assertTrue(deleteGraph(graphURI));
    }

    protected void createGraph(final URI graphURI, final String file) throws Exception {
        createGraph(graphURI, file, null);
    }

    protected void createGraph(final URI graphURI, final String file, final MediaType mediaType) throws Exception {
        if (_client.createGraph(graphURI, getFile(file), mediaType)) {
            assertGraphExists(graphURI);
            assertGraphGET(graphURI);
        }
    }

    protected URI createGraph(final String file) throws Exception {
        return createGraph(file, null);
    }

    protected URI createGraph(final String file, final MediaType mediaType) throws Exception {
        final URI uri = _client.createGraph(getFile(file), mediaType);
        if (uri != null) {
            assertGraphExists(uri);
            assertGraphGET(uri);
        }
        return uri;
    }

    protected boolean updateGraph(final URI graphURI, final String file) throws Exception {
        return updateGraph(graphURI, file, null);
    }

    protected boolean updateGraph(final URI graphURI, final String file, final MediaType mediaType) throws Exception {
        final boolean success = _client.updateGraph(graphURI, getFile(file), mediaType);
        if (success) {
            assertGraphExists(graphURI);
            assertGraphGET(graphURI);
        }
        return success;
    }

    protected boolean updateGraph(final String file) throws Exception {
        return updateGraph(file, null);
    }

    protected boolean updateGraph(final String file, final MediaType mediaType) throws Exception {
        return _client.updateGraph(getFile(file), mediaType);
    }

    protected boolean modifyGraph(final String query) throws Exception {
        return _client.modifyGraph(query, null);
    }

    protected boolean modifyGraph(final URI graphURI, final String query) throws Exception {
        return _client.modifyGraph(graphURI, query, null);
    }

    protected boolean modifyGraph(final String query, final MediaType mediaType) throws Exception {
        return _client.modifyGraph(query, mediaType);
    }

    protected boolean modifyGraph(final URI graphURI, final String query, final MediaType mediaType) throws Exception {
        return _client.modifyGraph(graphURI, query, mediaType);
    }

    protected boolean deleteGraph(final URI graphURI) throws Exception {
        final boolean success = _client.deleteGraph(graphURI) != null;
        if (success) {
            assertGraphNotExists(graphURI);
        }
        return success;
    }


    /*
     * Default tests, indepentently of serializations.
     */

    public void testDefaultGraphExists() throws Exception {
        assertTrue(_client.existsGraph());
    }

    public void testDefaultGraphDeleteExists() throws Exception {
        assertTrue(_client.existsGraph());
        assertTrue(_client.deleteGraph() != null);
        assertTrue(_client.existsGraph());
    }

    public void testGetDefaultGraph() throws Exception {
        assertNotNull(_client.getGraph(MEDIATYPE_ANY));
    }

    public void testGetDefaultGraphNoMediaType() throws Exception {
        assertNotNull(_client.getGraph());
    }

    public void testEmptyGraphCreation() throws Exception {
        final URI graphURI = URI.create("http://www.example.org/empty");
        assertGraphNotExists(graphURI);
        _client.createGraph(graphURI, getGraphWithDefaultMediaType(), getDefaultMediaType());
        assertGraphExists(graphURI);
        assertGraphGET(graphURI, getDefaultMediaType());
        assertGraphDelete(graphURI);
    }

    public void testIllegalGraphCreation() throws Exception {
        final URI graphURI = URI.create("http://www.example.org/illegal");
        assertGraphNotExists(graphURI);
        _client.createGraph(graphURI, getGraphWithDefaultMediaType(), INVALID_MEDIATYPE);
        assertGraphNotExists(graphURI);
    }

    /**
     * Acc. to spec the server should try to parse the submitted graph assuming
     * an approbiate media type. 
     * 
     * @throws Exception In case of an error.
     */
    public void testGraphCreationNoMediaType() throws Exception {
        final URI graphURI = URI.create("http://www.example.org/graph");
        assertGraphNotExists(graphURI);
        _client.createGraph(graphURI, getGraphWithDefaultMediaType());
        assertGraphExists(graphURI);
    }

}
