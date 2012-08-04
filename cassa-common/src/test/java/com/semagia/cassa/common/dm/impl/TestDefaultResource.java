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

import com.semagia.cassa.common.dm.IResource;
import com.semagia.cassa.common.dm.IResource.Role;

import junit.framework.TestCase;

/**
 * Tests against {@link DefaultResource}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestDefaultResource extends TestCase {

    private static final URI _DEFAULT_URI = URI.create("http://www.example.org/test");

    public void testRoleString() {
        assertEquals("", Role.NONE.toString());
        assertEquals("item-identifier", Role.ITEM_IDENTIFIER.toString());
        assertEquals("subject-identifier", Role.SUBJECT_IDENTIFIER.toString());
        assertEquals("subject-locator", Role.SUBJECT_LOCATOR.toString());
    }

    public void testURI() {
        final IResource res = new DefaultResource(_DEFAULT_URI);
        assertEquals(_DEFAULT_URI, res.getURI());
        assertEquals(Role.NONE, res.getRole());
        assertEquals("", res.getRole().toString());
    }

    public void testItemIdentifier() {
        final IResource res = new DefaultResource(_DEFAULT_URI, Role.ITEM_IDENTIFIER);
        assertEquals(_DEFAULT_URI, res.getURI());
        assertEquals(Role.ITEM_IDENTIFIER, res.getRole());
        assertEquals("item-identifier", res.getRole().toString());
    }

    public void testSubjectIdentifier() {
        final IResource res = new DefaultResource(_DEFAULT_URI, Role.SUBJECT_IDENTIFIER);
        assertEquals(_DEFAULT_URI, res.getURI());
        assertEquals(Role.SUBJECT_IDENTIFIER, res.getRole());
        assertEquals("subject-identifier", res.getRole().toString());
    }

    public void testSubjectLocator() {
        final IResource res = new DefaultResource(_DEFAULT_URI, Role.SUBJECT_LOCATOR);
        assertEquals(_DEFAULT_URI, res.getURI());
        assertEquals(Role.SUBJECT_LOCATOR, res.getRole());
        assertEquals("subject-locator", res.getRole().toString());
    }
    public void testEquals() {
        final IResource res1 = new DefaultResource(_DEFAULT_URI);
        IResource res2 = new DefaultResource(_DEFAULT_URI, Role.NONE);
        assertEquals(res1, res1);
        assertEquals(res1, res2);
        assertEquals(res2, res1);
        assertEquals(res1.hashCode(), res2.hashCode());
        res2 = new DefaultResource(_DEFAULT_URI, Role.SUBJECT_IDENTIFIER);
        assertFalse(res1.equals(res2));
        assertFalse(res1.hashCode() == res2.hashCode());
    }

    public void testNotEquals() {
        final IResource res1 = new DefaultResource(_DEFAULT_URI);
        final IResource res2 = new DefaultResource(_DEFAULT_URI, Role.SUBJECT_LOCATOR);
        assertFalse(res1.equals(res2));
        assertFalse(res2.equals(res1));
        assertFalse(res1.hashCode() == res2.hashCode());
        assertFalse(res1.equals(null));
        assertFalse(res1.equals(new Object()));
    }

    public void testURIIllegal() {
        try {
            new DefaultResource(null);
            fail("A resource without a URI shouldn't be allowed");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
        try {
            new DefaultResource(null, Role.SUBJECT_IDENTIFIER);
            fail("A resource without a URI shouldn't be allowed");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
        try {
            new DefaultResource(null, Role.SUBJECT_LOCATOR);
            fail("A resource without a URI shouldn't be allowed");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
        try {
            new DefaultResource(null, Role.ITEM_IDENTIFIER);
            fail("A resource without a URI shouldn't be allowed");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
    }

}
