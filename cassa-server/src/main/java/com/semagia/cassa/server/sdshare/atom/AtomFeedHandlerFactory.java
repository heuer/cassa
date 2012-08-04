/*
 * Copyright 2008 - 2009 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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
package com.semagia.cassa.server.sdshare.atom;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.server.sdshare.IOutputAwareFeedHandler;
import com.semagia.cassa.server.sdshare.spi.IFeedHandlerFactory;

/**
 * Factory for a handler which writes Atom 1.0 syntax.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class AtomFeedHandlerFactory implements IFeedHandlerFactory {

    /* (non-Javadoc)
     * @see com.semagia.atomico.server.feed.IFeedHandlerFactory#createFeedHandler()
     */
    @Override
    public IOutputAwareFeedHandler createFeedHandler() {
        return new AtomFeedHandler();
    }

    /* (non-Javadoc)
     * @see com.semagia.atomico.server.feed.IFeedHandlerFactory#getMediaType()
     */
    @Override
    public MediaType getMediaType() {
        return MediaType.ATOM_XML;
    }

}
