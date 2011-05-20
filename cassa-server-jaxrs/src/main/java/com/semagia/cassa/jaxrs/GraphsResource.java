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

import static com.semagia.cassa.jaxrs.ResponseUtils.badRequest;

import java.net.URI;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.semagia.cassa.server.store.StoreException;

/**
 * Represents the root for all graphs.
 * 
 * This resource allows HTTP POST only. Such a request creates a new graph.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
@Path("/g/")
public class GraphsResource extends AbstractGraphResource {

    /* (non-Javadoc)
     * @see com.semagia.cassa.jaxrs.AbstractGraphResource#getGraphURI()
     */
    @Override
    protected URI getGraphURI() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.jaxrs.AbstractGraphResource#getGraph()
     */
    @Override
    public Response getGraph() throws StoreException {
        return badRequest();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.jaxrs.AbstractGraphResource#getGraphInfo()
     */
    @Override
    public Response getGraphInfo() throws StoreException {
        return badRequest();
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.jaxrs.AbstractGraphResource#deleteGraph()
     */
    @Override
    public Response deleteGraph() throws StoreException {
        return badRequest();
    }

}
