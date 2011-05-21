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

import java.util.Iterator;
import java.util.ServiceLoader;

import com.semagia.cassa.server.spi.ServerApplicationFactory;

/**
 * Provider for {@link IServerApplication} instances.
 * <p>
 * The provider uses the {@link ServerApplicationFactory} SPI. Only the first
 * found {@link ServerApplicationFactory} is used to provide
 * {@link IServerApplication} instances.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class ServerApplicationProvider {

    private final static ServerApplicationProvider _INSTANCE = new ServerApplicationProvider();
    private final ServerApplicationFactory _factory;

    private ServerApplicationProvider() {
        final Iterator<ServerApplicationFactory> iter = ServiceLoader.load(ServerApplicationFactory.class, ServerApplicationProvider.class.getClassLoader()).iterator();
        _factory = iter.hasNext() ? iter.next() : null;
    }

    /**
     * Returns a {@link IServerApplication} instance. If a singleton or a 
     * new {@link IServerApplication} is returned depends on the underlying
     * {@link ServerApplicationFactory}.
     *
     * @return A server application instance.
     * @throws IllegalStateException In case no {@link ServerApplicationFactory} was found.
     */
    public static IServerApplication getServerApplication() throws IllegalStateException {
        if (_INSTANCE._factory == null) {
            throw new IllegalStateException("No ServerApplicationFactory found");
        }
        return _INSTANCE._factory.createServerApplication();
    }
}
