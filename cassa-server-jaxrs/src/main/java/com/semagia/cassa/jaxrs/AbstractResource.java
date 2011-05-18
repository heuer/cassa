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

import java.util.Date;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Variant;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.server.store.IStore;

/**
 * Common resource which provides some utility functions.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
abstract class AbstractResource {

    @Context 
    private Request _request;

    private IStore _store;

    /**
     * Returns the store.
     *
     * @return The store to operate on.
     */
    protected IStore getStore() {
        return _store;
    }

    /**
     * Creates a {@link ResponseBuilder} with a last-modified header.
     * 
     * If the request contains a <tt>If-Modified-Since</tt> header and the
     * resource wasn't modified, a {@link WebApplicationException} with
     * the status <tt>Not modified (304)</tt> is thrown.
     *
     * @return A response builder.
     * @throws WebApplicationException In case the resource wasn't modified.
     */
    protected final ResponseBuilder makeResponseBuilder(final long lastModification) throws WebApplicationException {
        final Date lastModificationDate = new Date(lastModification);
        final ResponseBuilder builder = lastModification != -1 ? _request.evaluatePreconditions(lastModificationDate) : null;
        if (builder != null) {
            // Preconditions are met, report the status to the client
            throw new WebApplicationException(builder.build());
        }
        return Response.ok().lastModified(lastModificationDate);
    }

    /**
     * Creates a {@link ResponseBuilder} with a last-modified header.
     * 
     * If the request contains a <tt>If-Modified-Since</tt> header and the
     * resource wasn't modified, a {@link WebApplicationException} with
     * the status <tt>Not modified (304)</tt> is thrown.
     *
     * @return A response builder.
     * @throws WebApplicationException In case the resource wasn't modified.
     */
    protected final ResponseBuilder makeResponseBuilder(final long lastModification, List<MediaType> mediaTypes) throws WebApplicationException {
        return makeResponseBuilder(lastModification)
                .variants(MediaTypeUtils.asVariants(mediaTypes));
    }

    /**
     * Returns the most appropriate media type which is supported by the server
     * and accepted by the client. 
     *
     * @param supportedMediaTypes The available/supported media types.
     * @return The preferred media type.
     * @throws WebApplicationException In case no compatible media type can be found.
     */
    protected final MediaType getMediaType(final List<MediaType> supportedMediaTypes) throws WebApplicationException {
        final Variant variant = _request.selectVariant(MediaTypeUtils.asVariants(supportedMediaTypes));
        if (variant == null) {
            throw new WebApplicationException(ResponseUtils.notAcceptable(supportedMediaTypes));
        }
        return MediaTypeUtils.toMediaType(variant.getMediaType());
    }

}
