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
package com.semagia.cassa.common;

import com.semagia.cassa.common.MediaType.Parameter;

import junit.framework.TestCase;

/**
 * Tests against the {@link MediaType} class.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestMediaType extends TestCase {

    public void testWildcard() {
        MediaType mt = MediaType.valueOf("*");
        assertEquals("*", mt.getType());
        assertEquals("*", mt.getSubtype());
        assertEquals("*/*", mt.toString());
    }

    public void testEqualsNull() {
        assertFalse(MediaType.RDF_XML.equals(null));
    }

    public void testCompatibleNull() {
        assertFalse(MediaType.RDF_XML.isCompatible(null));
    }

    public void testIllegalValueOf() {
        try {
            MediaType.valueOf(null);
            fail("MediaType.valueOf(null) is illegal");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
    }

    public void testTypeSubtype() {
        final MediaType mt = MediaType.valueOf("application / x-tm+xml");
        assertEquals("application", mt.getType());
        assertEquals("x-tm+xml", mt.getSubtype());
        assertEquals(0, mt.getParameters().size());
        assertEquals("application/x-tm+xml", mt.toString());
        final MediaType mt2 = MediaType.valueOf(mt.toString());
        assertEquals(mt, mt2);
        assertEquals(mt.hashCode(), mt2.hashCode());
    }

    public void testTypeSubtypeNormalization() {
        MediaType mt = MediaType.valueOf("APPLICATION / x-TM+xml");
        assertEquals("application", mt.getType());
        assertEquals("x-tm+xml", mt.getSubtype());
        assertEquals(0, mt.getParameters().size());
        assertEquals("application/x-tm+xml", mt.toString());
        final MediaType mt2 = MediaType.valueOf(mt.toString());
        assertEquals(mt, mt2);
        assertEquals(mt.hashCode(), mt2.hashCode());
    }

    public void testTypeSubtypeParameters() {
        MediaType mt = MediaType.valueOf("application / x-tm+xml;version=1.0");
        assertEquals("application", mt.getType());
        assertEquals("x-tm+xml", mt.getSubtype());
        assertEquals(1, mt.getParameters().size());
        Parameter param = mt.getParameters().get(0);
        assertEquals("version", param.getKey());
        assertEquals("1.0", param.getValue());
        assertEquals("application/x-tm+xml;version=1.0", mt.toString());
        assertEquals("application/x-tm+xml", mt.toStringWithoutParameters());
        final MediaType mt2 = MediaType.valueOf(mt.toString());
        assertEquals(mt, mt2);
        assertEquals(mt.hashCode(), mt2.hashCode());
    }

    public void testTypeSubtypeParametersWhitespaceNormalization() {
        MediaType mt = MediaType.valueOf("application / x-tm+xml;       version   =    1.0");
        assertEquals("application", mt.getType());
        assertEquals("x-tm+xml", mt.getSubtype());
        assertEquals(1, mt.getParameters().size());
        Parameter param = mt.getParameters().get(0);
        assertEquals("version", param.getKey());
        assertEquals("1.0", param.getValue());
        assertEquals("application/x-tm+xml;version=1.0", mt.toString());
        assertEquals("application/x-tm+xml", mt.toStringWithoutParameters());
        final MediaType mt2 = MediaType.valueOf(mt.toString());
        assertEquals(mt, mt2);
        assertEquals(mt.hashCode(), mt2.hashCode());
    }

    public void testTypeSubtypeParametersNormalization() {
        MediaType mt = MediaType.valueOf("application / x-tm+xml;VERsION=SomeValue");
        assertEquals("application", mt.getType());
        assertEquals("x-tm+xml", mt.getSubtype());
        assertEquals(1, mt.getParameters().size());
        Parameter param = mt.getParameters().get(0);
        assertEquals("version", param.getKey());
        assertEquals("SomeValue", param.getValue());
        assertEquals("application/x-tm+xml;version=SomeValue", mt.toString());
        final MediaType mt2 = MediaType.valueOf(mt.toString());
        assertEquals(mt, mt2);
        assertEquals(mt.hashCode(), mt2.hashCode());
    }

    public void testCompatible() {
        MediaType wildcard = MediaType.valueOf("*/*");
        MediaType mtApp = MediaType.valueOf("application/*");
        MediaType mt = MediaType.valueOf("application/x-tm+xml");
        assertTrue(mt.isCompatible(mt));
        MediaType mt2 = MediaType.valueOf("application/x-tm+xml");
        assertTrue(mt2.isCompatible(mt));
        assertTrue(mt.isCompatible(mt2));
        assertTrue(mt.isCompatible(wildcard));
        assertTrue(mt.isCompatible(mtApp));
        assertTrue(mtApp.isCompatible(mt));
    }

    public void testCompatibleWithParameters() {
        MediaType wildcard = MediaType.valueOf("*/*");
        MediaType mtApp = MediaType.valueOf("application/*");
        MediaType mt = MediaType.valueOf("application/x-tm+xml;version=1.0");
        assertTrue(mt.isCompatible(mt, true));
        MediaType mt2 = MediaType.valueOf("application/x-tm+xml;version=2.0");
        assertTrue(mt2.isCompatible(mt, false));
        assertFalse(mt2.isCompatible(mt, true));
        assertTrue(mt.isCompatible(mt2, false));
        assertFalse(mt.isCompatible(mt2, true));
        assertTrue(mt.isCompatible(wildcard, true));
        assertTrue(mt.isCompatible(mtApp, true));
        assertTrue(mtApp.isCompatible(mt, false));
        assertFalse(mtApp.isCompatible(mt, true));
    }

}
