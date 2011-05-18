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

import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IGraphInfo;
import com.semagia.cassa.server.storage.IStore;
import com.semagia.cassa.server.storage.RemovalStatus;
import com.semagia.cassa.server.storage.StorageException;

/**
 * Common graph resource which implements all operations above graphs.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public abstract class AbstractGraphResource extends AbstractResource {

    /**
     * Returns the graph IRI.
     *
     * @return The graph IRI to operate on. {@code null} indicates the default graph.
     */
    protected abstract URI getGraphURI();

    /**
     * 
     * 
     * @return A graph serialization.
     * @throws StorageException In case of an error.
     */
    @GET
    public Response getGraph() throws StorageException {
        final URI graphURI = getGraphURI();
        final IStore store = getStore();
        final IGraphInfo graph = store.getGraphInfo(graphURI);
        final MediaType mt = getMediaType(graph.getSupportedMediaTypes());
        return buildStreamingEntity(
                makeResponseBuilder(graph.getLastModification()), store.getGraph(graphURI, mt));
    }

    /**
     * 
     *
     * @return
     */
    @PUT
    public Response createGraph(InputStream in, @Context HttpHeaders header) throws StorageException {
        final URI graphURI = getGraphURI();
        final IStore store = getStore();
        final MediaType mt = MediaTypeUtils.toMediaType(header.getMediaType());
        final boolean wasKnown = store.containsGraph(graphURI);
        final IGraphInfo info = store.createOrReplaceGraph(graphURI, in, mt);
        //TODO: Is it a good idea to return the IRI here? It may refer to an external server...
        return wasKnown ? noContent() : created(info.getURI());
    }

    /**
     * 
     *
     * @return
     */
    @POST
    public Response mergeGraph(InputStream in, @Context HttpHeaders header) throws StorageException {
        //TODO: Ensure that the location points to this server.
        final URI graphURI = getGraphURI();
        final IStore store = getStore();
        final MediaType mt = MediaTypeUtils.toMediaType(header.getMediaType());
        final boolean wasKnown = store.containsGraph(graphURI);
        final IGraphInfo info = store.createOrUpdateGraph(graphURI, in, mt);
        return wasKnown ? noContent() : created(info.getURI());
    }

    /**
     * 
     *
     * @return
     */
    @DELETE
    public Response deleteGraph() throws StorageException {
        return getStore().deleteGraph(getGraphURI()) == RemovalStatus.DELAYED ? accepted() : noContent();
    }

}
