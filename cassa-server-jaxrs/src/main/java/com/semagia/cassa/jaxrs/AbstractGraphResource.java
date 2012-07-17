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

import static com.semagia.cassa.jaxrs.ResponseUtils.accepted;
import static com.semagia.cassa.jaxrs.ResponseUtils.buildStreamingEntity;
import static com.semagia.cassa.jaxrs.ResponseUtils.created;
import static com.semagia.cassa.jaxrs.ResponseUtils.noContent;
import static com.semagia.cassa.jaxrs.ResponseUtils.badRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IGraphInfo;
import com.semagia.cassa.common.dm.RemovalStatus;
import com.semagia.cassa.server.store.IStore;
import com.semagia.cassa.server.store.StoreException;

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
     * @throws StorageException In case of an error.
     */
    @GET
    public Response getGraph() throws StoreException {
        final URI graphURI = getGraphURI();
        final IStore store = getStore();
        final IGraphInfo graph = store.getGraphInfo(graphURI);
        final MediaType mt = getMediaType(graph.getSupportedMediaTypes());
        return buildStreamingEntity(
                makeResponseBuilder(graph), store.getGraph(graphURI, mt));
    }

    /**
     * Checks if a graph exists.
     *
     * @return A response with graph metadata.
     * @throws StorageException In case of an error.
     */
    @HEAD
    public Response getGraphInfo() throws StoreException {
        return makeResponseBuilder(getStore().getGraphInfo(getGraphURI())).build();
    }

    /**
     * Creates a new graph or replaces an existing graph.
     *
     * @return A response indicating if a graph was created or replaced.
     * @throws IOException In case of an I/O error. 
     * @throws StorageException In case of an error.
     */
    @PUT
    public Response createGraph(InputStream in, @Context HttpHeaders header) throws IOException, StoreException {
        final URI graphURI = getGraphURI();
        final IStore store = getStore();
        final MediaType mt = MediaTypeUtils.toMediaType(header.getMediaType());
        final boolean wasKnown = graphURI == IStore.DEFAULT_GRAPH || store.containsGraph(graphURI);
        final IGraphInfo info = store.createOrReplaceGraph(graphURI, in, getBaseURI(graphURI), mt);
        if (wasKnown) {
            return noContent();
        }
        if (!_uriInfo.getBaseUriBuilder()
                .path(GraphsResource.class).build().relativize(info.getURI()).isAbsolute()) {
            // Graph is a local graph
            return created(graphURI);
        }
        // Not a local graph -> set the location to the service resource
        return created(_uriInfo.getBaseUriBuilder()
                                    .path(ServiceResource.class)
                                    .queryParam("graph", "{graph}")
                                    .build(info.getURI()));
    }

    /**
     * Creates a new graph or updates an existing graph.
     *
     * @return A response indicating if a graph was created or updated.
     * @throws IOException In case of an I/O error.
     * @throws StorageException In case of an error.
     */
    @POST
    public Response createOrUpdateGraph(InputStream in, @Context HttpHeaders header) throws IOException, StoreException {
        final URI graphURI = getGraphURI();
        final IStore store = getStore();
        final MediaType mt = MediaTypeUtils.toMediaType(header.getMediaType());
        final URI base = getBaseURI(graphURI);
        final IGraphInfo info = graphURI == null ? store.createGraph(in, base, mt)
                                                 : store.updateGraph(graphURI, in, base, mt);
        return graphURI == null ? created(info.getURI()) : noContent();
    }

    /**
     * Deletes a graph.
     *
     * @return A response indicating if the graph was removed immediately or deletion is scheduled.
     * @throws StorageException In case of an error.
     */
    @DELETE
    public Response deleteGraph() throws StoreException {
        return getStore().deleteGraph(getGraphURI()) == RemovalStatus.DELAYED ? accepted() : noContent();
    }

    /**
     * Modifies a graph.
     *
     * @return A response indicating if the graph was modified successfully.
     * @throws StorageException In case of an error.
     */
    @PATCH
    public Response modifyGraph(InputStream in, @Context HttpHeaders header) throws IOException, StoreException {
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
     * @return A ResponseBuilder instance.
     */
    private ResponseBuilder makeResponseBuilder(final IGraphInfo graphInfo) {
        final ResponseBuilder builder = super.makeResponseBuilder(graphInfo.getLastModification());
        builder.variants(MediaTypeUtils.asVariants(graphInfo.getSupportedMediaTypes()));
        return builder;
    }

}
