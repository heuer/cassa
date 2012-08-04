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
package com.semagia.cassa.server.sdshare.spi;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.server.sdshare.IOutputAwareFeedHandler;

/**
 * Factory for a {@link IOutputAwareFeedHandler} which supports a particular 
 * media type.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public interface IFeedHandlerFactory {

    /**
     * Creates and returns a {@link IOutputAwareFeedHandler} which supports the 
     * media type returned by {@link #getMediaType()}.
     *
     * @return A {@link IOutputAwareFeedHandler} instance.
     */
    public IOutputAwareFeedHandler createFeedHandler();

    /**
     * Returns the media type which is supported by the 
     * {@link IOutputAwareFeedHandler} created by this factory.
     *
     * @return The media type.
     */
    public MediaType getMediaType();

}
