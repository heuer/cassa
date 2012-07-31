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
package com.semagia.cassa.jaxrs;

import static com.semagia.cassa.jaxrs.utils.ResponseUtils.accepted;
import static com.semagia.cassa.jaxrs.utils.ResponseUtils.badRequest;
import static com.semagia.cassa.jaxrs.utils.ResponseUtils.buildStreamingEntity;
import static com.semagia.cassa.jaxrs.utils.ResponseUtils.created;
import static com.semagia.cassa.jaxrs.utils.ResponseUtils.noContent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IGraphInfo;
import com.semagia.cassa.common.dm.IWritableRepresentation;
import com.semagia.cassa.common.dm.RemovalStatus;
import com.semagia.cassa.jaxrs.utils.GraphUtils;
import com.semagia.cassa.jaxrs.utils.MediaTypeUtils;
import com.semagia.cassa.server.store.GraphMismatchException;
import com.semagia.cassa.server.store.GraphNotExistsException;
import com.semagia.cassa.server.store.IStore;
import com.semagia.cassa.server.store.ParseException;
import com.semagia.cassa.server.store.QueryException;
import com.semagia.cassa.server.store.StoreException;
import com.semagia.cassa.server.store.UnsupportedMediaTypeException;
import com.semagia.cassa.server.utils.ETagUtils;

