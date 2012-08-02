/*
 * Copyright 2008 - 2011 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IWritableRepresentation;

/**
 * {@link Response} related utility functions.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class ResponseUtils {

    private ResponseUtils() {
        // noop.
    }

    /**
     * Returns a Not Acceptable reponse with the provided media types.
     *
     * @param supportedMediaTypes The supported media types.
     * @return A Not Acceptable response. 
     */
    public static Response notAcceptable(final List<MediaType> supportedMediaTypes) {
        return Response.notAcceptable(MediaTypeUtils.asVariants(supportedMediaTypes)).build();
    }

    /**
     * Returns a response using the provided {@link IWritablRepresentation} instance to 
     * serialize the body of the response.
     *
     * @param builder The builder used to build the response.
     * @param writable A {@link IWritableRepresentation} instance which is reposible to serialize the response body.
     * @return The reponse.
     */
    public static Response buildStreamingEntity(final ResponseBuilder builder, final IWritableRepresentation writable) {
        final String contentType = writable.getEncoding() == null ? writable.getMediaType().toString() 
                                                                  : writable.getMediaType().toString() + "; charset=" + writable.getEncoding();
        return builder.entity(new StreamingWritableOutput(writable))
                      .header(HttpHeaders.CONTENT_TYPE, contentType)
                      .build();
    }

    /**
     * Returns a HTTP No Content response.
     *
     * @return A No Content response.
     */
    public static Response noContent() {
        return Response.noContent().build();
    }

    /**
     * Returns a HTTP Created response.
     *
     * @return A Created response.
     */
    public static Response created(final URI uri) {
        return Response.created(uri).build();
    }

    /**
     * Returns a HTTP Accepted response.
     *
     * @return A Accepted response.
     */
    public static Response accepted() {
        return Response.status(Response.Status.ACCEPTED).build();
    }

    /**
     * Returns a HTTP Bad Request response.
     *
     * @return A Bad Request response.
     */
    public static Response badRequest() {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     * Returns a HTTP 301 Moved Permanently response with the provided URI.
     * 
     * @param location A URI to redirect to.
     * @return A Moved Permanently response.
     */
    public static Response redirect(final URI location) {
        return Response.status(Response.Status.MOVED_PERMANENTLY).location(location).build();
    }
}
