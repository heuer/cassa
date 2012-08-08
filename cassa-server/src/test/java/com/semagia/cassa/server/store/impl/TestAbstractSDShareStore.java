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

import com.semagia.cassa.server.store.IFragmentInfo;
import com.semagia.cassa.server.store.ISDShareStore;

import junit.framework.TestCase;

/**
 * Tests against {@link AbstractSDShareStore}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestAbstractSDShareStore extends TestCase {

    private ISDShareStore _sdstore;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _sdstore = new DummyReadOnlySDShareStore();
    }

    public void testLastModificationFragments() throws Exception {
        assertEquals(DummyReadOnlySDShareStore.FRAGMENT_INFO_2_UPDATED, _sdstore.getLastModificationFragments(DummyReadOnlySDShareStore.FRAGMENT_GRAPH));
    }

    public void testGetFragmentInfos() throws Exception {
        final List<IFragmentInfo> fragments = new ArrayList<IFragmentInfo>();
        for (IFragmentInfo info: _sdstore.getFragments(DummyReadOnlySDShareStore.FRAGMENT_GRAPH)) {
            fragments.add(info);
        }
        assertEquals(2, fragments.size());
        assertTrue(fragments.contains(DummyReadOnlySDShareStore.FRAGMENT_INFO_1));
        assertTrue(fragments.contains(DummyReadOnlySDShareStore.FRAGMENT_INFO_2));
    }

    public void testGetFragmentInfosSince() throws Exception {
        final long since = DummyReadOnlySDShareStore.FRAGMENT_INFO_2.getLastModification()+100;
        final List<IFragmentInfo> fragments = new ArrayList<IFragmentInfo>();
        for (IFragmentInfo info: _sdstore.getFragments(DummyReadOnlySDShareStore.FRAGMENT_GRAPH, since)) {
            fragments.add(info);
        }
        assertEquals(0, fragments.size());
    }

    public void testGetFragmentInfosSince2() throws Exception {
        final long since = DummyReadOnlySDShareStore.FRAGMENT_INFO_1.getLastModification()+1;
        final List<IFragmentInfo> fragments = new ArrayList<IFragmentInfo>();
        for (IFragmentInfo info: _sdstore.getFragments(DummyReadOnlySDShareStore.FRAGMENT_GRAPH, since)) {
            fragments.add(info);
        }
        assertEquals(1, fragments.size());
        assertTrue(fragments.contains(DummyReadOnlySDShareStore.FRAGMENT_INFO_2));
    }

    public void testGetFragmentInfosSince3() throws Exception {
        final long since = DummyReadOnlySDShareStore.FRAGMENT_INFO_2.getLastModification();
        final List<IFragmentInfo> fragments = new ArrayList<IFragmentInfo>();
        for (IFragmentInfo info: _sdstore.getFragments(DummyReadOnlySDShareStore.FRAGMENT_GRAPH, since)) {
            fragments.add(info);
        }
        assertEquals(1, fragments.size());
        assertTrue(fragments.contains(DummyReadOnlySDShareStore.FRAGMENT_INFO_2));
    }

    public void testGetFragmentInfosSince4() throws Exception {
        final long since = DummyReadOnlySDShareStore.FRAGMENT_INFO_1.getLastModification();
        final List<IFragmentInfo> fragments = new ArrayList<IFragmentInfo>();
        for (IFragmentInfo info: _sdstore.getFragments(DummyReadOnlySDShareStore.FRAGMENT_GRAPH, since)) {
            fragments.add(info);
        }
        assertEquals(2, fragments.size());
        assertTrue(fragments.contains(DummyReadOnlySDShareStore.FRAGMENT_INFO_1));
        assertTrue(fragments.contains(DummyReadOnlySDShareStore.FRAGMENT_INFO_2));
    }
}
