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
package com.semagia.cassa.server.store;

import java.util.Collections;
import java.util.List;

import com.semagia.cassa.common.MediaType;

/**
 * Exception thrown if a {@link IStore} cannot accept (reading) or support (writing)
 * the provided media type. 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
@SuppressWarnings("serial")
public class UnsupportedMediaTypeException extends StoreException {

    /**
     * List of supported media types, never null.
     */
    private final List<MediaType> _mediaTypes;

    /**
     * Creates an instance where the {@link #getMediaTypes()} list will be empty.
     *
     * @param msg The message.
     */
    public UnsupportedMediaTypeException(String msg) {
        this(msg, (MediaType) null);
    }

    /**
     * Creates an instance with an optional, supported media type.
     *
     * @param msg The message.
     * @param acceptedMediaType A supported media type or {@code null}.
     */
    public UnsupportedMediaTypeException(String msg, MediaType acceptedMediaType) {
        this(msg, acceptedMediaType != null ? Collections.singletonList(acceptedMediaType) : (List<MediaType>)null);
    }

    /**
     * Creates an instance with an optional list of supported media types.
     *
     * @param msg The message.
     * @param acceptedMediaTypes A list of supported media types or {@code null}.
     */
    public UnsupportedMediaTypeException(String msg, List<MediaType> acceptedMediaTypes) {
        super(msg);
        _mediaTypes = acceptedMediaTypes == null ? Collections.<MediaType>emptyList() 
                                                 : Collections.unmodifiableList(acceptedMediaTypes);
    }

    /**
     * Returns an immutable list of acceptable media types.
     *
     * @return A (maybe empty) list of supported media types, never {@code null}.
     */
    public List<MediaType> getMediaTypes() {
        return _mediaTypes;
    }

}
