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
package com.semagia.cassa.common.dm.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IGraphInfo;

import junit.framework.TestCase;

/**
 * Tests against {@link DefaultGraphInfo}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestDefaultGraphInfo extends TestCase {

    private static final URI _DEFAULT_URI = URI.create("http://www.example.org/test");

    public void testURI() {
        final IGraphInfo info = new DefaultGraphInfo(_DEFAULT_URI);
        assertEquals(_DEFAULT_URI, info.getURI());
        assertEquals(-1, info.getLastModification());
        assertEquals(0, info.getSupportedMediaTypes().size());
    }

    public void testMediaTypeSingleton() {
        final IGraphInfo info = new DefaultGraphInfo(_DEFAULT_URI, MediaType.XTM);
        assertEquals(_DEFAULT_URI, info.getURI());
        assertEquals(-1, info.getLastModification());
        assertEquals(1, info.getSupportedMediaTypes().size());
        assertEquals(MediaType.XTM, info.getSupportedMediaTypes().get(0));
    }

    public void testMediaTypeList() {
        final List<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.XTM);
        mediaTypes.add(MediaType.RDF_XML);
        final IGraphInfo info = new DefaultGraphInfo(_DEFAULT_URI, mediaTypes);
        assertEquals(_DEFAULT_URI, info.getURI());
        assertEquals(-1, info.getLastModification());
        assertEquals(2, info.getSupportedMediaTypes().size());
        assertTrue(info.getSupportedMediaTypes().contains(MediaType.XTM));
        assertTrue(info.getSupportedMediaTypes().contains(MediaType.RDF_XML));
    }

    public void testMediaTypeListNull() {
        final List<MediaType> mediaTypes = null;
        final IGraphInfo info = new DefaultGraphInfo(_DEFAULT_URI, mediaTypes);
        assertEquals(_DEFAULT_URI, info.getURI());
        assertEquals(-1, info.getLastModification());
        assertEquals(0, info.getSupportedMediaTypes().size());
    }

    public void testURIIllegal() {
        try {
            new DefaultGraphInfo(null);
            fail("Expected an exception for URI == null");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
        try {
            new DefaultGraphInfo(null, MediaType.XTM);
            fail("Expected an exception for URI == null");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
        try {
            new DefaultGraphInfo(null, MediaType.XTM, -1);
            fail("Expected an exception for URI == null");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
        try {
            new DefaultGraphInfo(null, Collections.singletonList(MediaType.XTM));
            fail("Expected an exception for URI == null");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
        try {
            new DefaultGraphInfo(null, Collections.singletonList(MediaType.XTM), -1);
            fail("Expected an exception for URI == null");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
    }

}
