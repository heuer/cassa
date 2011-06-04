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
package com.semagia.cassa.server.store;

/**
 * Exception thrown in case the query addresses another graph than the 
 * context graph.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
@SuppressWarnings("serial")
public class GraphMismatchException extends QueryException {

    /**
     * Constructs an instance without a cause.
     *
     * @param msg The message.
     */
    public GraphMismatchException(final String msg) {
        this(msg, null);
    }

    /**
     * Constructs an instance with a detail message and a cause.
     *
     * @param msg The message.
     * @param cause The cause.
     */
    public GraphMismatchException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

}
