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
package com.semagia.cassa.common.dm.impl;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IGraphInfo;

/**
 * Default immutable implementation of {@link IGraphInfo}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class DefaultGraphInfo implements IGraphInfo {

    private final URI _uri;
    private final List<MediaType> _mediaTypes;
    private final long _lastModification;
    private final String _title;
    private final String _description;

    public DefaultGraphInfo(final URI uri, List<MediaType> mediaTypes) {
        this(uri, mediaTypes, -1);
    }

    public DefaultGraphInfo(final URI uri, final MediaType mediaType) {
        this(uri, mediaType != null ? Collections.singletonList(mediaType) : null, -1);
    }

    public DefaultGraphInfo(final URI uri, final MediaType mediaType, final long lastModification) {
        this(uri, mediaType != null ? Collections.singletonList(mediaType) : null, lastModification);
    }

    public DefaultGraphInfo(final URI uri, final List<MediaType> mediaTypes, final long lastModification) {
        this(uri, mediaTypes, lastModification, null);
    }

    public DefaultGraphInfo(final URI uri, final List<MediaType> mediaTypes, final long lastModification, final String title) {
        this(uri, mediaTypes, lastModification, title, null);
    }

    public DefaultGraphInfo(final URI uri, final List<MediaType> mediaTypes, final long lastModification, final String title, final String description) {
        if (uri == null) {
            throw new IllegalArgumentException("The URI must not be null");
        }
        _uri = uri;
        if (mediaTypes == null) {
            throw new IllegalArgumentException("The media types must not be null; at least one media type is required");
        }
        _mediaTypes = Collections.unmodifiableList(mediaTypes);
        _lastModification = lastModification;
        _title = title;
        _description = description;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.common.dm.IGraphInfo#getLastModification()
     */
    @Override
    public long getLastModification() {
        return _lastModification;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.common.dm.IGraphInfo#getSupportedMediaTypes()
     */
    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return _mediaTypes;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.common.dm.IGraphInfo#getURI()
     */
    @Override
    public URI getURI() {
        return _uri;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.common.dm.IGraphInfo#getTitle()
     */
    @Override
    public String getTitle() {
        return _title;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.common.dm.IGraphInfo#getDescription()
     */
    @Override
    public String getDescription() {
        return _description;
    }

}
