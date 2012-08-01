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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mulgara.mrg.Bnode;
import org.mulgara.mrg.Graph;
import org.mulgara.mrg.ObjectNode;
import org.mulgara.mrg.PredicateNode;
import org.mulgara.mrg.SubjectNode;
import org.mulgara.mrg.Triple;
import org.mulgara.mrg.parser.N3GraphParser;
import org.mulgara.mrg.parser.XMLGraphParser;
import org.tinytim.mio.CXTMTopicMapWriter;
import org.tinytim.mio.TinyTimMapInputHandler;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystemFactory;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IGraphInfo;
import com.semagia.cassa.common.dm.IWritableRepresentation;
import com.semagia.mio.DeserializerRegistry;
import com.semagia.mio.IDeserializer;
import com.semagia.mio.Source;
import com.semagia.mio.Syntax;

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
    protected abstract void createGraph(T store, URI graphURI) throws Exception;

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

    private void createDefaultGraph() throws Exception {
        createGraph(_store, _VALID_GRAPH);
    }

    private int graphCount() throws StoreException {
        int i = 0;
        for (@SuppressWarnings("unused") IGraphInfo info: _store.getGraphInfos()) {
            i++;
        }
        return i;
    }

    private InputStream getInputStream(final String testFile) {
        return AbstractStoreTest.class.getResourceAsStream(testFile);
    }

    private Graph getRDFXMLGraph(final String testFile) throws Exception {
        return getRDFXMLGraph(getInputStream(testFile));
    }

    private Graph getRDFXMLGraph(final InputStream in) throws Exception {
        return new XMLGraphParser(in).getGraph();
    }

    private Graph getRDFXMLGraph(final ByteArrayOutputStream out) throws Exception {
        return getRDFXMLGraph(new ByteArrayInputStream(out.toByteArray()));
    }

    private Graph getTurtleGraph(final InputStream in) throws Exception {
        return new N3GraphParser(in).getGraph();
    }

    private Graph getTurtleGraph(final ByteArrayOutputStream out) throws Exception {
        return getTurtleGraph(new ByteArrayInputStream(out.toByteArray()));
    }

    private void assertGraphEquality(final Graph g1, final Graph g2) {
        assertEquals(g1.size(), g2.size());
        for (Triple t: g2.getTriples()) {
            SubjectNode s = t.getSubject();
            PredicateNode p = t.getPredicate();
            ObjectNode o = t.getObject();
            if (s instanceof Bnode) s = null;
            if (o instanceof Bnode) o = null;
            assertTrue("Graph does not contain: " + t, g1.match(s, p, o).hasNext());
          }
    }

    private TopicMap getXTMGraph(final ByteArrayOutputStream out, final URI base) throws Exception {
        final TopicMap tm = TopicMapSystemFactory.newInstance().newTopicMapSystem().createTopicMap(base.toString());
        final IDeserializer deser = DeserializerRegistry.getInstance().createDeserializer(Syntax.XTM);
        deser.setMapHandler(new TinyTimMapInputHandler(tm));
        deser.parse(new Source(new ByteArrayInputStream(out.toByteArray()), base.toString()));
        return tm;
    }

    private TopicMap getCTMGraph(final ByteArrayOutputStream out, final URI base) throws Exception {
        final TopicMap tm = TopicMapSystemFactory.newInstance().newTopicMapSystem().createTopicMap(base.toString());
        final IDeserializer deser = DeserializerRegistry.getInstance().createDeserializer(Syntax.CTM);
        deser.setMapHandler(new TinyTimMapInputHandler(tm));
        deser.parse(new Source(new ByteArrayInputStream(out.toByteArray()), base.toString()));
        return tm;
    }

    private void assertGraphEquality(final String cxtmReferenceFile, final TopicMap tm, URI base) throws Exception {
        final InputStream in = this.getInputStream(cxtmReferenceFile);
        final ByteArrayOutputStream expected = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1) {
            expected.write(b);
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final CXTMTopicMapWriter writer = new CXTMTopicMapWriter(out, base.toString());
        writer.write(tm);
        final byte[] reference = expected.toByteArray();
        final byte[] result = out.toByteArray();
        if (!Arrays.equals(reference, result)) {
            fail("Expected:\n" + expected.toString("utf-8") + "\n\ngot:\n" + out.toString("utf-8"));
        }
    }

    @SuppressWarnings("unused")
    public void testEmptyStore() throws Exception {
        for (IGraphInfo info: _store.getGraphInfos()) {
            fail("Expected no graphs");
        }
    }

    public void testGetGraphInfos() throws Exception {
        createDefaultGraph();
        final List<IGraphInfo> infos = new ArrayList<IGraphInfo>();
        for (IGraphInfo info: _store.getGraphInfos()) {
            infos.add(info);
        }
        assertEquals(1, infos.size());
        assertEquals(_VALID_GRAPH, infos.get(0).getURI());
    }

    public void testDefaultGraph() throws Exception {
        assertTrue("The default graph must always exists", 
                _store.containsGraph(IStore.DEFAULT_GRAPH));
        _store.deleteGraph(IStore.DEFAULT_GRAPH);
        assertTrue("After deleting the default graph it must be reconstructed", 
                _store.containsGraph(IStore.DEFAULT_GRAPH));
    }

    public void testDefaultGraphInfo() throws Exception {
        try {
            _store.getGraphInfo(IStore.DEFAULT_GRAPH);
        }
        catch (GraphNotExistsException ex) {
            fail("The default graph must always exist");
        }
    }

    public void testGetDefaultGraphRDF() throws Exception {
        if (!isRDFStore()) {
            return;
        }
        final IWritableRepresentation writer = _store.getGraph(IStore.DEFAULT_GRAPH, MediaType.RDF_XML);
        assertEquals(MediaType.RDF_XML, writer.getMediaType());
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.write(out);
        assertGraphEquality(getRDFXMLGraph("/empty.rdf"), getRDFXMLGraph(out));
    }

    public void testGetDefaultGraphTM() throws Exception {
        if (!isTMStore()) {
            return;
        }
        final IWritableRepresentation writer = _store.getGraph(IStore.DEFAULT_GRAPH, MediaType.XTM);
        assertEquals(MediaType.XTM, writer.getMediaType());
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.write(out);
        final URI base = URI.create("http://www.example.org/");
        assertGraphEquality("/empty.xtm.cxtm", getXTMGraph(out, base), base);
    }

    public void testContainsGraphInvalid() throws Exception {
        assertFalse(_store.containsGraph(_INVALID_GRAPH));
        assertFalse(_store.containsGraph(_VALID_GRAPH));
    }

    public void testContainsGraphValid() throws Exception {
        createDefaultGraph();
        assertFalse(_store.containsGraph(_INVALID_GRAPH));
        assertTrue(_store.containsGraph(_VALID_GRAPH));
    }

    public void testDeleteInvalid() throws StoreException, IOException {
        try {
            _store.deleteGraph(_INVALID_GRAPH);
            fail("Expected an exception for a non-existing graph");
        }
        catch (GraphNotExistsException ex) {
            // noop.
        }
    }

    public void testDeleteValid() throws Exception {
        assertEquals(0, graphCount());
        createDefaultGraph();
        assertEquals(1, graphCount());
        _store.deleteGraph(_VALID_GRAPH);
        assertEquals(0, graphCount());
    }

    public void testGetGraphInfoNotExisting() throws Exception {
        try {
            _store.getGraphInfo(_INVALID_GRAPH);
            fail("Expected an exception for a non-existing graph");
        }
        catch (GraphNotExistsException ex) {
            // noop.
        }
    }

    public void testGetGraphInfo() throws Exception {
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

    public void testGetNotExisting() throws Exception {
        try {
            _store.getGraph(_INVALID_GRAPH, MediaType.RDF_XML);
            fail("Expected an exception for a non-existing graph");
        }
        catch (GraphNotExistsException ex) {
            // noop.
        }
    }

    public void testGet() throws Exception {
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

    public void testWritingRDFSimple() throws Exception {
        if (!isRDFStore()) {
            return;
        }
        createDefaultGraph();
        final IWritableRepresentation writer = _store.getGraph(_VALID_GRAPH, MediaType.RDF_XML);
        assertNotNull(writer);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.write(out);
        assertTrue(out.size() > 0);
    }

    public void testWritingTMSimple() throws Exception {
        if (!isTMStore()) {
            return;
        }
        createDefaultGraph();
        final IWritableRepresentation writer = _store.getGraph(_VALID_GRAPH, MediaType.XTM);
        assertNotNull(writer);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.write(out);
        assertTrue(out.size() > 0);
    }

    public void testGetInvalidMediaType() throws Exception {
        createDefaultGraph();
        try {
            _store.getGraph(_VALID_GRAPH, _INVALID_MEDIATYPE);
            fail("Expected an exception for an unknown media type");
        }
        catch (UnsupportedMediaTypeException ex) {
            assertTrue(ex.getMediaTypes().size() > 0);
        }
    }

    public void testCreateOrReplaceIllegalMediaType() throws Exception {
        try {
            _store.createOrReplaceGraph(_VALID_GRAPH, _INVALID_INPUTSTREAM, _VALID_GRAPH, _INVALID_MEDIATYPE);
            fail("Expected an exception for an unknown media type");
        }
        catch (UnsupportedMediaTypeException ex) {
            assertTrue(ex.getMediaTypes().size() > 0);
        }
    }

    public void testUpdateIllegalMediaType() throws Exception {
        createDefaultGraph();
        try {
            _store.updateGraph(_VALID_GRAPH, _INVALID_INPUTSTREAM, _VALID_GRAPH, _INVALID_MEDIATYPE);
            fail("Expected an exception for an unknown media type");
        }
        catch (UnsupportedMediaTypeException ex) {
            assertTrue(ex.getMediaTypes().size() > 0);
        }
    }

    public void testCreateAndReplaceRDFGraph() throws Exception {
        if (!isRDFStore()) {
            return;
        }
        final MediaType mediaType = MediaType.RDF_XML;
        final String testFile1 = "/test.rdf";
        final String testFile2 = "/test2.rdf";
        assertEquals(0, graphCount());
        final IGraphInfo info = _store.createOrReplaceGraph(_VALID_GRAPH, getInputStream(testFile1), _VALID_GRAPH, mediaType);
        assertEquals(1, graphCount());
        assertEquals(_VALID_GRAPH, info.getURI());
        assertTrue(info.getSupportedMediaTypes().contains(mediaType));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        _store.getGraph(_VALID_GRAPH, mediaType).write(out);

        assertGraphEquality(getRDFXMLGraph(testFile1), getRDFXMLGraph(out));
        
        final IGraphInfo info2 = _store.createOrReplaceGraph(_VALID_GRAPH, getInputStream(testFile2), _VALID_GRAPH, mediaType);
        assertEquals(1, graphCount());
        assertEquals(info.getURI(), info2.getURI());
        assertTrue(info2.getSupportedMediaTypes().contains(mediaType));
        out = new ByteArrayOutputStream();
        _store.getGraph(_VALID_GRAPH, mediaType).write(out);

        assertGraphEquality(getRDFXMLGraph(testFile2), getRDFXMLGraph(out));
        
        _store.deleteGraph(_VALID_GRAPH);
        assertEquals(0, graphCount());
    }

    public void testCreateAndUpdateRDFGraph() throws Exception {
        if (!isRDFStore()) {
            return;
        }
        final MediaType mediaType = MediaType.RDF_XML;
        final String testFile1 = "/test.rdf";
        final String testFile2 = "/test2.rdf";
        final String testFilesMerged = "/test+test2.rdf";
        final URI base = URI.create("http://www.semagia.com/g/");
        assertEquals(0, graphCount());
        final IGraphInfo info = _store.createGraph(getInputStream(testFile1), base, mediaType);
        assertEquals(1, graphCount());
        assertFalse(base.relativize(info.getURI()).isAbsolute());
        assertTrue(info.getSupportedMediaTypes().contains(mediaType));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        _store.getGraph(info.getURI(), mediaType).write(out);
        
        assertGraphEquality(getRDFXMLGraph(testFile1), getRDFXMLGraph(out));
        
        final IGraphInfo info2 = _store.updateGraph(info.getURI(), getInputStream(testFile2), info.getURI(), mediaType);
        assertEquals(1, graphCount());
        assertEquals(info.getURI(), info2.getURI());
        assertTrue(info.getSupportedMediaTypes().contains(mediaType));
        out = new ByteArrayOutputStream();
        _store.getGraph(info.getURI(), mediaType).write(out);
        
        assertGraphEquality(getRDFXMLGraph(testFilesMerged), getRDFXMLGraph(out));

        _store.deleteGraph(info.getURI());
        assertEquals(0, graphCount());
    }

    public void testCreateAndReplaceRDFGraphTurtle() throws Exception {
        if (!isRDFStore()) {
            return;
        }
        final MediaType mediaType = MediaType.RDF_XML;
        final String testFile1 = "/test.rdf";
        final String testFile2 = "/test2.rdf";
        final MediaType readMediaType = MediaType.TURTLE;
        assertEquals(0, graphCount());
        final IGraphInfo info = _store.createOrReplaceGraph(_VALID_GRAPH, getInputStream(testFile1), _VALID_GRAPH, mediaType);
        assertEquals(1, graphCount());
        assertEquals(_VALID_GRAPH, info.getURI());
        assertTrue(info.getSupportedMediaTypes().contains(mediaType));

        if (!info.getSupportedMediaTypes().contains(readMediaType)) {
            return;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        _store.getGraph(_VALID_GRAPH, readMediaType).write(out);

        assertGraphEquality(getRDFXMLGraph(testFile1), getTurtleGraph(out));
        
        final IGraphInfo info2 = _store.createOrReplaceGraph(_VALID_GRAPH, getInputStream(testFile2), _VALID_GRAPH, mediaType);
        assertEquals(1, graphCount());
        assertEquals(info.getURI(), info2.getURI());
        assertTrue(info2.getSupportedMediaTypes().contains(mediaType));
        out = new ByteArrayOutputStream();
        _store.getGraph(_VALID_GRAPH, readMediaType).write(out);

        assertGraphEquality(getRDFXMLGraph(testFile2), getTurtleGraph(out));
        
        _store.deleteGraph(_VALID_GRAPH);
        assertEquals(0, graphCount());
    }

    public void testCreateAndUpdateRDFGraphTurtle() throws Exception {
        if (!isRDFStore()) {
            return;
        }
        final MediaType mediaType = MediaType.RDF_XML;
        final MediaType readMediaType = MediaType.TURTLE;
        final String testFile1 = "/test.rdf";
        final String testFile2 = "/test2.rdf";
        final String testFilesMerged = "/test+test2.rdf";
        final URI base = URI.create("http://www.semagia.com/g/");
        assertEquals(0, graphCount());
        final IGraphInfo info = _store.createGraph(getInputStream(testFile1), base, mediaType);
        assertEquals(1, graphCount());
        assertFalse(base.relativize(info.getURI()).isAbsolute());
        assertTrue(info.getSupportedMediaTypes().contains(mediaType));
        
        if (!info.getSupportedMediaTypes().contains(readMediaType)) {
            return;
        }
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        _store.getGraph(info.getURI(), readMediaType).write(out);
        
        assertGraphEquality(getRDFXMLGraph(testFile1), getTurtleGraph(out));
        
        final IGraphInfo info2 = _store.updateGraph(info.getURI(), getInputStream(testFile2), info.getURI(), mediaType);
        assertEquals(1, graphCount());
        assertEquals(info.getURI(), info2.getURI());
        assertTrue(info.getSupportedMediaTypes().contains(mediaType));
        out = new ByteArrayOutputStream();
        _store.getGraph(info.getURI(), readMediaType).write(out);
        
        assertGraphEquality(getRDFXMLGraph(testFilesMerged), getTurtleGraph(out));

        _store.deleteGraph(info.getURI());
        assertEquals(0, graphCount());
    }

    public void testCreateAndReplaceTMGraph() throws Exception {
        if (!isTMStore()) {
            return;
        }
        final String testFile1 = "/test.xtm";
        final String testFile2 = "/test2.xtm";
        final MediaType mediaType = MediaType.XTM;
        final URI base = _VALID_GRAPH;
        assertEquals(0, graphCount());
        final IGraphInfo info = _store.createOrReplaceGraph(_VALID_GRAPH, getInputStream(testFile1), _VALID_GRAPH, mediaType);
        assertEquals(1, graphCount());
        assertEquals(_VALID_GRAPH, info.getURI());
        assertTrue(info.getSupportedMediaTypes().contains(mediaType));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        _store.getGraph(_VALID_GRAPH, mediaType).write(out);

        assertGraphEquality(testFile1 + ".cxtm", getXTMGraph(out, base), base);
        
        final IGraphInfo info2 = _store.createOrReplaceGraph(_VALID_GRAPH, getInputStream(testFile2), _VALID_GRAPH, mediaType);
        assertEquals(1, graphCount());
        assertEquals(info.getURI(), info2.getURI());
        assertTrue(info2.getSupportedMediaTypes().contains(mediaType));
        out = new ByteArrayOutputStream();
        _store.getGraph(_VALID_GRAPH, mediaType).write(out);

        assertGraphEquality(testFile2 + ".cxtm", getXTMGraph(out, base), base);
        
        _store.deleteGraph(_VALID_GRAPH);
        assertEquals(0, graphCount());
    }

    public void testCreateAndUpdateTMGraph() throws Exception {
        if (!isTMStore()) {
            return;
        }
        final String testFile1 = "/test.xtm";
        final String testFile2 = "/test2.xtm";
        final String testFilesMerged = "/test+test2.xtm";
        final URI base = URI.create("http://www.semagia.com/g/");
        final MediaType mediaType = MediaType.XTM;
        assertEquals(0, graphCount());
        final IGraphInfo info = _store.createGraph(getInputStream(testFile1), base, mediaType);
        assertEquals(1, graphCount());
        assertFalse(base.relativize(info.getURI()).isAbsolute());
        assertTrue(info.getSupportedMediaTypes().contains(mediaType));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        _store.getGraph(info.getURI(), mediaType).write(out);
        
        assertGraphEquality(testFile1 + ".cxtm", getXTMGraph(out, base), base);
        
        final IGraphInfo info2 = _store.updateGraph(info.getURI(), getInputStream(testFile2), info.getURI(), mediaType);
        assertEquals(1, graphCount());
        assertEquals(info.getURI(), info2.getURI());
        assertTrue(info.getSupportedMediaTypes().contains(mediaType));
        out = new ByteArrayOutputStream();
        _store.getGraph(info.getURI(), mediaType).write(out);
        
        assertGraphEquality(testFilesMerged + ".cxtm", getXTMGraph(out, base), base);

        _store.deleteGraph(info.getURI());
        assertEquals(0, graphCount());
    }

    public void testCreateAndUpdateTMGraphCTM() throws Exception {
        if (!isTMStore()) {
            return;
        }
        final String testFile1 = "/test.xtm";
        final String testFile2 = "/test2.xtm";
        final String testFilesMerged = "/test+test2.xtm";
        final URI base = URI.create("http://www.semagia.com/g/");
        final MediaType mediaType = MediaType.XTM;
        final MediaType readMediaType = MediaType.CTM;
        assertEquals(0, graphCount());
        final IGraphInfo info = _store.createGraph(getInputStream(testFile1), base, mediaType);
        assertEquals(1, graphCount());
        assertFalse(base.relativize(info.getURI()).isAbsolute());
        assertTrue(info.getSupportedMediaTypes().contains(mediaType));
        if (!info.getSupportedMediaTypes().contains(readMediaType)) {
            return;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        _store.getGraph(info.getURI(), readMediaType).write(out);
        
        assertGraphEquality(testFile1 + ".cxtm", getCTMGraph(out, base), base);
        
        final IGraphInfo info2 = _store.updateGraph(info.getURI(), getInputStream(testFile2), info.getURI(), mediaType);
        assertEquals(1, graphCount());
        assertEquals(info.getURI(), info2.getURI());
        assertTrue(info.getSupportedMediaTypes().contains(mediaType));
        out = new ByteArrayOutputStream();
        _store.getGraph(info.getURI(), readMediaType).write(out);
        
        assertGraphEquality(testFilesMerged + ".cxtm", getCTMGraph(out, base), base);

        _store.deleteGraph(info.getURI());
        assertEquals(0, graphCount());
    }

    public void testCreateAndReplaceTMGraphCTM() throws Exception {
        if (!isTMStore()) {
            return;
        }
        final String testFile1 = "/test.xtm";
        final String testFile2 = "/test2.xtm";
        final MediaType mediaType = MediaType.XTM;
        final MediaType readMediaType = MediaType.CTM;
        final URI base = _VALID_GRAPH;
        assertEquals(0, graphCount());
        final IGraphInfo info = _store.createOrReplaceGraph(_VALID_GRAPH, getInputStream(testFile1), _VALID_GRAPH, mediaType);
        assertEquals(1, graphCount());
        assertEquals(_VALID_GRAPH, info.getURI());
        assertTrue(info.getSupportedMediaTypes().contains(mediaType));
        if (!info.getSupportedMediaTypes().contains(readMediaType)) {
            return;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        _store.getGraph(_VALID_GRAPH, readMediaType).write(out);

        assertGraphEquality(testFile1 + ".cxtm", getCTMGraph(out, base), base);
        
        final IGraphInfo info2 = _store.createOrReplaceGraph(_VALID_GRAPH, getInputStream(testFile2), _VALID_GRAPH, mediaType);
        assertEquals(1, graphCount());
        assertEquals(info.getURI(), info2.getURI());
        assertTrue(info2.getSupportedMediaTypes().contains(mediaType));
        out = new ByteArrayOutputStream();
        _store.getGraph(_VALID_GRAPH, readMediaType).write(out);

        assertGraphEquality(testFile2 + ".cxtm", getCTMGraph(out, base), base);
        
        _store.deleteGraph(_VALID_GRAPH);
        assertEquals(0, graphCount());
    }
}
