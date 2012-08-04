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
import java.util.Set;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IResource;
import com.semagia.cassa.server.store.IFragmentInfo;

/**
 * Default, immutable implementation of {@link IFragmentInfo}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class DefaultFragmentInfo extends DefaultGraphInfo implements
        IFragmentInfo {

    private final Set<IResource> _resources;

    /**
     * Creates an instance with the provided resource and media type; the last
     * modification time stamp will be {@code -1} and the title and description 
     * will be {@code null}.
     *
     * @param uri The fragment URI.
     * @param resource A resource which was modified by the fragment.
     * @param mediaType The supported media type.
     */
    public DefaultFragmentInfo(final URI uri, final IResource resource, final MediaType mediaType) {
        this(uri, resource, mediaType, -1);
    }

    /**
     * Creates an instance with the provided resource, media type and last
     * modification time stamp; the title and description will be {@code null}.
     *
     * @param uri The fragment URI.
     * @param resource A resource which was modified by the fragment.
     * @param mediaType The supported media type.
     * @param lastModification Last modification time stamp or {@code -1}.
     */
    public DefaultFragmentInfo(final URI uri, final IResource resource, final MediaType mediaType, final long lastModification) {
        this(uri, resource, mediaType, lastModification, null);
    }

    /**
     * Creates an instance with the provided resource, media type, last
     * modification time stamp and title; the description will be {@code null}.
     *
     * @param uri The fragment URI.
     * @param resource A resource which was modified by the fragment.
     * @param mediaType The supported media type.
     * @param lastModification Last modification time stamp or {@code -1}.
     * @param title The title or {@code null}.
     */
    public DefaultFragmentInfo(final URI uri, final IResource resource, final MediaType mediaType, final long lastModification, final String title) {
        this(uri, resource, mediaType, lastModification, title, null);
    }

    /**
     * Creates an instance with the provided resource, media type, last
     * modification time stamp, title and description.
     *
     * @param uri The fragment URI.
     * @param resource A resource which was modified by the fragment.
     * @param mediaType The supported media type.
     * @param lastModification Last modification time stamp or {@code -1}.
     * @param title The title or {@code null}.
     * @param description The description or {@code null}.
     */
    public DefaultFragmentInfo(final URI uri, final IResource resource, final MediaType mediaType, final long lastModification, final String title, final String description) {
        this(uri, resource, mediaType != null ? Collections.singletonList(mediaType) : (List<MediaType>) null, lastModification, title, description);
    }

    /**
     * Creates an instance with the provided resources and media type; last
     * modification time stamp will be {@code -1} and the title and description 
     * will be {@code null}.
     *
     * @param uri The fragment URI.
     * @param resources A set of resources which were modified by the fragment.
     * @param mediaType The supported media type.
     */
    public DefaultFragmentInfo(final URI uri, final Set<IResource> resources, final MediaType mediaType) {
        this(uri, resources, mediaType, -1);
    }

    /**
     * Creates an instance with the provided resources, media type and last
     * modification time stamp; the title and description will be {@code null}.
     *
     * @param uri The fragment URI.
     * @param resources A set of resources which were modified by the fragment.
     * @param mediaType The supported media type.
     * @param lastModification Last modification time stamp or {@code -1}.
     */
    public DefaultFragmentInfo(final URI uri, final Set<IResource> resources, final MediaType mediaType, final long lastModification) {
        this(uri, resources, mediaType, lastModification, null);
    }

    /**
     * Creates an instance with the provided resources, media type, last
     * modification time stamp and title; the description will be {@code null}.
     *
     * @param uri The fragment URI.
     * @param resources A set of resources which were modified by the fragment.
     * @param mediaType The supported media type.
     * @param lastModification Last modification time stamp or {@code -1}.
     * @param title The title or {@code null}.
     */
    public DefaultFragmentInfo(final URI uri, final Set<IResource> resources, final MediaType mediaType, final long lastModification, final String title) {
        this(uri, resources, mediaType, lastModification, title, null);
    }

    /**
     * Creates an instance with the provided resources, media type, last
     * modification time stamp, title and description.
     *
     * @param uri The fragment URI.
     * @param resources A set of resources which were modified by the fragment.
     * @param mediaType The supported media type.
     * @param lastModification Last modification time stamp or {@code -1}.
     * @param title The title or {@code null}.
     * @param description The description or {@code null}.
     */
    public DefaultFragmentInfo(final URI uri, final Set<IResource> resources, final MediaType mediaType, final long lastModification, final String title, final String description) {
        this(uri, resources, mediaType != null ? Collections.singletonList(mediaType) : (List<MediaType>) null, lastModification, title, description);
    }

    /**
     * Creates an instance with the provided resource and media types; last modification
     * time stamp will be set to {@code -1} and the title and description will be {@code null}.
     *
     * @param uri The fragment URI.
     * @param resource The resource which was modified by the fragment.
     * @param mediaTypes Supported media types.
     */
    public DefaultFragmentInfo(final URI uri, final IResource resource, final List<MediaType> mediaTypes) {
        this(uri, resource, mediaTypes, -1);
    }

    /**
     * Creates an instance with the provided resource, media types and last modification
     * time stamp; the title and description will be {@code null}.
     *
     * @param uri The fragment URI.
     * @param resource The resource which was modified by the fragment.
     * @param mediaTypes Supported media types.
     * @param lastModification Last modification time stamp or {@code -1}.
     */
    public DefaultFragmentInfo(final URI uri, final IResource resource, final List<MediaType> mediaTypes, final long lastModification) {
        this(uri, resource, mediaTypes, lastModification, null);
    }

    /**
     * Creates an instance with the provided resource, media types, last modification
     * time stamp and title; the description will be {@code null}.
     *
     * @param uri The fragment URI.
     * @param resource The resource which was modified by the fragment.
     * @param mediaTypes Supported media types.
     * @param lastModification Last modification time stamp or {@code -1}.
     * @param title The title or {@code null}.
     */
    public DefaultFragmentInfo(final URI uri, final IResource resource, final List<MediaType> mediaTypes, final long lastModification, final String title) {
        this(uri, resource, mediaTypes, lastModification, title, null);
    }

    /**
     * Creates an instance with the provided resource, media types, last modification
     * time stamp, title and description.
     *
     * @param uri The fragment URI.
     * @param resource The resource which was modified by the fragment.
     * @param mediaTypes Supported media types.
     * @param lastModification Last modification time stamp or {@code -1}.
     * @param title The title or {@code null}.
     * @param description The description or {@code null}.
     */
    public DefaultFragmentInfo(final URI uri, final IResource resource, final List<MediaType> mediaTypes, final long lastModification, final String title, final String description) {
        this(uri, resource != null ? Collections.singleton(resource) : null, mediaTypes, lastModification, title, description);
    }

    /**
     * Creates an instance with the provided resources, media types; the last
     * modification time stamp will be set to {@code -1} and the title and 
     * description will be {@code null}.
     *
     * @param uri The fragment URI.
     * @param resources A set of resources which were modified by the fragment.
     * @param mediaTypes Supported media types.
     */
    public DefaultFragmentInfo(final URI uri, final Set<IResource> resources, final List<MediaType> mediaTypes) {
        this(uri, resources, mediaTypes, -1);
    }

    /**
     * Creates an instance with the provided resources, media types and last modification
     * time stamp; the title and description will be {@code null}.
     *
     * @param uri The fragment URI.
     * @param resources A set of resources which were modified by the fragment.
     * @param mediaTypes Supported media types.
     * @param lastModification Last modification time stamp or {@code -1}.
     */
    public DefaultFragmentInfo(final URI uri, final Set<IResource> resources, final List<MediaType> mediaTypes, final long lastModification) {
        this(uri, resources, mediaTypes, lastModification, null);
    }

    /**
     * Creates an instance with the provided resources, media types, last modification
     * time stamp and title; the description will be {@code null}.
     *
     * @param uri The fragment URI.
     * @param resources A set of resources which were modified by the fragment.
     * @param mediaTypes Supported media types.
     * @param lastModification Last modification time stamp or {@code -1}.
     * @param title The title or {@code null}.
     */
    public DefaultFragmentInfo(final URI uri, final Set<IResource> resources, final List<MediaType> mediaTypes, final long lastModification, final String title) {
        this(uri, resources, mediaTypes, lastModification, title, null);
    }

    /**
     * Creates an instance with the provided resources, media types, last modification
     * time stamp, title and description.
     *
     * @param uri The fragment URI.
     * @param resources A set of resources which were modified by the fragment.
     * @param mediaTypes Supported media types.
     * @param lastModification Last modification time stamp or {@code -1}.
     * @param title The title or {@code null}.
     * @param description The description or {@code null}.
     */
    public DefaultFragmentInfo(final URI uri, final Set<IResource> resources, final List<MediaType> mediaTypes, final long lastModification, final String title, final String description) {
        super(uri, mediaTypes, lastModification, title, description);
        if (resources == null) {
            throw new IllegalArgumentException("Resource must not be null, at least one resource is required");
        }
        _resources = Collections.unmodifiableSet(resources);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.common.dm.IFragmentInfo#getResources()
     */
    @Override
    public Set<IResource> getResources() {
        return _resources;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) 
                && _resources.equals((((DefaultFragmentInfo) obj)._resources));
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.common.dm.impl.DefaultGraphInfo#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + _resources.hashCode();
        return result;
    }

}
