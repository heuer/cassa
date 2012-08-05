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
package com.semagia.cassa.server.store.utils;

import java.net.URI;
import java.util.Date;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.server.store.IGraphInfo;
import com.semagia.cassa.server.store.impl.DefaultGraphInfo;
import com.semagia.cassa.server.utils.ETagUtils;

import junit.framework.TestCase;

/**
 * Tests against {@link ETagUtils}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestETagUtils extends TestCase {

    private static final URI _DEFAULT_URI = URI.create("http://www.example.org/test");

    public void testByGraphInfo() {
        final long time = new Date().getTime();
        IGraphInfo info = new DefaultGraphInfo(_DEFAULT_URI, MediaType.RDF_XML, time);
        assertNull(ETagUtils.generateETag(info, null));
        assertNotNull(ETagUtils.generateETag(info, MediaType.RDF_XML));
        info = new DefaultGraphInfo(_DEFAULT_URI, MediaType.RDF_XML);
        assertNull(ETagUtils.generateETag(info, MediaType.RDF_XML));
    }

    public void testByParams() {
        final long time = new Date().getTime();
        assertNull(ETagUtils.generateETag(_DEFAULT_URI, time, null));
        assertNotNull(ETagUtils.generateETag(_DEFAULT_URI, time, MediaType.RDF_XML));
        assertNull(ETagUtils.generateETag(_DEFAULT_URI, -1, MediaType.RDF_XML));
    }

    public void testDifference() {
        final long time = new Date().getTime();
        final String etag = ETagUtils.generateETag(_DEFAULT_URI, time, MediaType.RDF_XML);
        assertNotNull(etag);
        // Create the same ETag again
        String etag2 = ETagUtils.generateETag(_DEFAULT_URI, time, MediaType.RDF_XML);
        assertNotNull(etag2);
        assertEquals(etag, etag2);
        // Change media type
        etag2 = ETagUtils.generateETag(_DEFAULT_URI, time, MediaType.TURTLE);
        assertNotNull(etag2);
        assertFalse(etag.equals(etag2));
        // Change URI, same media type
        etag2 = ETagUtils.generateETag(_DEFAULT_URI.resolve("?bla"), time, MediaType.RDF_XML);
        assertNotNull(etag2);
        assertFalse(etag.equals(etag2));
        // Change time, same URI, same media type
        etag2 = ETagUtils.generateETag(_DEFAULT_URI, time + 1, MediaType.RDF_XML);
        assertNotNull(etag2);
        assertFalse(etag.equals(etag2));
    }

}
