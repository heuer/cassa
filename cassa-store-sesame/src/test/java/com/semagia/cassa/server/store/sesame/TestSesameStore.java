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
package com.semagia.cassa.server.store.sesame;

import java.net.URI;

import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import com.semagia.cassa.server.store.AbstractStoreTest;

/**
 * Tests against the {@link SesameStore}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestSesameStore extends AbstractStoreTest<SesameStore> {

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.AbstractStoreTest#isRDFStore()
     */
    @Override
    protected boolean isRDFStore() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.AbstractStoreTest#isTMStore()
     */
    @Override
    protected boolean isTMStore() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.AbstractStoreTest#createStore()
     */
    @Override
    protected SesameStore createStore() throws Exception {
        Repository myRepository = new SailRepository(new MemoryStore());
        myRepository.initialize();
        return new SesameStore(myRepository.getConnection());
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.store.AbstractStoreTest#createGraph(com.semagia.cassa.server.store.IStore, java.net.URI)
     */
    @Override
    protected void createGraph(SesameStore store, URI graphURI)
            throws Exception {
        final ValueFactory factory = store._conn.getValueFactory();
        store._conn.add(factory.createBNode(), 
                    factory.createURI("http://psi.example.org/foo"), 
                    factory.createBNode(), 
                    factory.createURI(graphURI.toString()));
    }

}