/**
 * Common graph resource which implements all operations above graphs.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public abstract class AbstractGraphResource extends AbstractResource {

    @Context
    protected UriInfo _uriInfo;

    /**
     * Returns the graph IRI.
     *
     * @return The graph IRI to operate on. {@code null} indicates the default graph.
     */
    protected abstract URI getGraphURI();

    /**
     * Writes a graph.
     * 
     * @return A graph serialization.
     * @throws IOException In case of an I/O error.
     * @throws GraphNotExistsException In case the graph does not exist. 
     * @throws StorageException In case of an error.
     */
    @GET
    public Response getGraph() throws IOException, GraphNotExistsException, StoreException {
        final URI graphURI = getGraphURI();
        final IStore store = getStore();
        final IGraphInfo graph = store.getGraphInfo(graphURI);
        final MediaType mt = getMediaType(graph.getSupportedMediaTypes());
        final IWritableRepresentation writable = store.getGraph(graphURI, mt);
        return buildStreamingEntity(
                makeResponseBuilder(graph, writable.getMediaType()), writable);
    }

    /**
     * Checks if a graph exists.
     *
     * @return A response with graph metadata.
     * @throws GraphNotExistsException In case the graph does not exist. 
     * @throws StorageException In case of an error.
     */
    @HEAD
    public Response getGraphInfo() throws GraphNotExistsException, StoreException {
        final IGraphInfo graph = getStore().getGraphInfo(getGraphURI());
        final MediaType mt = getMediaType(graph.getSupportedMediaTypes());
        return makeResponseBuilder(graph, mt).build();
    }

    /**
     * Creates a new graph or replaces an existing graph.
     *
     * @return A response indicating if a graph was created or replaced.
     * @throws IOException In case of an I/O error. 
     * @throws ParseException In case the input could not be read, i.e. syntax error.
     * @throws StoreException In case of an error.
     */
    @PUT
    public Response create(InputStream in, @Context HttpHeaders header, @QueryParam("subject") URI subject) throws IOException, ParseException, StoreException {
        final URI graphURI = getGraphURI();
        final IStore store = getStore();
        final MediaType mt = MediaTypeUtils.toMediaType(header.getMediaType());
        final boolean wasKnown = graphURI == IStore.DEFAULT_GRAPH || store.containsGraph(graphURI);
        final IGraphInfo info = subject == null ? store.createOrReplaceGraph(graphURI, in, getBaseURI(graphURI), mt)
                                                : store.createOrReplaceSubject(graphURI, subject, in, getBaseURI(graphURI), mt);
        if (wasKnown) {
            return noContent();
        }
        return created(GraphUtils.linkToGraph(_uriInfo, info));
    }

    /**
     * Creates a new graph or updates an existing graph.
     *
     * @return A response indicating if a graph was created or updated.
     * @throws UnsupportedMediaTypeException In case the media type isn't supported. 
     * @throws IOException In case of an I/O error.
     * @throws ParseException In case the input could not be read, i.e. syntax error.
     * @throws StorageException In case of an error.
     */
    @POST
    public Response createOrUpdateGraph(InputStream in, @Context HttpHeaders header) throws UnsupportedMediaTypeException, IOException, ParseException, StoreException {
        final URI graphURI = getGraphURI();
        final IStore store = getStore();
        final MediaType mt = MediaTypeUtils.toMediaType(header.getMediaType());
        final URI base = getBaseURI(graphURI);
        final IGraphInfo info = graphURI == null ? store.createGraph(in, base, mt)
                                                 : store.updateGraph(graphURI, in, base, mt);
        return graphURI == null ? created(info.getURI()) : noContent();
    }

    /**
     * Deletes a graph or a subject from a graph.
     *
     * @return A response indicating if the graph was removed immediately or deletion is scheduled.
     * @throws IOException In case of an I/O error.
     * @throws GraphNotExistsException In case the graph does not exist. 
     * @throws StorageException In case of an error.
     */
    @DELETE
    public Response delete(@QueryParam("subject") URI subject) throws IOException, GraphNotExistsException, StoreException {
        final IStore store = getStore();
        final RemovalStatus res = subject == null ? store.deleteGraph(getGraphURI())
                                                  : store.deleteSubject(getGraphURI(), subject); 
        return res == RemovalStatus.DELAYED ? accepted() : noContent();
    }

    /**
     * Modifies a graph.
     *
     * @return A response indicating if the graph was modified successfully.
     * @throws QueryException In case of a query error, i.e. syntax error.
     * @throws UnsupportedMediaTypeException In case the media type isn't supported. 
     * @throws GraphMismatchException In case the query utilizes a different graph as the requested graph.
     * @throws StorageException In case of an error.
     */
    @PATCH
    public Response modifyGraph(InputStream in, @Context HttpHeaders header) throws IOException, GraphMismatchException, UnsupportedMediaTypeException, QueryException, StoreException {
        final URI graphURI = getGraphURI();
        final MediaType mt = MediaTypeUtils.toMediaType(header.getMediaType());
        return getStore().modifyGraph(graphURI, in, getBaseURI(graphURI), mt) ? noContent()
                                                                              : badRequest();
    }

    /**
     * Returns the base URI.
     *
     * @param graphURI The graph URI or {@code null}.
     * @return The {@code graphURI} if it does not represent the default graph or is {@code null}, 
     *          otherwise a URI which represents the path to this resource. 
     */
    private URI getBaseURI(final URI graphURI) {
        if (graphURI == null || graphURI == IStore.DEFAULT_GRAPH) {
            return _uriInfo.getAbsolutePath();
        }
        return graphURI;
    }

    /**
     * Returns a {@link ResponseBuilder} initialized with the provided graph metadata.
     *
     * @param graphInfo The graph's metadata.
     * @param mediaType The media type of the response or {@code null}.
     * @return A ResponseBuilder instance.
     */
    protected final ResponseBuilder makeResponseBuilder(final IGraphInfo graphInfo, final MediaType mediaType) {
        final ResponseBuilder builder = super.makeResponseBuilder(graphInfo.getLastModification(), createETag(graphInfo, mediaType));
        builder.variants(MediaTypeUtils.asVariants(graphInfo.getSupportedMediaTypes()));
        return builder;
    }

    /**
     * Creates an ETag for the provided {@link IGraphInfo} if reasonable, returns
     * {@code null} otherwise.
     * 
     * @param graphInfo The graph to create the ETag for.
     * @param mediaType The media type of the response or {@code null}.
     * @return An ETag or {@code null}.
     */
    private static EntityTag createETag(final IGraphInfo graphInfo, final MediaType mediaType) {
        final String etag = ETagUtils.generateETag(graphInfo, mediaType);
        return etag == null ? null : new EntityTag(etag);
    }

}
