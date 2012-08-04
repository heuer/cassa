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
package com.semagia.cassa.server.sdshare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.server.sdshare.spi.IFeedHandlerFactory;

/**
 * Registry to create {@link IOutputAwareFeedHandler}s and to look up available 
 * media types.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class FeedHandlerRegistry {

    private final static FeedHandlerRegistry _INSTANCE = new FeedHandlerRegistry();

    private final Map<MediaType, IFeedHandlerFactory> _mediaType2Factory;

    private final List<MediaType> _mediaTypes;

    private FeedHandlerRegistry() {
        _mediaType2Factory = new HashMap<MediaType, IFeedHandlerFactory>();
        _mediaTypes = new ArrayList<MediaType>();
        for (IFeedHandlerFactory factory: ServiceLoader.load(IFeedHandlerFactory.class, FeedHandlerRegistry.class.getClassLoader())) {
            if (_mediaType2Factory.put(factory.getMediaType(), factory) == null) {
                // Atom should be the first (preferred) media type
                if (MediaType.ATOM_XML.equals(factory.getMediaType())) {
                    _mediaTypes.add(0, factory.getMediaType());
                }
                else {
                    _mediaTypes.add(factory.getMediaType());
                }
            }
        }
    }

    /**
     * Returns available media types.
     * <p>
     * The available media types depend on the available 
     * {@link IOutputAwareFeedHandler}s.
     * </p>
     *
     * @return An immutable list of available media types.
     */
    public static List<MediaType> getMediaTypes() {
        return Collections.unmodifiableList(_INSTANCE._mediaTypes);
    }

    /**
     * Returns a {@link IOutputAwareFeedHandler} instance for the specified
     * media type.
     * 
     * @param mediaType
     *            The media type.
     * @return A {@link IOutputAwareFeedHandler} instance or <tt>null</tt> if no
     *         handler is available for the provided media type.
     */
    public static IOutputAwareFeedHandler createFeedHandler(MediaType mediaType) {
        IFeedHandlerFactory factory = _INSTANCE._mediaType2Factory.get(mediaType);
        return factory == null ? null : factory.createFeedHandler();
    }

}
