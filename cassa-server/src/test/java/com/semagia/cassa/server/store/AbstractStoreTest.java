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
package com.semagia.cassa.server.store;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.mulgara.mrg.Graph;
import org.mulgara.mrg.parser.ParseException;
import org.mulgara.mrg.parser.XMLGraphParser;
import org.mulgara.mrg.vocab.RDF;
import org.mulgara.mrg.vocab.uri.FOAF;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IGraphInfo;
import com.semagia.cassa.common.dm.IWritableRepresentation;

import junit.framework.TestCase;

/**
 * Tests against a {@link IStore} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public abstract class AbstractStoreTest<T extends IStore> extends TestCase {

    private static final URI 
        _INVALID_GRAPH = URI.create("http://www.semagia.com/invalid-graph"),
        _VALID_GRAPH = URI.create("http://www.semagia.com/valid-graph");

    private static final InputStream _INVALID_INPUTSTREAM = new ByteArrayInputStream("hello, i'm invalid".getBytes());

    private static final MediaType _INVALID_MEDIATYPE = MediaType.valueOf("foo/bar");
    
    protected T _store;

    /**
     * Creates an empty store.
     *
     * @return The empty store.
     */
    protected abstract T createStore() throws Exception;

    /**
     * Creates a graph under the provided URI.
     *
     * @param store The store.
     * @param graphURI
     * @throws StoreException
     */
    protected abstract void createGraph(T store, URI graphURI) throws StoreException;

    /**
     * Returns {@code true} iff this store understands RDF.
     *
     * @return {@code true} if this store understands RDF, {@code false} otherwise.
     */
    protected abstract boolean isRDFStore();

    /**
     * Returns {@code true} iff this store understands Topic Maps.
     *
     * @return {@code true} if this store understands Topic Maps, {@code false} otherwise.
     */
    protected abstract boolean isTMStore();

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _store = createStore();
        if (!isRDFStore() && !isTMStore()) {
            throw new Exception("The store must support RDF and/or Topic Maps");
        }
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        _store = null;
    }

    private void createDefaultGraph() throws StoreException {
        createGraph(_store, _VALID_GRAPH);
    }

    @SuppressWarnings("unused")
    private int graphCount() throws StoreException {
        int i = 0;
        for (IGraphInfo info: _store.getGraphInfos()) {
            i++;
        }
        return i;
    }

    @SuppressWarnings("unused")
    public void testEmptyStore() throws StoreException {
        for (IGraphInfo info: _store.getGraphInfos()) {
            fail("Expected no graphs");
        }
    }

    public void testGetGraphInfos() throws StoreException {
        createDefaultGraph();
        final List<IGraphInfo> infos = new ArrayList<IGraphInfo>();
        for (IGraphInfo info: _store.getGraphInfos()) {
            infos.add(info);
        }
        assertEquals(1, infos.size());
        assertEquals(_VALID_GRAPH, infos.get(0).getURI());
    }

    public void testHasGraphInvalid() throws StoreException {
        assertFalse(_store.containsGraph(_INVALID_GRAPH));
        assertFalse(_store.containsGraph(_VALID_GRAPH));
    }

    public void testHasGraphValid() throws StoreException {
        createDefaultGraph();
        assertFalse(_store.containsGraph(_INVALID_GRAPH));
        assertTrue(_store.containsGraph(_VALID_GRAPH));
    }

    public void testDeleteInvalid() throws StoreException {
        try {
            _store.deleteGraph(_INVALID_GRAPH);
            fail("Expected an exception for a non-existing graph");
        }
        catch (GraphNotExistsException ex) {
            // noop.
        }
    }

    public void testDeleteValid() throws StoreException {
        assertEquals(0, graphCount());
        createDefaultGraph();
        assertEquals(1, graphCount());
        _store.deleteGraph(_VALID_GRAPH);
        assertEquals(0, graphCount());
    }

    //TODO: Unsure about this test since the "default" graph may not be equivalent with "all graphs"
    public void testDeleteValid2() throws StoreException {
        assertEquals(0, graphCount());
        createDefaultGraph();
        assertEquals(1, graphCount());
        _store.deleteGraph(IStore.DEFAULT_GRAPH);
        assertEquals(0, graphCount());
    }

    public void testGetGraphInfoNotExisting() throws StoreException {
        try {
            _store.getGraphInfo(_INVALID_GRAPH);
            fail("Expected an exception for a non-existing graph");
        }
        catch (GraphNotExistsException ex) {
            // noop.
        }
    }

    public void testGetGraphInfoNotExisting2() throws StoreException {
        try {
            _store.getGraphInfo(IStore.DEFAULT_GRAPH);
            fail("Expected an exception for a non-existing graph");
        }
        catch (GraphNotExistsException ex) {
            // noop.
        }
    }

    public void testGetGraphInfo() throws StoreException {
        createDefaultGraph();
        final IGraphInfo info = _store.getGraphInfo(_VALID_GRAPH);
        assertEquals(_VALID_GRAPH, info.getURI());
        if (isRDFStore()) {
            assertTrue("An RDF store should support RDF/XML", info.getSupportedMediaTypes().contains(MediaType.RDF_XML));
        }
        if (isTMStore()) {
            assertTrue("A TM store should support XTM", info.getSupportedMediaTypes().contains(MediaType.XTM));
        }
    }

    public void testGetNotExisting() throws StoreException {
        try {
            _store.getGraph(_INVALID_GRAPH, MediaType.RDF_XML);
            fail("Expected an exception for a non-existing graph");
        }
        catch (GraphNotExistsException ex) {
            // noop.
        }
    }

    public void testGetNotExisting2() throws StoreException {
        try {
            _store.getGraph(IStore.DEFAULT_GRAPH, MediaType.RDF_XML);
            fail("Expected an exception for a non-existing graph");
        }
        catch (GraphNotExistsException ex) {
            // noop.
        }
    }

    public void testGet() throws StoreException {
        createDefaultGraph();
        if (isRDFStore()) {
            try {
                assertEquals(MediaType.RDF_XML, _store.getGraph(_VALID_GRAPH, MediaType.RDF_XML).getMediaType());
            }
            catch (UnsupportedMediaTypeException ex) {
                fail("A RDF store should support RDF/XML as serialization format");
            }
        }
        if (isTMStore()) {
            try {
                assertEquals(MediaType.XTM, _store.getGraph(_VALID_GRAPH, MediaType.XTM).getMediaType());
            }
            catch (UnsupportedMediaTypeException ex) {
                fail("A Topic Maps store should support XTM as serialization format");
            }
        }
    }

    public void testWritingRDFSimple() throws StoreException, IOException {
        if (!isRDFStore()) {
            return;
        }
        createDefaultGraph();
        final IWritableRepresentation writer = _store.getGraph(_VALID_GRAPH, MediaType.RDF_XML);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.write(out);
        assertTrue(out.size() > 0);
    }

    public void testWritingTMSimple() throws StoreException, IOException {
        if (!isTMStore()) {
            return;
        }
        createDefaultGraph();
        final IWritableRepresentation writer = _store.getGraph(_VALID_GRAPH, MediaType.XTM);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.write(out);
        assertTrue(out.size() > 0);
    }

    public void testGetInvalidMediaType() throws StoreException {
        createDefaultGraph();
        try {
            _store.getGraph(_VALID_GRAPH, _INVALID_MEDIATYPE);
            fail("Expected an exception for an unknown media type");
        }
        catch (UnsupportedMediaTypeException ex) {
            assertTrue(ex.getMediaTypes().size() > 0);
        }
    }

    public void testCreateOrReplaceIllegalMediaType() throws IOException, StoreException {
        try {
            _store.createOrReplaceGraph(_VALID_GRAPH, _INVALID_INPUTSTREAM, _VALID_GRAPH, _INVALID_MEDIATYPE);
            fail("Expected an exception for an unknown media type");
        }
        catch (UnsupportedMediaTypeException ex) {
            assertTrue(ex.getMediaTypes().size() > 0);
        }
    }

    public void testCreateOrUpdateIllegalMediaType() throws IOException, StoreException {
        try {
            _store.createOrUpdateGraph(_VALID_GRAPH, _INVALID_INPUTSTREAM, _VALID_GRAPH, _INVALID_MEDIATYPE);
            fail("Expected an exception for an unknown media type");
        }
        catch (UnsupportedMediaTypeException ex) {
            assertTrue(ex.getMediaTypes().size() > 0);
        }
    }

    public void testCreateAndReplaceRDFGraph() throws ParseException, IOException, StoreException, URISyntaxException {
        if (!isRDFStore()) {
            return;
        }
        final String p1 = "http://psi.example.org/P1";
        final String name1 = "Name";
        final String p2 = "http://psi.example.org/P2";
        final String name2 = "Name2";
        assertEquals(0, graphCount());
        InputStream in = AbstractStoreTest.class.getResourceAsStream("/test.rdf");
        IGraphInfo info = _store.createOrReplaceGraph(_VALID_GRAPH, in, _VALID_GRAPH, MediaType.RDF_XML);
        assertEquals(1, graphCount());
        assertEquals(_VALID_GRAPH, info.getURI());
        assertTrue(info.getSupportedMediaTypes().contains(MediaType.RDF_XML));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        _store.getGraph(_VALID_GRAPH, MediaType.RDF_XML).write(out);
        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
        Graph graph = new XMLGraphParser(input).getGraph();
        assertEquals(2, graph.size());
        assertTrue(graph.isAsserted(p1, RDF.TYPE, FOAF.PERSON));
        assertTrue(graph.isAsserted(p1, FOAF.NAME, name1));
        
        in = AbstractStoreTest.class.getResourceAsStream("/test2.rdf");
        info = _store.createOrReplaceGraph(_VALID_GRAPH, in, _VALID_GRAPH, MediaType.RDF_XML);
        assertEquals(1, graphCount());
        assertEquals(_VALID_GRAPH, info.getURI());
        assertTrue(info.getSupportedMediaTypes().contains(MediaType.RDF_XML));
        out = new ByteArrayOutputStream();
        _store.getGraph(_VALID_GRAPH, MediaType.RDF_XML).write(out);
        input = new ByteArrayInputStream(out.toByteArray());
        graph = new XMLGraphParser(input).getGraph();
        assertEquals(2, graph.size());
        assertFalse(graph.isAsserted(p1, RDF.TYPE, FOAF.PERSON));
        assertFalse(graph.isAsserted(p1, FOAF.NAME, name1));
        assertTrue(graph.isAsserted(p2, RDF.TYPE, FOAF.PERSON));
        assertTrue(graph.isAsserted(p2, FOAF.NAME, name2));
    }

    public void testCreateAndUpdateRDFGraph() throws ParseException, IOException, StoreException, URISyntaxException {
        if (!isRDFStore()) {
            return;
        }
        final String p1 = "http://psi.example.org/P1";
        final String name1 = "Name";
        final String p2 = "http://psi.example.org/P2";
        final String name2 = "Name2";
        assertEquals(0, graphCount());
        InputStream in = AbstractStoreTest.class.getResourceAsStream("/test.rdf");
        IGraphInfo info = _store.createOrUpdateGraph(_VALID_GRAPH, in, _VALID_GRAPH, MediaType.RDF_XML);
        assertEquals(1, graphCount());
        assertEquals(_VALID_GRAPH, info.getURI());
        assertTrue(info.getSupportedMediaTypes().contains(MediaType.RDF_XML));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        _store.getGraph(_VALID_GRAPH, MediaType.RDF_XML).write(out);
        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
        Graph graph = new XMLGraphParser(input).getGraph();
        assertEquals(2, graph.size());
        assertTrue(graph.isAsserted(p1, RDF.TYPE, FOAF.PERSON));
        assertTrue(graph.isAsserted(p1, FOAF.NAME, name1));
        
        in = AbstractStoreTest.class.getResourceAsStream("/test2.rdf");
        info = _store.createOrUpdateGraph(_VALID_GRAPH, in, _VALID_GRAPH, MediaType.RDF_XML);
        assertEquals(1, graphCount());
        assertEquals(_VALID_GRAPH, info.getURI());
        assertTrue(info.getSupportedMediaTypes().contains(MediaType.RDF_XML));
        out = new ByteArrayOutputStream();
        _store.getGraph(_VALID_GRAPH, MediaType.RDF_XML).write(out);
        input = new ByteArrayInputStream(out.toByteArray());
        graph = new XMLGraphParser(input).getGraph();
        assertEquals(4, graph.size());
        assertTrue(graph.isAsserted(p1, RDF.TYPE, FOAF.PERSON));
        assertTrue(graph.isAsserted(p1, FOAF.NAME, name1));
        assertTrue(graph.isAsserted(p2, RDF.TYPE, FOAF.PERSON));
        assertTrue(graph.isAsserted(p2, FOAF.NAME, name2));
    }

}
