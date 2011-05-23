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
package com.semagia.cassa.server.testsuite;

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
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public abstract class AbstractCassaTestCase extends TestCase implements IConstants {

    protected static final MediaType MEDIATYPE_ANY = MediaType.valueOf("*/*");

    protected GraphClient _client;

    protected abstract MediaType getDefaultMediaType() throws Exception;

    protected abstract InputStream getGraphWithDefaultMediaType() throws Exception;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _client = new GraphClient(TestUtils.getServiceEndpoint());
        _client.setDefaultMediaType(getDefaultMediaType());
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        _client = null;
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

    public void testEmptyGraphCreation() throws Exception {
        final URI graphURI = URI.create("http://www.example.org/");
        assertGraphNotExists(graphURI);
        _client.createGraph(graphURI, getGraphWithDefaultMediaType());
        assertGraphExists(graphURI);
        assertGraphGET(graphURI, getDefaultMediaType());
    }

    public void assertGraphGET(final URI graphURI) throws Exception {
        assertGraphGET(graphURI, MEDIATYPE_ANY);
    }

    public void assertGraphGET(final URI graphURI, final MediaType mediaType) throws Exception {
        final Graph graph = _client.getGraph(graphURI, mediaType);
        assertNotNull(graph);
        graph.close();
    }

    public void assertGraphNotExists(final URI graphURI) throws Exception {
        assertFalse(_client.existsGraph(graphURI));
    }

    public void assertGraphExists(final URI graphURI) throws Exception {
        assertTrue(_client.existsGraph(graphURI));
    }

    public void assertGraphGetMediaType(final URI graphURI, final MediaType mediaType) throws Exception {
        final Graph graph = _client.getGraph(graphURI, mediaType);
        assertNotNull(graph);
        assertEquals(mediaType, graph.getMediaType());
    }

    public void assertGraphDelete(final URI graphURI) throws Exception {
        assertTrue(_client.deleteGraph(graphURI) != null);
    }

    public void createGraph(final URI graphURI, final String file) throws Exception {
        assertTrue(_client.createGraph(graphURI, getFile(file)));
        assertGraphExists(graphURI);
        assertGraphGET(graphURI);
    }

}
