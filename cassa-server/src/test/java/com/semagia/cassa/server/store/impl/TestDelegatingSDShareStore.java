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
package com.semagia.cassa.server.store.impl;

import junit.framework.TestCase;

/**
 * Tests against {@link DelegatingSDShareStore}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestDelegatingSDShareStore extends TestCase {

    public void testIllegalConstructor() {
        try {
            new DelegatingSDShareStore(null);
            fail("The constructor shouldn't accept IStore==null");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
    }
}
