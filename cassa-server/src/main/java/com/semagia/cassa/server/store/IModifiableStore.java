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
import com.semagia.cassa.common.dm.RemovalStatus;

/**
 * Graph store.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public interface IModifiableStore extends IStore {

    /**
     * Deletes a graph.
     *
     * @param graphURI The URI of the graph ({@link #DEFAULT_GRAPH} indicates the default graph)
     * @return {@link RemovalStatus#IMMEDIATELY} if the graph was deleted, 
     *          {@link RemovalStatus#DELAYED} if graph removal is scheduled.
     * @throws GraphNotExistsException In case the graph does not exist.
     * @throws IOException In case of an I/O error.
     * @throws StoreException In case of an error.
     */
    public RemovalStatus deleteGraph(URI graphURI) throws GraphNotExistsException, IOException, StoreException;

    /**
     * Deletes a subject.
     *
     * @param graphURI The URI of the graph ({@link #DEFAULT_GRAPH} indicates the default graph)
     * @param subjectURI The URI of the subject to delete.
     * @return {@link RemovalStatus#IMMEDIATELY} if the graph was deleted, 
     *          {@link RemovalStatus#DELAYED} if graph removal is scheduled.
     * @throws GraphNotExistsException In case the graph does not exist.
     * @throws IOException In case of an I/O error.
     * @throws StoreException In case of an error.
     */
    public RemovalStatus deleteSubject(URI graphURI, URI subjectURI) throws GraphNotExistsException, IOException, StoreException;

    /**
     * Updates a graph.
     * 
     * @param graphURI The URI of the graph ({@link #DEFAULT_GRAPH} indicates the default graph)
     * @param in The input stream to read the graph from.
     * @param baseURI The base URI to resolve relative URIs against (in most cases identical to the graph IRI).
     * @param mediaType The media type of the input stream.
     * @return Metadata of the created/updated graph.
     * @throws UnsupportedMediaTypeException In case the media type isn't supported.
     * @throws IOException In case of an I/O error.
     * @throws ParseException In case of a parsing error, i.e. syntax error.
     * @throws StoreException In case of an error.
     */
    public IGraphInfo updateGraph(URI graphURI, InputStream in, URI baseURI, MediaType mediaType) throws UnsupportedMediaTypeException, IOException, ParseException, StoreException;

    /**
     * Modifies a graph with a query.
     *
     * @param graphURI The URI of the graph ({@link #DEFAULT_GRAPH} indicates the default graph)
     * @param in The input stream to read the query from.
     * @param baseURI The base URI to resolve relative URIs against (in most cases identical to the graph IRI).
     * @param mediaType The media type of the input stream.
     * @return {@code true} if the graph was modified sucessfully, {@code false} otherwise.
     * @throws UnsupportedMediaTypeException In case the media type isn't supported.
     * @throws IOException In case of an I/O error.
     * @throws QueryException In case of a syntax error or any other query-related error.
     * @throws GraphMismatchException If the {@code graphURI} is not identitical with graph which is referenced by the query.
     * @throws StoreException In case of an error.
     */
    public boolean modifyGraph(URI graphURI, InputStream in, URI baseURI, MediaType mediaType) throws UnsupportedMediaTypeException, IOException, QueryException, GraphMismatchException, StoreException;

    /**
     * Creates a graph.
     * 
     * @param in The input stream to read the graph from.
     * @param baseURI The base URI to resolve relative URIs against.
     * @param mediaType The media type of the input stream.
     * @return Metadata of the created/updated graph.
     * @throws UnsupportedMediaTypeException
     * @throws IOException In case of an I/O error.
     * @throws ParseException In case of a parsing error, i.e. syntax error.
     * @throws StoreException In case of an error.
     */
    public IGraphInfo createGraph(InputStream in, URI baseURI, MediaType mediaType) throws UnsupportedMediaTypeException, IOException, ParseException, StoreException;

    /**
     * Creates or updates the graph with the specified URI.
     *
     * @param graphURI The URI of the graph ({@link #DEFAULT_GRAPH} indicates the default graph)
     * @param in The input stream to read the graph from.
     * @param baseURI The base URI to resolve relative URIs against (in most cases identical to the graph IRI).
     * @param mediaType The media type of the input stream.
     * @return Metadata of the created/replaced graph.
     * @throws UnsupportedMediaTypeException
     * @throws IOException In case of an I/O error.
     * @throws ParseException In case of a parsing error, i.e. syntax error.
     * @throws StoreException In case of an error.
     */
    public IGraphInfo createOrReplaceGraph(URI graphURI, InputStream in, URI baseURI, MediaType mediaType) throws UnsupportedMediaTypeException, IOException, ParseException, StoreException;

    /**
     * Creates or updates a subject within a graph.
     *
     * @param graphURI The URI of the graph ({@link #DEFAULT_GRAPH} indicates the default graph)
     * @param subjectURI The URI of the subject.
     * @param in The input stream to read the subject from.
     * @param baseURI The base URI to resolve relative URIs against (in most cases identical to the graph IRI).
     * @param mediaType The media type of the input stream.
     * @return Metadata of the created/replaced graph.
     * @throws UnsupportedMediaTypeException
     * @throws IOException In case of an I/O error.
     * @throws ParseException In case of a parsing error, i.e. syntax error.
     * @throws StoreException In case of an error.
     */
    public IGraphInfo createOrReplaceSubject(URI graphURI, URI subjectURI, InputStream in, URI baseURI, MediaType mediaType) throws UnsupportedMediaTypeException, IOException, ParseException, StoreException;

}
