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
package com.semagia.cassa.server.testsuite.tm;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;

import org.tinytim.mio.CXTMTopicMapWriter;
import org.tmapi.core.TopicMap;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.server.testsuite.AbstractCassaTestCase;

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

    private void assertGraphEquality(final String cxtmReferenceFile, final TopicMap g2, URI base) throws Exception {
        final InputStream in = this.getInputStream(cxtmReferenceFile);
        final ByteArrayOutputStream expected = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1) {
            expected.write(b);
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final CXTMTopicMapWriter writer = new CXTMTopicMapWriter(out, base.toString());
        writer.write(g2);
        final byte[] reference = expected.toByteArray();
        final byte[] result = out.toByteArray();
        if (!Arrays.equals(reference, result)) {
            fail("Expected:\n" + expected.toString("utf-8") + "\n\ngot:\n" + out.toString("utf-8"));
        }
    }

}
