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
package com.semagia.cassa.server.store.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.server.store.IGraphInfo;
import com.semagia.cassa.server.store.IStore;
import com.semagia.cassa.server.store.UnsupportedMediaTypeException;

import junit.framework.TestCase;

/**
 * Tests against {@link AbstractReadOnlyStore}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestAbstractReadOnlyStore extends TestCase {

    private AbstractReadOnlyStore _store;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _store = new DummyReadOnlyStore();
    }

    public void testGetGraph() throws Exception {
        assertNotNull(_store.getGraph(DummyReadOnlyStore.GRAPH_INFO_1_URI, DummyReadOnlyStore.GRAPH_INFO_1_MEDIATYPE));
        assertNotNull(_store.getGraph(DummyReadOnlyStore.GRAPH_INFO_2_URI, DummyReadOnlyStore.GRAPH_INFO_2_MEDIATYPES.get(0)));
    }

    public void testSnapshotIllegalMediaType() throws Exception {
        final MediaType mediaType = MediaType.CTM;
        assertFalse(DummyReadOnlyStore.GRAPH_INFO_1_MEDIATYPE.equals(mediaType));
        try {
            _store.getGraph(DummyReadOnlyStore.GRAPH_INFO_1_URI, mediaType);
            fail("Expected an unsupported media type exception");
        }
        catch (UnsupportedMediaTypeException ex) {
            // noop.
        }
    }

    public void testContains() throws Exception {
        assertTrue(_store.containsGraph(IStore.DEFAULT_GRAPH));
        assertTrue(_store.containsGraph(DummyReadOnlyStore.GRAPH_INFO_1_URI));
        assertTrue(_store.containsGraph(DummyReadOnlyStore.GRAPH_INFO_2_URI));
        assertFalse(_store.containsGraph(DummyReadOnlyStore.GRAPH_INFO_1_URI.resolve("?foo=bar")));
    }

    public void testGetGraphInfos() throws Exception {
        final List<IGraphInfo> infos = new ArrayList<IGraphInfo>();
        for (IGraphInfo info: _store.getGraphInfos()) {
            infos.add(info);
        }
        assertEquals(2, infos.size());
        assertTrue(infos.contains(DummyReadOnlyStore.GRAPH_INFO_1));
        assertTrue(infos.contains(DummyReadOnlyStore.GRAPH_INFO_2));
    }

    public void testDeleteGraph() throws Exception {
        try {
            _store.deleteGraph(DummyReadOnlyStore.GRAPH_INFO_1_URI);
            fail("Expected an error for deletion");
        }
        catch (UnsupportedOperationException ex) {
            // noop.
        }
    }

    public void testDeleteSubject() throws Exception {
        try {
            _store.deleteSubject(DummyReadOnlyStore.GRAPH_INFO_1_URI, URI.create("http://www.example.org/a-subject"));
            fail("Expected an error for deletion");
        }
        catch (UnsupportedOperationException ex) {
            // noop.
        }
    }

    public void testCreateGraph() throws Exception {
        try {
            _store.createGraph(TestAbstractReadOnlyStore.class.getResourceAsStream("/test.rdf"), URI.create("http://www.example.org/a-graph"), MediaType.RDF_XML);
            fail("Expected an error for create");
        }
        catch (UnsupportedOperationException ex) {
            // noop.
        }
    }

    public void testCreateOrReplaceGraph() throws Exception {
        try {
            _store.createOrReplaceGraph(DummyReadOnlyStore.GRAPH_INFO_1_URI, TestAbstractReadOnlyStore.class.getResourceAsStream("/test.rdf"), URI.create("http://www.example.org/a-graph"), MediaType.RDF_XML);
            fail("Expected an error for create or replace");
        }
        catch (UnsupportedOperationException ex) {
            // noop.
        }
    }

}
