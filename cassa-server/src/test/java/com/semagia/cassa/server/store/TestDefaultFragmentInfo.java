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
package com.semagia.cassa.server.store;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IResource;
import com.semagia.cassa.common.dm.impl.DefaultResource;
import com.semagia.cassa.server.store.IFragmentInfo;
import com.semagia.cassa.server.store.IGraphInfo;
import com.semagia.cassa.server.store.impl.DefaultFragmentInfo;
import com.semagia.cassa.server.store.impl.DefaultGraphInfo;

import junit.framework.TestCase;

/**
 * Tests against {@link DefaultFragmentInfo}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestDefaultFragmentInfo extends TestCase {

    private static final URI _DEFAULT_URI = URI.create("http://www.example.org/test");
    private static final URI _DEFAULT_RESOURCE_URI = URI.create("http://www.example.org/test#resource");
    private static final IResource _DEFAULT_RESOURCE = new DefaultResource(_DEFAULT_RESOURCE_URI);

    public void testSingetonResourceAndMediaType() {
        final IFragmentInfo info = new DefaultFragmentInfo(_DEFAULT_URI, _DEFAULT_RESOURCE, MediaType.CTM);
        assertEquals(_DEFAULT_URI, info.getURI());
        assertEquals(-1, info.getLastModification());
        assertEquals(1, info.getSupportedMediaTypes().size());
        assertEquals(MediaType.CTM, info.getSupportedMediaTypes().get(0));
        assertEquals(1, info.getResources().size());
        assertEquals(_DEFAULT_RESOURCE, info.getResources().iterator().next());
        assertNull(info.getTitle());
        assertNull(info.getDescription());
    }

    public void testSingetonResourceAndMediaTypes() {
        final List<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.CTM);
        mediaTypes.add(MediaType.RDF_XML);
        final IFragmentInfo info = new DefaultFragmentInfo(_DEFAULT_URI, _DEFAULT_RESOURCE, mediaTypes);
        assertEquals(_DEFAULT_URI, info.getURI());
        assertEquals(-1, info.getLastModification());
        assertEquals(2, info.getSupportedMediaTypes().size());
        assertTrue(info.getSupportedMediaTypes().contains(MediaType.CTM));
        assertTrue(info.getSupportedMediaTypes().contains(MediaType.RDF_XML));
        assertEquals(1, info.getResources().size());
        assertEquals(_DEFAULT_RESOURCE, info.getResources().iterator().next());
        assertNull(info.getTitle());
        assertNull(info.getDescription());
    }

    public void testMultipleResourceAndMediaType() {
        final List<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.CTM);
        mediaTypes.add(MediaType.RDF_XML);
        final IResource resource = new DefaultResource(URI.create("http://www.exmaple.org/2ndresource"));
        final Set<IResource> resources = new HashSet<IResource>();
        resources.add(_DEFAULT_RESOURCE);
        resources.add(resource);
        final IFragmentInfo info = new DefaultFragmentInfo(_DEFAULT_URI, resources, mediaTypes);
        assertEquals(_DEFAULT_URI, info.getURI());
        assertEquals(-1, info.getLastModification());
        assertEquals(2, info.getSupportedMediaTypes().size());
        assertTrue(info.getSupportedMediaTypes().contains(MediaType.CTM));
        assertTrue(info.getSupportedMediaTypes().contains(MediaType.RDF_XML));
        assertEquals(2, info.getResources().size());
        assertTrue(info.getResources().contains(_DEFAULT_RESOURCE));
        assertTrue(info.getResources().contains(resource));
        assertNull(info.getTitle());
        assertNull(info.getDescription());
    }
    public void testMultipleResourceAndMediaTypes() {
        final IResource resource = new DefaultResource(URI.create("http://www.exmaple.org/2ndresource"));
        final Set<IResource> resources = new HashSet<IResource>();
        resources.add(_DEFAULT_RESOURCE);
        resources.add(resource);
        final IFragmentInfo info = new DefaultFragmentInfo(_DEFAULT_URI, resources, MediaType.CTM);
        assertEquals(_DEFAULT_URI, info.getURI());
        assertEquals(-1, info.getLastModification());
        assertEquals(1, info.getSupportedMediaTypes().size());
        assertEquals(MediaType.CTM, info.getSupportedMediaTypes().get(0));
        assertEquals(2, info.getResources().size());
        assertTrue(info.getResources().contains(_DEFAULT_RESOURCE));
        assertTrue(info.getResources().contains(resource));
        assertNull(info.getTitle());
        assertNull(info.getDescription());
    }

    public void testSingetonResourceAndMediaTypeTimestamp() {
        final long time = new Date().getTime();
        final IFragmentInfo info = new DefaultFragmentInfo(_DEFAULT_URI, _DEFAULT_RESOURCE, MediaType.CTM, time);
        assertEquals(_DEFAULT_URI, info.getURI());
        assertEquals(time, info.getLastModification());
        assertEquals(1, info.getSupportedMediaTypes().size());
        assertEquals(MediaType.CTM, info.getSupportedMediaTypes().get(0));
        assertEquals(1, info.getResources().size());
        assertEquals(_DEFAULT_RESOURCE, info.getResources().iterator().next());
        assertNull(info.getTitle());
        assertNull(info.getDescription());
    }

    public void testSingetonResourceAndMediaTypeTimestampTitle() {
        final long time = new Date().getTime();
        final String title = "Title";
        final IFragmentInfo info = new DefaultFragmentInfo(_DEFAULT_URI, _DEFAULT_RESOURCE, MediaType.CTM, time, title);
        assertEquals(_DEFAULT_URI, info.getURI());
        assertEquals(time, info.getLastModification());
        assertEquals(1, info.getSupportedMediaTypes().size());
        assertEquals(MediaType.CTM, info.getSupportedMediaTypes().get(0));
        assertEquals(1, info.getResources().size());
        assertEquals(_DEFAULT_RESOURCE, info.getResources().iterator().next());
        assertEquals(title, info.getTitle());
        assertNull(info.getDescription());
    }

    public void testSingetonResourceAndMediaTypeTimestampTitleDescr() {
        final long time = new Date().getTime();
        final String title = "Title";
        final String descr = "Descr";
        final IFragmentInfo info = new DefaultFragmentInfo(_DEFAULT_URI, _DEFAULT_RESOURCE, MediaType.CTM, time, title, descr);
        assertEquals(_DEFAULT_URI, info.getURI());
        assertEquals(time, info.getLastModification());
        assertEquals(1, info.getSupportedMediaTypes().size());
        assertEquals(MediaType.CTM, info.getSupportedMediaTypes().get(0));
        assertEquals(1, info.getResources().size());
        assertEquals(_DEFAULT_RESOURCE, info.getResources().iterator().next());
        assertEquals(title, info.getTitle());
        assertEquals(descr, info.getDescription());
    }

    public void testIllegalSingletonResource() {
        final IResource res = null;
        try {
            new DefaultFragmentInfo(_DEFAULT_URI, res, MediaType.CTM);
            fail("Expected IllegalArgumentException for resource==null");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
    }

    public void testIllegalMultipleResources() {
        final Set<IResource> resources = null;
        try {
            new DefaultFragmentInfo(_DEFAULT_URI, resources, MediaType.CTM);
            fail("Expected IllegalArgumentException for resources==null");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
    }

    public void testEquals() {
        final IFragmentInfo info1 = new DefaultFragmentInfo(_DEFAULT_URI, _DEFAULT_RESOURCE, MediaType.CTM);
        IFragmentInfo info2 = new DefaultFragmentInfo(_DEFAULT_URI, _DEFAULT_RESOURCE, MediaType.CTM);
        assertEquals(info1, info1);
        assertEquals(info1, info2);
        assertEquals(info2, info1);
        assertEquals(info1.hashCode(), info2.hashCode());
        info2 = new DefaultFragmentInfo(_DEFAULT_URI, _DEFAULT_RESOURCE, MediaType.XTM);
        assertFalse(info1.equals(info2));
        assertFalse(info2.equals(info1));
        assertFalse(info1.hashCode() == info2.hashCode());
    }

    public void testNotEquals() {
        final IFragmentInfo info1 = new DefaultFragmentInfo(_DEFAULT_URI, _DEFAULT_RESOURCE, MediaType.CTM);
        IGraphInfo info2 = new DefaultGraphInfo(_DEFAULT_URI, MediaType.CTM);
        assertFalse(info1.equals(info2));
        assertFalse(info2.equals(info1));
        assertFalse(info1.hashCode() == info2.hashCode());
    }

}
