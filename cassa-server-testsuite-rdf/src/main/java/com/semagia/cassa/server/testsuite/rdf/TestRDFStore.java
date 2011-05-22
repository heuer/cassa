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
package com.semagia.cassa.server.testsuite.rdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;

import org.mulgara.mrg.Bnode;
import org.mulgara.mrg.Graph;
import org.mulgara.mrg.ObjectNode;
import org.mulgara.mrg.PredicateNode;
import org.mulgara.mrg.SubjectNode;
import org.mulgara.mrg.Triple;
import org.mulgara.mrg.parser.N3GraphParser;
import org.mulgara.mrg.parser.XMLGraphParser;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IWritableRepresentation;
import com.semagia.cassa.server.testsuite.AbstractCassaTestCase;

/**
 * Runs tests against a RDF store.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestRDFStore extends AbstractCassaTestCase {

    static {
        System.setProperty(SERVICE_ENDPOINT, "http://localhost:8080/cassa/service/");
        System.setProperty(GRAPH_BASE, "http://localhost:8080/cassa/g/");
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.testsuite.AbstractCassaTestCase#getDefaultMediaType()
     */
    @Override
    protected MediaType getDefaultMediaType() {
        return MediaType.RDF_XML;
    }

    /* (non-Javadoc)
     * @see com.semagia.cassa.server.testsuite.AbstractCassaTestCase#getEmptyGraphDefaultMediaType()
     */
    @Override
    protected InputStream getGraphDefaultMediaType() throws Exception {
        return getInputStream("/test.rdf");
    }

    private Graph getRDFXMLGraph(final String testFile) throws Exception {
        return getRDFXMLGraph(getInputStream(testFile));
    }

    private Graph getRDFXMLGraph(final InputStream in) throws Exception {
        return new XMLGraphParser(in).getGraph();
    }

    private Graph getRDFXMLGraph(final ByteArrayOutputStream out) throws Exception {
        return getRDFXMLGraph(new ByteArrayInputStream(out.toByteArray()));
    }

    private Graph getTurtleGraph(final InputStream in) throws Exception {
        return new N3GraphParser(in).getGraph();
    }

    private Graph getTurtleGraph(final ByteArrayOutputStream out) throws Exception {
        return getTurtleGraph(new ByteArrayInputStream(out.toByteArray()));
    }

    private void assertGraphEquality(final Graph g1, final IWritableRepresentation writer) throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.write(out);
        assertGraphEquality(g1, getRDFXMLGraph(out));
    }

    private void assertGraphEquality(final Graph g1, final Graph g2) {
        assertEquals(g1.size(), g2.size());
        for (Triple t: g2.getTriples()) {
            SubjectNode s = t.getSubject();
            PredicateNode p = t.getPredicate();
            ObjectNode o = t.getObject();
            if (s instanceof Bnode) s = null;
            if (o instanceof Bnode) o = null;
            assertTrue("Graph does not contain: " + t, g1.match(s, p, o).hasNext());
          }
    }

    public void testEmpty() throws Exception {
        assertGraphEquality(getRDFXMLGraph("/empty.rdf"), getGraph());
    }

    public void testCreation() {
        final URI uri = URI.create("http://www.example.org/");
        createGraph(uri, "/test.rdf");
    }

}
