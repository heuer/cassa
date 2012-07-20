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
package com.semagia.cassa.http.testsuite;

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

/**
 * Runs tests against a RDF store.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestRDFStore extends AbstractHTTPTestCase {

    @Override
    protected MediaType getDefaultMediaType() {
        return MediaType.RDF_XML;
    }

    @Override
    protected MediaType getDefaultUpdateMediaType() throws Exception {
        return MediaType.SPARQL_UPDATE;
    }

    @Override
    protected InputStream getGraphWithDefaultMediaType() throws Exception {
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
        Graph graph = null;
        final MediaType mt = writer.getMediaType();
        if (mt.equals(MediaType.RDF_XML)) {
            graph = getRDFXMLGraph(out);
        }
        else if (mt.equals(MediaType.TURTLE)) {
            graph = getTurtleGraph(out);
        }
        else {
            fail("Cannot handle media type: " + mt);
        }
        assertGraphEquality(g1, graph);
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

    public void testUpdateDefaultGraphNoMediaType() throws Exception {
        final String fileName1 = "/test.rdf";
        final String fileName2 = "/test2.rdf";
        final String filesMerged = "/test+test2.rdf";
        updateGraph(fileName1);
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph());
        updateGraph(fileName2);
        assertGraphEquality(getRDFXMLGraph(filesMerged), getGraph());
    }

    public void testUpdateDefaultGraph() throws Exception {
        final String fileName1 = "/test.rdf";
        final String fileName2 = "/test2.rdf";
        final String filesMerged = "/test+test2.rdf";
        updateGraph(fileName1, MediaType.RDF_XML);
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph());
        updateGraph(fileName2, MediaType.RDF_XML);
        assertGraphEquality(getRDFXMLGraph(filesMerged), getGraph());
    }

    public void testCreationNoMediaType() throws Exception {
        final URI uri = URI.create("http://www.example.org/");
        final String fileName = "/test.rdf";
        createGraph(uri, fileName);
        assertGraphEquality(getRDFXMLGraph(fileName), getGraph(uri));
        assertGraphEquality(getRDFXMLGraph(fileName), getGraph(uri, MediaType.TURTLE));
    }

    public void testCreation() throws Exception {
        final URI uri = URI.create("http://www.example.org/");
        final String fileName = "/test.rdf";
        createGraph(uri, fileName, MediaType.RDF_XML);
        assertGraphEquality(getRDFXMLGraph(fileName), getGraph(uri));
        assertGraphEquality(getRDFXMLGraph(fileName), getGraph(uri, MediaType.TURTLE));
    }

    public void testCreationIllegal() throws Exception {
        final URI uri = URI.create("http://www.example.org/create-illegal");
        final String fileName = "/test.rdf";
        createGraph(uri, fileName, INVALID_MEDIATYPE);
        assertGraphNotExists(uri);
    }

    public void testCreationIllegal2() throws Exception {
        final URI uri = URI.create("http://www.example.org/create-illegal2");
        final String fileName = "/test-invalid.rdf";
        createGraph(uri, fileName);
        assertGraphNotExists(uri);
    }

    public void testDeletion() throws Exception {
        final URI uri = URI.create("http://www.example.org/graph");
        final String fileName = "/test.rdf";
        assertGraphNotExists(uri);
        createGraph(uri, fileName, MediaType.RDF_XML);
        assertGraphExists(uri);
        assertGraphEquality(getRDFXMLGraph(fileName), getGraph(uri));
        assertGraphDelete(uri);
        assertGraphNotExists(uri);
    }

    public void testDeletionIllegal() throws Exception {
        final URI uri = URI.create("http://www.example.org/non-existing");
        assertGraphNotExists(uri);
        assertFalse(deleteGraph(uri));
    }

    public void testCreateUpdateNoMediaType() throws Exception {
        final URI uri = URI.create("http://www.example.org/create-update");
        final String fileName1 = "/test.rdf";
        final String fileName2 = "/test2.rdf";
        final String filesMerged = "/test+test2.rdf";
        assertGraphNotExists(uri);
        createGraph(uri, fileName1);
        assertGraphExists(uri);
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph(uri));
        updateGraph(uri, fileName2);
        assertGraphEquality(getRDFXMLGraph(filesMerged), getGraph(uri));
        assertGraphDelete(uri);
    }

    public void testCreateUpdate() throws Exception {
        final URI uri = URI.create("http://www.example.org/create-update");
        final String fileName1 = "/test.rdf";
        final String fileName2 = "/test2.rdf";
        final String filesMerged = "/test+test2.rdf";
        assertGraphNotExists(uri);
        createGraph(uri, fileName1, MediaType.RDF_XML);
        assertGraphExists(uri);
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph(uri));
        updateGraph(uri, fileName2, MediaType.RDF_XML);
        assertGraphEquality(getRDFXMLGraph(filesMerged), getGraph(uri));
        assertGraphDelete(uri);
    }

    public void testCreateUpdateIllegal() throws Exception {
        final URI uri = URI.create("http://www.example.org/create-update-illegal");
        final String fileName1 = "/test.rdf";
        final String fileName2 = "/test2.rdf";
        assertGraphNotExists(uri);
        createGraph(uri, fileName1);
        assertGraphExists(uri);
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph(uri));
        updateGraph(uri, fileName2, INVALID_MEDIATYPE);
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph(uri));
        assertGraphDelete(uri);
    }

    public void testCreateUpdateIllegal2() throws Exception {
        final URI uri = URI.create("http://www.example.org/create-update-illegal2");
        final String fileName1 = "/test.rdf";
        final String fileName2 = "/test-invalid.rdf";
        assertGraphNotExists(uri);
        createGraph(uri, fileName1);
        assertGraphExists(uri);
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph(uri));
        updateGraph(uri, fileName2);
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph(uri));
        assertGraphDelete(uri);
    }

    public void testReplaceNoMediaType() throws Exception {
        final URI uri = URI.create("http://www.example.org/create-replace");
        final String fileName1 = "/test.rdf";
        final String fileName2 = "/test2.rdf";
        assertGraphNotExists(uri);
        createGraph(uri, fileName1);
        assertGraphExists(uri);
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph(uri));
        createGraph(uri, fileName2);
        assertGraphEquality(getRDFXMLGraph(fileName2), getGraph(uri));
        assertGraphDelete(uri);
    }

    public void testReplace() throws Exception {
        final URI uri = URI.create("http://www.example.org/create-replace");
        final String fileName1 = "/test.rdf";
        final String fileName2 = "/test2.rdf";
        assertGraphNotExists(uri);
        createGraph(uri, fileName1, MediaType.RDF_XML);
        assertGraphExists(uri);
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph(uri));
        createGraph(uri, fileName2, MediaType.RDF_XML);
        assertGraphEquality(getRDFXMLGraph(fileName2), getGraph(uri));
        assertGraphDelete(uri);
    }

    public void testReplaceIllegal() throws Exception {
        final URI uri = URI.create("http://www.example.org/create-replace-illegal");
        final String fileName1 = "/test.rdf";
        final String fileName2 = "/test2.rdf";
        assertGraphNotExists(uri);
        createGraph(uri, fileName1);
        assertGraphExists(uri);
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph(uri));
        createGraph(uri, fileName2, INVALID_MEDIATYPE);
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph(uri));
        assertGraphDelete(uri);
    }

    public void testReplaceIllegal2() throws Exception {
        final URI uri = URI.create("http://www.example.org/create-replace-illegal2");
        final String fileName1 = "/test.rdf";
        final String fileName2 = "/test-invalid.rdf";
        assertGraphNotExists(uri);
        createGraph(uri, fileName1);
        assertGraphExists(uri);
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph(uri));
        createGraph(uri, fileName2);
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph(uri));
        assertGraphDelete(uri);
    }

    public void testCreateLocalGraphNoMediaType() throws Exception {
        final String fileName = "/test.rdf";
        final URI uri = createGraph(fileName);
        assertGraphEquality(getRDFXMLGraph(fileName), getGraph(uri));
        assertGraphDelete(uri);
    }

    public void testCreateLocalGraph() throws Exception {
        final String fileName = "/test.rdf";
        final URI uri = createGraph(fileName, MediaType.RDF_XML);
        assertGraphEquality(getRDFXMLGraph(fileName), getGraph(uri));
        assertGraphDelete(uri);
    }

    public void testCreateLocalGraphIllegal() throws Exception {
        final String fileName = "/test.rdf";
        final URI uri = createGraph(fileName, INVALID_MEDIATYPE);
        assertNull(uri);
    }

    public void testModificationNoMediaType() throws Exception {
        final URI uri = URI.create("http://www.example.org/modification");
        final String fileName1 = "/test.rdf";
        final String fileName2 = "/test2.rdf";
        final String filesMerged = "/test+test2.rdf";
        assertGraphNotExists(uri);
        createGraph(uri, fileName1);
        assertGraphExists(uri);
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph(uri));
        updateGraph(uri, fileName2);
        assertGraphEquality(getRDFXMLGraph(filesMerged), getGraph(uri));
        // Delete statements which come from graph 2
        final String query = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
                             "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                             "DELETE DATA {" +
                             "<http://psi.example.org/P2> rdf:type foaf:Person;" +
                             "                            foaf:name \"Name2\"." +
                             "}";
        assertTrue(modifyGraph(uri, query));
        // Result: Only statements from graph 1 should be available on the server
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph(uri));
        assertGraphDelete(uri);
    }

    public void testModification() throws Exception {
        final URI uri = URI.create("http://www.example.org/modification");
        final String fileName1 = "/test.rdf";
        final String fileName2 = "/test2.rdf";
        final String filesMerged = "/test+test2.rdf";
        assertGraphNotExists(uri);
        createGraph(uri, fileName1, MediaType.RDF_XML);
        assertGraphExists(uri);
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph(uri));
        updateGraph(uri, fileName2, MediaType.RDF_XML);
        assertGraphEquality(getRDFXMLGraph(filesMerged), getGraph(uri));
        // Delete statements which come from graph 2
        final String query = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
                             "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                             "DELETE DATA {" +
                             "<http://psi.example.org/P2> rdf:type foaf:Person;" +
                             "                            foaf:name \"Name2\"." +
                             "}";
        assertTrue(modifyGraph(uri, query, MediaType.SPARQL_UPDATE));
        // Result: Only statements from graph 1 should be available on the server
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph(uri));
        assertGraphDelete(uri);
    }

    public void testModificationInvalidSyntax() throws Exception {
        final URI uri = URI.create("http://www.example.org/modification");
        final String fileName1 = "/test.rdf";
        final String fileName2 = "/test2.rdf";
        final String filesMerged = "/test+test2.rdf";
        assertGraphNotExists(uri);
        createGraph(uri, fileName1, MediaType.RDF_XML);
        assertGraphExists(uri);
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph(uri));
        updateGraph(uri, fileName2, MediaType.RDF_XML);
        assertGraphEquality(getRDFXMLGraph(filesMerged), getGraph(uri));
        final String query = "PREIX foaf: <http://xmlns.com/foaf/0.1/>" +
                             "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                             "DELETE DATA {" +
                             "<http://psi.example.org/P2> rdf:type foaf:Person;" +
                             "                            foaf:name \"Name2\"." +
                             "}";
        assertFalse(modifyGraph(uri, query, MediaType.SPARQL_UPDATE));
        assertGraphEquality(getRDFXMLGraph(filesMerged), getGraph(uri));
        assertGraphDelete(uri);
    }

    public void testModificationUnknownMediaType() throws Exception {
        final URI uri = URI.create("http://www.example.org/modification");
        final String fileName1 = "/test.rdf";
        final String fileName2 = "/test2.rdf";
        final String filesMerged = "/test+test2.rdf";
        assertGraphNotExists(uri);
        createGraph(uri, fileName1, MediaType.RDF_XML);
        assertGraphExists(uri);
        assertGraphEquality(getRDFXMLGraph(fileName1), getGraph(uri));
        updateGraph(uri, fileName2, MediaType.RDF_XML);
        assertGraphEquality(getRDFXMLGraph(filesMerged), getGraph(uri));
        final String query = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
                             "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                             "DELETE DATA {" +
                             "<http://psi.example.org/P2> rdf:type foaf:Person;" +
                             "                            foaf:name \"Name2\"." +
                             "}";
        assertFalse(modifyGraph(uri, query, INVALID_MEDIATYPE));
        assertGraphEquality(getRDFXMLGraph(filesMerged), getGraph(uri));
        assertGraphDelete(uri);
    }
}
