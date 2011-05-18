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
package com.semagia.cassa.jaxrs;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IWritableRepresentation;

/**
 * {@link Response} related utility functions.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class ResponseUtils {

    private ResponseUtils() {
        // noop.
    }

    /**
     * Returns a Not Acceptable reponse with the provided media types.
     *
     * @param supportedMediaTypes The supported media types.
     * @return A Not Acceptable response. 
     */
    public static Response notAcceptable(List<MediaType> supportedMediaTypes) {
        return Response.notAcceptable(MediaTypeUtils.asVariants(supportedMediaTypes)).build();
    }

    /**
     * Returns a response using the provided {@link IWritablRepresentation} instance to 
     * serialize the body of the response.
     *
     * @param builder The builder used to build the response.
     * @param writable A {@link IWritable} instance which is reposible to serialize the response body.
     * @return The reponse.
     */
    public static Response buildStreamingEntity(final ResponseBuilder builder, final IWritableRepresentation writable) {
        builder.entity(new StreamingWritableOutput(writable));
        builder.type(MediaTypeUtils.toJaxRSMediaType(writable.getMediaType()));
        return builder.build();
    }

    /**
     * Returns a HTTP Method Not Allowed (405) response with an "Allow" header
     * set to "GET, HEAD".
     *
     * @return A Method Not Allowed response.
     */
    public static Response methodNotAllowed() {
        return Response.status(405).header("Allow", "GET, HEAD").build();
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
     * Returns a HTTP Accepted response.
     *
     * @return A Accepted response.
     */
    public static Response accepted() {
        return Response.status(Response.Status.ACCEPTED).build();
    }

}
