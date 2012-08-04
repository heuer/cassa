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
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.semagia.cassa.server.store.GraphNotExistsException;
import com.semagia.cassa.server.store.IFragmentInfo;
import com.semagia.cassa.server.store.ISDShareStore;
import com.semagia.cassa.server.store.StoreException;

/**
 * Abstract {@link ISDShareStore} which implements some common methods.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public abstract class AbstractSDShareStore extends AbstractReadOnlyStore
        implements ISDShareStore {

    /**
     * Calls {@link #getFragments(URI)} and filters the returned fragments.
     * 
     * Derived classes may want to override this method with a store-specific,
     * native filter.
     * 
     * @see com.semagia.cassa.server.store.ISDShareStore#getFragments(java.net.URI, long)
     */
    @Override
    public Iterable<IFragmentInfo> getFragments(final URI graphURI, final long since)
            throws GraphNotExistsException, StoreException {
        Iterable<IFragmentInfo> fragments = getFragments(graphURI);
        return new SinceFilter(fragments, since);
    }


    /**
     * Iterable/iterator which returns only those {@link IFragmentInfo} instances
     * which match the "since" criterion.
     */
    private static class SinceFilter implements Iterable<IFragmentInfo>, Iterator<IFragmentInfo> {

        private final Iterator<IFragmentInfo> _fragmentsIterator;
        private final long _since;
        private IFragmentInfo _next;
        private boolean _advanced;

        private SinceFilter(final Iterable<IFragmentInfo> fragments, final long since) {
            _fragmentsIterator = fragments.iterator();
            _since = since;
        }

        /* (non-Javadoc)
         * @see java.lang.Iterable#iterator()
         */
        @Override
        public Iterator<IFragmentInfo> iterator() {
            return this;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            if (_advanced) {
                return true;
            }
            return _advance();
        }

        /**
         * Moves the cursor forward and returns if there is a remaining element.
         * 
         * @return {@code true} if the iterable has an element, otherwise {@code false}.
         */
        private boolean _advance() {
            while (_fragmentsIterator.hasNext()) {
                final IFragmentInfo next = _fragmentsIterator.next();
                if (next.getLastModification() >= _since) {
                    _next = next;
                    _advanced = true;
                    return true;
                }
            }
            return false;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        @Override
        public IFragmentInfo next() {
            if (!_advanced) {
                if (!_advance()) {
                    throw new NoSuchElementException();
                }
            }
            _advanced = false;
            return _next;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
