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
package com.semagia.cassa.server.store.tmapi;

import java.net.URI;

import org.tmapi.core.TopicMapSystemFactory;

import com.semagia.cassa.server.store.AbstractStoreTest;

/**
 * Tests against the {@link TMAPIStore}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestTMAPIStore extends AbstractStoreTest<TMAPIStore> {

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.AbstractStoreTest#createStore()
     */
    @Override
    protected TMAPIStore createStore() throws Exception {
        return new TMAPIStore(TopicMapSystemFactory.newInstance().newTopicMapSystem());
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.AbstractStoreTest#createGraph(com.semagia.cassa.server.store.IStore, java.net.URI)
     */
    @Override
    protected void createGraph(TMAPIStore store, URI graphURI)
            throws Exception {
        _store._sys.createTopicMap(graphURI.toString());
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.AbstractStoreTest#isRDFStore()
     */
    @Override
    protected boolean isRDFStore() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.AbstractStoreTest#isTMStore()
     */
    @Override
    protected boolean isTMStore() {
        return true;
    }

}
