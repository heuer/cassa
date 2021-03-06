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
package com.semagia.cassa.server.store.impl;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.server.store.IGraphInfo;

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

    /**
     * Creates an instance with a URI and a list of media types; last modification
     * will be set to {@code -1} and the title and description will be {@code null}.
     * 
     * @param uri The graph URI.
     * @param mediaTypes The media types.
     */
    public DefaultGraphInfo(final URI uri, final List<MediaType> mediaTypes) {
        this(uri, mediaTypes, -1);
    }

    /**
     * Creates an instance with a URI and one media type; last modification
     * will be set to {@code -1} and the title and description will be {@code null}.
     * 
     * @param uri The graph URI.
     * @param mediaType The media type.
     */
    public DefaultGraphInfo(final URI uri, final MediaType mediaType) {
        this(uri, mediaType, -1);
    }

    /**
     * Creates an instance with a URI and one media type and the provided
     * last modification time stamp; the title and description will be {@code null}.
     * 
     * @param uri The graph URI.
     * @param mediaType The media type.
     * @param lastModification The time stamp of the last modification or {@code -1}.
     */
    public DefaultGraphInfo(final URI uri, final MediaType mediaType, final long lastModification) {
        this(uri, mediaType, lastModification, null);
    }

    /**
     * Creates an instance with a URI and one media type and the provided
     * last modification time stamp and title; the description will be {@code null}.
     *
     * @param uri The graph URI.
     * @param mediaType The media type.
     * @param lastModification The time stamp of the last modification or {@code -1}.
     * @param title The graph title or {@code null}.
     */
    public DefaultGraphInfo(final URI uri, final MediaType mediaType, final long lastModification, final String title) {
        this(uri, mediaType, lastModification, title, null);
    }

    /**
     * Creates an instance with a URI and one media type and the provided
     * last modification time stamp, title and description.
     * 
     * @param uri The graph URI.
     * @param mediaType The media type.
     * @param lastModification The time stamp of the last modification or {@code -1}.
     * @param title The graph title or {@code null}.
     * @param description A description of the graph or {@code null}.
     */
    public DefaultGraphInfo(final URI uri, final MediaType mediaType, final long lastModification, final String title, final String description) {
        this(uri, mediaType != null ? Collections.singletonList(mediaType) : (List<MediaType>)null, lastModification, title, description);
    }

    /**
     * Creates an instance with a URI and supported media types and the provided
     * last modification time stamp; the title and description will be {@code null}.
     * 
     * @param uri The graph URI.
     * @param mediaTypes The media types.
     * @param lastModification The time stamp of the last modification or {@code -1}.
     */
    public DefaultGraphInfo(final URI uri, final List<MediaType> mediaTypes, final long lastModification) {
        this(uri, mediaTypes, lastModification, null);
    }

    /**
     * Creates an instance with a URI and supported media types and the provided
     * last modification time stamp and title; the description will be {@code null}.
     * 
     * @param uri The graph URI.
     * @param mediaTypes The media types.
     * @param lastModification The time stamp of the last modification or {@code -1}.
     * @param title The graph title or {@code null}.
     */
    public DefaultGraphInfo(final URI uri, final List<MediaType> mediaTypes, final long lastModification, final String title) {
        this(uri, mediaTypes, lastModification, title, null);
    }

    /**
     * Creates an instance with a URI and supported media types and the provided
     * last modification time stamp, title and description.
     * 
     * @param uri The graph URI.
     * @param mediaTypes The media types.
     * @param lastModification The time stamp of the last modification or {@code -1}.
     * @param title The graph title or {@code null}.
     * @param description A description of the graph or {@code null}.
     */
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

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final DefaultGraphInfo other = (DefaultGraphInfo) obj;
        return _lastModification == other._lastModification
                && _uri.equals(other._uri)
                && _mediaTypes.equals(other._mediaTypes)
                && _title == null ? other._title == null 
                                  : other._title != null && _title.equals(other._title)
                && _description == null ? other._description == null 
                                        : other._description != null && _description.equals(other._description);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((_description == null) ? 0 : _description.hashCode());
        result = prime * result
                + (int) (_lastModification ^ (_lastModification >>> 32));
        result = prime * result
                + _mediaTypes.hashCode();
        result = prime * result + ((_title == null) ? 0 : _title.hashCode());
        result = prime * result + _uri.hashCode();
        return result;
    }

}
