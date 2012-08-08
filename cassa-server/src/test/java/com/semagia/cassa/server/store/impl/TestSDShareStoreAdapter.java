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

import java.util.ArrayList;
import java.util.List;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.server.store.IFragmentInfo;
import com.semagia.cassa.server.store.IGraphInfo;
import com.semagia.cassa.server.store.ISDShareStore;
import com.semagia.cassa.server.store.UnsupportedMediaTypeException;

import junit.framework.TestCase;

/**
 * Tests against {@link SDShareStoreAdapter}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestSDShareStoreAdapter extends TestCase {

    private DummyReadOnlyStore _store;
    private ISDShareStore _sdstore;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _store = new DummyReadOnlyStore();
        _sdstore = new SDShareStoreAdapter(_store);
    }

    public void testSnapshots() throws Exception {
        assertNotNull(_sdstore.getGraph(DummyReadOnlyStore.GRAPH_INFO_1_URI, DummyReadOnlyStore.GRAPH_INFO_1_MEDIATYPE));
        assertNotNull(_sdstore.getGraph(DummyReadOnlyStore.GRAPH_INFO_2_URI, DummyReadOnlyStore.GRAPH_INFO_2_MEDIATYPES.get(0)));
    }

    public void testSnapshotIllegalMediaType() throws Exception {
        final MediaType mediaType = MediaType.CTM;
        assertFalse(DummyReadOnlyStore.GRAPH_INFO_1_MEDIATYPE.equals(mediaType));
        try {
            _sdstore.getGraph(DummyReadOnlyStore.GRAPH_INFO_1_URI, mediaType);
            fail("Expected an unsupported media type exception");
        }
        catch (UnsupportedMediaTypeException ex) {
            // noop.
        }
    }

    public void testLastModificationFragments() throws Exception {
        assertEquals(_sdstore.getLastModification(), _sdstore.getLastModificationFragments(DummyReadOnlyStore.GRAPH_INFO_1_URI));
        assertEquals(_sdstore.getLastModification(), _sdstore.getLastModificationFragments(DummyReadOnlyStore.GRAPH_INFO_2_URI));
    }

    public void testLastModificationSnapshots() throws Exception {
        assertEquals(_sdstore.getLastModification(), _sdstore.getLastModificationSnapshots(DummyReadOnlyStore.GRAPH_INFO_1_URI));
        assertEquals(_sdstore.getLastModification(), _sdstore.getLastModificationSnapshots(DummyReadOnlyStore.GRAPH_INFO_2_URI));
    }

    public void testContains() throws Exception {
        assertTrue(_sdstore.containsGraph(DummyReadOnlyStore.GRAPH_INFO_1_URI));
        assertTrue(_sdstore.containsGraph(DummyReadOnlyStore.GRAPH_INFO_2_URI));
        assertFalse(_sdstore.containsGraph(DummyReadOnlyStore.GRAPH_INFO_1_URI.resolve("?foo=bar")));
    }

    public void testGetFragmentInfos() throws Exception {
        final List<IFragmentInfo> fragments = new ArrayList<IFragmentInfo>();
        for (IFragmentInfo info: _sdstore.getFragments(DummyReadOnlyStore.GRAPH_INFO_1_URI)) {
            fragments.add(info);
        }
        assertEquals(0, fragments.size());

        for (IFragmentInfo info: _sdstore.getFragments(DummyReadOnlyStore.GRAPH_INFO_2_URI)) {
            fragments.add(info);
        }
        assertEquals(0, fragments.size());
    }

    public void testGetFragmentInfosSince() throws Exception {
        final long since = DummyReadOnlyStore.GRAPH_INFO_1.getLastModification()-100;
        final List<IFragmentInfo> fragments = new ArrayList<IFragmentInfo>();
        for (IFragmentInfo info: _sdstore.getFragments(DummyReadOnlyStore.GRAPH_INFO_1_URI, since)) {
            fragments.add(info);
        }
        assertEquals(0, fragments.size());

        for (IFragmentInfo info: _sdstore.getFragments(DummyReadOnlyStore.GRAPH_INFO_2_URI, since)) {
            fragments.add(info);
        }
        assertEquals(0, fragments.size());
    }

    public void testGetSnapshotInfos() throws Exception {
        final List<IGraphInfo> snapshots = new ArrayList<IGraphInfo>();
        for (IGraphInfo info: _sdstore.getSnapshots(DummyReadOnlyStore.GRAPH_INFO_1_URI)) {
            snapshots.add(info);
        }
        assertEquals(1, snapshots.size());
        assertTrue(snapshots.contains(DummyReadOnlyStore.GRAPH_INFO_1));
        
        snapshots.clear();

        for (IGraphInfo info: _sdstore.getSnapshots(DummyReadOnlyStore.GRAPH_INFO_2_URI)) {
            snapshots.add(info);
        }
        assertEquals(1, snapshots.size());
        assertTrue(snapshots.contains(DummyReadOnlyStore.GRAPH_INFO_2));
    }

    public void testGetGraphInfo() throws Exception {
        IGraphInfo info = _sdstore.getGraphInfo(DummyReadOnlyStore.GRAPH_INFO_1_URI);
        assertEquals(DummyReadOnlyStore.GRAPH_INFO_1, info);
        info = _sdstore.getGraphInfo(DummyReadOnlyStore.GRAPH_INFO_2_URI);
        assertEquals(DummyReadOnlyStore.GRAPH_INFO_2, info);
    }

    public void testGetGraphInfos() throws Exception {
        final List<IGraphInfo> infos = new ArrayList<IGraphInfo>();
        for (IGraphInfo info: _sdstore.getGraphInfos()) {
            infos.add(info);
        }
        assertEquals(2, infos.size());
        assertTrue(infos.contains(DummyReadOnlyStore.GRAPH_INFO_1));
        assertTrue(infos.contains(DummyReadOnlyStore.GRAPH_INFO_2));
    }

}
