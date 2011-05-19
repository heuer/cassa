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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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

    protected T _store;

    /**
     * Creates an empty store.
     *
     * @return The empty store.
     */
    protected abstract T createStore() throws Exception;

    protected abstract void createGraph(T store, URI graphURI) throws StoreException;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _store = createStore();
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
        createDefaultGraph();
        _store.deleteGraph(_VALID_GRAPH);
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

    public void testGetGraphInfo() throws StoreException {
        createDefaultGraph();
        assertEquals(_VALID_GRAPH, _store.getGraphInfo(_VALID_GRAPH).getURI());
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

    public void testGet() throws StoreException {
        createDefaultGraph();
        IWritableRepresentation writable = null;
        try {
            writable = _store.getGraph(_VALID_GRAPH, MediaType.RDF_XML);
        }
        catch (UnsupportedMediaTypeException ex) {
            writable = _store.getGraph(_VALID_GRAPH, MediaType.XTM);
        }
        if (writable == null) {
            fail("The store should support RDF/XML or XTM");
        }
    }

    public void testGetInvalidMediaType() throws StoreException {
        createDefaultGraph();
        try {
            _store.getGraph(_VALID_GRAPH, MediaType.valueOf("foo/bar"));
            fail("Expected an exception for an unknown media type");
        }
        catch (UnsupportedMediaTypeException ex) {
            // noop.
        }
    }

}
