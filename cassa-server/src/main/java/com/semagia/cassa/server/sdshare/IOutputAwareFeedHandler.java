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
package com.semagia.cassa.server.sdshare;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A feed handler receives events and translates these events into a syntax
 * which is written to an output stream.
 * <p>
 * A handler may ignore events which do not make sense in the particular syntax
 * but the general goal should be to serialize all events.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public interface IOutputAwareFeedHandler extends IFeedHandler {

    /**
     * Initializes the handler using the provided output stream.
     * <p>
     * This is always the very first method invocation.
     * </p>
     * 
     * @param out
     *            The output stream which is used to write to.
     * @throws IOException
     *             In case of an error.
     */
    public void init(OutputStream out) throws IOException;

}
