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
package com.semagia.cassa.jaxrs.utils;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.semagia.cassa.common.dm.IGraphInfo;
import com.semagia.cassa.jaxrs.GraphsResource;

/**
 * Utility functions related to graphs.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class GraphUtils {

    private GraphUtils() {
        // noop.
    }

    private static UriBuilder pathToGraphsResource(final UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(GraphsResource.class);
    }

    /**
     * Returns if the provided graph URI is a local graph.
     * 
     * @param uriInfo UriInfo instance.
     * @param graphInfo {@link IGraphInfo} instance.
     * @return {@code true} if the graph is a local graph, otherwise {@code false}.
     */
    public static boolean isLocalGraph(final UriInfo uriInfo, final IGraphInfo graphInfo) {
        return isLocalGraph(uriInfo, graphInfo.getURI());
    }

    /**
     * Returns if the provided graph URI is a local graph.
     * 
     * @param uriInfo UriInfo instance.
     * @param graphURI Graph URI.
     * @return {@code true} if the graph is a local graph, otherwise {@code false}.
     */
    public static boolean isLocalGraph(final UriInfo uriInfo, final URI graphURI) {
        return !pathToGraphsResource(uriInfo).build().relativize(graphURI).isAbsolute();
    }

    /**
     * Returns an URI which points either to a local graph (Direct Graph Identification)
     * or to the service with a graph parameter (Indirect Graph Identification). 
     * 
     * @param uriInfo UriInfo instance.
     * @param graphInfo {@link IGraphInfo} instance.
     * @return An absolute URI to the provided graph.
     */
    public static URI linkToGraph(final UriInfo uriInfo, final IGraphInfo graphInfo) {
        return linkToGraph(uriInfo, graphInfo.getURI());
    }

    /**
     * Returns an URI which points either to a local graph (Direct Graph Identification)
     * or to the service with a graph parameter (Indirect Graph Identification). 
     * 
     * @param uriInfo UriInfo instance.
     * @param graphURI Graph URI.
     * @return An absolute URI to the provided graph.
     */
    public static URI linkToGraph(final UriInfo uriInfo, final URI graphURI) {
        final UriBuilder builder = pathToGraphsResource(uriInfo);
        final UriBuilder builder2 = builder.clone();    //TODO: Is a UriBuilder reusable after .build() has been called? Works with Jersey...
        if (builder.build().relativize(graphURI).isAbsolute()) {
            return builder2.queryParam("graph", graphURI).build();
        }
        else {
            // local graph
            return graphURI;
        }
    }

}
