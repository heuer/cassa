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

import static com.semagia.cassa.jaxrs.utils.ResponseUtils.notAcceptable;

import java.util.Date;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Variant;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.jaxrs.utils.MediaTypeUtils;
import com.semagia.cassa.server.ServerApplicationProvider;
import com.semagia.cassa.server.store.IStore;

/**
 * Common resource which provides some utility functions.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public abstract class AbstractResource {

    private final IStore _store = ServerApplicationProvider.getServerApplication().getStore();

    @Context 
    private Request _request;

    /**
     * Returns the store.
     *
     * @return The store to operate on.
     */
    protected final IStore getStore() {
        return _store;
    }

    /**
     * Creates a {@link ResponseBuilder} with a last-modified header.
     * 
     * If the request contains a {@code If-Modified-Since} header and the
     * resource wasn't modified, a {@link WebApplicationException} with
     * the status {@code Not modified (304)} is thrown.
     *
     * @param lastModification Last modification date or {@code -1} to indicate an unknown date
     * @return A response builder.
     * @throws WebApplicationException In case the resource wasn't modified.
     */
    protected final ResponseBuilder makeResponseBuilder(final long lastModification) throws WebApplicationException {
        return makeResponseBuilder(lastModification, null);
    }

    /**
     * Creates a {@link ResponseBuilder} with a last-modified header and ETag.
     * 
     * If the request contains a {@code If-Modified-Since} header and the
     * resource wasn't modified, a {@link WebApplicationException} with
     * the status {@code Not modified (304)} is thrown.
     *
     * @param lastModification Last modification date or {@code -1} to indicate an unknown date
     * @param etag An ETag or {@code null}.
     * @return A response builder.
     * @throws WebApplicationException In case the resource wasn't modified.
     */
    protected final ResponseBuilder makeResponseBuilder(final long lastModification, final EntityTag etag) throws WebApplicationException {
        final Date lastModificationDate = new Date(lastModification);
        ResponseBuilder builder = null;
        if (lastModification != -1) {
            builder =  etag == null ? _request.evaluatePreconditions(lastModificationDate)
                                    : _request.evaluatePreconditions(lastModificationDate, etag);
        }
        else if (etag != null) {
            builder = _request.evaluatePreconditions(etag);
        }
        if (builder != null) {
            // Client has up to date version; report status to the client
            throw new WebApplicationException(builder.build());
        }
        builder = Response.ok();
        if (lastModification != -1) {
            builder.lastModified(lastModificationDate);
        }
        if (etag != null) {
            builder.tag(etag);
        }
        return builder;
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
            throw new WebApplicationException(notAcceptable(supportedMediaTypes));
        }
        return MediaTypeUtils.toMediaType(variant.getMediaType());
    }

}
