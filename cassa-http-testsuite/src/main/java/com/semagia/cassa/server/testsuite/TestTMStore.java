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
package com.semagia.cassa.server.testsuite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;

import org.tinytim.mio.CXTMTopicMapWriter;
import org.tinytim.mio.TinyTimMapInputHandler;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystemFactory;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IWritableRepresentation;
import com.semagia.mio.DeserializerRegistry;
import com.semagia.mio.IDeserializer;
import com.semagia.mio.Source;
import com.semagia.mio.Syntax;

/**
 * Runs tests against a Topic Maps store.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestTMStore extends AbstractCassaTestCase {

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.testsuite.AbstractCassaTestCase#getDefaultMediaType()
     */
    @Override
    protected MediaType getDefaultMediaType() throws Exception {
        return MediaType.XTM;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.testsuite.AbstractCassaTestCase#getGraphWithDefaultMediaType()
     */
    @Override
    protected InputStream getGraphWithDefaultMediaType() throws Exception {
        return getInputStream("/test.xtm");
    }

    private TopicMap getXTMGraph(final ByteArrayOutputStream out, final URI base) throws Exception {
        final TopicMap tm = TopicMapSystemFactory.newInstance().newTopicMapSystem().createTopicMap(base.toString());
        final IDeserializer deser = DeserializerRegistry.getInstance().createDeserializer(Syntax.XTM);
        deser.setMapHandler(new TinyTimMapInputHandler(tm));
        deser.parse(new Source(new ByteArrayInputStream(out.toByteArray()), base.toString()));
        return tm;
    }

    private TopicMap getCTMGraph(final ByteArrayOutputStream out, final URI base) throws Exception {
        final TopicMap tm = TopicMapSystemFactory.newInstance().newTopicMapSystem().createTopicMap(base.toString());
        final IDeserializer deser = DeserializerRegistry.getInstance().createDeserializer(Syntax.CTM);
        deser.setMapHandler(new TinyTimMapInputHandler(tm));
        deser.parse(new Source(new ByteArrayInputStream(out.toByteArray()), base.toString()));
        return tm;
    }

    private void assertGraphEquality(final String tmFile, final IWritableRepresentation writer) throws Exception {
        TopicMap tm = null;
        final MediaType mt = writer.getMediaType();
        final URI base = URI.create("http://www.example.org");
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.write(out);
        if (MediaType.XTM.equals(mt)) {
            tm = getXTMGraph(out, base);
        }
        else if (MediaType.CTM.equals(mt)) {
            tm = getCTMGraph(out, base);
        }
        else {
            fail("Cannot handle media type: " + mt);
        }
        assertGraphEquality(tmFile + ".cxtm", tm, base);
    }

    private void assertGraphEquality(final String cxtmReferenceFile, final TopicMap tm, URI base) throws Exception {
        final InputStream in = getInputStream(cxtmReferenceFile);
        final ByteArrayOutputStream expected = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1) {
            expected.write(b);
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final CXTMTopicMapWriter writer = new CXTMTopicMapWriter(out, base.toString());
        writer.write(tm);
        final byte[] reference = expected.toByteArray();
        final byte[] result = out.toByteArray();
        if (!Arrays.equals(reference, result)) {
            fail("Expected:\n" + expected.toString("utf-8") + "\n\ngot:\n" + out.toString("utf-8"));
        }
    }

    public void testEmpty() throws Exception {
        assertGraphEquality("/empty.xtm", getGraph());
    }

    public void testUpdateDefaultGraph() throws Exception {
        final String fileName1 = "/test.xtm";
        final String fileName2 = "/test2.xtm";
        final String filesMerged = "/test+test2.xtm";
        updateGraph(fileName1);
        assertGraphEquality(fileName1, getGraph());
        updateGraph(fileName2);
        assertGraphEquality(filesMerged, getGraph());
    }

    public void testCreation() throws Exception {
        final URI uri = URI.create("http://www.example.org/");
        final String fileName = "/test.xtm";
        createGraph(uri, fileName);
        assertGraphEquality(fileName, getGraph(uri));
        assertGraphEquality(fileName, getGraph(uri, MediaType.CTM));
    }

    public void testDeletion() throws Exception {
        final URI uri = URI.create("http://www.example.org/graph");
        final String fileName = "/test.xtm";
        assertGraphNotExists(uri);
        createGraph(uri, fileName);
        assertGraphExists(uri);
        assertGraphEquality(fileName, getGraph(uri));
        assertGraphDelete(uri);
        assertGraphNotExists(uri);
    }

    public void testCreateUpdate() throws Exception {
        final URI uri = URI.create("http://www.example.org/create-update");
        final String fileName1 = "/test.xtm";
        final String fileName2 = "/test2.xtm";
        final String filesMerged = "/test+test2.xtm";
        assertGraphNotExists(uri);
        createGraph(uri, fileName1);
        assertGraphExists(uri);
        assertGraphEquality(fileName1, getGraph(uri));
        updateGraph(uri, fileName2);
        assertGraphEquality(filesMerged, getGraph(uri));
        assertGraphDelete(uri);
    }

    public void testReplace() throws Exception {
        final URI uri = URI.create("http://www.example.org/create-replace");
        final String fileName1 = "/test.xtm";
        final String fileName2 = "/test2.xtm";
        assertGraphNotExists(uri);
        createGraph(uri, fileName1);
        assertGraphExists(uri);
        assertGraphEquality(fileName1, getGraph(uri));
        createGraph(uri, fileName2);
        assertGraphEquality(fileName2, getGraph(uri));
        assertGraphDelete(uri);
    }

    public void testCreateLocalGraph() throws Exception {
        final String fileName = "/test.xtm";
        final URI uri = createGraph(fileName);
        assertGraphEquality(fileName, getGraph(uri));
        assertGraphDelete(uri);
    }

}
