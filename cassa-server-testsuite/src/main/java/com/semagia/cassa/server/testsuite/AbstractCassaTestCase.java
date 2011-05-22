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

import java.io.InputStream;
import java.net.URI;

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

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _client = new GraphClient(TestUtils.getServiceEndpoint());
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        _client = null;
    }

    protected final InputStream getInputStream(final String testFile) {
        return this.getClass().getResourceAsStream(testFile);
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

    public void assertGraphGET(final URI graphURI) throws Exception {
        assertNotNull(_client.getGraph(graphURI, MEDIATYPE_ANY));
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

}
