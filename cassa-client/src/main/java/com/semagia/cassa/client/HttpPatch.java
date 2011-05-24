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
package com.semagia.cassa.client;

import java.net.URI;

import org.apache.http.client.methods.HttpPost;

/**
 * HTTP PATCH method.
 * 
 * The HTTP PATCH method is defined in 
 * <a href="http://tools.ietf.org/html/rfc5789">RFC 5789</a>. 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class HttpPatch extends HttpPost {

    /**
     * 
     */
    public HttpPatch() {
        super();
    }

    /**
     * 
     * @param uri The URI for the request.
     */
    public HttpPatch(String uri) {
        super(uri);
    }

    /**
     * 
     * @param uri The URI for the request.
     * @throws IllegalArgumentException If the URI is invalid.
     */
    public HttpPatch(URI uri) throws IllegalArgumentException {
        super(uri);
    }

    /* (non-Javadoc)
     * @see org.apache.http.client.methods.HttpPost#getMethod()
     */
    @Override
    public String getMethod() {
        return "PATCH";
    }

}
