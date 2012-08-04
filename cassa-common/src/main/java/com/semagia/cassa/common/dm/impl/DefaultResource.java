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

import com.semagia.cassa.common.dm.IResource;

/**
 * Immutable {@link IResource} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class DefaultResource implements IResource {

    private final URI _uri;
    private final Role _kind;

    public DefaultResource(final URI uri) {
        this(uri, Role.NONE);
    }

    public DefaultResource(final URI uri, final Role role) {
        if (uri == null) {
            throw new IllegalArgumentException("The URI must not be null");
        }
        _uri = uri;
        _kind = role;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.common.dm.IResource#getURI()
     */
    @Override
    public URI getURI() {
        return _uri;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.common.dm.IResource#getKind()
     */
    @Override
    public Role getRole() {
        return _kind;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + _kind.hashCode();
        result = prime * result + _uri.hashCode();
        return result;
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
        final DefaultResource other = (DefaultResource) obj;
        return _kind == other._kind
                && _uri.equals(other._uri);
    }



}
