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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.semagia.cassa.server.store.IStore;
import com.semagia.cassa.server.store.StoreException;

/**
 * Represents the root for all graphs.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
@Path("/")
public class GraphsResource extends AbstractGraphResource {

    /**
     * The URI of the requested graph.
     */
    private final URI _graph;
    private final boolean _wantServiceDescription;

    public GraphsResource(@QueryParam("default") String defaultGraph, @QueryParam("graph") URI graph) {
        final boolean isDefaultGraph = defaultGraph != null;
        if (isDefaultGraph && (!defaultGraph.isEmpty() || graph != null)) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (graph != null && !graph.isAbsolute()) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        _wantServiceDescription = !isDefaultGraph && graph == null;
        _graph = isDefaultGraph ? IStore.DEFAULT_GRAPH : graph;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.jaxrs.AbstractGraphResource#getGraphURI()
     */
    @Override
    protected URI getGraphURI() {
        return _graph;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.jaxrs.AbstractGraphResource#getGraph()
     */
    @Override
    @GET
    public Response getGraph() throws StoreException {
        return _wantServiceDescription ? getServiceDescription() : super.getGraph();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.jaxrs.AbstractGraphResource#getGraphInfo()
     */
    @Override
    @HEAD
    public Response getGraphInfo() throws StoreException {
        notServiceDescription(); //TODO
        return super.getGraphInfo();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.jaxrs.AbstractGraphResource#createGraph(java.io.InputStream, javax.ws.rs.core.HttpHeaders)
     */
    @Override
    @PUT
    public Response createGraph(InputStream in, @Context HttpHeaders header)
            throws IOException, StoreException {
        notServiceDescription();
        return super.createGraph(in, header);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.jaxrs.AbstractGraphResource#createOrUpdateGraph(java.io.InputStream, javax.ws.rs.core.HttpHeaders)
     */
    @Override
    @POST
    public Response createOrUpdateGraph(InputStream in,
            @Context HttpHeaders header) throws IOException, StoreException {
        notServiceDescription();
        return super.createOrUpdateGraph(in, header);
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.jaxrs.AbstractGraphResource#deleteGraph()
     */
    @Override
    @DELETE
    public Response deleteGraph() throws StoreException {
        notServiceDescription();
        return super.deleteGraph();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.jaxrs.AbstractGraphResource#modifyGraph(java.io.InputStream, javax.ws.rs.core.HttpHeaders)
     */
    @Override
    @PATCH
    public Response modifyGraph(InputStream in, @Context HttpHeaders header)
            throws IOException, StoreException {
        notServiceDescription();
        return super.modifyGraph(in, header);
    }

    /**
     * Returns the service description.
     *
     * @return The service description document.
     * @throws StoreException In case of an error.
     */
    @OPTIONS
    public Response getServiceDescription() throws StoreException {
        // Not supported yet.
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    private void notServiceDescription() {
        if (_wantServiceDescription) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }
}
