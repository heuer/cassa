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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import javax.ws.rs.core.UriInfo;

import com.semagia.cassa.jaxrs.GraphsResource;
import com.semagia.cassa.server.store.IGraphInfo;

/**
 * Internal utility functions related to graphs.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class GraphUtils {

    private GraphUtils() {
        // noop.
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
        return !uriToGraphResource(uriInfo).relativize(graphURI).isAbsolute();
    }

    /**
     * Returns an URI which points either to a local graph (Direct Graph Identification)
     * or to the service with a graph parameter (Indirect Graph Identification). 
     * 
     * @param uriInfo UriInfo instance.
     * @param graphInfo {@link IGraphInfo} instance.
     * @return An absolute URI to the provided graph.
     * @throws UnsupportedEncodingException In case the UTF-8 encoding is not supported.
     */
    public static URI linkToGraph(final UriInfo uriInfo, final IGraphInfo graphInfo) throws UnsupportedEncodingException {
        return linkToGraph(uriInfo, graphInfo.getURI());
    }

    /**
     * Returns an URI which points either to a local graph (Direct Graph Identification)
     * or to the service with a graph parameter (Indirect Graph Identification). 
     * 
     * @param uriInfo UriInfo instance.
     * @param graphURI Graph URI.
     * @return An absolute URI to the provided graph.
     * @throws UnsupportedEncodingException In case the UTF-8 encoding is not supported.
     */
    public static URI linkToGraph(final UriInfo uriInfo, final URI graphURI) throws UnsupportedEncodingException {
        return linkToGraph(uriToGraphResource(uriInfo), graphURI);
    }

    /**
     * Returns an absolute URI which references the graphs resource.
     * 
     * @param uriInfo A {@link UriInfo} instance used to build the absolute URI.
     * @return An absolute URI pointing to the graphs resource.
     */
    public static URI uriToGraphResource(final UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(GraphsResource.class).build();
    }

    /**
     * Returns an absolute URI which is used to retrieve the provided graph.
     * 
     * Returns either a local graph URI or a URI which uses Indirect Graph Identification:
     * {@code http://www.example.org/store/graph-A} or {@code http://www.example.org/store/?graph=http://www.example.net/graph-B}
     * 
     * @param graphResourceURI An absolute URI pointing to the graphs resource.
     * @param graphURI The graph URI.
     * @return An absolute URI which can be used to retrieve the graph specified by {@code graphURI}.
     * @throws UnsupportedEncodingException If case UTF-8 encoding is not supported.
     */
    public static URI linkToGraph(final URI graphResourceURI, final URI graphURI) throws UnsupportedEncodingException {
        if (graphResourceURI.relativize(graphURI).isAbsolute()) {
            return graphResourceURI.resolve("?graph=" + URLEncoder.encode(graphURI.toString(), "UTF-8"));
        }
        else {
            // local graph
            return graphURI;
        }
    }

}
