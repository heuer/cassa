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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IWritableRepresentation;

import junit.framework.TestCase;

/**
 * Tests against {@link InputStreamWritableRepresentation}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestInputStreamWritableRepresentation extends TestCase {

    public void testIllegalStream() {
        try {
            new InputStreamWritableRepresentation(null, MediaType.JSON);
            fail("InputStream == null shouldn't be allowed");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
    }

    public void testIllegalMediaType() {
        final InputStream stream = new ByteArrayInputStream("hello".getBytes());
        try {
            new InputStreamWritableRepresentation(stream, null);
            fail("InputStream == null shouldn't be allowed");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
    }

    @SuppressWarnings("resource")
    public void testMediaType() {
        final InputStream stream = new ByteArrayInputStream("hello".getBytes());
        final IWritableRepresentation rep = new InputStreamWritableRepresentation(stream, MediaType.CTM);
        assertEquals(MediaType.CTM, rep.getMediaType());
    }

    @SuppressWarnings("resource")
    public void testEncoding() {
        final InputStream stream = new ByteArrayInputStream("hello".getBytes());
        IWritableRepresentation rep = new InputStreamWritableRepresentation(stream, MediaType.CTM);
        assertNull(rep.getEncoding());
        rep = new InputStreamWritableRepresentation(stream, MediaType.CTM, "utf-8");
        assertEquals("utf-8", rep.getEncoding());
    }

    @SuppressWarnings("resource")
    public void testContentLength() {
        final byte[] data = "hello".getBytes();
        final InputStream stream = new ByteArrayInputStream(data);
        InputStreamWritableRepresentation rep = new InputStreamWritableRepresentation(stream, MediaType.CTM);
        assertEquals(-1, rep.getContentLength());
        rep = new InputStreamWritableRepresentation(stream, MediaType.CTM, null, data.length);
        assertEquals(data.length, rep.getContentLength());
    }

    @SuppressWarnings("resource")
    public void testWrite() throws Exception {
        final byte[] data = "hello".getBytes();
        final InputStream stream = new ByteArrayInputStream(data);
        final IWritableRepresentation rep = new InputStreamWritableRepresentation(stream, MediaType.CTM);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        rep.write(out);
        final byte[] written = out.toByteArray();
        assertEquals(data.length, written.length);
        assertTrue(Arrays.equals(data, written));
    }

    @SuppressWarnings("resource")
    public void testWriteContentLength() throws Exception {
        final byte[] data = "hello".getBytes();
        final InputStream stream = new ByteArrayInputStream(data);
        final IWritableRepresentation rep = new InputStreamWritableRepresentation(stream, MediaType.CTM, null, data.length);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        rep.write(out);
        final byte[] written = out.toByteArray();
        assertEquals(data.length, written.length);
        assertTrue(Arrays.equals(data, written));
    }

    @SuppressWarnings("resource")
    public void testWriteContentLength2() throws Exception {
        final byte[] data = "hello".getBytes();
        final InputStream stream = new ByteArrayInputStream(data);
        final IWritableRepresentation rep = new InputStreamWritableRepresentation(stream, MediaType.CTM, null, data.length-1);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        rep.write(out);
        final byte[] written = out.toByteArray();
        assertEquals(data.length-1, written.length);
        final byte[] expectedData = Arrays.copyOf(data, data.length-1);
        assertTrue(Arrays.equals(expectedData, written));
    }

    @SuppressWarnings("resource")
    public void testInputStream() throws Exception {
        final byte[] data = "hello".getBytes();
        final InputStream stream = new ByteArrayInputStream(data);
        InputStreamWritableRepresentation rep = new InputStreamWritableRepresentation(stream, MediaType.CTM);
        final InputStream in = rep.getInputStream();
        assertSame(stream, in);
    }

}
