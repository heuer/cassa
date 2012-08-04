/*
 * Copyright 2008 - 2009 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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

import junit.framework.TestCase;

/**
 * Tests against the {@link DateTimeUtils} class.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestDateTimeUtils extends TestCase {

    private static final String[][] _VALID = new String[][] {
        {"1976-09-19T11:00:00.000Z", "211978800000"},
    };

    private static final String[] _INVALID = new String[] {
        "",
        "1976-09-19",
        "1976-09-19T11:00:00.000"
    };

    public void testValidValues() {
        for (String[] valid: _VALID) {
            String stringDate = valid[0];
            long time = Long.parseLong(valid[1]);
            assertEquals(time, DateTimeUtils.fromISO8601Date(stringDate));
            assertEquals(stringDate, DateTimeUtils.toISO8601Date(time));
        }
    }

    public void testIllegalValue() {
        for (String invalid: _INVALID) {
            try {
                DateTimeUtils.fromISO8601Date("");
                fail("'" + invalid + "' is not a valid date");
            }
            catch (IllegalArgumentException ex) {
                // noop.
            }
        }
    }
}
