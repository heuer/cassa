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
package com.semagia.cassa.server.store;

import java.net.URI;


/**
 * SDShare store.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public interface ISDShareStore extends IStore {
    
    /**
     * Returns the last modification time of the fragments of the provided graphURI.
     * 
     * In case the modification time is unknown, {@code -1} must be returned.
     * 
     * @param graphURI The URI of the graph
     * @return The last modification time or -1.
     * @throws GraphNotExistsException In case the graph does not exist.
     * @throws StoreException In case of an error.
     */
    public long getLastModificationFragments(URI graphURI) throws GraphNotExistsException, StoreException;

    /**
     * Returns the last modification time of the snapshots of the provided graphURI.
     * 
     * In case the modification time is unknown, {@code -1} must be returned.
     * 
     * @param graphURI The URI of the graph
     * @return The last modification time or -1.
     * @throws GraphNotExistsException In case the graph does not exist.
     * @throws StoreException In case of an error.
     */
    public long getLastModificationSnapshots(URI graphURI) throws GraphNotExistsException, StoreException;

    /**
     * Returns the fragments associated with the provided graph URI.
     * 
     * @param graphURI The URI of the graph
     * @return A (maybe empty) iterable of fragments related to the provided graph URI.
     * @throws GraphNotExistsException In case the graph does not exist.
     * @throws StoreException In case of an error.
     */
    public Iterable<IFragmentInfo> getFragments(URI graphURI) throws GraphNotExistsException, StoreException;

    /**
     * Returns the fragments associated with the provided graph URI which have 
     * been updated after timestamp {@code since}.
     * 
     * @param graphURI The URI of the graph
     * @param since A timestamp.
     * @return A (maybe empty) iterable of fragments related to the provided graph URI.
     * @throws GraphNotExistsException In case the graph does not exist.
     * @throws StoreException In case of an error.
     */
    public Iterable<IFragmentInfo> getFragments(URI graphURI, long since) throws GraphNotExistsException, StoreException;

    /**
     * Returns the snapshots associated with the provided graph URI.
     * 
     * @param graphURI The URI of the graph.
     * @return A (maybe empty) iterable of snapshots related to the provided graph URI.
     * @throws GraphNotExistsException In case the graph does not exist.
     * @throws StoreException In case of an error.
     */
    public Iterable<IGraphInfo> getSnapshots(URI graphURI) throws GraphNotExistsException, StoreException;

}
