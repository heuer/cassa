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
import java.io.InputStream;
import java.net.URI;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IGraphInfo;
import com.semagia.cassa.common.dm.IWritableRepresentation;
import com.semagia.cassa.common.dm.RemovalStatus;

/**
 * Graph store.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public interface IStore {

    /**
     * Constant for the default graph.
     */
    public static final URI DEFAULT_GRAPH = URI.create("");

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
     * @param graphURI The URI of the graph.
     * @param mediaType The requested media type of the serialization. 
     * @return A {@link IWritableRepresentation} that serializes the graph into the provided media type.
     * @throws GraphNotExistsException In case the graph does not exist.
     * @throws UnsupportedMediaTypeException In case the media type is not available.
     * @throws StoreException In case of an error.
     */
    public IWritableRepresentation getGraph(URI graphURI, MediaType mediaType) throws GraphNotExistsException, UnsupportedMediaTypeException, StoreException;

    /**
     * Returns if the graph is part of this store.
     *
     * @param graphURI The graph URI.
     * @return {@code true} if the graph exists, otherwise {@code false}.
     * @throws StoreException In case of an error.
     */
    public boolean containsGraph(URI graphURI) throws StoreException;

    /**
     * Returns the metadata about the graph.
     *
     * @param graphURI The graph URI.
     * @return The metadata.
     * @throws GraphNotExistsException In case the graph does not exist.
     * @throws StoreException In case of an error.
     */
    public IGraphInfo getGraphInfo(URI graphURI) throws GraphNotExistsException, StoreException;

    /**
     * Deletes a graph.
     *
     * @param graphURI The graph URI.
     * @return {@link RemovalStatus#IMMEDIATELY} if the graph was deleted, {@link RemovalStatus#DELAYED}
     *          if graph removal is sheduled.
     * @throws GraphNotExistsException In case the graph does not exist.
     * @throws StoreException In case of an error.
     */
    public RemovalStatus deleteGraph(URI graphURI) throws GraphNotExistsException, StoreException;

    /**
     * Updates a graph.
     * 
     * @param graphURI The graph URI.
     * @param in The input stream to read the graph from.
     * @param baseURI The base URI to resolve relative URIs against (in most cases identical to the graph IRI).
     * @param mediaType The media type of the input stream.
     * @return Metadata of the created/updated graph.
     * @throws UnsupportedMediaTypeException In case the media type isn't supported.
     * @throws IOException In case of an I/O error.
     * @throws StoreException In case of an error.
     */
    public IGraphInfo updateGraph(URI graphURI, InputStream in, URI baseURI, MediaType mediaType) throws UnsupportedMediaTypeException, IOException, StoreException;

    /**
     * Modifies a graph with a query.
     *
     * @param graphURI The graph URI.
     * @param in The input stream to read the query from.
     * @param baseURI The base URI to resolve relative URIs against (in most cases identical to the graph IRI).
     * @param mediaType The media type of the input stream.
     * @return Metadata of the modified graph.
     * @throws UnsupportedMediaTypeException In case the media type isn't supported.
     * @throws IOException In case of an I/O error.
     * @throws QueryException In case of a syntax error or any other query-related error.
     * @throws GraphMismatchException If the {@code graphURI} is not identitical with graph which is referenced by the query.
     * @throws StoreException In case of an error.
     */
    public IGraphInfo modifyGraph(URI graphURI, InputStream in, URI baseURI, MediaType mediaType) throws UnsupportedMediaTypeException, IOException, QueryException, GraphMismatchException, StoreException;

    /**
     * Creates a graph.
     * 
     * @param in The input stream to read the graph from.
     * @param baseURI The base URI to resolve relative URIs against.
     * @param mediaType The media type of the input stream.
     * @return Metadata of the created/updated graph.
     * @throws UnsupportedMediaTypeException
     * @throws IOException In case of an I/O error.
     * @throws StoreException In case of an error.
     */
    public IGraphInfo createGraph(InputStream in, URI baseURI, MediaType mediaType) throws UnsupportedMediaTypeException, IOException, StoreException;

    /**
     * Creates or updates the graph with the specified URI.
     *
     * @param graphURI The graph URI.
     * @param in The input stream to read the graph from.
     * @param baseURI The base URI to resolve relative URIs against (in most cases identical to the graph IRI).
     * @param mediaType The media type of the input stream.
     * @return Metadata of the created/replaced graph.
     * @throws UnsupportedMediaTypeException
     * @throws IOException In case of an I/O error.
     * @throws StoreException In case of an error.
     */
    public IGraphInfo createOrReplaceGraph(URI graphURI, InputStream in, URI baseURI, MediaType mediaType) throws UnsupportedMediaTypeException, IOException, StoreException;

}
