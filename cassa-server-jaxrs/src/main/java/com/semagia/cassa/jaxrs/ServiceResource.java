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

import java.net.URI;

import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.semagia.cassa.server.store.IStore;
import com.semagia.cassa.server.store.StoreException;

/**
 * Represents the /service resource.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
@Path("/service")
public class ServiceResource extends AbstractGraphResource {

    /**
     * The URI of the requested graph.
     * 
     * If the URI is null, it indicates the default graph.
     */
    private final URI _graph;
    private final boolean _wantServiceDescription;

    public ServiceResource(@QueryParam("default") String defaultGraph, @QueryParam("graph") URI graph) {
        final boolean isDefaultGraph = defaultGraph != null;
        if (isDefaultGraph && (!defaultGraph.isEmpty() || graph != null)) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (graph != null && (!isDefaultGraph && !graph.isAbsolute())) {
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
    public Response getGraph() throws StoreException {
        return _wantServiceDescription ? getServiceDescription() : super.getGraph();
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

}
