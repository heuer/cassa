/*
 * Copyright 2008 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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
package com.semagia.cassa.server.sdshare;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestTagURIGenerator extends TestCase {

    /**
     * 
     *
     */
    public void testIdGeneration() {
        TagURIGenerator generator = new TagURIGenerator("semagia.com");
        long time = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
        String today = dateFormat.format(new Date(time));
        String id = "id";
        final String collectionTag = "tag:semagia.com," + today + ":cassa:" + time + ":collection:" + id;
        final String fragmentTag = "tag:semagia.com," + today + ":cassa:" + time + ":fragment:" + id;
        final String snapshotTag = "tag:semagia.com," + today + ":cassa:" + time + ":snapshot:" + id;
        assertEquals(collectionTag, generator.generateCollectionIRI(time, id));
        assertEquals(fragmentTag, generator.generateFragmentIRI(time, id));
        assertEquals(snapshotTag, generator.generateSnapshotIRI(time, id));
    }

    public void testValidDomains() {
        String[] valid = new String[] {"www.semagia.com", "semagia.com", "xy.semagia.com", "www.se-magia.com"};
        for (String domain: valid) {
            try {
                new TagURIGenerator(domain);
            }
            catch (IllegalArgumentException ex) {
                fail("Unexpected error for " + domain);
            }
        }
    }

    public void testInvalidDomains() {
        String[] invalid = new String[] {"http://www.semagia.com/", "semagia", "www.semagia/", null};
        for (String domain: invalid) {
            try {
                new TagURIGenerator(domain);
                fail("Expected a failure for " + domain);
            }
            catch (IllegalArgumentException ex) {
                // noop.
            }
        }
    }

    public void testIllegalId() {
        final TagURIGenerator generator = new TagURIGenerator("www.semagia.com");
        try {
            generator.generateCollectionIRI(-1, null);
            fail("generator.generateCollectionIRI(time, null) is illegal");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
        try {
            generator.generateFragmentIRI(-1, null);
            fail("generator.generateFragmentIRI(time, null) is illegal");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
        try {
            generator.generateSnapshotIRI(-1, null);
            fail("generator.generateSnapshotIRI(time, null) is illegal");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
    }
}
