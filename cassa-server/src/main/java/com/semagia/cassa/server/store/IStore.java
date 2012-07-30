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

import java.io.IOException;
import java.net.URI;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IGraphInfo;
import com.semagia.cassa.common.dm.IWritableRepresentation;

/**
 * Graph store.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public interface IStore {

    /**
     * Constant for the default graph.
     * 
     * This constant is meant to be compared by reference NOT by equality.
     */
    public static final URI DEFAULT_GRAPH = URI.create("");

    /**
     * Returns the last modification date.
     * 
     * In case the modification time is unknown, {@code -1} must be returned.
     *
     * @return The last modification time or -1.
     */
    public long getLastModification() throws StoreException;

    /**
     * Returns all available graphs.
     *
     * @return A (maybe empty) iterable of graphs.
     * @throws StoreException In case of an error.
     */
    public Iterable<IGraphInfo> getGraphInfos() throws StoreException;

    /**
     * Returns a serialization of the graph.
     *
     * @param graphURI The URI of the graph ({@link #DEFAULT_GRAPH} indicates the default graph)
     * @param mediaType The requested media type of the serialization. 
     * @return A {@link IWritableRepresentation} that serializes the graph into the provided media type.
     * @throws GraphNotExistsException In case the graph does not exist.
     * @throws UnsupportedMediaTypeException In case the media type is not available.
     * @throws IOException In case of an I/O error.
     * @throws StoreException In case of an error.
     */
    public IWritableRepresentation getGraph(URI graphURI, MediaType mediaType) throws GraphNotExistsException, UnsupportedMediaTypeException, IOException, StoreException;

    /**
     * Returns if the graph is part of this store.
     *
     * @param graphURI The URI of the graph ({@link #DEFAULT_GRAPH} indicates the default graph)
     * @return {@code true} if the graph exists, otherwise {@code false}.
     * @throws StoreException In case of an error.
     */
    public boolean containsGraph(URI graphURI) throws StoreException;

    /**
     * Returns the metadata about the graph.
     *
     * @param graphURI The URI of the graph ({@link #DEFAULT_GRAPH} indicates the default graph)
     * @return The metadata.
     * @throws GraphNotExistsException In case the graph does not exist.
     * @throws StoreException In case of an error.
     */
    public IGraphInfo getGraphInfo(URI graphURI) throws GraphNotExistsException, StoreException;

}
