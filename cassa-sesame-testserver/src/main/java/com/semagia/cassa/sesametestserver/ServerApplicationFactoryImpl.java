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
package com.semagia.cassa.sesametestserver;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import com.semagia.cassa.server.IServerApplication;
import com.semagia.cassa.server.spi.ServerApplicationFactory;
import com.semagia.cassa.server.store.IStore;
import com.semagia.cassa.server.store.sesame.SesameStore;

/**
 * ServerApplicationFactory to run tests against.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class ServerApplicationFactoryImpl implements ServerApplicationFactory {
    
    private static IServerApplication _APP = new SesameServerApplication();

    @Override
    public IServerApplication createServerApplication() {
        return _APP;
    }
    
    static class SesameServerApplication implements IServerApplication {
        
        private Repository _repository;
        private IStore _store;

        public SesameServerApplication() {
            _repository = new SailRepository(new MemoryStore());
            try {
                _repository.initialize();
            } 
            catch (RepositoryException ex) {
                throw new RuntimeException(ex);
            }
            _store = new SesameStore(_repository);
        }

        @Override
        public IStore getStore() {
            return _store;
        }
        
    }

}
