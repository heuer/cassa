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
import java.net.URI;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static com.semagia.cassa.jaxrs.utils.ResponseUtils.badRequest;
import static com.semagia.cassa.jaxrs.utils.ResponseUtils.redirect;

import com.semagia.cassa.common.dm.impl.DefaultGraphInfo;
import com.semagia.cassa.jaxrs.utils.GraphUtils;
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
     * 
     * This property may be {@code null} if the root resource is meant.
     */
    private final URI _graph;


    public GraphsResource(@Context UriInfo uriInfo, @QueryParam("default") String defaultGraph, @QueryParam("graph") URI graph) {
        final boolean isDefaultGraph = defaultGraph != null;
        if (isDefaultGraph && (!defaultGraph.isEmpty() || graph != null)) {
            throw new WebApplicationException(BAD_REQUEST);
        }
        if (graph != null) {
            if (!graph.isAbsolute()) {
                throw new WebApplicationException(BAD_REQUEST);
            }
            else if (GraphUtils.isLocalGraph(uriInfo, graph)) {
                throw new WebApplicationException(redirect(graph));
            }
        }
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
    public Response getGraph() throws IOException, StoreException {
        return _graph == null ? getServiceDescription() : super.getGraph();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.jaxrs.AbstractGraphResource#getGraphInfo()
     */
    @Override
    @HEAD
    public Response getGraphInfo() throws StoreException {
        if (_graph == null) {
            return makeResponseBuilder(new DefaultGraphInfo(_uriInfo.getAbsolutePath(), getStore().getLastModification()), null).build();
        }
        return super.getGraphInfo();
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
        return badRequest();
    }

}
