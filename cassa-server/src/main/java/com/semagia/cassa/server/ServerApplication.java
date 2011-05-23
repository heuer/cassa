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
package com.semagia.cassa.server;

import com.semagia.cassa.server.store.IStore;

/**
 * Thread-safe, immutable {@link IServerApplication} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class ServerApplication implements IServerApplication {

    private final IStore _store;

    /**
     * Creates a new instance with the provided storage and configuration.
     *
     * @param store The store.
     */
    public ServerApplication(final IStore store) {
        if (store == null) {
            throw new IllegalArgumentException("The store must not be null");
        }
        _store = store;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.IServerApplication#getStore()
     */
    @Override
    public IStore getStore() {
        return _store;
    }

}
