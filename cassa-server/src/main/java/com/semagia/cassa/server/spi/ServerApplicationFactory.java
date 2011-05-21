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
package com.semagia.cassa.server.spi;

import com.semagia.cassa.server.IServerApplication;

/**
 * Factory which creates {@link IServerApplication} instance.
 * <p>
 * Implementations of this class may return either a singleton instance of
 * {@link IServerApplication} or a new instance of {@link IServerApplication}
 * for each invocation of {@link #createServerApplication()}.
 * </p>
 * <p>
 * Implementations are meant to be thread-safe.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public interface ServerApplicationFactory {

    /**
     * Returns a {@link IServerApplication} instance.
     *
     * @return A {@link IServerApplication} instance, never <tt>null</tt>.
     */
    public IServerApplication createServerApplication();

}
